package com.zzqs.app_kc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zzqs.app_kc.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lance on 15/6/4.
 */
public class GuideAfterSignupActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    ViewPager mViewPager;
    private List<View> views;
    View view1, view2, view3, view4, view5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_guide_after_signup);
        init();
    }

    private void init() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<View>();
        view1 = inflater.inflate(R.layout.loading, null);
        view2 = inflater.inflate(R.layout.loading, null);
        view3 = inflater.inflate(R.layout.loading, null);
        view4 = inflater.inflate(R.layout.loading, null);
        view5 = inflater.inflate(R.layout.loading, null);
        view5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), com.zzqs.app_kc.z_kc.activity.MainActivity.class));
                finish();
            }
        });
        views.add(view1);
        views.add(view2);
        views.add(view3);
        views.add(view4);
        views.add(view5);
        mViewPager.setAdapter(pager);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

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
                    ((ImageView) view1.findViewById(R.id.img)).setImageResource(R.drawable.gas1);
                    break;
                case 1:
                    ((ImageView) view2.findViewById(R.id.img)).setImageResource(R.drawable.gas2);
                    break;
                case 2:
                    ((ImageView) view3.findViewById(R.id.img)).setImageResource(R.drawable.gas3);
                    break;
                case 3:
                    ((ImageView) view4.findViewById(R.id.img)).setImageResource(R.drawable.gas4);
                    break;
                case 4:
                    ((ImageView) view5.findViewById(R.id.img)).setImageResource(R.drawable.gas5);
                    break;
            }
            container.addView(views.get(position));

            return views.get(position);
        }
    };
}
