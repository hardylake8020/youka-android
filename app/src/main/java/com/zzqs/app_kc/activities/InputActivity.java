package com.zzqs.app_kc.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.entity.User;
import com.zzqs.app_kc.net.RestAPI;
import com.zzqs.app_kc.utils.StringTools;
import com.zzqs.app_kc.widgets.DateTimePickDialogUtil;
import com.zzqs.app_kc.widgets.DialogView;
import com.zzqs.app_kc.widgets.SafeProgressDialog;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lance on 15/11/8.
 */
public class InputActivity extends BaseActivity implements View.OnClickListener {
    EditText editText;
    TextView title, commit, driverDate,back;
    public static final String INFO_TYPE = "info_type";
    public static final int USER_NAME = 1;
    public static final int DRIVER_YEAR = 2;
    public static final int MOBLIE_PHONE = 3;
    private int type;
    private User user;
    private String lastInfo;
    SafeProgressDialog pd;
    DateTimePickDialogUtil dateTimePickDialogUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_input);
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pd.dismiss();
    }

    private void initView() {
        pd = new SafeProgressDialog(this);
        editText = (EditText) findViewById(R.id.input_info);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {
                InputMethodManager inputManager =
                        (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);
            }

        }, 200);
        back = (TextView) findViewById(R.id.head_back);
        back.setOnClickListener(this);
        title = (TextView) findViewById(R.id.head_title);
        commit = (TextView) findViewById(R.id.head_right);
        commit.setVisibility(View.VISIBLE);
        commit.setOnClickListener(this);
        driverDate = (TextView) findViewById(R.id.driver_date);
        driverDate.setOnClickListener(this);
    }

    private void initData() {
        user = ZZQSApplication.getInstance().getUser();
        type = getIntent().getIntExtra(INFO_TYPE, 0);
        commit.setText(R.string.view_tv_finish);
        switch (type) {
            case USER_NAME:
                title.setText(R.string.view_tv_modify_name);
                lastInfo = user.getNickname();
                if (!StringTools.isEmp(lastInfo)) {
                    editText.setText(lastInfo);
                }
                editText.setHint(R.string.view_et_input_name);
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case DRIVER_YEAR:
                driverDate.setVisibility(View.VISIBLE);
                editText.setVisibility(View.GONE);
                title.setText(R.string.view_tv_modify_driving_date);
                lastInfo = user.getDrivingDate();
                if (!StringTools.isEmp(lastInfo)) {
                    driverDate.setText(lastInfo);
                } else {
                    driverDate.setText(R.string.view_et_input_driving_date);
                }
                dateTimePickDialogUtil = new DateTimePickDialogUtil(this, lastInfo);
                break;
            case MOBLIE_PHONE:
                title.setText(R.string.view_tv_modify_phone);
                lastInfo = user.getPhone();
                if (!StringTools.isEmp(lastInfo)) {
                    editText.setText(lastInfo);
                } else {
                    editText.setText(user.getUsername());
                }
                editText.setHint(R.string.view_et_input_phone);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            default:
                break;
        }
        editText.setSelection(editText.getText().length());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.head_back:
                finish();
                break;
            case R.id.head_right:
                String newInfo;
                if (type == DRIVER_YEAR) {
                    newInfo = driverDate.getText().toString().trim();
                } else {
                    newInfo = editText.getText().toString().trim();
                }
                if (!newInfo.equals(lastInfo)) {
                    if (!StringTools.isEmp(newInfo)) {
                        switch (type) {
                            case USER_NAME:
                                user.setNickname(newInfo);
                                break;
                            case DRIVER_YEAR:
                                user.setDrivingDate(newInfo);
                                break;
                            case MOBLIE_PHONE:
                                if (StringTools.isMobileNumber(newInfo)) {
                                    user.setPhone(newInfo);
                                } else {
                                    Toast.makeText(this, R.string.prompt_phone_is_error, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                break;
                        }
                    } else {
                        Animation anim = AnimationUtils.loadAnimation(this, R.anim.jitter);
                        editText.startAnimation(anim);
                        Toast.makeText(this, R.string.prompt_no_content, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    pd.setMessage(getString(R.string.prompt_dl_saving));
                    pd.show();
                    RestAPI.getInstance(getApplicationContext()).updateProfile(user, new RestAPI.RestResponse() {
                        @Override
                        public void onSuccess(Object object) {
                            if (pd.isShowing()) {
                                pd.dismiss();
                            }
                            ZZQSApplication.getInstance().updateUser(user);
                            Toast.makeText(InputActivity.this, R.string.prompt_modify_user_info_success, Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailure(Object object) {
                            if (pd != null) {
                                pd.dismiss();
                            }
                            if (object.toString().equals("disconnected")) {
                                DialogView.showChoiceDialog(ZZQSApplication.getInstance().getCurrentContext(), DialogView.SINGLE_BTN, getString(R.string.prompt_dl_other_equipment_login_title), getString(R.string.prompt_dl_other_equipment_login_msg), new Handler() {
                                    @Override
                                    public void handleMessage(Message msg) {
                                        ZZQSApplication.getInstance().clearUser(InputActivity.this);
                                        ZZQSApplication.getInstance().cleanAllActivity();
                                        startActivity(new Intent(InputActivity.this, LoginActivity.class));
                                    }
                                });
                            } else {
                                Toast.makeText(InputActivity.this, object.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    Toast.makeText(this, R.string.prompt_no_modify, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case R.id.driver_date:
                dateTimePickDialogUtil.dateTimePicKDialog(driverDate);
                break;
            default:
                break;
        }
    }
}
