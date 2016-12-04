package com.zzqs.app_kc.activities;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.widgets.SafeProgressDialog;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lance on 15/4/24.
 */
public class GuideActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener{
    ViewPager mViewPager;
    View view1, view2, view3, view4, view5, view6, view7, view8;
    private List<View> views;
    TextView title,back;
    ImageView turnLeft, turnRight;
    private int index;
    private SafeProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_guide);
        pd = new SafeProgressDialog(this);
        pd.show();
        initView();
        initData();
        pd.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pd.dismiss();
    }

    private void initView() {
        title = (TextView) findViewById(R.id.head_title);
        back = (TextView) findViewById(R.id.head_back);
        back.setOnClickListener(this);
        turnLeft = (ImageView) findViewById(R.id.turn_left);
        turnLeft.setOnClickListener(this);
        turnRight = (ImageView) findViewById(R.id.turn_right);
        turnRight.setOnClickListener(this);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        LayoutInflater inflater = LayoutInflater.from(this);
        view1 = inflater.inflate(R.layout.guide_page, null);
        view2 = inflater.inflate(R.layout.guide_page, null);
        view3 = inflater.inflate(R.layout.guide_page, null);
        view4 = inflater.inflate(R.layout.guide_page, null);
        view5 = inflater.inflate(R.layout.guide_page, null);
        view6 = inflater.inflate(R.layout.guide_page, null);
        view7 = inflater.inflate(R.layout.guide_page, null);
        view8 = inflater.inflate(R.layout.guide_page, null);
    }

    private void initData() {
        title.setText(R.string.view_tv_guidebook);
        views = new ArrayList<View>();
        views.add(view1);
        views.add(view2);
        views.add(view3);
        views.add(view4);
        views.add(view5);
        views.add(view6);
        views.add(view7);
        views.add(view8);
        mViewPager.setAdapter(pager);
        mViewPager.setOnPageChangeListener(this);
    }

    PagerAdapter pager = new PagerAdapter() {

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            switch (position) {
                case 0:
                    ((TextView) view1.findViewById(R.id.page_title)).setText("运单列表入口");
                    ((TextView) view1.findViewById(R.id.page_number)).setText("(1/8)");
                    ((TextView) view1.findViewById(R.id.page_content)).setText("进入APP后，默认首页即为运单列表，您能够通过点击“待提货”、“运输中”和“已完成”来查看不同状态下的运单列表。");
                    ((ImageView) view1.findViewById(R.id.page_img)).setImageResource(R.drawable.guide1);
                    break;
                case 1:
                    ((TextView) view2.findViewById(R.id.page_title)).setText("运单操作页面");
                    ((TextView) view2.findViewById(R.id.page_number)).setText("(2/8)");
                    ((TextView) view2.findViewById(R.id.page_content)).setText("点击一张运单后，您可以看到货物的信息和发、收货方的联系方式，您需要依次完成入场、货物和单据的照片拍摄的操作。您还可以点击右上角的“中途事件”来报告运程中的各种问题。");
                    ((ImageView) view2.findViewById(R.id.page_img)).setImageResource(R.drawable.guide2);
                    break;
                case 2:
                    ((TextView) view3.findViewById(R.id.page_title)).setText("拍照");
                    ((TextView) view3.findViewById(R.id.page_number)).setText("(3/8)");
                    ((TextView) view3.findViewById(R.id.page_content)).setText("在提、交货过程中，您需要对货物和单据进行拍照和上传，以确保客户得到即时的货运反馈信息。");
                    ((ImageView) view3.findViewById(R.id.page_img)).setImageResource(R.drawable.guide3);
                    break;
                case 3:
                    ((TextView) view4.findViewById(R.id.page_title)).setText("提、交货报告");
                    ((TextView) view4.findViewById(R.id.page_number)).setText("(4/8)");
                    ((TextView) view4.findViewById(R.id.page_content)).setText("提、交报告页面中，您能够浏览和删除已经保存的货物、单据照片，完成选择货物状态和添加备注（可选）之后就能够提交报告了。");
                    ((ImageView) view4.findViewById(R.id.page_img)).setImageResource(R.drawable.guide4);
                    break;
                case 4:
                    ((TextView) view5.findViewById(R.id.page_title)).setText("完成运单");
                    ((TextView) view5.findViewById(R.id.page_number)).setText("(5/8)");
                    ((TextView) view5.findViewById(R.id.page_content)).setText("当您完成了提货和收货流程后，订单就完成了，该订单会自动进入“已完成运单”列表，您可以点击查看详细内容和整个时间轴流程。");
                    ((ImageView) view5.findViewById(R.id.page_img)).setImageResource(R.drawable.guide5);
                    break;
                case 5:
                    ((TextView) view6.findViewById(R.id.page_title)).setText("查看已完成运单");
                    ((TextView) view6.findViewById(R.id.page_number)).setText("(6/8)");
                    ((TextView) view6.findViewById(R.id.page_content)).setText("回到主页，点击“已完成”即可查看已完成的运单列表，在里面您能看到所有您已经完成的运单。");
                    ((ImageView) view6.findViewById(R.id.page_img)).setImageResource(R.drawable.guide6);
                    break;
                case 6:
                    ((TextView) view7.findViewById(R.id.page_title)).setText("查看货物详情");
                    ((TextView) view7.findViewById(R.id.page_number)).setText("(7/8)");
                    ((TextView) view7.findViewById(R.id.page_content)).setText("您可以点击一张已经完成的运单来查看该运单的详细内容。");
                    ((ImageView) view7.findViewById(R.id.page_img)).setImageResource(R.drawable.guide7);
                    break;
                case 7:
                    ((TextView) view8.findViewById(R.id.page_title)).setText("查看完整时间轴");
                    ((TextView) view8.findViewById(R.id.page_number)).setText("(8/8)");
                    ((TextView) view8.findViewById(R.id.page_content)).setText("时间轴页面上详细记载了您提、交货以及中途事件的的时间和照片。");
                    ((ImageView) view8.findViewById(R.id.page_img)).setImageResource(R.drawable.guide8);
                    break;
            }
            container.addView(views.get(position));

            return views.get(position);
        }
    };

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        index = i;
        if (i == 0) {
            turnLeft.setVisibility(View.GONE);
            turnRight.setVisibility(View.VISIBLE);
        } else if (i == 7) {
            turnLeft.setVisibility(View.VISIBLE);
            turnRight.setVisibility(View.GONE);
        } else {
            turnLeft.setVisibility(View.VISIBLE);
            turnRight.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.head_back:
                finish();
                break;
            case R.id.turn_left:
                if (index != 0) {
                    mViewPager.setCurrentItem(index - 1);
                }
                break;
            case R.id.turn_right:
                if (index != 7) {
                    mViewPager.setCurrentItem(index + 1);
                }
                break;
        }
    }
}
