package com.zzqs.app_kc.widgets.slidingmenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * 类名：MyRelativeLayout
 * 描述：
 * Created by ray on 15/11/26.
 */
public class MyRelativeLayout extends RelativeLayout {
    private SlidingMenuLayout sm;

    public MyRelativeLayout(Context context) {
        super(context);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setSlidingMenuLayout(SlidingMenuLayout sm) {
        this.sm = sm;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (sm.getStatus() != SlidingMenuLayout.Status.Close) {
            return true;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (sm.getStatus() != SlidingMenuLayout.Status.Close) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                sm.close();
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

}