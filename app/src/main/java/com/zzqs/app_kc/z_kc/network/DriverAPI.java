package com.zzqs.app_kc.z_kc.network;

import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import retrofit2.http.Body;
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

    /**
     * 搜索司机
     */
    @FormUrlEncoded
    @POST("/tender/driver/searchDrivers")
    Observable<JsonObject> searchDrivers(@Field("access_token") String accessToken, @Field("keyword") String keyword);

    /**
     * 新建司机
     */
    @POST("/tender/driver/addNewDriver")
    Observable<JsonObject> addNewDriver(@Body RequestBody requestBody);

    /**
     * 添加已有司机
     */
    @FormUrlEncoded
    @POST("/tender/driver/addDriversToOwner")
    Observable<JsonObject> addDriversToOwner(@Field("access_token") String accessToken, @Field("driver_id") String driver_id);

    /**
     * 获取司机信息
     */
    @FormUrlEncoded
    @POST("/tender/driver/getDriverProfile")
    Observable<JsonObject> getDriverProfile(@Field("access_token") String accessToken);

    /**
     * 提交司机信息
     */
    @FormUrlEncoded
    @POST("/tender/driver/updateDriverProfile")
    Observable<JsonObject> updateDriverProfile(@Field("access_token") String accessToken, @Field("nickname") String nickname,
                                               @Field("id_card_number") String id_card_number, @Field("truck_number") String truck_number,
                                               @Field("truck_type") String truck_type, @Field("bank_number") String bank_number,
                                               @Field("bank_name") String bank_name,@Field("bank_username") String bank_username,
                                               @Field("photo") String photo, @Field("id_card_photo") String id_card_photo,
                                               @Field("driving_id_photo") String driving_id_photo, @Field("truck_photo") String truck_photo,
                                               @Field("plate_photo") String plate_photo, @Field("travel_id_photo") String travel_id_photo,
                                               @Field("bank_number_photo") String bank_number_photo);
}
