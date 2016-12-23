package com.zzqs.app_kc.z_kc.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.widgets.xlistView.XListView;
import com.zzqs.app_kc.z_kc.adapter.TenderAdapter;
import com.zzqs.app_kc.z_kc.entitiy.Tender;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lance on 2016/12/4.
 */

public class FindGoodsActivity extends BaseActivity implements XListView.IXListViewListener {
    TextView tvLeft, tvTitle, tvSelectAll, tvSelectBidding, tvSelectGrab;
    EditText etStart, etEnd;
    XListView lvTenders;
    TenderAdapter adapter;
    private List<Tender> tenderList;
    private int selectType;
    private static final int ALL = 1;
    private static final int BIDDING = 2;
    private static final int GRAB = 3;

    @Override
    public void initVariables() {
        tenderList = new ArrayList<>();
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.z_kc_act_find_goods);
        tvLeft = (TextView) findViewById(R.id.head_back);
        tvLeft.setText("");
        tvLeft.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                finish();
            }
        });
        tvTitle = (TextView) findViewById(R.id.head_title);
        tvTitle.setText(R.string.source_of_goods);
        tvSelectAll = (TextView) findViewById(R.id.tvSelectAll);
        tvSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectType(ALL);
            }
        });
        tvSelectAll.setTextColor(ContextCompat.getColor(this, R.color.red));
        tvSelectBidding = (TextView) findViewById(R.id.tvSelectBidding);
        tvSelectBidding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectType(BIDDING);
            }
        });
        tvSelectGrab = (TextView) findViewById(R.id.tvSelectGrab);
        tvSelectGrab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectType(GRAB);
            }
        });
        etStart = (EditText) findViewById(R.id.etStart);
        etEnd = (EditText) findViewById(R.id.etEnd);
        lvTenders = (XListView) findViewById(R.id.lvTenders);
        lvTenders.setPullRefreshEnable(true);
        lvTenders.setPullLoadEnable(false);
        lvTenders.setXListViewListener(this);
        adapter = new TenderAdapter(this, tenderList, false);
        lvTenders.setAdapter(adapter);
        lvTenders.stopRefresh();
        lvTenders.stopLoadMore();
    }

    @Override
    public void loadData() {

    }

    @Override
    public void onRefresh() {
        getKCOrders();
        onLoad();
    }

    @Override
    public void onLoadMore() {
        getKCOrders();
        onLoad();
    }

    private void getKCOrders() {
    }

    /**
     * 停止刷新，
     */
    private void onLoad() {
        lvTenders.stopRefresh();
        lvTenders.stopLoadMore();
        lvTenders.setRefreshTime(getString(R.string.xilstview_refreshed));
        if (tenderList.size() >= 10) {
            lvTenders.setPullLoadEnable(true);
        }
    }

    private void setSelectType(int type) {
        if (selectType == type) {
            return;
        }
        tvSelectAll.setTextColor(ContextCompat.getColor(this, R.color.click_gray));
        tvSelectBidding.setTextColor(ContextCompat.getColor(this, R.color.click_gray));
        tvSelectGrab.setTextColor(ContextCompat.getColor(this, R.color.click_gray));
        switch (type) {
            case ALL:
                tvSelectAll.setTextColor(ContextCompat.getColor(this, R.color.red));
                break;
            case BIDDING:
                tvSelectBidding.setTextColor(ContextCompat.getColor(this, R.color.red));
                break;
            case GRAB:
                tvSelectGrab.setTextColor(ContextCompat.getColor(this, R.color.red));
                break;
        }
        selectType = type;

        getKCOrders();
    }
}
