package com.zzqs.app_kc.z_kc.network;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.zzqs.app_kc.z_kc.entitiy.ErrorInfo;
import com.zzqs.app_kc.z_kc.entitiy.Tender;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by ray on 2016/12/20.
 * Class name : TenderApiImpl
 * Description :
 */
public class TenderApiImpl {
    private static TenderApiImpl tenderApiImpl;
    private static TenderApi tenderApi;
    private static Gson gson;

    public static TenderApiImpl getUserApiImpl() {
        if (tenderApiImpl == null) {
            tenderApi = NetWork.getRetrofit().create(TenderApi.class);
            gson = NetWork.getGson();
            tenderApiImpl = new TenderApiImpl();
        }
        return tenderApiImpl;
    }

    public void getUnStartedListByDriver(@NonNull String accessToken, @NonNull int currentCount, @NonNull int limit, Subscriber<ErrorInfo> subscriber) {
        tenderApi.getUnStartedListByDriver(accessToken, currentCount, limit)
                .map(new Func1<JsonObject, ErrorInfo>() {
                    @Override
                    public ErrorInfo call(JsonObject jsonObject) {
                        ErrorInfo errorInfo = new ErrorInfo();
                        if (jsonObject.has(errorInfo.ERR)) {
                            Log.e("getUnStartedListByDriver", jsonObject.toString());
                            JsonObject errObj = jsonObject.getAsJsonObject(errorInfo.ERR);
                            errorInfo = gson.fromJson(errObj, ErrorInfo.class);
                        } else {
                            errorInfo.setType(ErrorInfo.SUCCESS);
                            JsonArray jsonArray = jsonObject.getAsJsonArray(Tender.TENDERS);
                            for (int i = 0; i < jsonArray.size(); i++) {
                                JsonObject obj = (JsonObject) jsonArray.get(i);
                                Log.e("obj", obj.toString());
                            }
                            Type type = new TypeToken<List<Tender>>() {
                            }.getType();
                            List<Tender> list = gson.fromJson(jsonArray, type);
                            for (Tender tender : list) {
                                Log.e("tender", tender.toString());
                            }
                            errorInfo.object = list;
                        }
                        return errorInfo;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(subscriber);
    }

    public void getStartedListByDriver(@NonNull String accessToken, @NonNull int currentCount, @NonNull int limit, @NonNull String status, Subscriber<ErrorInfo> subscriber) {
        tenderApi.getStartedListByDriver(accessToken, currentCount, limit, status)
                .map(new Func1<JsonObject, ErrorInfo>() {
                    @Override
                    public ErrorInfo call(JsonObject jsonObject) {
                        ErrorInfo errorInfo = new ErrorInfo();
                        if (jsonObject.has(errorInfo.ERR)) {
                            Log.e("getStartedListByDriver", jsonObject.toString());
                            JsonObject errObj = jsonObject.getAsJsonObject(errorInfo.ERR);
                            errorInfo = gson.fromJson(errObj, ErrorInfo.class);
                        } else {
                            errorInfo.setType(ErrorInfo.SUCCESS);
                            JsonArray jsonArray = jsonObject.getAsJsonArray(Tender.TENDERS);
                            for (int i = 0; i < jsonArray.size(); i++) {
                                JsonObject obj = (JsonObject) jsonArray.get(i);
                                Log.e("obj", obj.toString());
                            }
                            Type type = new TypeToken<List<Tender>>() {
                            }.getType();
                            List<Tender> list = gson.fromJson(jsonArray, type);
                            for (Tender tender : list) {
                                Log.e("tender", tender.toString());
                            }
                            errorInfo.object = list;
                        }
                        return errorInfo;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(subscriber);
    }

    public void grabTender(@NonNull String accessToken, @NonNull String tenderId, Subscriber<ErrorInfo> subscriber) {
        tenderApi.grabTender(accessToken, tenderId)
                .map(new Func1<JsonObject, ErrorInfo>() {
                    @Override
                    public ErrorInfo call(JsonObject jsonObject) {
                        ErrorInfo errorInfo = new ErrorInfo();
                        if (jsonObject.has(errorInfo.ERR)) {
                            Log.e("grabTender", jsonObject.toString());
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
