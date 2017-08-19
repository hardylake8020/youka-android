package com.zzqs.app_kc.z_kc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.net.Connectivities;
import com.zzqs.app_kc.utils.CommonTools;
import com.zzqs.app_kc.widgets.xlistView.XListView;
import com.zzqs.app_kc.z_kc.adapter.TenderAdapter;
import com.zzqs.app_kc.z_kc.entitiy.ErrorInfo;
import com.zzqs.app_kc.z_kc.entitiy.Tender;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;
import com.zzqs.app_kc.z_kc.network.TenderApiImpl;
import com.zzqs.app_kc.z_kc.tool.LocationTool;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;


/**
 * Created by lance on 2016/12/4.
 */

public class FindGoodsActivity extends BaseActivity implements XListView.IXListViewListener {
    TextView tvLeft, tvTitle, tvSelectAll, tvSelectBidding, tvSelectGrab;
    EditText etStart, etEnd;
    XListView lvTenders;
    TenderAdapter adapter;
    private ImageView ivSearch;
    private List<Tender> tenderList;
    private int selectType;
    private static final int ALL = 1;
    private static final int BIDDING = 2;
    private static final int GRAB = 3;
    private String currentType = "";
    private LocationTool locationTool;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LocationTool.LOCATION_CHANGED:
                    locationTool.locationStop();
                    if (!TextUtils.isEmpty(locationTool.getCity())) {
                        etStart.setText(locationTool.getCity());
                    }
                    getTenders(true, locationTool.getCity(), "", "");
                    break;
            }
        }
    };

    @Override
    public void initVariables() {
        tenderList = new ArrayList<>();
        locationTool = new LocationTool(this);
        locationTool.enableLocationListener(handler);
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
        lvTenders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FindGoodsActivity.this, TenderDetailActivity.class);
                intent.putExtra(Tender.TENDER, tenderList.get(position-1));
                startActivity(intent);
            }
        });
        ivSearch = (ImageView) findViewById(R.id.ivSearch);
        ivSearch.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                getTendersOnAction(true);
            }
        });
    }

    @Override
    public void loadData() {
        if (!Connectivities.isGpsConnected(this)) {
            Toast.makeText(this, getString(R.string.prompt_unable_get_location_1), Toast.LENGTH_LONG).show();
            getTenders(true, "", "", "");
        } else {
            locationTool.locationStart();
        }
    }

    @Override
    public void onRefresh() {
        getTendersOnAction(true);
        onLoad();
    }


    @Override
    public void onLoadMore() {
        getTendersOnAction(false);
        onLoad();
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
                currentType = "";
                break;
            case BIDDING:
                tvSelectBidding.setTextColor(ContextCompat.getColor(this, R.color.red));
                currentType = Tender.COMPARE;
                break;
            case GRAB:
                tvSelectGrab.setTextColor(ContextCompat.getColor(this, R.color.red));
                currentType = Tender.GRAB;
                break;
        }
        selectType = type;
        getTendersOnAction(true);
    }

    private void getTendersOnAction(final boolean isRefresh) {
        String startCity = etStart.getText().toString();
        String endCity = etEnd.getText().toString();
        getTenders(isRefresh, startCity, endCity, currentType);
    }

    private void getTenders(final boolean isRefresh, String pickUpAddress, String deliveryAddress, String tenderType) {
        if (!checkConnected()) {
            return;
        }
        int count = 0;
        if (!isRefresh) {
            count = tenderList.size();
        }
        safePd.setMessage("搜索中...");
        safePd.show();
        TenderApiImpl.getUserApiImpl().getUnStartedListByDriver(CommonTools.getToken(this), count, 10, pickUpAddress, deliveryAddress, tenderType, new Subscriber<ErrorInfo>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                onLoad();
                safePd.dismiss();
                e.printStackTrace();
            }

            @Override
            public void onNext(ErrorInfo errorInfo) {
                safePd.dismiss();
                if (errorInfo.getType().equals(ErrorInfo.SUCCESS)) {
                    List<Tender> list = (ArrayList) errorInfo.object;
                    if (isRefresh) {
                        tenderList.clear();
                    }
                    tenderList.addAll(list);
                    adapter.notifyDataSetChanged();
                } else {
                    System.out.println(errorInfo.getType());
                    Toast.makeText(mContext, errorInfo.getType(), Toast.LENGTH_LONG).show();
                }
                onLoad();
            }
        });
    }
}
