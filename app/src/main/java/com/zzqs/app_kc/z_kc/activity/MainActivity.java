package com.zzqs.app_kc.z_kc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.activities.LaunchActivity;
import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.service.LocationService;
import com.zzqs.app_kc.utils.CommonTools;
import com.zzqs.app_kc.widgets.CircleImageView;
import com.zzqs.app_kc.widgets.DialogView;
import com.zzqs.app_kc.widgets.xlistView.XListView;
import com.zzqs.app_kc.z_kc.adapter.TenderAdapter;
import com.zzqs.app_kc.z_kc.entitiy.CountNumber;
import com.zzqs.app_kc.z_kc.entitiy.ErrorInfo;
import com.zzqs.app_kc.z_kc.entitiy.Tender;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;
import com.zzqs.app_kc.z_kc.network.TenderApiImpl;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

/**
 * Created by lance on 2016/12/3.
 */

public class MainActivity extends BaseActivity implements XListView.IXListViewListener {
    CircleImageView cvUserPhoto;
    TextView tvUnDealOrderNum, tvUnDealWaybillNum;
    RelativeLayout rlFindGoods, rlMyCars, rlMyWallet, rlMyOilCard;
    LinearLayout llZCOrder, llDriverOrder;
    XListView lvTenders;
    TenderAdapter adapter;
    private List<Tender> tenderList;

    @Override
    public void initVariables() {
        tenderList = new ArrayList<>();
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.z_kc_act_main);
        cvUserPhoto = (CircleImageView) findViewById(R.id.cvUserPhoto);
        cvUserPhoto.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                DialogView.showConfirmDialog(mContext, "", "确认退出吗？", new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == DialogView.ACCEPT) {
                            stopService(new Intent(mContext, LocationService.class));
                            ZZQSApplication.getInstance().clearUser(mContext);
                            ZZQSApplication.getInstance().cleanAllActivity();
                            Intent intent = new Intent();
                            intent.setClass(getApplicationContext(), LaunchActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //注意本行的FLAG设置
                            startActivity(intent);
                        }
                    }
                });
            }
        });
        tvUnDealOrderNum = (TextView) findViewById(R.id.tvUnDealOrderNum);
        tvUnDealWaybillNum = (TextView) findViewById(R.id.tvUnDealWaybillNum);
        rlFindGoods = (RelativeLayout) findViewById(R.id.rlFindGoods);
        rlFindGoods.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                startActivity(new Intent(mContext, FindGoodsActivity.class));
            }
        });
        rlMyCars = (RelativeLayout) findViewById(R.id.rlMyCars);
        rlMyCars.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                Intent intent = new Intent(mContext, MyCarsActivity.class);
                intent.putExtra(MyCarsActivity.IS_SELECT_CAR, false);
                startActivity(intent);
            }
        });
        rlMyWallet = (RelativeLayout) findViewById(R.id.rlMyWallet);
        rlMyWallet.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
//                startActivity(new Intent(mContext, MyWalletActivity.class));
                startActivity(new Intent(mContext, MapViewActivity.class));

            }
        });
        rlMyOilCard = (RelativeLayout) findViewById(R.id.rlMyOilCard);
        rlMyOilCard.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                startActivity(new Intent(mContext, MyOilCardActivity.class));
            }
        });
        lvTenders = (XListView) findViewById(R.id.lvTenders);
        lvTenders.setPullRefreshEnable(true);
        lvTenders.setPullLoadEnable(false);
        lvTenders.setXListViewListener(this);
        adapter = new TenderAdapter(this, tenderList, false);
        lvTenders.setAdapter(adapter);
        lvTenders.stopRefresh();
        lvTenders.stopLoadMore();

        llZCOrder = (LinearLayout) findViewById(R.id.llZCOrder);
        llZCOrder.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                startActivity(new Intent(mContext, MyTendersActivity.class));
            }
        });
        llDriverOrder = (LinearLayout) findViewById(R.id.llDriverOrder);
        llDriverOrder.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                startActivity(new Intent(mContext, com.zzqs.app_kc.activities.MainActivity.class));
            }
        });
    }

    @Override
    public void loadData() {
//        getTenders(true);
//        getDashboard();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTenders(true);
        getDashboard();
    }

    @Override
    public void onRefresh() {
        getTenders(true);
    }

    @Override
    public void onLoadMore() {
        getTenders(false);
    }

    private void getTenders(final boolean isRefresh) {
        int count = 0;
        if (!isRefresh) {
            count = tenderList.size();
        }
        TenderApiImpl.getUserApiImpl().getUnStartedListByDriver(CommonTools.getToken(this), count, 10, "", "", "", new Subscriber<ErrorInfo>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                onLoad();
                e.printStackTrace();
            }

            @Override
            public void onNext(ErrorInfo errorInfo) {
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

    private void getDashboard() {
        TenderApiImpl.getUserApiImpl().getDashboard(CommonTools.getToken(this), new Subscriber<ErrorInfo>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(ErrorInfo errorInfo) {
                CountNumber countNumber = (CountNumber) errorInfo.object;
                tvUnDealOrderNum.setText(countNumber.getTender_count() + "");
                tvUnDealWaybillNum.setText(countNumber.getOrder_count() + "");

            }
        });
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
}
