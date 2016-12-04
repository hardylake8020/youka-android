package com.zzqs.app_kc.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.activities.MainActivity;
import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.db.DaoManager;
import com.zzqs.app_kc.db.hibernate.dao.BaseDao;
import com.zzqs.app_kc.entity.LogInfo;
import com.zzqs.app_kc.net.Connectivities;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lance on 15/9/17.
 */
public class HeartbeatService extends Service {
    public static boolean IS_LIVE = false;
    private Timer timer;
    private TimerTask timerTask;
    private FileUploadThread fileUploadThread;
    private BaseDao<LogInfo> logInfoDao;
    public static boolean isDialogOpen = false;
    private boolean isNotification = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void setLogInfo(String type, String content) {
        LogInfo logInfo = new LogInfo();
        logInfo.setType(type);
        logInfo.setContent(content);
        logInfo.setTime(System.currentTimeMillis());
        logInfoDao.insert(logInfo);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (logInfoDao == null) {
            logInfoDao = DaoManager.getLogInfoDao(getApplicationContext());
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MainActivity.class), 0);
        final Notification notification = new Notification.Builder(getApplicationContext()).setContentTitle(getString(R.string.app_name))
                .setContentText("柱柱签收正在后台运转，请勿清除")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .build();
        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        setLogInfo(LogInfo.TYPE_OF_NORMAL, "心跳服务启动");
        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (ZZQSApplication.getInstance().getCurrentContext() == null && DaoManager.isHaveOrderExceptCommit(getApplicationContext())) {
                        if (!isNotification) {
                            isNotification = true;
                            startForeground(0x1988, notification);
                        }
                    } else {
                        if (isNotification) {
                            isNotification = false;
                            stopForeground(true);
                        }
                    }

                    if (!UploadTraceService.isStart) {
                        setLogInfo(LogInfo.TYPE_OF_NORMAL, "检测到定位数据上传服务未启动，启动之");
                        startService(new Intent(getApplicationContext(), UploadTraceService.class));
                    }
                    if (ZZQSApplication.getInstance().getUser() != null) {
                        if (DaoManager.isHaveOrderExceptCommit(getApplicationContext())) {
                            if (!LocationService.isStart) {
                                setLogInfo(LogInfo.TYPE_OF_NORMAL, "检测到定位信息获取服务未启动，启动之");
                                startService(new Intent(getApplicationContext(), LocationService.class));
                            }
                        } else {
                            if (LocationService.isStart) {
                                setLogInfo(LogInfo.TYPE_OF_NORMAL, "检测到当前用户已没有需要定位的运单，关闭定位信息获取服务");
                                stopService(new Intent(getApplicationContext(), LocationService.class));
                            }
                        }
                    } else {
                        if (LocationService.isStart) {
                            stopService(new Intent(getApplicationContext(), LocationService.class));
                        }
                    }

                    if (fileUploadThread == null) {
                        setLogInfo(LogInfo.TYPE_OF_NORMAL, "检测到文件上传线程未创建，创建并启动");
                        fileUploadThread = FileUploadThread.getInstance(getApplicationContext());
                        if (!fileUploadThread.isAlive()) {
                            fileUploadThread.start();
                        }
                    } else {
                        if (!fileUploadThread.isAlive()) {
                            setLogInfo(LogInfo.TYPE_OF_NORMAL, "检测到文件上传线程未启动，启动之");
                            fileUploadThread.start();
                        }
                    }
                    if (!Connectivities.isGpsConnected(getApplicationContext())) {
                        Intent intent = new Intent();
                        intent.setAction("com.zhuzhuqs.android.GPS_UNOPEN");
                        sendBroadcast(intent);
                    }
                    if (!Connectivities.isConnected(getApplicationContext()) && null != ZZQSApplication.getInstance().getCurrentContext()) {
                        Intent intent = new Intent();
                        intent.setAction("com.zhuzhuqs.android.NET_WORK_UNOPEN");
                        sendBroadcast(intent);
                    }
                }
            };
        }
        if (timer == null) {
            timer = new Timer();
            timer.schedule(timerTask, 1000, 10 * 1000);
        }
        IS_LIVE = true;
        return super.onStartCommand(intent, START_STICKY, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        setLogInfo(LogInfo.TYPE_OF_WARN, "心跳服务挂了");
        IS_LIVE = false;
    }
}
