package com.zzqs.app_kc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.adapter.OrdersAdapter;
import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.db.DaoManager;
import com.zzqs.app_kc.db.hibernate.dao.BaseDao;
import com.zzqs.app_kc.entity.Order;
import com.zzqs.app_kc.net.RestAPI;
import com.zzqs.app_kc.widgets.DialogView;
import com.zzqs.app_kc.widgets.xlistView.XListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lance on 15/5/20.
 */
public class DeliveryOrderActivity extends BaseActivity implements XListView.IXListViewListener {
    public OrdersAdapter adapter;
    public ArrayList<Order> orders;
    XListView xListView;
    private Order currentOrder;
    private BaseDao<Order> orderDao;
    public ArrayList<Order> seekList;
    EditText etSearch;
    ImageView ivDeleteText;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_delivery_orders);
        initView();
        initData();
        initSearch();
    }

    private void initView() {
        orderDao = DaoManager.getOrderDao(this);
        xListView = (XListView) findViewById(R.id.XListView);
        xListView.setPullRefreshEnable(false);
        xListView.setPullLoadEnable(false);
        xListView.setXListViewListener(this);
        xListView.setLongClickable(true);
        orders = new ArrayList<Order>();
        seekList = new ArrayList<Order>();
        seekList.addAll(orders);
        adapter = new OrdersAdapter(seekList, this);
        xListView.setAdapter(adapter);
        xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i >= 1) {
                    Intent it = new Intent(getApplicationContext(), OrderDetailsActivity.class);
                    currentOrder = orders.get(i - 1);
                    if (currentOrder.getIsNew() == Order.NEW || currentOrder.getIsNew() == Order.UPDATE) {
                        currentOrder.setIsNew(Order.EXIST);
                        orderDao.update(currentOrder);
                    }
                    it.putExtra(Order.ORDER, currentOrder);
                    startActivity(it);
                }
            }
        });
        findViewById(R.id.head_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ((TextView) findViewById(R.id.head_title)).setText(R.string.view_tv_completed_order);
    }

    private void initSearch() {
        ivDeleteText = (ImageView) findViewById(R.id.ivDeleteText);
        ivDeleteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etSearch.setText("");
                ivDeleteText.setVisibility(View.GONE);
                seekList.clear();
                seekList.addAll(orders);
                adapter.notifyDataSetChanged();
            }
        });
        etSearch = (EditText) findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if (s.length() == 0) {
                    ivDeleteText.setVisibility(View.GONE);
                    seekList.clear();
                    seekList.addAll(orders);
                    adapter.notifyDataSetChanged();
                } else {
                    ivDeleteText.setVisibility(View.VISIBLE);
                    mHandler.postDelayed(eChanged, 100);
                }
            }
        });
    }

    Runnable eChanged = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            String data = etSearch.getText().toString();
            List<Order> seekOrders = new ArrayList<Order>();
            for (Order order : orders) {
                String orderString = order.getRefNum() + "," + order.getSerialNo() + "," + order.getGoodsName() + "," + order.getToAddress() + "," + order.getToPhone() + ","
                        + order.getToMobilePhone() + "," + order.getToContact();
                if (orderString.contains(data)) {
                    seekOrders.add(order);
                }
            }
            seekList.clear();
            seekList.addAll(seekOrders);
            adapter.notifyDataSetChanged();
        }
    };

    public void initData() {
        orders.clear();
        if (orderDao == null) {
            orderDao = DaoManager.getOrderDao(getApplicationContext());
        }
        orders.addAll(orderDao.rawQuery("select * from zz_order where status=? or status=? order by _id desc", new String[]{Order.STATUS_COMMIT, Order.STATUS_INVALID}));
        seekList.clear();
        seekList.addAll(orders);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                initData();
                onLoad();
            }
        };
        RestAPI.getInstance(getApplicationContext()).getOrders(new String[]{Order.STATUS_COMMIT}, Order.DRIVER_ORDER, new RestAPI.RestResponse() {
            @Override
            public void onSuccess(Object object) {
                final List<Order> newList = (ArrayList<Order>) object;
                if (newList.size() > 0) {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            ArrayList<Order> updateList = new ArrayList<>();
                            ArrayList<Order> insertList = new ArrayList<>();
                            for (Order order : newList) {
                                order.setIsNew(Order.EXIST);
                                List<Order> list = orderDao.find(null, "order_id=?", new String[]{order.getOrderId() + ""}
                                        , null, null, null, null);
                                if (list == null || list.size() == 0) {
                                    insertList.add(order);
                                } else {
                                    order.set_id(list.get(0).get_id());
                                    updateList.add(order);
                                    if (order.getStatus().equals(Order.STATUS_INVALID)) {
                                        DialogView.showChoiceDialog(DeliveryOrderActivity.this, DialogView.SINGLE_BTN, getString(R.string.prompt_dl_title_1), "运单" + order.getSerialNo() + "已被删除", new Handler() {

                                        });
                                    }
                                }
                            }
                            orderDao.updates(updateList);
                            orderDao.inserts(insertList);
                            handler.sendEmptyMessage(0);
                        }
                    }.start();
                } else {
                    onLoad();
                }
            }

            @Override
            public void onFailure(Object object) {
                onLoad();
                if (object.toString().equals("disconnected")) {
                    DialogView.showChoiceDialog(ZZQSApplication.getInstance().getCurrentContext(), DialogView.SINGLE_BTN, getString(R.string.prompt_dl_other_equipment_login_title), getString(R.string.prompt_dl_other_equipment_login_msg), new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            ZZQSApplication.getInstance().clearUser(DeliveryOrderActivity.this);
                            ZZQSApplication.getInstance().cleanAllActivity();
                            finish();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        }
                    });
                }
            }
        });
        RestAPI.getInstance(this).getOrders(new String[]{Order.STATUS_COMMIT}, Order.WAREHOUSE_ORDER, new RestAPI.RestResponse() {
            @Override
            public void onSuccess(Object object) {
                final List<Order> newList = (ArrayList<Order>) object;
                if (newList.size() > 0 && adapter != null) {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            ArrayList<Order> updateList = new ArrayList<>();
                            ArrayList<Order> insertList = new ArrayList<>();
                            for (Order order : newList) {
                                order.setIsNew(Order.EXIST);
                                List<Order> list = orderDao.find(null, "order_id=?", new String[]{order.getOrderId() + ""}
                                        , null, null, null, null);
                                if (list == null || list.size() == 0) {
                                    insertList.add(order);
                                } else {
                                    order.set_id(list.get(0).get_id());
                                    updateList.add(order);
                                }
                            }
                            orderDao.updates(updateList);
                            orderDao.inserts(insertList);
                            handler.sendEmptyMessage(0);
                        }
                    }.start();
                } else {
                    onLoad();
                }
            }

            @Override
            public void onFailure(Object object) {
                onLoad();
                if (object.toString().equals("disconnected")) {
                    DialogView.showChoiceDialog(ZZQSApplication.getInstance().getCurrentContext(), DialogView.SINGLE_BTN, getString(R.string.prompt_dl_other_equipment_login_title), getString(R.string.prompt_dl_other_equipment_login_msg), new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            ZZQSApplication.getInstance().clearUser(DeliveryOrderActivity.this);
                            ZZQSApplication.getInstance().cleanAllActivity();
                            finish();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onLoadMore() {

    }

    /**
     * 停止刷新，
     */
    private void onLoad() {
        xListView.stopRefresh();
        xListView.stopLoadMore();
        xListView.setRefreshTime(getString(R.string.xilstview_refreshed));
    }
}
