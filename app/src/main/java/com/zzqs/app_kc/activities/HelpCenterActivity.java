package com.zzqs.app_kc.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zzqs.app_kc.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lance on 15/4/24.
 */
public class HelpCenterActivity extends BaseActivity implements View.OnClickListener {
    TextView title, serviceTel,back;
    RelativeLayout guidebook;
    LinearLayout callServiceTel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_help_center);
        init();
    }

    private void init() {
        title = (TextView) findViewById(R.id.head_title);
        title.setText(R.string.view_tv_help_center);
        serviceTel = (TextView) findViewById(R.id.service_tel);
        callServiceTel = (LinearLayout) findViewById(R.id.call_service_tel);
        guidebook = (RelativeLayout) findViewById(R.id.guidebook);
        back = (TextView) findViewById(R.id.head_back);
        callServiceTel.setOnClickListener(this);
        guidebook.setOnClickListener(this);
        back.setOnClickListener(this);
        findViewById(R.id.logEntry).setOnClickListener(this);
    }
    private long clickTime = 0;
    private int clickNumber = 0;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.head_back:
                finish();
                break;
            case R.id.call_service_tel:
                String servicePhone = getResources().getString(R.string.view_tv_service_phone_number);
                Pattern p = Pattern.compile("[^0-9]");
                Matcher m = p.matcher(servicePhone);
                servicePhone = m.replaceAll("");
                Intent phoneIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + servicePhone));
                startActivity(phoneIntent);
                break;
            case R.id.guidebook:
                startActivity(new Intent(getApplicationContext(), GuideActivity.class));
                break;
            case R.id.logEntry:
                ++clickNumber;
                if (clickTime == 0) {
                    clickTime = System.currentTimeMillis();
                } else {
                    if (System.currentTimeMillis() - clickTime < 3000) {
                        if (clickNumber > 5) {
                            startActivity(new Intent(HelpCenterActivity.this, LogInfoActivity.class));
                        }
                    } else {
                        clickNumber = 0;
                        clickTime = 0;
                    }
                }
                break;
            default:
                break;
        }
    }
}
