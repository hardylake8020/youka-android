package com.zzqs.app_kc.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.db.DaoManager;
import com.zzqs.app_kc.db.hibernate.dao.BaseDao;
import com.zzqs.app_kc.entity.DriverTrace;
import com.zzqs.app_kc.entity.LogInfo;
import com.zzqs.app_kc.entity.User;
import com.zzqs.app_kc.utils.CommonFiled;
import com.zzqs.app_kc.utils.StringTools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lance on 15/9/22.
 */
public class LocationService extends Service implements BDLocationListener {
    private BaseDao<DriverTrace> driverTraceDao;
    private BaseDao<LogInfo> logInfoDao;
    private LocationClient mLocationClient;
    public static DriverTrace lastTrace;
    public static boolean isStart = false;
    private User user;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onReceiveLocation(BDLocation location) {
        if (null == location) {
            return;
        }
        int type = location.getLocType();
        if ((type == BDLocation.TypeGpsLocation || type == BDLocation.TypeNetWorkLocation)) {
            LogInfo logInfo = new LogInfo();
            logInfo.setType(LogInfo.TYPE_OF_NORMAL);
            logInfo.setContent("定位信息已获取：" + type + "---" + location.getTime() + "\n" + location.getLatitude() + ":" + location.getLongitude());
            logInfo.setTime(System.currentTimeMillis());
            logInfoDao.insert(logInfo);
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                long lastTime = 0;
                if (lastTrace.getTime() != null) {
                    lastTime = sdf.parse(lastTrace.getTime()).getTime();
                }
                long nowTime = sdf.parse(location.getTime()).getTime();
                if (nowTime < lastTime) {
                    return;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (lastTrace.getLatitude() != location.getLatitude() || lastTrace.getLongitude() != location.getLongitude()) {
                String traceType = null;
                switch (type) {
                    case BDLocation.TypeGpsLocation:
                        traceType = DriverTrace.GPS;
                        break;
                    case BDLocation.TypeNetWorkLocation:
                        traceType = DriverTrace.BASE_STATION;
                        break;
                    default:
                        break;
                }
                lastTrace.setLatitude(location.getLatitude());
                lastTrace.setLongitude(location.getLongitude());
                if (location.hasAddr() && !StringTools.isEmp(location.getAddrStr())) {
                    lastTrace.setAddress(location.getAddrStr());
                }
                if (!StringTools.isEmp(location.getTime())) {
                    lastTrace.setTime(location.getTime());
                } else {
                    lastTrace.setTime(sdf.format(new Date()));
                }
                lastTrace.setType(traceType);
                lastTrace.setUsername(user.getUsername());
                lastTrace.setStatus(DriverTrace.STATUS_NEW);
                driverTraceDao.insert(lastTrace);
                LogInfo log = new LogInfo();
                log.setType(LogInfo.TYPE_OF_NORMAL);
                log.setContent("定位信息已保存：" + type + "---" + location.getTime() + "\n" + location.getLatitude() + ":" + location.getLongitude());
                log.setTime(System.currentTimeMillis());
                logInfoDao.insert(log);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isStart = true;
        logInfoDao = DaoManager.getLogInfoDao(getApplicationContext());
        driverTraceDao = DaoManager.getDriverTraceDao(getApplicationContext());
        lastTrace = new DriverTrace();
        user = ZZQSApplication.getInstance().getUser();
        getBaiduLocation();
        return super.onStartCommand(intent, START_STICKY, startId);
    }

    private void getBaiduLocation() {
        mLocationClient = new LocationClient(getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置高精度定位模式
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(CommonFiled.LOCATION_REQUEST_INTERVAL);//设置发起定位请求的间隔时间
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
        option.setNeedDeviceDirect(false);//返回的定位结果包含手机机头的方向
        option.setOpenGps(true);
        option.setProdName("zhuzhuqs");
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(this); //注册监听函数
        mLocationClient.start();
        LogInfo logInfo = new LogInfo();
        logInfo.setType(LogInfo.TYPE_OF_NORMAL);
        logInfo.setContent("定位服务中的百度定位监听启动");
        logInfo.setTime(System.currentTimeMillis());
        logInfoDao.insert(logInfo);
    }

    @Override
    public boolean stopService(Intent name) {
        isStart = false;
        mLocationClient.unRegisterLocationListener(this);
        mLocationClient.stop();
        mLocationClient = null;
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
