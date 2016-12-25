package com.zzqs.app_kc.z_kc.network;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zzqs.app_kc.z_kc.entitiy.ErrorInfo;

import java.util.concurrent.TimeUnit;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by ray on 2016/12/25.
 * Class name : DriverApiImpl
 * Description :
 */
public class DriverApiImpl {
    private static DriverApiImpl driverApiImpl;
    private static DriverAPI driverAPI;
    private static Gson gson;

    public static DriverApiImpl getDriverApiImpl() {
        if (driverApiImpl == null) {
            driverAPI = NetWork.getRetrofit().create(DriverAPI.class);
            gson = NetWork.getGson();
            driverApiImpl = new DriverApiImpl();
        }
        return driverApiImpl;
    }

    public void assginDriver(@NonNull String accessToken, @NonNull String tenderId, @NonNull String truckId, @NonNull String cardId, Subscriber<ErrorInfo> subscriber) {
        driverAPI.assginDriver(accessToken, tenderId, truckId, cardId)
                .map(new Func1<JsonObject, ErrorInfo>() {
                    @Override
                    public ErrorInfo call(JsonObject jsonObject) {
                        ErrorInfo errorInfo = new ErrorInfo();
                        if (jsonObject.has(errorInfo.ERR)) {
                            Log.e("assginDriver", jsonObject.toString());
                            JsonObject errObj = jsonObject.getAsJsonObject(errorInfo.ERR);
                            errorInfo = gson.fromJson(errObj, ErrorInfo.class);
                        } else {
                            errorInfo.setType(ErrorInfo.SUCCESS);
                        }
                        return errorInfo;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(subscriber);
    }
}
