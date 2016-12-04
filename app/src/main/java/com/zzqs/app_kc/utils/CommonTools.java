package com.zzqs.app_kc.utils;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zzqs.app_kc.activities.LoginActivity;
import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.entity.User;
import com.zzqs.app_kc.net.RestAPI;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lance on 15/3/21.
 */
public class CommonTools {
    /**
     * 尝试获取手机号码
     *
     * @param context
     * @return phoneNumber
     */
    public static String getPhoneNumber(Context context) {
        //创建电话管理
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //获取手机号码
        String phoneNumber = tm.getLine1Number();
        if (!StringTools.isEmp(phoneNumber)) {
            boolean b = phoneNumber.startsWith("+86");
            if (b) {
                phoneNumber = phoneNumber.substring(3, phoneNumber.length());
            }
        }
        return phoneNumber;
    }

    /**
     * 保存用户信息
     *
     * @param context
     * @param tokenId
     * @param username
     * @param password
     */
    public static void saveUserInfo(Context context, String tokenId, String username, String password) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CommonFiled.DEFAULT, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CommonFiled.TOKENID, tokenId);
        editor.putString(CommonFiled.USERNAME, username);
        editor.putString(CommonFiled.PASSWORD, password);
        editor.commit();
    }

    /**
     * 获取token
     *
     * @param context
     * @return tokenId
     */
    public static String getToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CommonFiled.DEFAULT, context.MODE_PRIVATE);
        String tokenId = sharedPreferences.getString(CommonFiled.TOKENID, null);
        return tokenId;
    }

    /**
     * 后台重新登录
     *
     * @param context
     */
    public static void restLogin(final Context context, final RestAPI.RestResponse restResponse) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CommonFiled.DEFAULT, context.MODE_PRIVATE);
        final String username = sharedPreferences.getString(CommonFiled.USERNAME, null);
        final String password = sharedPreferences.getString(CommonFiled.PASSWORD, null);
        if (!StringTools.isEmp(username) && !StringTools.isEmp(password)) {
            RequestParams params = new RequestParams();
            params.put("username", username);
            params.put("password", password);
            RestAPI.getInstance(context).client.post(RestAPI.BASE_URL + "/driver/signin", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        if (response.has("err")) {
                            JSONObject errJsonObject = response.optJSONObject("err");
                            String type = errJsonObject.optString("type");
                            if (type.equals("internal_system_error")) {
                                restResponse.onFailure(true);
                            } else if (type.equals("invalid_password")) {
                                restResponse.onFailure(true);
                            } else if (type.equals("account_not_exist") || type.equals("invalid_phone")) {
                                ZZQSApplication.getInstance().clearUser(context);//因为服务器未知原因用户丢失，需清除本地所有数据以在用户进行操作时提示其重新注册
                                Intent intent = new Intent(context, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                context.startActivity(intent);
                                restResponse.onFailure(false);
                            }
                        } else if (response.has("driver")) {
                            String tokenId = response.optString("access_token");
                            CommonTools.saveUserInfo(context, tokenId, username, password);
                            JSONObject object = response.getJSONObject("driver");
                            User user = new User();
                            user.setPhone(object.optString("phone"));
                            user.setDevice_id(object.optString("device_id"));
                            user.setPhone(object.optString("photo"));
                            user.setUsername(object.optString("username"));
                            restResponse.onSuccess(user);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ZZQSApplication.getInstance().CrashToLogin();
                    }
                }

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        restResponse.onFailure(RestAPI.errMessage(RestAPI.TIMEOUT));
                    } else
                        restResponse.onFailure(RestAPI.errMessage(statusCode));
                }

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        restResponse.onFailure(RestAPI.errMessage(RestAPI.TIMEOUT));
                    } else
                        restResponse.onFailure(RestAPI.errMessage(statusCode));
                }

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        restResponse.onFailure(RestAPI.errMessage(RestAPI.TIMEOUT));
                    } else
                        restResponse.onFailure(RestAPI.errMessage(statusCode));
                }
            });
        }
    }

    public static Uri SMS_INBOX = Uri.parse("content://sms/");

    public static void getSmsFromPhone(Context context, Handler smsHandler) {
        ContentResolver cr = context.getContentResolver();
        String[] projection = new String[]{"body", "address", "person"};
        String where = " date >  " + (System.currentTimeMillis() - 10 * 60 * 1000);
        Cursor cur = cr.query(SMS_INBOX, projection, where, null, "date desc");
        if (null == cur)
            return;
        if (cur.moveToNext()) {
            String body = cur.getString(cur.getColumnIndex("body"));
            // 这里我是要获取自己短信服务号码中的验证码~~
            Pattern pattern = Pattern.compile("[0-9]{4,6}");
            Matcher matcher = pattern.matcher(body);
            if (matcher.find()) {
                String res = matcher.group().substring(0, matcher.group().length());// 获取短信的内容
                Message msg = smsHandler.obtainMessage();
                msg.obj = res;
                smsHandler.sendMessage(msg);
            }
        }
    }

    /**
     * 储存新消息的状态
     *
     * @param index 状态标识
     */
    public static void setNewMessage(String index, boolean save, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CommonFiled.NEW_MESSAGE, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(index, save);
        editor.commit();
    }

    public static boolean getNewMessage(String index, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CommonFiled.NEW_MESSAGE, context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(index, false);
    }

    /**
     * 储存版本号
     *
     * @param versionCode 版本号
     */
    public static void setVersionCode(int versionCode, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CommonFiled.DEFAULT, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(CommonFiled.VERSION_CODE, versionCode);
        editor.commit();
    }

    /**
     * 获取当前版本号
     */
    public static int getVersionCode(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CommonFiled.DEFAULT, context.MODE_PRIVATE);
        return sharedPreferences.getInt(CommonFiled.VERSION_CODE, 0);
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    /**
     * 是否为第一次安装APP
     */
    public static boolean isFirstInstallApp(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CommonFiled.DEFAULT, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(CommonFiled.IS_FIRST_INSTALL_APP, true);
    }


    /**
     * 第一次安装后设置属性为false
     */
    public static void setIsFirstInstallApp(Context context, boolean isFirst) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CommonFiled.DEFAULT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(CommonFiled.IS_FIRST_INSTALL_APP, isFirst);
        editor.commit();
    }

    public static boolean isTopActivity(Context context) {
        String packageName = "com.zzqs.app";
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            //应用程序位于堆栈的顶层
            if (packageName.equals(tasksInfo.get(0).topActivity.getPackageName())) {
                return true;
            }
        }
        return false;
    }


    //获取ID号
    public static String getPhoneId(Context context) {

        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        return uniqueId;
    }
}
