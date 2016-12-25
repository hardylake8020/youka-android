package com.zzqs.app_kc.z_kc.network;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.zzqs.app_kc.z_kc.entitiy.ErrorInfo;
import com.zzqs.app_kc.z_kc.entitiy.OilCard;

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
public class OilCardApiImpl {
    private static OilCardApiImpl oilcardapiImpl;
    private static OilCardApi oilCardApi;
    private static Gson gson;

    public static OilCardApiImpl getOilCardApiImpl() {
        if (oilcardapiImpl == null) {
            oilCardApi = NetWork.getRetrofit().create(OilCardApi.class);
            gson = NetWork.getGson();
            oilcardapiImpl = new OilCardApiImpl();
        }
        return oilcardapiImpl;
    }

    public void getOilCardListByDriver(@NonNull String accessToken, Subscriber<ErrorInfo> subscriber) {
        oilCardApi.getOilCardListByDriver(accessToken)
                .map(new Func1<JsonObject, ErrorInfo>() {
                    @Override
                    public ErrorInfo call(JsonObject jsonObject) {
                        ErrorInfo errorInfo = new ErrorInfo();
                        if (jsonObject.has(errorInfo.ERR)) {
                            JsonObject errObj = jsonObject.getAsJsonObject(errorInfo.ERR);
                            errorInfo = gson.fromJson(errObj, ErrorInfo.class);
                        } else {
                            errorInfo.setType(ErrorInfo.SUCCESS);
                            JsonArray jsonArray = jsonObject.getAsJsonArray(OilCard.CARDS);
                            Type type = new TypeToken<List<OilCard>>() {
                            }.getType();
                            List<OilCard> list=gson.fromJson(jsonArray, type);
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

    public void addOilCardByDriver(@NonNull String accessToken, @NonNull String number, @NonNull String type, Subscriber<ErrorInfo> subscriber) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject cardInfo = new JSONObject();
            cardInfo.put("number", number);
            cardInfo.put("type", type);
            jsonObject.put("access_token", accessToken);
            jsonObject.put("card_info", cardInfo);
            RequestBody body = okhttp3.RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
            oilCardApi.addOilCardByDriver(body)
                    .map(new Func1<JsonObject, ErrorInfo>() {
                        @Override
                        public ErrorInfo call(JsonObject jsonObject) {
                            ErrorInfo errorInfo = new ErrorInfo();
                            if (jsonObject.has(errorInfo.ERR)) {
                                JsonObject errObj = jsonObject.getAsJsonObject(errorInfo.ERR);
                                errorInfo = gson.fromJson(errObj, ErrorInfo.class);
                            } else {
                                errorInfo.setType(ErrorInfo.SUCCESS);
                                OilCard oilCard = gson.fromJson(jsonObject, OilCard.class);
                                errorInfo.object = oilCard;
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
}
