package com.zzqs.app_kc.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zzqs.app_kc.BuildConfig;
import com.zzqs.app_kc.R;
import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.db.DaoManager;
import com.zzqs.app_kc.db.hibernate.dao.BaseDao;
import com.zzqs.app_kc.entity.EventFile;
import com.zzqs.app_kc.entity.Order;
import com.zzqs.app_kc.entity.OrderEvent;
import com.zzqs.app_kc.net.UpgradeManager;
import com.zzqs.app_kc.service.LocationService;
import com.zzqs.app_kc.utils.StringTools;
import com.zzqs.app_kc.widgets.SafeProgressDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lance on 15/4/10.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "PreferencesActivity";
    //    TextView clear_hc_mb;
    TextView version_code, clear_commit_order;
    private List<EventFile> eventFiles;
    private List<Order> orders;
    private BaseDao<EventFile> eventFileDao;
    private BaseDao<OrderEvent> orderEventDao;
    public static int NO_NEW_VERSION = 200;
    private SafeProgressDialog progressDialog;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == NO_NEW_VERSION) {
                Toast.makeText(SettingActivity.this, R.string.prompt_is_latest_version, Toast.LENGTH_SHORT).show();
            }
        }
    };

    protected BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == UpgradeManager.UPGRADE_STATUS_BROADCAST) {
                int status = intent.getIntExtra(UpgradeManager.UPGRADE_STATUS, UpgradeManager.UPGRADE_FAIL);
                if (status == UpgradeManager.UPGRADE_SUCCESS) {
                    Toast.makeText(SettingActivity.this, R.string.prompt_download_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingActivity.this, R.string.prompt_download_failed, Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_setting);
        LinearLayout update_password = (LinearLayout) findViewById(R.id.update_password);
        update_password.setOnClickListener(this);
        progressDialog = new SafeProgressDialog(this);
        version_code = (TextView) findViewById(R.id.version_code);
        version_code.setText("当前版本：" + BuildConfig.VERSION_NAME);
        eventFileDao = DaoManager.getEventFileDao(getApplicationContext());
        orderEventDao = DaoManager.getOrderEventDao(getApplicationContext());
        clear_commit_order = (TextView) findViewById(R.id.clear_commit_order);
        orders = DaoManager.getOrderDao(getApplicationContext()).find(null, "status=? or status=?", new String[]{Order.STATUS_COMMIT, Order.STATUS_INVALID}, null, null, null, null);
        eventFiles = new ArrayList<EventFile>();
        eventFiles.addAll(eventFileDao.find(null, "status=?", new String[]{EventFile.STATUS_COMMIT + ""}, null, null, null, null));
        long fileSize = 0;
        for (EventFile eventFile : eventFiles) {
            fileSize += getFileSize(eventFile.getFilePath());
        }
        DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String size = decimalFormat.format((float) fileSize / 1024 / 1024);
        clear_commit_order.setText(size + "MB");

        findViewById(R.id.update_version).setOnClickListener(this);
        findViewById(R.id.logout).setOnClickListener(this);
        findViewById(R.id.clear_finish_bill).setOnClickListener(this);
        findViewById(R.id.head_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.head_title)).setText(R.string.view_tv_system_setting);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UpgradeManager.UPGRADE_STATUS_BROADCAST);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mBroadcastReceiver, intentFilter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mBroadcastReceiver);
        progressDialog.dismiss();
    }


    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.head_back:
                finish();
                break;
            case R.id.update_version://检查更新
                if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    Toast.makeText(SettingActivity.this, R.string.prompt_no_sdcard, Toast.LENGTH_LONG).show();
                    return;
                }
                UpgradeManager.getInstance(getApplicationContext())
                        .upgrade(ZZQSApplication.getVersion(), SettingActivity.this, handler);

                break;
            case R.id.update_password:// 修改密码
                startActivity(new Intent(getApplicationContext(), ForgetPwdActivity.class));
                break;
            case R.id.logout:// 注销
                showHints();
                break;
            case R.id.clear_finish_bill://清除已完成的订单
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.prompt_dl_title_1))
                        .setMessage(getString(R.string.prompt_dl_clear_data))
                        .setPositiveButton(getString(R.string.view_bt_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                progressDialog.setMessage("清除中，请稍后。。。");
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                                for (EventFile eventFile : eventFiles) {
                                    if (!StringTools.isEmp(eventFile.getFilePath())) {
                                        File file = new File(eventFile.getFilePath());
                                        if (file.exists()) {
                                            file.delete();
                                        }
                                        eventFileDao.delete(eventFile.get_id());
                                    }
                                }
                                List<OrderEvent> orderEvents = new ArrayList<OrderEvent>();
                                for (Order order : orders) {
                                    orderEvents.addAll(orderEventDao.find(null, "order_id=?", new String[]{order.getOrderId()}, null, null, null, null));
                                }
                                for (OrderEvent orderEvent : orderEvents) {
                                    orderEventDao.delete(orderEvent.get_id());
                                }
                                BaseDao<Order> orderDao = DaoManager.getOrderDao(SettingActivity.this);
                                orderDao.execSql("delete from zz_order where status=?", new String[]{Order.STATUS_COMMIT + ""});
                                progressDialog.dismiss();
                                clear_commit_order.setText("0.00MB");
                                Toast.makeText(SettingActivity.this, R.string.prompt_clear_over, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        }).setCancelable(true).show();
                break;
        }

    }


    private void showHints() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.prompt_dl_title_1))
                .setMessage(getString(R.string.prompt_dl_login_out))
                .setPositiveButton(getString(R.string.view_bt_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        stopService(new Intent(SettingActivity.this, LocationService.class));
                        ZZQSApplication.getInstance().clearUser(SettingActivity.this);
                        ZZQSApplication.getInstance().cleanAllActivity();
                        Intent intent = new Intent();
                        intent.setClass(getApplicationContext(), LaunchActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //注意本行的FLAG设置
                        startActivity(intent);

                    }
                })
                .setNegativeButton(getString(R.string.view_tv_cancel), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                }).setCancelable(true).show();
    }

    private long getFileSize(String path) {
        if (StringTools.isEmp(path)) {
            return 0;
        }
        File file = new File(path);
        if (!file.exists()) {
            return 0;
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            long size = fis.available();
            return size;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
