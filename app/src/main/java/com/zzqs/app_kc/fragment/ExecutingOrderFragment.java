package com.zzqs.app_kc.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.activities.LoginActivity;
import com.zzqs.app_kc.activities.MainActivity;
import com.zzqs.app_kc.activities.OrderOperation.SingleOperationActivity;
import com.zzqs.app_kc.adapter.OrdersAdapter;
import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.db.DaoManager;
import com.zzqs.app_kc.db.hibernate.dao.BaseDao;
import com.zzqs.app_kc.entity.Events;
import com.zzqs.app_kc.entity.Order;
import com.zzqs.app_kc.net.RestAPI;
import com.zzqs.app_kc.utils.StringTools;
import com.zzqs.app_kc.widgets.DialogView;
import com.zzqs.app_kc.widgets.xlistView.XListView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by lance on 15/5/7.
 */
public class ExecutingOrderFragment extends Fragment implements XListView.IXListViewListener {
    private MainActivity mActivity;
    private View view;
    public OrdersAdapter adapter;
    public ArrayList<Order> orders;
    XListView xListView;
    private Order currentOrder;
    private BaseDao<Order> orderDao;
    public ArrayList<Order> seekList;
    EditText etSearch;
    ImageView ivDeleteText;
    public TextView tvNoOrder;

    private boolean isUpdate = true;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                DialogView.showChoiceDialog(mActivity, DialogView.SINGLE_BTN, "提示", "运单" + msg.obj.toString() + "已被删除", null);
            }
            initData();
            isUpdate = false;
            onLoad();
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (MainActivity) activity;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_item_order, container, false);
        init(view);
        initSearch(view);
        EventBus.getDefault().register(this);
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onPause() {
        super.onPause();
        etSearch.setText("");
        ivDeleteText.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(Events.OrderEvent event) {
        switch (event.getType()) {
            case Events.ADD_ORDER_TO_EXECUTING_ORDER_FRAGMENT:
            case Events.UPDATE_ORDER_IN_EXECUTING_ORDER_FRAGMENT:
                initData();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void init(View view) {
        orderDao = DaoManager.getOrderDao(mActivity.getApplicationContext());
        xListView = (XListView) view.findViewById(R.id.XListView);
        xListView.setPullRefreshEnable(true);
        xListView.setPullLoadEnable(false);
        xListView.setXListViewListener(this);
        xListView.setLongClickable(true);
        orders = new ArrayList<Order>();
        seekList = new ArrayList<Order>();
        adapter = new OrdersAdapter(seekList, mActivity);
        xListView.setAdapter(adapter);
        xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i >= 1) {
                    Intent it = new Intent(mActivity.getApplicationContext(), SingleOperationActivity.class);
                    currentOrder = seekList.get(i - 1);
                    String orderId = currentOrder.getOrderId();
                    it.putExtra(Order.ORDER_ID, orderId);
                    startActivity(it);
                }
            }
        });
        tvNoOrder = (TextView) view.findViewById(R.id.tv_no_order);
    }

    private void initSearch(View view) {
        ivDeleteText = (ImageView) view.findViewById(R.id.ivDeleteText);
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
        etSearch = (EditText) view.findViewById(R.id.etSearch);
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
            String data = etSearch.getText().toString().toLowerCase();
            List<Order> seekOrders = new ArrayList<Order>();
            for (Order order : orders) {
                String orderString = (order.getRefNum() + "," + order.getSerialNo() + "," + order.getGoodsName() + "," + order.getToAddress() + "," + order.getToPhone() + ","
                        + order.getToMobilePhone() + "," + order.getToContact() + "," + order.getReceiverName() + "," + order.getFromAddress() + "," + order.getFromContact() + "," + order.getFromMobilePhone() + "," + order.getFromPhone()).toLowerCase();
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
        if (orderDao == null) {
            orderDao = DaoManager.getOrderDao(mActivity.getApplicationContext());
        }
        orders.clear();
        seekList.clear();
        orders.addAll(orderDao.rawQuery("select * from zz_order where order_type=? and (status=? or status=?) order by _id desc", new String[]{Order.DRIVER_ORDER, Order.UN_DELIVERY, Order.UN_DELIVERY_ENTRANCE}));
        seekList.addAll(orders);
        adapter.notifyDataSetChanged();
        isUpdate = false;
        if (seekList.size() == 0) {
            tvNoOrder.setVisibility(View.VISIBLE);
            tvNoOrder.setText(R.string.view_tv_no_executing_order);
        } else {
            tvNoOrder.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefresh() {
        if (!isUpdate) {
            isUpdate = true;
            RestAPI.getInstance(mActivity.getApplicationContext()).getOrders(new String[]{Order.UN_DELIVERY_ENTRANCE, Order.UN_DELIVERY}, Order.DRIVER_ORDER, new RestAPI.RestResponse() {
                @Override
                public void onSuccess(Object object) {
                    final List<Order> newList = (ArrayList<Order>) object;
                    if (newList.size() > 0 && adapter != null) {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                boolean haveDeleteOrder = false;
                                ArrayList<Order> insertList = new ArrayList<Order>();
                                ArrayList<Order> updateList = new ArrayList<Order>();
                                for (Order order : newList) {
                                    order.setIsNew(Order.EXIST);
                                    List<Order> list = orderDao.find(null, "order_id=?", new String[]{order.getOrderId() + ""}, null, null, null, null);
                                    if (list == null || list.size() == 0) {
                                        if (!order.getStatus().equals(Order.STATUS_INVALID)) {
                                            insertList.add(order);
                                        }
                                    } else {
                                        Order oldOrder = list.get(0);
                                        if (!StringTools.isEmp(oldOrder.getActualGoodsId())) {//新类型运单
                                            if (StringTools.isEmp(oldOrder.getOperationGoodsName())) {
                                                order.setOperationGoodsCount(oldOrder.getOperationGoodsCount());
                                                order.setOperationHasDamage(oldOrder.getOperationHasDamage());
                                                order.setOperationHasLack(oldOrder.getOperationHasLack());
                                            }
                                        }
                                        order.set_id(list.get(0).get_id());
                                        order.setIsNew(Order.EXIST);
                                        updateList.add(order);
                                        if (!list.get(0).getStatus().equals(Order.STATUS_INVALID) && order.getStatus().equals(Order.STATUS_INVALID)) {
                                            haveDeleteOrder = true;
                                        }
                                    }
                                }
                                orderDao.updates(updateList);
                                orderDao.inserts(insertList);
                                if (haveDeleteOrder) {
                                    mHandler.sendEmptyMessage(1);
                                } else {
                                    mHandler.sendEmptyMessage(0);
                                }
                            }
                        }.start();
                    } else {
                        mHandler.sendEmptyMessage(0);
                        Toast.makeText(mActivity, R.string.view_tv_no_executing_order, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Object object) {
                    onLoad();
                    if (object.toString().equals("disconnected")) {
                        DialogView.showChoiceDialog(ZZQSApplication.getInstance().getCurrentContext(), DialogView.SINGLE_BTN, getString(R.string.prompt_dl_other_equipment_login_title), getString(R.string.prompt_dl_other_equipment_login_msg), new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                ZZQSApplication.getInstance().clearUser(mActivity);
                                ZZQSApplication.getInstance().cleanAllActivity();
                                startActivity(new Intent(mActivity.getApplicationContext(), LoginActivity.class));
                            }
                        });
                    }
                }
            });
        }
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
