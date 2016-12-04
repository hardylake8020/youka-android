package com.zzqs.app_kc.activities;

import android.database.ContentObserver;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.entity.User;
import com.zzqs.app_kc.net.RestAPI;
import com.zzqs.app_kc.utils.CommonTools;
import com.zzqs.app_kc.utils.StringTools;
import com.zzqs.app_kc.widgets.SafeProgressDialog;

/**
 * Created by lance on 15/4/12.
 */
public class ForgetPwdActivity extends BaseActivity implements View.OnClickListener {
    EditText mobilePhone, authCode, password, passwordAgain;
    Button retrieveBtn, sendAuthCode;
    TextView headMessage;
    private MyCount mc;
    private String smsId;
    private SafeProgressDialog pd;
    public Handler smsHandler = new Handler() {
        // 这里可以进行回调的操作
        public void handleMessage(android.os.Message msg) {
            if (null != msg.obj) {
                String verifyCode = (String) msg.obj;
                authCode.setText(verifyCode);
                mc.onFinish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_forget_pwd);
        initView();
        initData();
        ContentObserver contentObserver = new ContentObserver(smsHandler) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                // 每当有新短信到来时，使用我们获取短消息的方法
                CommonTools.getSmsFromPhone(getApplicationContext(), smsHandler);
            }
        };
        getContentResolver().registerContentObserver(CommonTools.SMS_INBOX, true, contentObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pd.dismiss();
    }

    private void initView() {
        pd = new SafeProgressDialog(this);
        mobilePhone = (EditText) findViewById(R.id.mobile_phone);
        authCode = (EditText) findViewById(R.id.auth_code);
        password = (EditText) findViewById(R.id.password);
        passwordAgain = (EditText) findViewById(R.id.password_again);
        retrieveBtn = (Button) findViewById(R.id.retrieveBtn);
        sendAuthCode = (Button) findViewById(R.id.send_auth_code);
        headMessage = (TextView) findViewById(R.id.head_title);
        retrieveBtn.setOnClickListener(this);
        sendAuthCode.setOnClickListener(this);
        findViewById(R.id.head_back).setOnClickListener(this);
    }

    private void initData() {
        headMessage.setText(R.string.view_tv_retrieve_pwd);
        mc = new MyCount(120000, 1000);
        if (ZZQSApplication.getInstance().getUser()!=null){
            User user = ZZQSApplication.getInstance().getUser();
            mobilePhone.setText(user.getUsername());
        }
        ((TextView) findViewById(R.id.head_title)).setText(R.string.view_tv_retrieve_pwd);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.head_back:
                finish();
                break;
            case R.id.send_auth_code://发送验证码
                if (StringTools.isEmp(this.mobilePhone.getText().toString())) {
                    Toast.makeText(this, R.string.prompt_phone_is_null,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!StringTools.isMobileNumber(mobilePhone.getText().toString())) {
                    Toast.makeText(this, R.string.prompt_phone_is_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                sendAuthCode.setEnabled(false);
                sendAuthCode.setBackgroundResource(R.drawable.bg_gray_corners);
                mc.start();
                RestAPI.getInstance(getApplicationContext()).verifyOfRetrieve(mobilePhone.getText().toString(), new RestAPI.RestResponse() {
                    @Override
                    public void onSuccess(Object object) {
                        smsId = (String) object;
                    }

                    @Override
                    public void onFailure(Object object) {
                        Toast.makeText(ForgetPwdActivity.this, object.toString(), Toast.LENGTH_SHORT).show();
                        mc.onFinish();
                    }
                });
                break;
            case R.id.retrieveBtn:
                final String mobilePhone = this.mobilePhone.getText().toString().trim();
                final String pwd = password.getText().toString().trim();
                String pwdAgain = passwordAgain.getText().toString().trim();
                String smsCode = authCode.getText().toString().trim();
                if (StringTools.isEmp(pwd)) {
                    Toast.makeText(this, R.string.prompt_pwd_is_null, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!pwdAgain.equals(pwd)) {
                    Toast.makeText(this, R.string.prompt_pwd_is_different, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pwd.length() < 6) {
                    Toast.makeText(this, R.string.prompt_pwd_is_short, Toast.LENGTH_SHORT).show();
                    return;
                }
                pd.setCancelable(false);
                pd.setMessage(getString(R.string.prompt_dl_submitting));
                pd.show();
                RestAPI.getInstance(getApplicationContext()).retrievePwd(mobilePhone, pwd, smsCode, smsId, new RestAPI.RestResponse() {
                    @Override
                    public void onSuccess(Object object) {
                        User entity = (User) object;
                        User user = ZZQSApplication.getInstance().getUser();
                        if (null != entity) {
                            if (user == null) {
                                ZZQSApplication.getInstance().registerUser(entity);
                            } else if (!entity.getUsername().equals(user.getUsername())) {
                                ZZQSApplication.getInstance().unregisterUser(user);
                                ZZQSApplication.getInstance().registerUser(entity);
                            }
                            if (pd.isShowing()) {
                                pd.dismiss();
                            }
                            Toast.makeText(ForgetPwdActivity.this, R.string.prompt_resetting_pwd_success, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Object object) {
                        if (pd.isShowing()) {
                            pd.dismiss();
                        }
                        Toast.makeText(ForgetPwdActivity.this, object.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }

    class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            sendAuthCode.setText(l / 1000 + "秒");
        }

        @Override
        public void onFinish() {
            cancel();
            sendAuthCode.setEnabled(true);
            sendAuthCode.setBackgroundResource(R.drawable.bg_blue_corners);
            sendAuthCode.setText(R.string.view_bt_send_auth_code);
        }
    }
}
