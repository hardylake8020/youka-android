package com.zzqs.app_kc.z_kc.network;

import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by lance on 2016/12/24.
 */

public interface TruckApi {
  @POST("/tender/driver/truck/create")
  Observable<JsonObject> createCar(@Body RequestBody requestBody);

  @FormUrlEncoded
  @POST("/tender/driver/truck/getListByDriver")
  Observable<JsonObject> getListByDriver(@Field("access_token") String accessToken);

  @FormUrlEncoded
  @POST("/tender/driver/truck/getById")
  Observable<JsonObject> getTruckById(@Field("access_token") String accessToken,@Field("truck_id") String truckId);
}
