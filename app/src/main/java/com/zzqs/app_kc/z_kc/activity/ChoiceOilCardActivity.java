package com.zzqs.app_kc.z_kc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.utils.CommonTools;
import com.zzqs.app_kc.widgets.xlistView.XListView;
import com.zzqs.app_kc.z_kc.adapter.OilCardAdapter;
import com.zzqs.app_kc.z_kc.entitiy.Car;
import com.zzqs.app_kc.z_kc.entitiy.ErrorInfo;
import com.zzqs.app_kc.z_kc.entitiy.OilCard;
import com.zzqs.app_kc.z_kc.entitiy.Tender;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;
import com.zzqs.app_kc.z_kc.network.DriverApiImpl;
import com.zzqs.app_kc.z_kc.network.OilCardApiImpl;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;


/**
 * Created by ray on 2016/12/24.
 * Class name : ChoiceOilCardActivity
 * Description :选择油卡界面
 */
public class ChoiceOilCardActivity extends BaseActivity implements XListView.IXListViewListener {
    private static final int ADD_CARD = 100;
    private TextView tvConfirm, tvLeft, tvTitle, tvRight;
    private XListView lvOilCards;
    private List<OilCard> oilCards;
    private OilCardAdapter oilCardAdapter;
    private Car car;
    private Tender tender;

    @Override
    public void initVariables() {
        oilCards = new ArrayList<>();
        car = getIntent().getParcelableExtra("car");
        tender = getIntent().getParcelableExtra("tender");
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.z_kc_act_choice_card);
        tvConfirm = (TextView) findViewById(R.id.tvConfirm);
        tvConfirm.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                if (!hasCarChoise()) {
                    Toast.makeText(ChoiceOilCardActivity.this, getString(R.string.need_choice_card), Toast.LENGTH_LONG).show();
                    return;
                }
                OilCard oilCard = new OilCard();
                for (OilCard card : oilCards) {
                    if (card.isSelect()) {
                        oilCard = card;
                    }
                }
                assginDriver(oilCard);
            }
        });
        tvLeft = (TextView) findViewById(R.id.head_back);
        tvLeft.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                finish();
            }
        });
        tvTitle = (TextView) findViewById(R.id.head_title);
        tvTitle.setText(getString(R.string.choice_oil_card));
        tvRight = (TextView) findViewById(R.id.head_right);
        tvRight.setText(getString(R.string.add_oil_card));
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                startActivityForResult(new Intent(mContext, AddOilCardActivity.class), ADD_CARD);
            }
        });
        lvOilCards = (XListView) findViewById(R.id.lvOilCards);
        lvOilCards.setPullRefreshEnable(true);
        lvOilCards.setPullLoadEnable(false);
        lvOilCards.setXListViewListener(this);
        oilCardAdapter = new OilCardAdapter(this, oilCards, true);
        lvOilCards.setAdapter(oilCardAdapter);
        lvOilCards.stopRefresh();
        lvOilCards.stopLoadMore();
        lvOilCards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < 0) {
                    return;
                }
                OilCard oilCard = oilCards.get(position - 1);
                if (oilCard.isSelect()) {
                    return;
                }
                for (OilCard card : oilCards) {
                    card.setSelect(false);
                }
                oilCard.setSelect(true);
                oilCardAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void loadData() {
        getOilCardListByDriver();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            OilCard oilCard = data.getParcelableExtra(OilCard.OILCARD);
            if (oilCard == null) {
                return;
            }
            oilCards.add(0, oilCard);
        }
    }

    @Override
    public void onRefresh() {
        getOilCardListByDriver();
    }

    @Override
    public void onLoadMore() {

    }

    private void getOilCardListByDriver() {
        safePd.show();
        OilCardApiImpl.getOilCardApiImpl().getOilCardListByDriver(CommonTools.getToken(this), new Subscriber<ErrorInfo>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                safePd.dismiss();
                onLoad();
            }

            @Override
            public void onNext(ErrorInfo errorInfo) {
                safePd.dismiss();
                if (errorInfo.getType().equals(ErrorInfo.SUCCESS)) {

                    List<OilCard> list = new ArrayList<OilCard>();
                    for (OilCard oilCard : (List<OilCard>) errorInfo.object) {
                        if (TextUtils.isEmpty(oilCard.getTruck_number())) {
                            list.add(oilCard);
                        }
                    }
                    oilCards.clear();
                    oilCards.addAll(list);
                    oilCardAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(mContext, errorInfo.getMessage(), Toast.LENGTH_LONG).show();
                }
                onLoad();
            }
        });
    }

    private void assginDriver(OilCard card) {
        safePd.show();
        DriverApiImpl.getDriverApiImpl().assginDriver(CommonTools.getToken(this), tender.getTender_id(), car.getTruck_id(), card.getCard_id(), new Subscriber<ErrorInfo>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                safePd.dismiss();
            }

            @Override
            public void onNext(ErrorInfo errorInfo) {
                safePd.dismiss();
                if (errorInfo.getType().equals(ErrorInfo.SUCCESS)) {
                    Toast.makeText(mContext, "分配成功", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, errorInfo.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * 停止刷新，
     */
    private void onLoad() {
        lvOilCards.stopRefresh();
        lvOilCards.stopLoadMore();
        lvOilCards.setRefreshTime(getString(R.string.xilstview_refreshed));
    }

    private boolean hasCarChoise() {
        boolean result = false;
        for (OilCard card : oilCards) {
            if (card.isSelect()) {
                result = true;
            }
        }
        return result;
    }
}
