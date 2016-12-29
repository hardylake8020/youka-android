package com.zzqs.app_kc.z_kc.network;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.zzqs.app_kc.z_kc.entitiy.Car;
import com.zzqs.app_kc.z_kc.entitiy.ErrorInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by ray on 2016/12/21.
 * Class name : OilCardApiImpl
 * Description :
 */
public class CarApiImpl {
  private static CarApiImpl carApiImpl;
  private static CarApi carApi;
  private static Gson gson;

  public static CarApiImpl getCarApiImpl() {
    if (carApiImpl == null) {
      carApi = NetWork.getRetrofit().create(CarApi.class);
      gson = NetWork.getGson();
      carApiImpl = new CarApiImpl();
    }
    return carApiImpl;
  }

  public void createCar(@NonNull String accessToken, @NonNull String driverPhone, @NonNull String carNumber, @NonNull String truckType, Subscriber<ErrorInfo> subscriber) {
    try {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("access_token", accessToken);
      JSONObject truckInfo = new JSONObject();
      truckInfo.put("driver_number", driverPhone);
      truckInfo.put("truck_number", carNumber);
      truckInfo.put("truck_type", truckType);
      jsonObject.put("truck_info", truckInfo);

      RequestBody body = okhttp3.RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
      carApi.createCar(body)
          .map(new Func1<JsonObject, ErrorInfo>() {
            @Override
            public ErrorInfo call(JsonObject jsonObject) {
              ErrorInfo errorInfo = new ErrorInfo();
              if (jsonObject.has(errorInfo.ERR)) {
                Log.e("createCar", jsonObject.toString());
                JsonObject errObj = jsonObject.getAsJsonObject(errorInfo.ERR);
                errorInfo = gson.fromJson(errObj, ErrorInfo.class);
              } else {
                errorInfo.setType(ErrorInfo.SUCCESS);
                Car car = gson.fromJson(jsonObject, Car.class);
                errorInfo.object = car;
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

  public void getListByDriver(@NonNull String accessToken, Subscriber<ErrorInfo> subscriber) {
    carApi.getListByDriver(accessToken)
        .map(new Func1<JsonObject, ErrorInfo>() {
          @Override
          public ErrorInfo call(JsonObject jsonObject) {
            ErrorInfo errorInfo = new ErrorInfo();
            if (jsonObject.has(errorInfo.ERR)) {
              Log.e("getListByDriver", jsonObject.toString());
              JsonObject errObj = jsonObject.getAsJsonObject(errorInfo.ERR);
              errorInfo = gson.fromJson(errObj, ErrorInfo.class);
            } else {
              errorInfo.setType(ErrorInfo.SUCCESS);
              JsonArray jsonArray = jsonObject.getAsJsonArray(Car.TRUCKS);
              Type type = new TypeToken<List<Car>>() {
              }.getType();
              List<Car> list = gson.fromJson(jsonArray, type);
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
}
