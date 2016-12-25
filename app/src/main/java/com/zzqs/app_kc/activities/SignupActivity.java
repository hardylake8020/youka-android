package com.zzqs.app_kc.activities;

import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.entity.User;
import com.zzqs.app_kc.net.RestAPI;
import com.zzqs.app_kc.receiver.PushReceiver;
import com.zzqs.app_kc.utils.BPUtil;
import com.zzqs.app_kc.utils.CommonTools;
import com.zzqs.app_kc.utils.StringTools;
import com.zzqs.app_kc.widgets.SafeProgressDialog;


/**
 * Created by lance on 15/3/20.
 */
public class SignupActivity extends BaseActivity implements View.OnClickListener {
    EditText mobilePhone, authCode, password, passwordAgain, etImageCode;
    ImageView ivImageCode;
    Button commitSignup, sendAuthCode;
    TextView UserAgreement, headMessage;
    private MyCount mc;
    private String smsId;
    private SafeProgressDialog dialog;
    public Handler smsHandler = new Handler() {
        // 这里可以进行回调的操作
        public void handleMessage(android.os.Message msg) {
            if (null != msg.obj) {
                String verifyCode = (String) msg.obj;
                authCode.setText(verifyCode);
                if (null != mc) {
                    mc.onFinish();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_signup);
        ContentObserver contentObserver = new ContentObserver(smsHandler) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                // 每当有新短信到来时，使用我们获取短消息的方法
                CommonTools.getSmsFromPhone(SignupActivity.this, smsHandler);
            }
        };
        getContentResolver().registerContentObserver(CommonTools.SMS_INBOX, true, contentObserver);
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialog.dismiss();
    }

    private void initView() {
        dialog = new SafeProgressDialog(this);
        mobilePhone = (EditText) findViewById(R.id.mobile_phone);
        authCode = (EditText) findViewById(R.id.auth_code);
        password = (EditText) findViewById(R.id.password);
        passwordAgain = (EditText) findViewById(R.id.password_again);
        commitSignup = (Button) findViewById(R.id.commit_signup);
        sendAuthCode = (Button) findViewById(R.id.send_auth_code);
        UserAgreement = (TextView) findViewById(R.id.user_agreement);
        headMessage = (TextView) findViewById(R.id.head_title);
        commitSignup.setOnClickListener(this);
        sendAuthCode.setOnClickListener(this);
        findViewById(R.id.head_back).setOnClickListener(this);
        UserAgreement.setOnClickListener(this);
        ((TextView) findViewById(R.id.head_title)).setText(R.string.view_bt_sign_up);
        etImageCode = (EditText) findViewById(R.id.etImageCode);
        ivImageCode = (ImageView) findViewById(R.id.ivImageCode);
        ivImageCode.setOnClickListener(this);
    }

    private void initData() {
        headMessage.setText(R.string.view_bt_sign_up);
        mc = new MyCount(60000, 1000);
        String phoneNumber = CommonTools.getPhoneNumber(this);
        if (!StringTools.isEmp(phoneNumber))
            this.mobilePhone.setText(phoneNumber);
        ivImageCode.setImageBitmap(BPUtil.getInstance().createBitmap());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivImageCode:
                ivImageCode.setImageBitmap(BPUtil.getInstance().createBitmap());
                break;
            case R.id.head_back:
                finish();
                break;
            case R.id.user_agreement://查看用户协议
                break;
            case R.id.send_auth_code://发送验证码
                if (StringTools.isEmp(mobilePhone.getText().toString())) {
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
                sendAuthCode.setText("60秒");
                mc.start();
                RestAPI.getInstance(getApplicationContext()).verify(mobilePhone.getText().toString(), new RestAPI.RestResponse() {
                    @Override
                    public void onSuccess(Object object) {
                        smsId = (String) object;
                    }

                    @Override
                    public void onFailure(Object object) {
                        Toast.makeText(SignupActivity.this, object.toString(), Toast.LENGTH_SHORT).show();
                        if (null != mc) {
                            mc.onFinish();
                        }
                    }
                });
                break;
            case R.id.commit_signup:
                final String phone = mobilePhone.getText().toString().trim();
                final String pwd = password.getText().toString().trim();
                final String pwdAgain = passwordAgain.getText().toString().trim();
                String smsCode = authCode.getText().toString().trim();
                String imageCode = etImageCode.getText().toString().trim().toLowerCase();
                if (!imageCode.equals(BPUtil.getInstance().getCode())) {
                    Toast.makeText(this, "你输入的图片验证码有误,请检查", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringTools.isEmp(phone)) {
                    Toast.makeText(this, R.string.prompt_phone_is_null,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!StringTools.isMobileNumber(phone)) {
                    Toast.makeText(this, R.string.prompt_phone_is_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringTools.isEmp(pwd)) {
                    Toast.makeText(this, R.string.prompt_pwd_is_null, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!pwdAgain.equals(pwd)) {
                    Toast.makeText(this, R.string.prompt_pwd_is_different, Toast.LENGTH_SHORT).show();
                    return;
                }
//                if (StringTools.isEmp(smsCode)) {
//                    Toast.makeText(this, R.string.smscode_is_null, Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if (pwd.length() < 6) {
                    Toast.makeText(this, R.string.prompt_pwd_is_short, Toast.LENGTH_SHORT).show();
                    return;
                }

                dialog.setCancelable(false);
                dialog.setMessage(getString(R.string.prompt_dl_submitting));
                dialog.show();
                RestAPI.getInstance(getApplicationContext()).signup(phone, pwd, "100", "100", new RestAPI.RestResponse() {
                    @Override
                    public void onSuccess(Object object) {
                        RestAPI.getInstance(getApplicationContext()).login(phone, pwd, new RestAPI.RestResponse() {
                            @Override
                            public void onSuccess(Object object) {
                                if (object instanceof User) {
                                    final User user = (User) object;
                                    if (dialog.isShowing()) {
                                        dialog.dismiss();
                                    }
                                    Toast.makeText(SignupActivity.this, R.string.prompt_sign_up_success, Toast.LENGTH_SHORT).show();
                                    final String cid = PushReceiver.cid;
                                    if (!StringTools.isEmp(cid)) {
                                        RestAPI.getInstance(getApplicationContext()).setDeviceId(cid, new RestAPI.RestResponse() {

                                            @Override
                                            public void onSuccess(Object object) {
                                                user.setDevice_id(cid);
                                                ZZQSApplication.getInstance().updateUser(user);
                                            }

                                            @Override
                                            public void onFailure(Object object) {
                                            }
                                        });
                                    }
                                }
                                ZZQSApplication.getInstance().cleanAllActivity();
                                startActivity(new Intent(getApplicationContext(), GuideAfterSignupActivity.class));
                            }

                            @Override
                            public void onFailure(Object object) {
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                ivImageCode.setImageBitmap(BPUtil.getInstance().createBitmap());
                                Toast.makeText(SignupActivity.this, object.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Object object) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        ivImageCode.setImageBitmap(BPUtil.getInstance().createBitmap());
                        Toast.makeText(SignupActivity.this, object.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            default:
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
            sendAuthCode.setBackgroundResource(R.drawable.bg_green_corners);
            sendAuthCode.setText(R.string.view_bt_send_auth_code);
        }
    }
}
