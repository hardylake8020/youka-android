package com.zzqs.app_kc.z_kc.network;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.zzqs.app_kc.entity.User;
import com.zzqs.app_kc.z_kc.entitiy.Driver;
import com.zzqs.app_kc.z_kc.entitiy.ErrorInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.RequestBody;
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

    public void searchDrivers(@NonNull String accessToken, @NonNull String keyword, Subscriber<ErrorInfo> subscriber) {
        driverAPI.searchDrivers(accessToken, keyword)
                .map(new Func1<JsonObject, ErrorInfo>() {
                    @Override
                    public ErrorInfo call(JsonObject jsonObject) {
                        ErrorInfo errorInfo = new ErrorInfo();
                        if (jsonObject.has(errorInfo.ERR)) {
                            JsonObject errObj = jsonObject.getAsJsonObject(errorInfo.ERR);
                            errorInfo = gson.fromJson(errObj, ErrorInfo.class);
                        } else {
                            errorInfo.setType(ErrorInfo.SUCCESS);
                            JsonArray jsonArray = jsonObject.getAsJsonArray(Driver.DRIVERS);
                            Type type = new TypeToken<List<Driver>>() {
                            }.getType();
                            List<Driver> list = gson.fromJson(jsonArray, type);
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

    public void addNewDriver(@NonNull String accessToken, @NonNull String truck_number, @NonNull String truckType, @NonNull String driverPhone, String nickName, Subscriber<ErrorInfo> subscriber) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("access_token", accessToken);
            JSONObject driverInfo = new JSONObject();
            driverInfo.put("driver_number", driverPhone);
            driverInfo.put("truck_number", truck_number);
            driverInfo.put("truck_type", truckType);
            if (!TextUtils.isEmpty(nickName)) {
                driverInfo.put("nickname", nickName);
            }
            jsonObject.put("driver_info", driverInfo);
            RequestBody body = okhttp3.RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
            driverAPI.addNewDriver(body)
                    .map(new Func1<JsonObject, ErrorInfo>() {
                        @Override
                        public ErrorInfo call(JsonObject jsonObject) {
                            ErrorInfo errorInfo = new ErrorInfo();
                            if (jsonObject.has(errorInfo.ERR)) {
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateDriverProfile(@NonNull String accessToken, User user, Map<String, File> fileMap, Subscriber<ErrorInfo> subscriber) {
        String photo = "", id_card_photo = "", driving_id_photo = "", truck_photo = "", plate_photo = "", travel_id_photo = "", bank_number_photo = "";
        if (fileMap != null && fileMap.size() > 0) {
            for (Map.Entry<String, File> entry : fileMap.entrySet()) {
                if (entry.getKey().equals("photo")) {
                    photo = entry.getValue().getName();
                    continue;
                }
                if (entry.getKey().equals("idCardPhoto")) {
                    id_card_photo = entry.getValue().getName();
                    continue;
                }
                if (entry.getKey().equals("drivingIdPhoto")) {
                    driving_id_photo = entry.getValue().getName();
                    continue;
                }
                if (entry.getKey().equals("truck_photo")) {
                    truck_photo = entry.getValue().getName();
                    continue;
                }
                if (entry.getKey().equals("plate_photo")) {
                    plate_photo = entry.getValue().getName();
                    continue;
                }
                if (entry.getKey().equals("travelIdPhoto")) {
                    travel_id_photo = entry.getValue().getName();
                    continue;
                }
                if (entry.getKey().equals("bank_number_photo")) {
                    bank_number_photo = entry.getValue().getName();
                    continue;
                }
            }
        }
        driverAPI.updateDriverProfile(accessToken, user.getNickname(),
                user.getId_card_number(), user.getTruck_number(),
                user.getTruck_type(), user.getBank_number(),
                user.getBank_name(), user.getBank_username(),
                photo, id_card_photo,
                driving_id_photo, truck_photo,
                plate_photo, travel_id_photo,
                bank_number_photo)
                .map(new Func1<JsonObject, ErrorInfo>() {
                    @Override
                    public ErrorInfo call(JsonObject jsonObject) {
                        ErrorInfo errorInfo = new ErrorInfo();
                        if (jsonObject.has(errorInfo.ERR)) {
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

    public void addDriversToOwner(@NonNull String accessToken, @NonNull String driver_id, Subscriber<ErrorInfo> subscriber) {
        driverAPI.addDriversToOwner(accessToken, driver_id)
                .map(new Func1<JsonObject, ErrorInfo>() {
                    @Override
                    public ErrorInfo call(JsonObject jsonObject) {
                        ErrorInfo errorInfo = new ErrorInfo();
                        if (jsonObject.has(errorInfo.ERR)) {
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

    public void getDriverProfile(@NonNull String accessToken, Subscriber<ErrorInfo> subscriber) {//暂时无用
        driverAPI.getDriverProfile(accessToken)
                .map(new Func1<JsonObject, ErrorInfo>() {
                    @Override
                    public ErrorInfo call(JsonObject jsonObject) {
                        ErrorInfo errorInfo = new ErrorInfo();
                        if (jsonObject.has(errorInfo.ERR)) {
                            JsonObject errObj = jsonObject.getAsJsonObject(errorInfo.ERR);
                            errorInfo = gson.fromJson(errObj, ErrorInfo.class);
                        } else {
                            errorInfo.setType(ErrorInfo.SUCCESS);
                            Driver driver = gson.fromJson(jsonObject, Driver.class);
                            errorInfo.object = driver;
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
