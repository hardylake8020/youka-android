package com.zzqs.app_kc.z_kc.network;

import com.google.gson.JsonObject;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by ray on 2016/12/20.
 * Class name : TenderApi
 * Description :
 */
public interface TenderApi {
    @FormUrlEncoded
    @POST("/tender/driver/getUnStartedListByDriver")
    Observable<JsonObject> getUnStartedListByDriver(@Field("access_token") String accessToken, @Field("currentCount") int currentCount, @Field("limit") int limit);
}
