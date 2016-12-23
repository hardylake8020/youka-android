package com.zzqs.app_kc.z_kc.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.utils.ScreenUtil;
import com.zzqs.app_kc.z_kc.entitiy.Tender;
import com.zzqs.app_kc.z_kc.fragment.MyTenderFragment;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lance on 2016/12/23.
 */

public class MyTendersActivity extends BaseActivity {
  android.support.v4.app.FragmentManager mFragmentManager;
  ViewPager mPager;
  TextView tvLeft, tvTitle, tvUnAssigned, tvInProgress, tvCompleted;
  private ImageView cursor;

  MyTenderFragment fmUnAssigned, fmInProgress, fmCompleted;
  private FragmentAdapter mFragmentAdapter;
  private List<Fragment> listFragments; // Tab页面列表
  public int currIndex = 0;// 当前页卡编号
  private int screenWidth;

  @Override
  public void initVariables() {
    screenWidth = ScreenUtil.getScreenWidth(getApplicationContext());// 获取分辨率宽度
  }

  @Override
  public void initViews(Bundle savedInstanceState) {
    setContentView(R.layout.z_kc_act_my_tenders);
    tvLeft = (TextView) findViewById(R.id.head_back);
    tvLeft.setText("");
    tvLeft.setOnClickListener(new MyOnClickListener() {
      @Override
      public void OnceOnClick(View view) {
        finish();
      }
    });
    tvTitle = (TextView) findViewById(R.id.head_title);
    tvTitle.setText(R.string.un_deal_order);
    tvUnAssigned = (TextView) findViewById(R.id.tvUnAssigned);
    tvUnAssigned.setOnClickListener(new CursorClickListener(0));
    tvInProgress = (TextView) findViewById(R.id.tvInProgress);
    tvInProgress.setOnClickListener(new CursorClickListener(0));
    tvCompleted = (TextView) findViewById(R.id.tvCompleted);
    tvCompleted.setOnClickListener(new CursorClickListener(0));

    cursor = (ImageView) findViewById(R.id.cursor);
    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) cursor.getLayoutParams();
    lp.width = screenWidth / 3;
    cursor.setLayoutParams(lp);

    mFragmentManager = getSupportFragmentManager();
    mPager = (ViewPager) findViewById(R.id.vPager);
    mPager.setOffscreenPageLimit(2);
    listFragments = new ArrayList<>();
    fmUnAssigned = new MyTenderFragment();
    Bundle bundle1 = new Bundle();
    bundle1.putString(Tender.TENDER_STATUS, Tender.UN_ASSIGNED);
    fmUnAssigned.setArguments(bundle1);

    fmInProgress = new MyTenderFragment();
    Bundle bundle2 = new Bundle();
    bundle2.putString(Tender.TENDER_STATUS, Tender.IN_PROGRESS);
    fmInProgress.setArguments(bundle2);

    fmCompleted = new MyTenderFragment();
    Bundle bundle3 = new Bundle();
    bundle3.putString(Tender.TENDER_STATUS, Tender.COMPLETED);
    fmCompleted.setArguments(bundle3);

    listFragments.add(fmUnAssigned);
    listFragments.add(fmInProgress);
    listFragments.add(fmCompleted);
    mFragmentAdapter = new FragmentAdapter(mFragmentManager, listFragments);
    mPager.setAdapter(mFragmentAdapter);
    mPager.addOnPageChangeListener(new MyOnPageChangeListener());
    mPager.setCurrentItem(currIndex);
  }

  @Override
  public void loadData() {

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

  /**
   * 头标点击监听
   */
  public class CursorClickListener implements View.OnClickListener {
    private int index = 0;

    public CursorClickListener(int i) {
      index = i;
    }

    @Override
    public void onClick(View v) {
      mPager.setCurrentItem(index);
    }
  }

  private void resetTextView() {
    tvUnAssigned.setTextColor(ContextCompat.getColor(this, R.color.gray));
    tvInProgress.setTextColor(ContextCompat.getColor(this, R.color.gray));
    tvCompleted.setTextColor(ContextCompat.getColor(this, R.color.gray));
  }

  /**
   * 页卡切换监听
   */
  public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {


    @Override
    public void onPageSelected(int position) {
      resetTextView();
      switch (position) {
        case 0:
          tvUnAssigned.setTextColor(Color.WHITE);
          break;
        case 1:
          tvInProgress.setTextColor(Color.WHITE);
          break;
        case 2:
          tvCompleted.setTextColor(Color.WHITE);
          break;
      }
      currIndex = position;
    }

    @Override
    public void onPageScrolled(int position, float offset, int offsetPixels) {
      RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) cursor
          .getLayoutParams();

      /**
       * 利用currIndex(当前所在页面)和position(下一个页面)以及offset来
       * 设置mTabLineIv的左边距 滑动场景：
       * 记3个页面,
       * 从左到右分别为0,1,2
       * 0->1; 1->2; 2->1; 1->0
       */

      if (currIndex == 0 && position == 0)// 0->1
      {
        lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 3) + currIndex * (screenWidth / 3));
      } else if (currIndex == 1 && position == 0) // 1->0
      {
        lp.leftMargin = (int) (-(1 - offset) * (screenWidth * 1.0 / 3) + currIndex * (screenWidth / 3));

      } else if (currIndex == 1 && position == 1) // 1->2
      {
        lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 3) + currIndex * (screenWidth / 3));
      } else if (currIndex == 2 && position == 1) // 2->1
      {
        lp.leftMargin = (int) (-(1 - offset) * (screenWidth * 1.0 / 3) + currIndex * (screenWidth / 3));
      }
      cursor.setLayoutParams(lp);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }
  }
}
