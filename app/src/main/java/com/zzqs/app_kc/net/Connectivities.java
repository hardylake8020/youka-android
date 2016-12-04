package com.zzqs.app_kc.net;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import java.util.List;

public class Connectivities {

    /**
     * 检查当前WIFI是否连接
     *
     * @param context
     * @return true表示当前网络处于连接状态，且是WIFI，否则返回false
     */
    public static boolean isWifiConnected(Context context) {
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    /**
     * 检查当前GPS是否连接
     *
     * @param context
     * @return true表示当前GSP处于连接状态，否则返回false
     */
    public static boolean isGpsConnected(Context context) {
        LocationManager locationManager =
                ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 检测手机是否有GPS模块
     */

    public static boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    /**
     * 检查当前网络是否连接
     *
     * @param context
     * @return true表示当前网络处于连接状态，否则返回false
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();
        if (network != null) {
            return network.isAvailable();
        }
        return false;
    }
}
