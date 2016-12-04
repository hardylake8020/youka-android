package com.zzqs.app_kc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.service.HeartbeatService;

/**
 * Created by lance on 15/10/8.
 */
public class BaseActivity extends FragmentActivity {
    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ZZQSApplication.getInstance().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZZQSApplication.getInstance().removeActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ZZQSApplication.getInstance().setCurrentContext(this);
        if (!HeartbeatService.IS_LIVE){
            startService(new Intent(getApplicationContext(), HeartbeatService.class));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ZZQSApplication.getInstance().setCurrentContext(null);
    }

}
