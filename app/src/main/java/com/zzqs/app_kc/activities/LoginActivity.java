package com.zzqs.app_kc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.entity.User;
import com.zzqs.app_kc.net.RestAPI;
import com.zzqs.app_kc.receiver.PushReceiver;
import com.zzqs.app_kc.utils.StringTools;
import com.zzqs.app_kc.widgets.SafeProgressDialog;

/**
 * Created by lance on 15/3/20.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    EditText mobilePhone, password;
    Button commitLogin;
    TextView forgetPassword, siginup;
    private User user;
    private SafeProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        user = ZZQSApplication.getInstance().getUser();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialog.dismiss();
    }

    private void initView() {
        dialog = new SafeProgressDialog(this);
        mobilePhone = (EditText) findViewById(R.id.mobile_phone);
        password = (EditText) findViewById(R.id.password);
        forgetPassword = (TextView) findViewById(R.id.forgetPassword);
        siginup = (TextView) findViewById(R.id.signup);
        String text = getResources().getString(R.string.view_tv_sign_up_from_login);
        SpannableString styledText = new SpannableString(text);
        styledText.setSpan(new TextAppearanceSpan(this, R.style.color_gray), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        siginup.setText(styledText, TextView.BufferType.SPANNABLE);
        commitLogin = (Button) findViewById(R.id.commitLogin);
        forgetPassword.setOnClickListener(this);
        siginup.setOnClickListener(this);
        findViewById(R.id.head_back).setOnClickListener(this);
        commitLogin.setOnClickListener(this);
        ((TextView) findViewById(R.id.head_title)).setText(R.string.view_bt_login);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.head_back:
                finish();
                break;
            case R.id.forgetPassword:
                startActivity(new Intent(getApplicationContext(), ForgetPwdActivity.class));
                finish();
                break;
            case R.id.signup:
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
                finish();
                break;
            case R.id.commitLogin:
                String username = mobilePhone.getText().toString().trim();
                String pwd = password.getText().toString().trim();
                if (StringTools.isEmp(mobilePhone.getText().toString().trim())) {
                    Toast.makeText(this, R.string.prompt_phone_is_null, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringTools.isEmp(password.getText().toString().trim())) {
                    Toast.makeText(this, R.string.prompt_pwd_is_null, Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.setCancelable(false);
                dialog.setMessage(getString(R.string.prompt_dl_logining));
                dialog.show();
                RestAPI.getInstance(getApplicationContext()).login(username, pwd, new RestAPI.RestResponse() {
                    @Override
                    public void onSuccess(Object object) {
                        User entity = (User) object;
                        if (null != entity) {
                            if (user == null) {
                                ZZQSApplication.getInstance().registerUser(entity);
                            } else if (!entity.getUsername().equals(user.getUsername())) {
                                ZZQSApplication.getInstance().unregisterUser(user);
                                ZZQSApplication.getInstance().registerUser(entity);
                            }
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            final String cid = PushReceiver.cid;
                            if (!StringTools.isEmp(cid)) {
                                RestAPI.getInstance(LoginActivity.this).setDeviceId(cid, new RestAPI.RestResponse() {

                                    @Override
                                    public void onSuccess(Object object) {
                                        ZZQSApplication.getInstance().getUser().setDevice_id(cid);
                                        ZZQSApplication.getInstance().updateUser(user);
                                    }

                                    @Override
                                    public void onFailure(Object object) {
                                    }
                                });
                            }
                            Toast.makeText(LoginActivity.this, R.string.prompt_login_success, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), com.zzqs.app_kc.z_kc.activity.MainActivity.class));
                            finish();
                        }
                    }


                    @Override
                    public void onFailure(Object object) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(LoginActivity.this, object.toString(), Toast.LENGTH_LONG).show();
                    }
                });
        }
    }
}
