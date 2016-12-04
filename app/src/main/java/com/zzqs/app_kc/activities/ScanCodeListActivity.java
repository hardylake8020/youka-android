package com.zzqs.app_kc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.adapter.OrderCodeAdapter;
import com.zzqs.app_kc.db.DaoManager;
import com.zzqs.app_kc.db.hibernate.dao.BaseDao;
import com.zzqs.app_kc.entity.Order;
import com.zzqs.app_kc.entity.OrderCode;
import com.zzqs.app_kc.entity.OrderEvent;
import com.zzqs.app_kc.utils.SetTypeFace;
import com.zzqs.app_kc.utils.StringTools;
import com.zzqs.app_kc.widgets.swipelistview.BaseSwipeListViewListener;
import com.zzqs.app_kc.widgets.swipelistview.SwipeListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lance on 15/5/20.
 */
public class ScanCodeListActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG = "ScanCodeListActivity";
    private Order order;
    private BaseDao<OrderEvent> orderEventDao;
    TextView scanCode;
    Button saveCode;
    SwipeListView listView;
    private List<OrderCode> orderCodeList = null;
    private OrderEvent orderEvent;
    private OrderCodeAdapter orderCodeAdapter;
    private boolean isExist=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_scan_code);
        initView();
        initData();
    }

    private void initView() {
        findViewById(R.id.head_back).setOnClickListener(this);
        ((TextView)findViewById(R.id.head_title)).setText("扫码");
        scanCode = (TextView) findViewById(R.id.scan);
        saveCode = (Button) findViewById(R.id.save_code);
        listView = (SwipeListView) findViewById(R.id.codes);
        scanCode.setOnClickListener(this);
        saveCode.setOnClickListener(this);
        listView.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onListChanged() {
                listView.closeOpenedItems();
            }
        });
        SetTypeFace.setTypeFace(this, scanCode, 0, 0);
    }

    private void initData() {
        orderEventDao = DaoManager.getOrderEventDao(getApplicationContext());
        orderCodeList = new ArrayList<OrderCode>();
        order = getIntent().getParcelableExtra(Order.ORDER);
        OrderCode code = getIntent().getParcelableExtra(OrderCode.ORDER_CODE);
        orderCodeAdapter = new OrderCodeAdapter(orderCodeList, this, listView);
        listView.setAdapter(orderCodeAdapter);
        if (code != null) {
            orderCodeList.add(code);
            orderCodeAdapter.notifyDataSetInvalidated();
        }
        String eventMold = null;
        if (order.getStatus().equals(Order.UN_PICKUP) || order.getStatus().equals(Order.UN_PICKUP_ENTRANCE)) {
            eventMold = OrderEvent.MOLD_PICKUP;
        } else if (order.getStatus().equals(Order.UN_DELIVERY) || order.getStatus().equals(Order.UN_DELIVERY_ENTRANCE)) {
            eventMold = OrderEvent.MOLD_DELIVERY;
        }
        List<OrderEvent> event = orderEventDao.find(null, "order_id=? and mold=? and status=?", new String[]{order.getOrderId() + "", eventMold, OrderEvent.STATUS_NEW + ""}, null, null, null, null);
        if (event.size() == 1) {
            orderEvent = event.get(0);
        } else {
            orderEvent = new OrderEvent();
            orderEvent.setOrderId(order.getOrderId());
            orderEvent.setMold(eventMold);
            orderEvent.setStatus(OrderEvent.STATUS_NEW);
            orderEventDao.insert(orderEvent);
            orderEvent.set_id(orderEventDao.find(null, "order_id=? and mold=? and status=?", new String[]{order.getOrderId() + "", eventMold, OrderEvent.STATUS_NEW + ""}, null, null, null, null).get(0).get_id());
        }
        if (!StringTools.isEmp(orderEvent.getOrderCode())) {
            String[] scanCodes = orderEvent.getOrderCode().split(";");
            for (int i = 0; i < scanCodes.length; i++) {
                String orderCodeStr = scanCodes[i];
                OrderCode orderCode = new OrderCode();
                String[] codeAndTime = orderCodeStr.split(",");
                orderCode.setCode(codeAndTime[0]);
                orderCode.setScanTime(codeAndTime[1]);
                orderCodeList.add(orderCode);
            }
            orderCodeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.head_back:
                if (orderCodeList.size() == 0) {
                    orderEvent.setOrderCode("");
                    orderEventDao.update(orderEvent);
                }else if(orderCodeList.size()>0){
                    StringBuilder stringBuilder = new StringBuilder();
                    for (OrderCode orderCode : orderCodeList) {
                        String code = orderCode.getCode();
                        String time = orderCode.getScanTime();
                        stringBuilder.append(code + "," + time + ";");
                    }
                    stringBuilder.substring(0, stringBuilder.length() - 1);
                    orderEvent.setOrderCode(stringBuilder.toString());
                    orderEventDao.update(orderEvent);
                    Toast.makeText(this, R.string.prompt_save_success, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                }
                finish();
                break;
            case R.id.scan:
                Intent intent = new Intent(getApplicationContext(), MipcaActivityCapture.class);
                startActivityForResult(intent, MipcaActivityCapture.SCAN_CODE);
                break;
            case R.id.save_code:
                if (orderCodeList.size() > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (OrderCode orderCode : orderCodeList) {
                        String code = orderCode.getCode();
                        String time = orderCode.getScanTime();
                        stringBuilder.append(code + "," + time + ";");
                    }
                    stringBuilder.substring(0, stringBuilder.length() - 1);
                    orderEvent.setOrderCode(stringBuilder.toString());
                    orderEventDao.update(orderEvent);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, R.string.prompt_no_code_save, Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == MipcaActivityCapture.SCAN_CODE) {
            Bundle bundle = data.getExtras();
            String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
            Pattern patt = Pattern.compile(regex);
            String text = bundle.getString("result");
            Matcher matcher = patt.matcher(bundle.getString("result"));
            boolean isMatch = matcher.matches();
            if (!isMatch) {
                //获取扫码时间
                SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
                Date date = new Date(System.currentTimeMillis());
                String time = format.format(date);
                //如果只是一般得字符串码则保留显示
                OrderCode orderCode = new OrderCode();
                orderCode.setScanTime(time);
                orderCode.setCode(text);
                for (OrderCode code : orderCodeList){
                    if (orderCode.getCode().equals(code.getCode())){
                        isExist=true;
                        break;
                    }
                }
                if(isExist){
                    Toast.makeText(this, R.string.prompt_code_existing, Toast.LENGTH_SHORT).show();
                    isExist = false;
                }else {
                    orderCodeList.add(orderCode);
                }
                orderCodeAdapter.notifyDataSetInvalidated();
            }else{
                Toast.makeText(this, R.string.prompt_code_invalid, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
