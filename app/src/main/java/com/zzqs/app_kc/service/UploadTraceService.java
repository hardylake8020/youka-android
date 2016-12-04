package com.zzqs.app_kc.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.db.DaoManager;
import com.zzqs.app_kc.db.hibernate.dao.BaseDao;
import com.zzqs.app_kc.entity.DriverTrace;
import com.zzqs.app_kc.entity.LogInfo;
import com.zzqs.app_kc.net.RestAPI;
import com.zzqs.app_kc.utils.CommonFiled;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lance on 15/9/23.
 */
public class UploadTraceService extends Service {
    private BaseDao<DriverTrace> driverTraceDao;
    private BaseDao<LogInfo> logInfoDao;
    private Timer timer;
    private TimerTask timerTask;
    private List<DriverTrace> traceList;
    private Handler uploadTraceHandler;
    public static boolean isStart = false;
    private String username;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        isStart = true;
        if (driverTraceDao == null) {
            driverTraceDao = DaoManager.getDriverTraceDao(getApplicationContext());
        }
        if (logInfoDao == null) {
            logInfoDao = DaoManager.getLogInfoDao(getApplicationContext());
        }
        if (traceList == null) {
            traceList = new ArrayList<DriverTrace>();
        }
        if (ZZQSApplication.getInstance().getUser() != null) {
            username = ZZQSApplication.getInstance().getUser().getUsername();
        }
        if (uploadTraceHandler == null) {
            uploadTraceHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (username == null) {
                        return;
                    }
                    if (msg.obj != null) {
                        ArrayList<JSONObject> traceArray = (ArrayList<JSONObject>) msg.obj;
                        if (traceArray.size() > 0) {
                            RestAPI.getInstance(getApplicationContext()).trace(traceArray, new RestAPI.RestResponse() {
                                @Override
                                public void onSuccess(final Object object) {
                                    new Thread() {
                                        @Override
                                        public void run() {
                                            super.run();
                                            StringBuilder stringBuilder = new StringBuilder();
                                            Integer[] ids = new Integer[traceList.size()];
                                            for (int i = 0;i<traceList.size();i++) {
                                                ids[i] = traceList.get(i).get_id();
                                                stringBuilder.append("?");
                                                if (i < (traceList.size() - 1)) {
                                                    stringBuilder.append(",");
                                                }
                                            }
                                            String sql = "delete from driver_trace where _id in (" + stringBuilder.toString() + ")";
                                            driverTraceDao.execSql(sql,ids);
                                            LogInfo logInfo = new LogInfo();
                                            logInfo.setType(LogInfo.TYPE_OF_NORMAL);
                                            logInfo.setContent("定位信息上传成功" + object.toString());
                                            logInfo.setTime(System.currentTimeMillis());
                                            logInfoDao.insert(logInfo);
                                            traceList.clear();
                                        }
                                    }.start();
                                }

                                @Override
                                public void onFailure(Object object) {
                                    LogInfo logInfo = new LogInfo();
                                    logInfo.setType(LogInfo.TYPE_OF_ERR);
                                    logInfo.setContent("定位信息上传失败" + object.toString());
                                    logInfo.setTime(System.currentTimeMillis());
                                    logInfoDao.insert(logInfo);
                                    traceList.clear();
                                }
                            });
                        }
                    }
                }
            };
        }


        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    Message msg = uploadTraceHandler.obtainMessage();
                    traceList = driverTraceDao.find(null, "username=? and status=?", new String[]{username + "", DriverTrace.STATUS_NEW + ""}, null, null, null, 50 + "");
                    if (traceList.size() > 0) {
                        ArrayList<JSONObject> objectList = new ArrayList<JSONObject>();
                        for (DriverTrace driverTrace : traceList) {
                            JSONObject object = new JSONObject();
                            try {
                                object.put("longitude", driverTrace.getLongitude());
                                object.put("latitude", driverTrace.getLatitude());
                                object.put("time", driverTrace.getTime());
                                object.put("type", driverTrace.getType());
                                objectList.add(object);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                return;
                            }
                        }
                        msg.what = 1;
                        msg.obj = objectList;
                        msg.sendToTarget();
                    }
                }
            };
        }

        if (timer == null) {
            timer = new Timer();
            timer.schedule(timerTask, 0, CommonFiled.LOCATION_REPORT_INTERVAL);
        }

        return super.onStartCommand(intent, START_STICKY, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isStart = false;
    }
}
