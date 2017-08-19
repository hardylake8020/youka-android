package com.zzqs.app_kc.z_kc.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.net.Connectivities;
import com.zzqs.app_kc.widgets.SafeProgressDialog;

/**
 * Created by lance on 2016/12/3.
 */

public abstract class BaseActivity extends FragmentActivity {
    public SafeProgressDialog safePd;
    protected Context mContext;
    protected boolean isVisible;
    private InputMethodManager manager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        safePd = new SafeProgressDialog(this);
        ZZQSApplication.getInstance().addActivity(this);
        initVariables();
        initViews(savedInstanceState);
        loadData();
    }

    public abstract void initVariables();//初始化变量

    public abstract void initViews(Bundle savedInstanceState);//初始化布局、控件和绑定点击事件

    public abstract void loadData();//初始化数据

    public boolean onTouchEvent(MotionEvent event) {

        // TODO Auto-generated method stub
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (safePd != null) {
            safePd.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
        if (safePd != null) {
            safePd.dismiss();
        }
    }

    public void showToast(String msg, int length) {
        Toast.makeText(this, msg, length).show();
    }

    public boolean checkConnected() {
        if (!Connectivities.isConnected(this)) {
            showToast(getString(R.string.no_connected), Toast.LENGTH_LONG);
            return false;
        } else {
            return true;
        }
    }
}
