package com.zzqs.app_kc.activities.OrderOperation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.activities.OrderDetailsActivity;
import com.zzqs.app_kc.activities.UploadEvent.BaseUploadEventActivity;
import com.zzqs.app_kc.activities.UploadEvent.BatchEntranceAndMidwayEventActivity;
import com.zzqs.app_kc.activities.UploadEvent.BatchPickAndDeliveryEventActivity;
import com.zzqs.app_kc.adapter.BatchOrdersAdapter;
import com.zzqs.app_kc.entity.Order;
import com.zzqs.app_kc.entity.OrderEvent;

import java.util.ArrayList;

/**
 * Created by lance on 15/11/19.
 */
public class BatchOperationActivity extends BaseOperationActivity {
    TextView tvOrdersNumber;
    RelativeLayout rlOrders;
    ListView lvOrders;
    private BatchOrdersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        for (Order order1 : orders) {
            if (order1.getStatus().equals(Order.UN_PICKUP)) {
                order.setStatus(Order.UN_PICKUP);
            } else if (order1.getStatus().equals(Order.UN_DELIVERY)) {
                order.setStatus(Order.UN_DELIVERY);
            }
            if (order1.getPickupEntranceForce().equals("true")) {
                order.setPickupEntranceForce("true");
            }
            if (order1.getPickupPhotoForce().equals("true")) {
                order.setPickupPhotoForce("true");
            }
            if (order1.getDeliveryEntranceForce().equals("true")) {
                order.setPickupEntranceForce("true");
            }
            if (order1.getDeliveryPhotoForce().equals("true")) {
                order.setDeliveryPhotoForce("true");
            }
            if (order1.getIsNew() == Order.NEW) {
                order1.setIsNew(Order.EXIST);
            }
        }
        setOrder(order);
        timeLine.setVisibility(View.GONE);
        scan.setVisibility(View.GONE);
        RelativeLayout batchOrdersView = (RelativeLayout) inflater.inflate(R.layout.view_batch_orders, null);
        insertView.addView(batchOrdersView);
        tvOrdersNumber = (TextView) findViewById(R.id.orders_number);
        lvOrders = (ListView) findViewById(R.id.orders);
        rlOrders = (RelativeLayout) findViewById(R.id.rl_orders);
        rlOrders.setOnClickListener(this);
        tvOrdersNumber.setText("...共" + orders.size() + "单");
        lvOrders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Order order1 = orders.get(i);
                Intent intent = new Intent(BatchOperationActivity.this, OrderDetailsActivity.class);
                intent.putExtra(Order.ORDER, order1);
                startActivity(intent);
            }
        });
        adapter = new BatchOrdersAdapter(this, orders);
        lvOrders.setAdapter(adapter);
    }

    @Override
    public void toShootActivity() {
        Intent intent = new Intent(getApplicationContext(), BatchPickAndDeliveryEventActivity.class);
        intent.putExtra(Order.ORDER_ID, order.getOrderId());
        String type;
        if (order.getStatus().equals(Order.UN_PICKUP_ENTRANCE) || order.getStatus().equals(Order.UN_PICKUP)) {
            type = OrderEvent.MOLD_PICKUP;
        } else {
            type = OrderEvent.MOLD_DELIVERY;
        }
        intent.putExtra(BaseUploadEventActivity.EVENT_TYPE, type);
        ArrayList<String> orderIdList = new ArrayList<>();
        for (Order order : orders) {
            orderIdList.add(order.getOrderId());
        }
        intent.putStringArrayListExtra(Order.ORDER_IDS, orderIdList);
        startActivity(intent);
    }

    @Override
    public void toUploadEventActivity(int type) {
        Intent intent = null;
        if (type == TO_ENTRANCE) {
            if (order.getStatus().equals(Order.UN_PICKUP_ENTRANCE) || order.getStatus().equals(Order.UN_DELIVERY_ENTRANCE)) {
                intent = new Intent(this, BatchEntranceAndMidwayEventActivity.class);
                intent.putExtra(Order.ORDER_ID, order.getOrderId());
                ArrayList<String> orderIdList = new ArrayList<>();
                for (Order order : orders) {
                    orderIdList.add(order.getOrderId());
                }
                intent.putStringArrayListExtra(Order.ORDER_IDS, orderIdList);
                if (order.getStatus().equals(Order.UN_PICKUP_ENTRANCE)) {
                    intent.putExtra(BaseUploadEventActivity.EVENT_TYPE, OrderEvent.MOLD_PICKUP_ENTRANCE);
                } else if (order.getStatus().equals(Order.UN_DELIVERY_ENTRANCE)) {
                    intent.putExtra(BaseUploadEventActivity.EVENT_TYPE, OrderEvent.MOLD_DELIVERY_ENTRANCE);
                }
            } else {
                Toast.makeText(this, R.string.prompt_unable_repeat_enter, Toast.LENGTH_SHORT).show();
                return;
            }
        } else if (type == TO_HALF_EVENT) {
            intent = new Intent(getApplicationContext(), BatchEntranceAndMidwayEventActivity.class);
            intent.putExtra(Order.ORDER_ID, order.getOrderId());
            ArrayList<String> orderIdList = new ArrayList<>();
            for (Order order : orders) {
                orderIdList.add(order.getOrderId());
            }
            intent.putStringArrayListExtra(Order.ORDER_IDS, orderIdList);
            intent.putExtra(BaseUploadEventActivity.EVENT_TYPE, OrderEvent.MOLD_HALFWAY);
        }
        startActivity(intent);
    }
}
