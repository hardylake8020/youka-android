package com.zzqs.app_kc.z_kc.network;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.zzqs.app_kc.z_kc.entitiy.ErrorInfo;
import com.zzqs.app_kc.z_kc.entitiy.OilCard;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

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
                            Log.e("getOilCardListByDriver", jsonObject.toString());
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

    public void addOilCardByDriver(@NonNull String accessToken, @NonNull OilCard oilCard, Subscriber<ErrorInfo> subscriber) {
        JSONObject jsonObject = new JSONObject();
        JSONObject cardInfo = new JSONObject();
        try {
            cardInfo.put("number", oilCard.getNumber());
            cardInfo.put("type", oilCard.getType());
            jsonObject.put("access_token", accessToken);
            jsonObject.put("card_info", cardInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        oilCardApi.addOilCardByDriver(body)
                .map(new Func1<JsonObject, ErrorInfo>() {
                    @Override
                    public ErrorInfo call(JsonObject jsonObject) {
                        ErrorInfo errorInfo = new ErrorInfo();
                        if (jsonObject.has(errorInfo.ERR)) {
                            Log.e("addOilCardByDriver", jsonObject.toString());
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
