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
    /**
     * 获取未开始的标书
     */
    @FormUrlEncoded
    @POST("/tender/driver/getUnStartedListByDriver")
    Observable<JsonObject> getUnStartedListByDriver(@Field("access_token") String accessToken, @Field("currentCount") int currentCount, @Field("limit") int limit,
                                                    @Field("pickup_address") String pickup_address, @Field("delivery_address") String delivery_address,
                                                    @Field("tender_type") String tender_type);

    /**
     * 获取已开始的标书
     */
    @FormUrlEncoded
    @POST("/tender/driver/getStartedListByDriver")
    Observable<JsonObject> getStartedListByDriver(@Field("access_token") String accessToken, @Field("currentCount") int currentCount, @Field("limit") int limit, @Field("status") String status);

    /**
     * 抢标书
     */
    @FormUrlEncoded
    @POST("/tender/driver/grab")
    Observable<JsonObject> grabTender(@Field("access_token") String accessToken, @Field("tender_id") String tenderId);

    @FormUrlEncoded
    @POST("/tender/driver/transportevent")
    Observable<JsonObject> getTransportEvents(@Field("access_token") String accessToken, @Field("tender_id") String tenderId);
}
