package com.zzqs.app_kc.z_kc.tool;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.zzqs.app_kc.net.Connectivities;
import com.zzqs.app_kc.utils.StringTools;

import java.io.Serializable;

/**
 * Created by ray on 16/7/3.
 */
public class LocationTool implements OnGetGeoCoderResultListener, Serializable {
    public static final int LOCATION_CHANGED = 201;
    private double currentLat = 0.0;
    private double currentLng = 0.0;
    private LatLng currentLatLng = null;
    private double distance;
    private String address;
    private String city;
    private String CityName;

    private BDLocationListenerImpl locationListener;
    private LocationClient locationClient;
    private GeoCoder mSearch = null;//地图搜索模块
    private Context context;
    private Handler handler;


    public LocationTool(Context context) {
        this.context = context;
        initLocationEntity();
    }

    private void initLocationEntity() {
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);


        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationClient = new LocationClient(this.context, option);
    }

    public void enableLocationListener(Handler handler) {
        locationListener = new BDLocationListenerImpl();
        locationClient.registerLocationListener(locationListener);
        this.handler = handler;
    }

    public void onDestroy() {
        locationClient.stop();
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        this.currentLocationChanged(
                reverseGeoCodeResult.getLocation().latitude,
                reverseGeoCodeResult.getLocation().longitude,
                reverseGeoCodeResult.getAddress(),
                reverseGeoCodeResult.getAddressDetail().city);
    }

    public void sendMessageWithParam(int what, Object obj, Handler handler) {
        if (handler == null) {
            return;
        }
        Message msg = handler.obtainMessage();
        msg.what = what;
        msg.obj = obj;
        handler.sendMessage(msg);
    }

    public class BDLocationListenerImpl implements BDLocationListener {
        /**
         * 接收异步返回的定位结果，参数是BDLocation类型参数
         */
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }

            if (!locationClient.isStarted()) {
                return;
            }

            currentLocationChanged(location.getLatitude(), location.getLongitude(), location.getAddrStr(), location.getCity());
        }
    }

    public void currentLocationChanged(double lat, double lng, String address, String city) {
        if (lat <= 0 || lat >= 180 || lng <= 0 || lng >= 180 || lat == 4.9E-324 || lng == 4.9E-324) {
            return;
        }

        this.currentLat = lat;
        this.currentLng = lng;
        currentLatLng = getCurrentLatLngObject();

        if (!StringTools.isEmp(address)) {
            this.address = address;
        }

        if (!StringTools.isEmp(city)) {
            this.city = city;
        }

        if (currentLatLng == null) {
            locationStart();
            return;
        }

        if (address == null && Connectivities.isConnected(this.context)) {
            mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(getCurrentLatLng()));
            return;
        }

        sendMessageWithParam(LOCATION_CHANGED, null, handler);
    }

    private LatLng getCurrentLatLngObject() {
        if (this.currentLng == 0.0 || this.currentLat == 0.0 || this.currentLng == 4.9E-324 || this.currentLat == 4.9E-324) {
            return null;
        }

        return new LatLng(this.currentLat, this.currentLng);
    }

    public void locationStart() {
        if (!locationClient.isStarted()) {
            locationClient.start();
        }
        locationClient.requestLocation();
    }

    public void locationStop() {
        if (locationClient.isStarted()) {
            locationClient.stop();
        }
    }

    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public double getCurrentLng() {
        return currentLng;
    }

    public void setCurrentLng(double currentLng) {
        this.currentLng = currentLng;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityName() {
        return CityName;
    }


    public void setCityName(String cityName) {
        CityName = cityName;
    }

    public LatLng getCurrentLatLng() {
        return currentLatLng;
    }

    public void setCurrentLatLng(LatLng currentLatLng) {
        this.currentLatLng = currentLatLng;
    }
}