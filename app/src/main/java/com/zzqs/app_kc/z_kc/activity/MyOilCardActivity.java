package com.zzqs.app_kc.z_kc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.utils.ScreenUtil;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ray on 16/12/14.
 * Class name : MyOilCardActivity
 * Description :我的油卡
 */
public class MyOilCardActivity extends BaseActivity {
    private static final int ADD_CARD = 100;
    private TextView tvOilCard, tvETCCard, tvLeft, tvTitle, tvRight;
    private ImageView ivOilCursor;
    private ViewPager vpOilCard;
    private android.support.v4.app.FragmentManager mFragmentManager;
    private FragmentAdapter mFragmentAdapter;
    private List<Fragment> listFragments; // Tab页面列表
    public int currIndex = 0;// 当前页卡编号
    private int screenWidth;

    @Override

    public void initVariables() {

    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.z_kc_act_oil_card);
        tvTitle = (TextView) findViewById(R.id.head_title);
        tvTitle.setText(R.string.my_cards);
        tvLeft = (TextView) findViewById(R.id.head_back);
        tvLeft.setText("");
        tvLeft.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                finish();
            }
        });
        tvRight = (TextView) findViewById(R.id.head_right);
        tvRight.setText(getString(R.string.add_oil_card));
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                startActivityForResult(new Intent(mContext, AddOilCardActivity.class), ADD_CARD);

            }
        });
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
        mFragmentManager = getSupportFragmentManager();
        vpOilCard= (ViewPager) findViewById(R.id.vpOilCard);
        listFragments = new ArrayList<>();

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

    /**
     * ViewPager适配器
     */
    public class FragmentAdapter extends FragmentPagerAdapter {

        List<Fragment> fragmentList = new ArrayList<Fragment>();

        public FragmentAdapter(android.support.v4.app.FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }
    }
}
