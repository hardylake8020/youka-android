package com.zzqs.app_kc.z_kc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.widgets.xlistView.XListView;
import com.zzqs.app_kc.z_kc.entitiy.OilCard;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ray on 2016/12/24.
 * Class name : ChoiceOilCardActivity
 * Description :选择油卡界面
 */
public class ChoiceOilCardActivity extends BaseActivity {
    private static final int ADD_CARD = 100;
    private TextView tvConfirm, tvLeft, tvTitle, tvRight;
    private XListView lvOilCards;
    private List<OilCard> oilCards;

    @Override
    public void initVariables() {
        oilCards = new ArrayList<>();
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.z_kc_act_choice_card);
        tvConfirm = (TextView) findViewById(R.id.tvConfirm);
        tvConfirm.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {

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
    }

    @Override
    public void loadData() {

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
}
