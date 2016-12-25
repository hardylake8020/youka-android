package com.zzqs.app_kc.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.adapter.OrderEventsAdapter;
import com.zzqs.app_kc.db.DaoManager;
import com.zzqs.app_kc.db.hibernate.dao.BaseDao;
import com.zzqs.app_kc.entity.Order;
import com.zzqs.app_kc.entity.OrderEvent;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by lance on 15/4/11.
 */
public class OrderTimeAxisActivity extends BaseActivity {
    private Order order;
    private BaseDao<OrderEvent> orderEventBaseDao;
    private List<OrderEvent> events;
    ListView mListView;
    TextView headMessage;
    private OrderEventsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_order_time_axis);
        order = this.getIntent().getParcelableExtra(Order.ORDER);
        orderEventBaseDao = DaoManager.getOrderEventDao(this);
        mListView = (ListView) findViewById(R.id.lvEvents);
        headMessage = (TextView) findViewById(R.id.head_title);
        headMessage.setText(R.string.view_tv_check_time_axis);
        findViewById(R.id.head_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        events = orderEventBaseDao.find(null, "order_id=? and status=?", new String[]{order.getOrderId(), OrderEvent.STATUS_COMMIT + ""}, null, null, null, null);
        Collections.sort(events, new Comparator<OrderEvent>() {
            @Override
            public int compare(OrderEvent e1, OrderEvent e2) {
                int status = 0;
                if (null != e1.getCreateTime() && null != e2.getCreateTime()) {
                    status = e1.getCreateTime().compareToIgnoreCase(e2.getCreateTime());
                }
                return status;
            }
        });
        Collections.reverse(events);
        if (events == null || events.size() <= 0) {
            Toast.makeText(this, R.string.prompt_no_detail_info, Toast.LENGTH_LONG).show();
        } else {
            mAdapter = new OrderEventsAdapter(this, events);
            mListView.setAdapter(mAdapter);
        }
    }
}
