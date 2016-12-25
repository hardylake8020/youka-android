package com.zzqs.app_kc.z_kc.network;

import com.google.gson.JsonObject;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by ray on 2016/12/25.
 * Class name : DriverAPI
 * Description :
 */
public interface DriverAPI {
    /**
     * 分配司机
     */
    @FormUrlEncoded
    @POST("/tender/driver/assginDriver")
    Observable<JsonObject> assginDriver(@Field("access_token") String accessToken, @Field("tender_id") String tenderId, @Field("truck_id") String truckId, @Field("card_id") String cardId);

}
