package com.zzqs.app_kc.z_kc.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.utils.ScreenUtil;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;

/**
 * Created by ray on 16/12/14.
 * Class name : MyOilCardActivity
 * Description :我的油卡
 */
public class MyOilCardActivity extends BaseActivity {
    private TextView tvOilCard, tvETCCard;
    private ImageView ivOilCursor;
    private ViewPager vpOilCard;
    private int screenWidth;

    @Override

    public void initVariables() {

    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.z_kc_act_oil_card);
        tvOilCard = (TextView) findViewById(R.id.tvOilCard);
        tvETCCard = (TextView) findViewById(R.id.tvETCCard);
        tvOilCard.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {

            }
        });
        tvETCCard.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {

            }
        });
        initImageView();
    }

    @Override
    public void loadData() {

    }



    private void initImageView() {
        ivOilCursor = (ImageView) findViewById(R.id.ivOilCursor);
        screenWidth = ScreenUtil.getScreenWidth(getApplicationContext());// 获取分辨率宽度
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivOilCursor.getLayoutParams();
        lp.width = screenWidth / 2;
        ivOilCursor.setLayoutParams(lp);
    }
}
