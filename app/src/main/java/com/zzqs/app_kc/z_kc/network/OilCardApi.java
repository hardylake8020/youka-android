package com.zzqs.app_kc.z_kc.network;

import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by ray on 2016/12/21.
 * Class name : OilCardApi
 * Description :油卡接口
 */
public interface OilCardApi {
  /**
   * 获取油卡列表
   */
  @FormUrlEncoded
  @POST("/tender/driver/card/getListByDriver")
  Observable<JsonObject> getOilCardListByDriver(@Field("access_token") String accessToken);

  /**
   * 添加油卡
   */
  @POST("/tender/driver/card/create")
  Observable<JsonObject> addOilCardByDriver(@Body RequestBody requestBody);


  @FormUrlEncoded
  @POST("/tender/driver/card/getById")
  Observable<JsonObject> getCardById(@Field("access_token") String accessToken, @Field("card_id") String cardId);
}
