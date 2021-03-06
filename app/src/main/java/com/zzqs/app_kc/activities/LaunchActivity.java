package com.zzqs.app_kc.activities;

import android.Manifest;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;
import com.testin.agent.TestinAgent;
import com.zzqs.app_kc.BuildConfig;
import com.zzqs.app_kc.R;
import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.entity.User;
import com.zzqs.app_kc.net.RestAPI;
import com.zzqs.app_kc.receiver.PushReceiver;
import com.zzqs.app_kc.utils.CommonTools;
import com.zzqs.app_kc.utils.StringTools;
import com.zzqs.app_kc.widgets.SafeProgressDialog;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;


public class LaunchActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    Button login, signup;
    private User user;
    private SoundPool mSoundPool;
    private int soundId;
    private List<View> views;
    View view1, view2, view3;
    ViewPager mViewPager;
    private SafeProgressDialog pd;
    private EditText etUserName, etPassWord;
    private RelativeLayout rlLogin;
    private TextView tvRegister, tvResetPassWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pd = new SafeProgressDialog(this);
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundId = mSoundPool.load(getApplicationContext(), R.raw.transformation, 1);
        if (BuildConfig.VERSION_CODE > CommonTools.getVersionCode(getApplicationContext())) {
            CommonTools.setVersionCode(BuildConfig.VERSION_CODE, getApplicationContext());
        }
        if (CommonTools.isFirstInstallApp(getApplicationContext())) {
            CommonTools.setIsFirstInstallApp(getApplicationContext(), false);
            setContentView(R.layout.act_launch_update);
            mViewPager = (ViewPager) findViewById(R.id.viewPager);
            LayoutInflater inflater = LayoutInflater.from(this);
            views = new ArrayList<View>();
            view1 = inflater.inflate(R.layout.loading, null);
            view2 = inflater.inflate(R.layout.loading, null);
            view3 = inflater.inflate(R.layout.loading, null);
            view3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    views.clear();
                    view1 = null;
                    view2 = null;
                    view3 = null;
                    choiceContentView();
                }
            });
            views.add(view1);
            views.add(view2);
            views.add(view3);
            mViewPager.setAdapter(pager);
            mViewPager.setOnPageChangeListener(this);
        } else {
            choiceContentView();
        }
    }

    private boolean isCanClick;

    private void getPermissions() {
        RxPermissions.getInstance(this)
                .request(Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.RECORD_AUDIO)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            isCanClick = true;
                        } else {
                            isCanClick = false;
                        }
                    }
                });
    }

    private void choiceContentView() {
        user = ZZQSApplication.getInstance().getUser();
        if (null != user) {
            TestinAgent.setUserInfo(user.getUsername());
            setContentView(R.layout.act_launch_welcome);
            mSoundPool.play(soundId, 1, 1, 1, 0, 1f);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), com.zzqs.app_kc.z_kc.activity.MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.out_alpha, R.anim.enter_alpha);
                    finish();
                }
            }, 3000);
        } else {
            setContentView(R.layout.act_launch);
            login = (Button) findViewById(R.id.login);
            signup = (Button) findViewById(R.id.signup);
            login.setOnClickListener(this);
            signup.setOnClickListener(this);
            etUserName = (EditText) findViewById(R.id.etUserName);
            etPassWord = (EditText) findViewById(R.id.etPassWord);
            rlLogin = (RelativeLayout) findViewById(R.id.rlLogin);
            rlLogin.setOnClickListener(new MyOnClickListener() {
                @Override
                public void OnceOnClick(View view) {
                    login();
                }
            });
            tvRegister = (TextView) findViewById(R.id.tvRegister);
            tvRegister.setOnClickListener(new MyOnClickListener() {
                @Override
                public void OnceOnClick(View view) {
                    startActivity(new Intent(getApplicationContext(), SignupActivity.class));
                }
            });
            tvResetPassWord = (TextView) findViewById(R.id.tvResetPassWord);
            tvResetPassWord.setOnClickListener(new MyOnClickListener() {
                @Override
                public void OnceOnClick(View view) {
                    startActivity(new Intent(getApplicationContext(), ForgetPwdActivity.class));
                }
            });
        }
        getPermissions();
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
                    view1.setBackgroundResource(R.drawable.guide_page1);
                case 1:
                    view2.setBackgroundResource(R.drawable.guide_page2);
                    break;
                case 2:
                    view3.setBackgroundResource(R.drawable.guide_page3);
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

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pd.dismiss();
        System.gc();
    }

    @Override
    public void onClick(View view) {
        if (isCanClick) {
            Intent intent = new Intent();
            switch (view.getId()) {
                case R.id.login:
                    intent.setClass(getApplicationContext(), LoginActivity.class);
                    break;
                case R.id.signup:
                    intent.setClass(getApplicationContext(), SignupActivity.class);
                    break;
            }
            startActivity(intent);
        } else {
            getPermissions();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            System.exit(0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void login() {
        String userName = etUserName.getText().toString();
        String passWord = etPassWord.getText().toString();
        if (StringTools.isEmp(userName)) {
            Toast.makeText(this, R.string.prompt_phone_is_null, Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringTools.isEmp(passWord)) {
            Toast.makeText(this, R.string.prompt_pwd_is_null, Toast.LENGTH_SHORT).show();
            return;
        }
        pd.setCancelable(false);
        pd.setMessage(getString(R.string.prompt_dl_logining));
        pd.show();
        RestAPI.getInstance(getApplicationContext()).login(userName, passWord, new RestAPI.RestResponse() {
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
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                    final String cid = PushReceiver.cid;
                    if (!StringTools.isEmp(cid)) {
                        RestAPI.getInstance(LaunchActivity.this).setDeviceId(cid, new RestAPI.RestResponse() {

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
                    Toast.makeText(LaunchActivity.this, R.string.prompt_login_success, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), com.zzqs.app_kc.z_kc.activity.MainActivity.class));
                    finish();
                }
            }


            @Override
            public void onFailure(Object object) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                Toast.makeText(LaunchActivity.this, object.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
