package com.zzqs.app_kc.z_kc.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zzqs.app_kc.utils.CommonFiled;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lance on 16/4/12.
 */
public class NetWork {
    private static Retrofit retrofit;
    private static String accessToken;
    private static Gson gson;


    /**
     * 获取retrofit单例对象
     *
     * @author lance
     * @time 16/7/19 下午5:08
     */
    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.addInterceptor(logging);
            builder.connectTimeout(30, TimeUnit.SECONDS);
            retrofit = new Retrofit.Builder()
                    .baseUrl(getBaseUrl())
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static void removeAccessToken() {
        accessToken = null;
    }

    public static String getAccessToken(Context context) {
        if (accessToken == null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(CommonFiled.DEFAULT, context.MODE_PRIVATE);
            accessToken = sharedPreferences.getString(CommonFiled.TOKENID, null);
        }
        return accessToken;
    }

    /**
     * 获取gson单例对象
     *
     * @author lance
     * @time 16/7/19 下午5:08
     */
    public static Gson getGson() {
        if (gson == null) {
            GsonBuilder gb = new GsonBuilder();
            gb.serializeNulls();
            gson = gb.create();
        }
        return gson;
    }

    /**
     * 获取服务器地址
     *
     * @author lance
     * @time 16/7/19 下午5:09
     */
    public static String getBaseUrl() {
        return "http://183.131.76.178:3006";
//        return BuildConfig.BASE_URL;
    }
}
