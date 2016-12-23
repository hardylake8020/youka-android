package com.zzqs.app_kc.z_kc.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;

/**
 * Created by ray on 2016/12/22.
 * Class name : MyTendersActivity
 * Description :比价、抢单中
 */
public class MyTendersActivity extends BaseActivity {
    private TextView tvWaiting, tvInProgress, tvCompleted;
    private ImageView ivCursor;
    private ViewPager vPager;

    @Override
    public void initVariables() {

    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.z_kc_my_tenders);
        ivCursor = (ImageView) findViewById(R.id.ivCursor);
        vPager = (ViewPager) findViewById(R.id.vPager);
        tvWaiting = (TextView) findViewById(R.id.tvWaiting);
        tvWaiting.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {

            }
        });
        tvInProgress = (TextView) findViewById(R.id.tvInProgress);
        tvInProgress.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {

            }
        });
        tvCompleted = (TextView) findViewById(R.id.tvCompleted);
        tvCompleted.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {

            }
        });
    }

    @Override
    public void loadData() {

    }
}
