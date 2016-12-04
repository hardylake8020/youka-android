package com.zzqs.app_kc.net;

import android.content.Context;
import android.text.TextUtils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.testin.agent.TestinAgent;
import com.zzqs.app_kc.BuildConfig;
import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.db.DaoManager;
import com.zzqs.app_kc.db.hibernate.dao.BaseDao;
import com.zzqs.app_kc.entity.Company;
import com.zzqs.app_kc.entity.Evaluation;
import com.zzqs.app_kc.entity.EventFile;
import com.zzqs.app_kc.entity.LogInfo;
import com.zzqs.app_kc.entity.Order;
import com.zzqs.app_kc.entity.OrderEvent;
import com.zzqs.app_kc.entity.Upgrade;
import com.zzqs.app_kc.entity.User;
import com.zzqs.app_kc.utils.CommonTools;
import com.zzqs.app_kc.utils.StringTools;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lance on 15/3/20.
 */
public class RestAPI {
    public static String BASE_URL = BuildConfig.BASE_URL;
    private Context paramContext;
    private static RestAPI restAPI;
    public AsyncHttpClient client;

    public interface RestResponse {
        public void onSuccess(Object object);

        public void onFailure(Object object);
    }

    public static RestAPI getInstance(Context paramContext) {
        if (restAPI == null) {
            restAPI = new RestAPI(paramContext);
        }
        return restAPI;
    }

    private RestAPI(Context paramContext) {
        this.paramContext = paramContext;
        client = new AsyncHttpClient();
        client.setTimeout(15 * 1000);
        client.setMaxConnections(1000);
        PersistentCookieStore myCookieStore = new PersistentCookieStore(paramContext);
        client.setCookieStore(myCookieStore);
    }

    public AsyncHttpClient getClient() {
        if (client == null) {
            client = new AsyncHttpClient();
            client.setTimeout(30 * 1000);
            PersistentCookieStore myCookieStore = new PersistentCookieStore(paramContext);
            client.setCookieStore(myCookieStore);
        }
        return client;
    }

    private void commonErr(JSONObject json, RestResponse restResponse) {
        JSONObject errJsonObject = json.optJSONObject("err");
        String type = errJsonObject.optString("type");
        if (type.equals("invalid_password")) {
            restResponse.onFailure("密码错误，请重新输入");
        } else if (type.equals("internal_system_error")) {
            restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
        } else if (type.equals("sms_send_error")) {
            restResponse.onFailure("验证短信发送失败，请与客服人员联系");
        } else if (type.equals("invalid_phone")) {
            restResponse.onFailure("错误的手机号码，请检查手机号码是否输入正确");
        } else if (type.equals("account_not_exist")) {
            restResponse.onFailure("没有这个用户，请检查手机号码是否输入正确");
        } else if (type.equals("account_exist")) {
            restResponse.onFailure("此用户已存在！请使用其他手机号码注册");
        } else if (type.equals("invalid_verify_code") || type.equals("invalid_verify_id")) {
            restResponse.onFailure("验证码错误，请检查是否输入正确");
        } else if (type.equals("account_disconnected")) {
            restResponse.onFailure(errMessage(DISCONNECTED));
        }
    }

    /**
     * 发送注册验证码信息
     *
     * @param username 手机号码
     */
    public RequestHandle verify(String username, final RestResponse restResponse) {
        RequestParams params = new RequestParams();
        params.put("username", username);
        return client.post(BASE_URL + "/driver/getsmsverifycode", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject json) {
                if (json.has("err")) {
                    commonErr(json, restResponse);
                } else {
                    String inviteUserId = json.optString("_id");
                    if (!StringTools.isEmp(inviteUserId)) {
                        restResponse.onSuccess(inviteUserId);
                    } else {
                        restResponse.onFailure("error");
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONArray errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }
        });
    }

    /**
     * 发送重置密码验证码信息
     *
     * @param username 手机号码
     */
    public RequestHandle verifyOfRetrieve(String username, final RestResponse restResponse) {
        RequestParams params = new RequestParams();
        params.put("username", username);
        return client.post(BASE_URL + "/driver/passwordcode", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject json) {
                if (json.has("err")) {
                    commonErr(json, restResponse);
                } else {
                    String inviteUserId = json.optString("_id");
                    if (!StringTools.isEmp(inviteUserId)) {
                        restResponse.onSuccess(inviteUserId);
                    } else
                        restResponse.onFailure("error");
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONArray errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }
        });
    }

    /**
     * 注册用户
     *
     * @param mobilePhone 手机号码
     * @param password    密码
     * @param smsCode     验证码
     * @param smsId       获取验证码时服务器返回的短信id
     */
    public RequestHandle signup(String mobilePhone, String password, String smsCode, String smsId, final RestResponse restResponse) {
        RequestParams params = new RequestParams();
        params.put("username", mobilePhone);
        params.put("password", password);
        params.put("sms_verify_code", smsCode);
        params.put("sms_verify_id", smsId);
        return client.post(BASE_URL + "/driver/signup", params, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                if (response.has("err")) {
                    commonErr(response, restResponse);
                } else {
                    restResponse.onSuccess(true);
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONArray errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }
        });
    }

    /**
     * 重置密码
     *
     * @param password 密码
     * @param smsCode  验证码
     * @param smsId    获取验证码时服务器返回的短信id
     */

    public RequestHandle retrievePwd(final String mobilePhone, final String password, String smsCode, String smsId, final RestResponse restResponse) {
        RequestParams params = new RequestParams();
        params.put("username", mobilePhone);
        params.put("password", password);
        params.put("sms_verify_code", smsCode);
        params.put("sms_verify_id", smsId);
        return client.post(BASE_URL + "/driver/password/update", params, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                try {
                    if (response.has("err")) {
                        commonErr(response, restResponse);
                    } else if (response.has("driver")) {
                        String tokenId = response.optString("access_token");
                        CommonTools.saveUserInfo(paramContext, tokenId, mobilePhone, password);
                        JSONObject object = response.getJSONObject("driver");
                        User user = new User();
                        if (!StringTools.isEmp(object.optString("phone"))) {
                            user.setPhone(object.optString("phone"));
                        } else {
                            user.setPhone(mobilePhone);
                        }
                        user.setDevice_id(object.optString("device_id"));
                        user.setPhoto(object.optString("photo"));
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
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONArray errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }
        });
    }

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     */
    public RequestHandle login(final String username, final String password, final RestResponse restResponse) {
        final RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("password", password);
        params.put("phone_id", CommonTools.getPhoneId(paramContext));
        return client.post(BASE_URL + "/driver/signin", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                try {
                    if (response.has("err")) {
                        commonErr(response, restResponse);
                    } else if (response.has("driver")) {
//                        Throwable throwable = new Throwable("user versionCode");
//                        TestinAgent.uploadException(paramContext, "username:" + username + ";versionCode:" + BuildConfig.VERSION_CODE, throwable);
                        String tokenId = response.optString("access_token");
                        CommonTools.saveUserInfo(paramContext, tokenId, username, password);
                        JSONObject object = response.getJSONObject("driver");
                        User user = new User();
                        if (!StringTools.isEmp(object.optString("phone"))) {
                            user.setPhone(object.optString("phone"));
                        } else {
                            user.setPhone(username);
                        }
                        user.setNickname(object.optString("nickname"));
                        user.setDevice_id(object.optString("device_id"));
                        user.setUsername(object.optString("username"));
                        String drivingDate = object.optString("driving_date");
                        if (!StringTools.isEmp(drivingDate)) {
                            if (drivingDate.length() > 10) {
                                user.setDrivingDate(drivingDate.substring(0, 10));
                            } else {
                                user.setDrivingDate(drivingDate);
                            }
                        }
                        user.setPhoto(object.optString("photo"));
                        user.setIdCardPhoto(object.optString("id_card_photo"));
                        user.setDrivingIdPhoto(object.optString("driving_id_photo"));
                        user.setTravelIdPhoto(object.optString("travel_id_photo"));
                        user.setTradingIdPhoto(object.optString("operating_permits_photo"));
                        if (object.has("plate_photos")) {
                            JSONArray jsonArray = object.optJSONArray("plate_photos");
                            JSONArray jsonArray2 = object.optJSONArray("plate_numbers");
                            StringBuilder stringBuilder1 = new StringBuilder();
                            StringBuilder stringBuilder2 = new StringBuilder();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                String s = jsonArray.optString(i);
                                String n = jsonArray2.optString(i);
                                if (s != null) {
                                    stringBuilder1.append(s.toString() + ";");
                                    stringBuilder2.append(n.toString() + ";");
                                }
                            }
                            if (stringBuilder1.length() > 0) {
                                stringBuilder1.deleteCharAt(stringBuilder1.length() - 1);
                                stringBuilder2.deleteCharAt(stringBuilder2.length() - 1);
                            }
                            user.setPlatePhotos(stringBuilder1.toString());
                            user.setPlateNumbers(stringBuilder2.toString());
                        }
                        ZZQSApplication.getInstance().registerUser(user);

                        TestinAgent.setUserInfo(user.getUsername());

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
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONArray errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }
        });
    }


    /**
     * 获取邀请公司
     */
    public RequestHandle getCompanies(final RestResponse restResponse) {
        RequestParams params = new RequestParams();
        params.put("access_token", CommonTools.getToken(paramContext));
        params.put("phone_id", CommonTools.getPhoneId(paramContext));
        return client.get(BASE_URL + "/driver/partner", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                if (response.has("err")) {
                    commonErr(response, restResponse);
                    JSONObject errJsonObject = response.optJSONObject("err");
                    String type = errJsonObject.optString("type");
                    if (type.equals("internal_system_error")) {
                        restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                    } else if (type.equals("undefined_access_token") || type.equals("invalid_access_token") || type.equals("account_not_exist")) {
                        CommonTools.restLogin(paramContext, new RestResponse() {
                            @Override
                            public void onSuccess(Object object) {
                                getCompanies(restResponse);
                            }

                            @Override
                            public void onFailure(Object object) {
                                restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                            }
                        });
                    } else if (type.equals("account_disconnected")) {
                        restResponse.onFailure(errMessage(DISCONNECTED));
                    }
                } else {
                    restResponse.onSuccess(true);
                }
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray response) {
                List<Company> list = new ArrayList<Company>();
                for (int i = 0; i < response.length(); i++) {
                    JSONObject jsonObject = response.optJSONObject(i);
                    String status = jsonObject.optString("status");
                    JSONObject companyObject = jsonObject.optJSONObject("company");
                    Company company = new Company();
                    company.setUsername(companyObject.optString("username"));
                    company.setCompany_name(companyObject.optString("name"));
                    company.setCompany_id(companyObject.optString("_id"));
                    company.setAddress(companyObject.optString("address"));
                    company.setType(companyObject.optString("type"));
                    if (status.equals("inviting")) {
                        company.setStatus(Company.UN_ACCEPT);
                        list.add(company);
                    } else if (status.equals("accepted")) {
                        company.setStatus(Company.ACCEPT);
                        list.add(company);
                    }
                }
                restResponse.onSuccess(list);
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONArray errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }
        });
    }

    /**
     * 接受邀请
     */
    public RequestHandle accept(final String companyId, final RestResponse restResponse) {
        RequestParams params = new RequestParams();
        params.put("access_token", CommonTools.getToken(paramContext));
        params.put("company_id", companyId);
        params.put("phone_id", CommonTools.getPhoneId(paramContext));
        return client.post(BASE_URL + "/driver/partner/accept", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                if (response.has("err")) {
                    JSONObject errJsonObject = response.optJSONObject("err");
                    String type = errJsonObject.optString("type");
                    if (type.equals("internal_system_error")) {
                        restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                    } else if (type.equals("undefined_access_token") || type.equals("invalid_access_token") || type.equals("account_not_exist")) {
                        CommonTools.restLogin(paramContext, new RestResponse() {
                            @Override
                            public void onSuccess(Object object) {
                                accept(companyId, restResponse);
                            }

                            @Override
                            public void onFailure(Object object) {
                                restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                            }
                        });
                    } else if (type.equals("uninvited_partner")) {
                        restResponse.onFailure("此公司已经取消了对您的邀请");
                    } else if (type.equals("driver_has_accepted_partner")) {
                        restResponse.onSuccess(true);
                    } else if (type.equals("account_disconnected")) {
                        restResponse.onFailure(errMessage(DISCONNECTED));
                    }
                } else {
                    restResponse.onSuccess(true);
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONArray errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }
        });
    }

    /**
     * 拒绝邀请
     */
    public RequestHandle refused(final String companyId, final RestResponse restResponse) {
        RequestParams params = new RequestParams();
        params.put("access_token", CommonTools.getToken(paramContext));
        params.put("company_id", companyId);
        params.put("phone_id", CommonTools.getPhoneId(paramContext));
        return client.post(BASE_URL + "/driver/partner/confuse", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                if (response.has("err")) {
                    JSONObject errJsonObject = response.optJSONObject("err");
                    String type = errJsonObject.optString("type");
                    if (type.equals("internal_system_error")) {
                        restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                    } else if (type.equals("undefined_access_token") || type.equals("invalid_access_token") || type.equals("account_not_exist")) {
                        CommonTools.restLogin(paramContext, new RestResponse() {
                            @Override
                            public void onSuccess(Object object) {
                                refused(companyId, restResponse);
                            }

                            @Override
                            public void onFailure(Object object) {
                                restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                            }
                        });
                    } else if (type.equals("uninvited_partner")) {
                        restResponse.onSuccess(true);
                    } else if (type.equals("driver_has_accepted_partner")) {
                        restResponse.onFailure("您之前已经成为此公司的合作司机，如有疑问请与客服人员联系");
                    } else if (type.equals("account_disconnected")) {
                        restResponse.onFailure(errMessage(DISCONNECTED));
                    }
                } else {
                    restResponse.onSuccess(true);
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONArray errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }
        });
    }

    /**
     * 获取指定运单
     */
    public RequestHandle getOrder(final String orderId, final RestResponse restResponse) {
        RequestParams params = new RequestParams();
        params.put("access_token", CommonTools.getToken(paramContext));
        params.put("order_id", orderId);
        params.put("phone_id", CommonTools.getPhoneId(paramContext));
        return client.get(BASE_URL + "/driver/order/getbyid", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                        if (response.has("err")) {
                            JSONObject errJsonObject = response.optJSONObject("err");
                            String type = errJsonObject.optString("type");
                            if (type.equals("internal_system_error")) {
                                restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                            } else if (type.equals("undefined_access_token") || type.equals("invalid_access_token") || type.equals("account_not_exist")) {
                                CommonTools.restLogin(paramContext, new RestResponse() {
                                    @Override
                                    public void onSuccess(Object object) {
                                        getOrder(orderId, restResponse);
                                    }

                                    @Override
                                    public void onFailure(Object object) {
                                        restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                                    }
                                });
                            } else if (type.equals("order_not_exist") || type.equals("order_not_visible")) {
                                restResponse.onFailure(Order.ORDER_NOT_EXIST);
                            } else if (type.equals("account_disconnected")) {
                                restResponse.onFailure(errMessage(DISCONNECTED));
                            }
                        } else {
                            if (null != response) {
                                Order order = orderParse(paramContext, response);
                                if (null != order) {
                                    restResponse.onSuccess(order);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                        if (throwable.getCause() instanceof ConnectTimeoutException) {
                            restResponse.onFailure(errMessage(TIMEOUT));
                        } else
                            restResponse.onFailure(errMessage(statusCode));
                    }

                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        if (throwable.getCause() instanceof ConnectTimeoutException) {
                            restResponse.onFailure(errMessage(TIMEOUT));
                        } else
                            restResponse.onFailure(errMessage(statusCode));
                    }

                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        if (throwable.getCause() instanceof ConnectTimeoutException) {
                            restResponse.onFailure(errMessage(TIMEOUT));
                        } else
                            restResponse.onFailure(errMessage(statusCode));
                    }
                }

        );
    }

    /**
     * 获取对应状态运单数组
     *
     * @param status 运单状态
     */

    public RequestHandle getOrders(final String[] status, final String orderType, final RestResponse restResponse) {
        RequestParams params = new RequestParams();
        params.put("access_token", CommonTools.getToken(paramContext));
        params.put("status", status);
        params.put("type", orderType);
        params.put("phone_id", CommonTools.getPhoneId(paramContext));
        return client.get(BASE_URL + "/driver/order/getbystatus", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                if (response.has("err")) {
                    JSONObject errJsonObject = response.optJSONObject("err");
                    String type = errJsonObject.optString("type");
                    if (type.equals("internal_system_error")) {
                        restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                    } else if (type.equals("undefined_access_token") || type.equals("invalid_access_token") || type.equals("account_not_exist")) {
                        CommonTools.restLogin(paramContext, new RestResponse() {
                            @Override
                            public void onSuccess(Object object) {
                                getOrders(status, orderType, restResponse);
                            }

                            @Override
                            public void onFailure(Object object) {
                                restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                            }
                        });
                    } else if (type.equals("account_disconnected")) {
                        restResponse.onFailure(errMessage(DISCONNECTED));
                    }
                } else {
                    if (null != response) {
                        List<Order> orders = new ArrayList<Order>();
                        if (response.has("orders")) {
                            JSONArray jsonArray = response.optJSONArray("orders");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject orderObject = jsonArray.optJSONObject(i);
                                Order order = orderParse(paramContext, orderObject);
                                if (!order.getStatus().equals(Order.STATUS_INVALID)) {
                                    orders.add(order);
                                }
                            }
                        }
                        restResponse.onSuccess(orders);
                    }
                }

            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONArray errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }
        });
    }

    /**
     * 获取图片储存时七牛云key
     */
    public RequestHandle getQiniuImgToken(final RestResponse restResponse) {
        RequestParams params = new RequestParams();
        params.put("access_token", CommonTools.getToken(paramContext));
        params.put("phone_id", CommonTools.getPhoneId(paramContext));
        return client.get(BASE_URL + "/token/image/upload", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                if (response.has("err")) {
                    JSONObject errJsonObject = response.optJSONObject("err");
                    String type = errJsonObject.optString("type");
                    if (type.equals("internal_system_error")) {
                        restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                    } else if (type.equals("undefined_access_token") || type.equals("invalid_access_token") || type.equals("account_not_exist")) {
                        CommonTools.restLogin(paramContext, new RestResponse() {
                            @Override
                            public void onSuccess(Object object) {
                                getQiniuImgToken(restResponse);
                            }

                            @Override
                            public void onFailure(Object object) {
                                restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                            }
                        });
                    } else if (type.equals("account_disconnected")) {
                        restResponse.onFailure(errMessage(DISCONNECTED));
                    } else {
                        restResponse.onFailure(response.toString());
                    }
                } else {
                    if (null == response) {
                        restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                    } else {
                        String token = response.optString("token");
                        restResponse.onSuccess(token);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONArray errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }
        });
    }

    /**
     * 获取音频储存时七牛云key
     */
    public RequestHandle getQiniuVoiceToken(final String key, final RestResponse restResponse) {
        RequestParams params = new RequestParams();
        params.put("access_token", CommonTools.getToken(paramContext));
        params.put("phone_id", CommonTools.getPhoneId(paramContext));
        if (!StringTools.isEmp(key)) {
            String newVoiceFile = key.substring(0, key.lastIndexOf('.')) + ".mp3";
            params.put("mp3_key", newVoiceFile);
        }
        return client.get(BASE_URL + "/token/amr/upload", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                if (response.has("err")) {
                    JSONObject errJsonObject = response.optJSONObject("err");
                    String type = errJsonObject.optString("type");
                    if (type.equals("internal_system_error")) {
                        restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                    } else if (type.equals("undefined_access_token") || type.equals("invalid_access_token") || type.equals("account_not_exist")) {
                        CommonTools.restLogin(paramContext, new RestResponse() {
                            @Override
                            public void onSuccess(Object object) {
                                getQiniuVoiceToken(key, restResponse);
                            }

                            @Override
                            public void onFailure(Object object) {
                                restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                            }
                        });
                    } else if (type.equals("account_disconnected")) {
                        restResponse.onFailure(errMessage(DISCONNECTED));
                    } else {
                        restResponse.onFailure(response.toString());
                    }
                } else {
                    if (null == response) {
                        restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                    } else {
                        String token = response.optString("token");
                        restResponse.onSuccess(token);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONArray errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }
        });
    }

    /**
     * 单运单事件提交
     */
    public RequestHandle uploadEvent(final String updateTime, final OrderEvent event, final String voiceFile, final List<EventFile> photos, final String delivery_by_qrcode, final List goods, final RestResponse restResponse) {
        RequestParams params = new RequestParams();
        params.put("access_token", CommonTools.getToken(paramContext));
        params.put("update_time", updateTime);
        params.put("order_id", event.getOrderId());
        params.put("type", event.getMold());
        params.put("address", event.getAddress());
        params.put("longitude", event.getLongitude());
        params.put("latitude", event.getLatitude());
        params.put("damaged", event.getIsDamaged() == 0 ? false : true);
        params.put("time", event.getCreateTime());
        params.put("remark", event.getRemark());
        params.put("event_id", event.getEventId());
        params.put("delivery_by_qrcode", delivery_by_qrcode);
        params.put("phone_id", CommonTools.getPhoneId(paramContext));
        params.put("goods", goods);
        if (!StringTools.isEmp(voiceFile)) {
            String newVoiceFile = voiceFile.substring(0, voiceFile.lastIndexOf('.')) + ".mp3";
            params.put("voice_file", newVoiceFile);
        }
        ArrayList<JSONObject> jsonObjectArrayList = new ArrayList<JSONObject>();
        if (null != photos) {
            for (EventFile eventFile : photos) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("name", eventFile.getConfigName());
                    String path;
                    if (!TextUtils.isEmpty(eventFile.getBigFilePath())) {
                        path = eventFile.getBigFilePath();
                    } else {
                        path = eventFile.getFilePath();
                    }
                    obj.put("url", path);
                    jsonObjectArrayList.add(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                    ZZQSApplication.getInstance().CrashToLogin();
                }
            }
            params.put("photos", jsonObjectArrayList);
        }

        if (!StringTools.isEmp(event.getOrderCode())) {
            String[] scanCodes = event.getOrderCode().split(";");
            List<String> scanCodeList = new ArrayList<String>();
            for (int i = 0; i < scanCodes.length; i++) {
                String scanCode = scanCodes[i].split(",")[0];
                scanCodeList.add(scanCode);
            }
            params.put("order_codes", scanCodeList);
        }
        return client.post(BASE_URL + "/transport_event/upload", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                        if (response.has("err")) {
                            JSONObject errJsonObject = response.optJSONObject("err");
                            String type = errJsonObject.optString("type");
                            if (type.equals("internal_system_error")) {
                                restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                            } else if (type.equals("undefined_access_token") || type.equals("invalid_access_token") || type.equals("account_not_exist")) {
                                CommonTools.restLogin(paramContext, new RestResponse() {
                                    @Override
                                    public void onSuccess(Object object) {
                                        uploadEvent(updateTime, event, voiceFile, photos, delivery_by_qrcode, goods, restResponse);
                                    }

                                    @Override
                                    public void onFailure(Object object) {
                                        restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                                    }
                                });
                            } else if (type.equals("parent_order_not_exist") || type.equals("order_not_exist") || type.equals("order_driver_not_match") || type.equals("order_is_deleted")) {
                                restResponse.onFailure(Order.ORDER_NOT_EXIST);
                            } else if (type.equals("order_has_been_complete") || type.equals("can_not_execute_pickupSign") || type.equals("can_not_execute_pickup") || type.equals("can_not_execute_deliverySign") || type.equals("can_not_execute_delivery")) {
                                restResponse.onFailure(Order.ORDER_STATUS_HAS_CHANGED);
                            } else if (type.equals("event_exist")) {
                                restResponse.onSuccess(true);
                            } else if (type.equals("account_disconnected")) {
                                restResponse.onFailure(errMessage(DISCONNECTED));
                            }
                        } else {
                            if (response.has("order")) {
                                JSONObject orderObject = response.optJSONObject("order");
                                if (!orderObject.optString("updated").equals(updateTime)) {
                                    BaseDao<Order> orderDao = DaoManager.getOrderDao(paramContext);
                                    Order order = orderParse(paramContext, orderObject);
                                    if (orderDao.find(null, "order_id=?", new String[]{event.getOrderId()}, null, null, null, null).size() == 1) {
                                        order.set_id(orderDao.find(null, "order_id=?", new String[]{event.getOrderId()}, null, null, null, null).get(0).get_id());
                                    }
                                    restResponse.onSuccess(order);
                                } else {
                                    restResponse.onSuccess(true);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                        if (throwable.getCause() instanceof ConnectTimeoutException) {
                            restResponse.onFailure(errMessage(TIMEOUT));
                        } else
                            restResponse.onFailure(errMessage(statusCode));
                    }

                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        if (throwable.getCause() instanceof ConnectTimeoutException) {
                            restResponse.onFailure(errMessage(TIMEOUT));
                        } else
                            restResponse.onFailure(errMessage(statusCode));
                    }

                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        if (throwable.getCause() instanceof ConnectTimeoutException) {
                            restResponse.onFailure(errMessage(TIMEOUT));
                        } else
                            restResponse.onFailure(errMessage(statusCode));
                    }
                }

        );
    }

    /**
     * 多运单事件提交
     */

    public RequestHandle uploadEvents(final List<String> updateTime,
                                      final List<String> orderIds,
                                      final OrderEvent event,
                                      final String voiceFile,
                                      final List<EventFile> photos,
                                      final RestResponse restResponse) {
        RequestParams params = new RequestParams();
        params.put("access_token", CommonTools.getToken(paramContext));
        params.put("update_times", updateTime);
        params.put("order_ids", orderIds);
        params.put("type", event.getMold());
        params.put("address", event.getAddress());
        params.put("longitude", event.getLongitude());
        params.put("latitude", event.getLatitude());
        params.put("damaged", event.getIsDamaged() == 0 ? false : true);
        params.put("time", event.getCreateTime());
        params.put("remark", event.getRemark());
        params.put("phone_id", CommonTools.getPhoneId(paramContext));
        if (!StringTools.isEmp(voiceFile)) {
            String newVoiceFile = voiceFile.substring(0, voiceFile.lastIndexOf('.')) + ".mp3";
            params.put("voice_file", newVoiceFile);
        }
        ArrayList<JSONObject> jsonObjectArrayList = new ArrayList<>();
        if (null != photos) {
            for (EventFile eventFile : photos) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("name", eventFile.getConfigName());
                    obj.put("url", eventFile.getFilePath());
                    jsonObjectArrayList.add(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                    ZZQSApplication.getInstance().CrashToLogin();
                }
            }
            params.put("photos", jsonObjectArrayList);
        }
        return client.post(BASE_URL + "/transport_event/multiUpload", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                HashMap<String, String> result = new HashMap<String, String>();
                if (response.has("err")) {
                    JSONArray errJsonArray = response.optJSONArray("err");
                    for (int i = 0; i < errJsonArray.length(); i++) {
                        JSONObject errJsonObject = errJsonArray.optJSONObject(i);
                        String type = errJsonObject.optString("type");
                        if (type.equals("internal_system_error")) {
                            restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                            return;
                        } else if (type.equals("account_disconnected")) {
                            restResponse.onFailure(errMessage(DISCONNECTED));
                        } else if (type.equals("undefined_access_token") || type.equals("invalid_access_token") || type.equals("account_not_exist")) {
                            CommonTools.restLogin(paramContext, new RestResponse() {
                                @Override
                                public void onSuccess(Object object) {
                                    uploadEvents(updateTime, orderIds, event, voiceFile, photos, restResponse);
                                }

                                @Override
                                public void onFailure(Object object) {
                                    restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                                    return;
                                }
                            });
                        } else if (type.equals("parent_order_not_exist") || type.equals("order_not_exist") || type.equals("order_driver_not_match")) {
                            result.put(errJsonObject.optString("order_id"), Order.ORDER_NOT_EXIST);
                        } else if (type.equals("order_has_been_complete") || type.equals("can_not_execute_pickupSign") || type.equals("can_not_execute_pickup") || type.equals("can_not_execute_deliverySign") || type.equals("can_not_execute_delivery")) {
                            result.put(errJsonObject.optString("order_id"), Order.ORDER_STATUS_HAS_CHANGED);
                        }
                    }
                }
                if (response.has("success")) {
                    JSONArray successJsonArray = response.optJSONArray("success");
                    for (int i = 0; i < successJsonArray.length(); i++) {
                        JSONObject json = successJsonArray.optJSONObject(i);
                        result.put(json.optString("order_id"), "true");
                    }
                }
                if (response.has("update")) {
                    final List<String> orderIds = new ArrayList<String>();
                    JSONArray updateJsonArray = response.optJSONArray("update");
                    for (int i = 0; i < updateJsonArray.length(); i++) {
                        JSONObject json = updateJsonArray.optJSONObject(i);
                        orderIds.add(json.optString("order_id"));
                        result.put(json.optString("order_id"), "true");
                    }
                    for (int i = 0; i < orderIds.size(); i++) {
                        final int number = i;
                        final ArrayList<Order> updateList = new ArrayList<Order>();
                        getOrder(orderIds.get(i), new RestResponse() {
                            @Override
                            public void onSuccess(Object object) {
                                if (object instanceof Order) {
                                    Order newOrder = (Order) object;
                                    if (DaoManager.getOrderDao(paramContext).find(null, "order_id=?", new String[]{orderIds.get(number)}, null, null, null, null).size() == 1) {
                                        newOrder.set_id(DaoManager.getOrderDao(paramContext).find(null, "order_id=?", new String[]{orderIds.get(number)}, null, null, null, null).get(0).get_id());
                                    }
                                    updateList.add(newOrder);
                                    if (number == orderIds.size() - 1) {
                                        DaoManager.getOrderDao(paramContext).updates(updateList);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Object object) {
                            }
                        });
                    }
                }
                restResponse.onSuccess(result);

            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONArray errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }
        });
    }

    /**
     * 上报地理位置信息
     *
     * @param traces
     */
    public RequestHandle trace(final ArrayList<JSONObject> traces, final RestResponse restResponse) {
        RequestParams params = new RequestParams();
        params.put("access_token", CommonTools.getToken(paramContext));
        params.put("trace_infos", traces);
        params.put("phone_id", CommonTools.getPhoneId(paramContext));
        return client.post(BASE_URL + "/trace/multiupload", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                LogInfo logInfo = new LogInfo();
                logInfo.setType(LogInfo.TYPE_OF_NORMAL);
                logInfo.setContent(response.toString());
                logInfo.setTime(System.currentTimeMillis());
                DaoManager.getLogInfoDao(paramContext).insert(logInfo);

                if (response.has("err")) {
                    JSONObject errJsonObject = response.optJSONObject("err");
                    String type = errJsonObject.optString("type");
                    if (type.equals("internal_system_error")) {
                        restResponse.onFailure("服务器出错");
                    } else if (type.equals("undefined_access_token") || type.equals("invalid_access_token") || type.equals("account_not_exist")) {
                        CommonTools.restLogin(paramContext, new RestResponse() {
                            @Override
                            public void onSuccess(Object object) {
                                trace(traces, restResponse);
                            }

                            @Override
                            public void onFailure(Object object) {
                                restResponse.onFailure(false);
                            }
                        });
                    } else if (type.equals("account_disconnected")) {
                        restResponse.onFailure(errMessage(DISCONNECTED));
                    }
                } else {
                    restResponse.onSuccess(true);
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONArray errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }
        });
    }

    /**
     * 上传个推的用户cid
     *
     * @param deviceId
     */
    public RequestHandle setDeviceId(final String deviceId, final RestResponse restResponse) {
        RequestParams params = new RequestParams();
        params.put("access_token", CommonTools.getToken(paramContext));
        params.put("device_id", deviceId);
        params.put("phone_id", CommonTools.getPhoneId(paramContext));
        return client.post(BASE_URL + "/driver/device/update", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                if (response.has("err")) {
                    JSONObject errJsonObject = response.optJSONObject("err");
                    String type = errJsonObject.optString("type");
                    if (type.equals("internal_system_error")) {
                        restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                    } else if (type.equals("undefined_access_token") || type.equals("invalid_access_token") || type.equals("account_not_exist")) {
                        CommonTools.restLogin(paramContext, new RestResponse() {
                            @Override
                            public void onSuccess(Object object) {
                                setDeviceId(deviceId, restResponse);
                            }

                            @Override
                            public void onFailure(Object object) {
                                restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                            }
                        });
                    }
                } else {
                    boolean success = response.optBoolean("success");
                    if (success) {
                        restResponse.onSuccess(success);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONArray errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }
        });
    }

    public RequestHandle updateProfile(final User user, final RestResponse restResponse) {
        RequestParams params = new RequestParams();
        JSONObject json = new JSONObject();
        try {
            json.put("nickname", user.getNickname());
            json.put("photo", user.getPhoto());
            json.put("id_card_photo", user.getIdCardPhoto());
            json.put("driving_id_photo", user.getDrivingIdPhoto());
            json.put("travel_id_photo", user.getTravelIdPhoto());
            json.put("operating_permits_photo", user.getTradingIdPhoto());
            json.put("driving_date", user.getDrivingDate());
            if (!StringTools.isEmp(user.getPlatePhotos())) {
                String[] platePhotos = user.getPlatePhotos().split(";");
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < platePhotos.length; i++) {
                    jsonArray.put(platePhotos[i]);
                }
                json.put("plate_photos", jsonArray);
            }
            if (!StringTools.isEmp(user.getPlateNumbers())) {
                String[] plateNumbers = user.getPlateNumbers().split(";");
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < plateNumbers.length; i++) {
                    jsonArray.put(plateNumbers[i]);
                }
                json.put("plate_numbers", jsonArray);
            }
            params.put("profile", json);
            params.put("access_token", CommonTools.getToken(paramContext));
            params.put("phone_id", CommonTools.getPhoneId(paramContext));
        } catch (JSONException e) {
            e.printStackTrace();
            ZZQSApplication.getInstance().CrashToLogin();
        }
        return client.post(BASE_URL + "/driver/profile", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                if (response.has("err")) {
                    JSONObject errJsonObject = response.optJSONObject("err");
                    String type = errJsonObject.optString("type");
                    if (type.equals("internal_system_error")) {
                        restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                    } else if (type.equals("undefined_access_token") || type.equals("invalid_access_token") || type.equals("account_not_exist")) {
                        CommonTools.restLogin(paramContext, new RestResponse() {
                            @Override
                            public void onSuccess(Object object) {
                                updateProfile(user, restResponse);
                            }

                            @Override
                            public void onFailure(Object object) {
                                restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                            }
                        });
                    } else if (type.equals("account_disconnected")) {
                        restResponse.onFailure(errMessage(DISCONNECTED));

                    }
                } else {
                    restResponse.onSuccess(true);
                }

            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONArray errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }
        });
    }

    /**
     * 检测应用升级
     */

    public RequestHandle upgrade(final RestResponse restResponse) {
        RequestParams params = new RequestParams();
        params.put("access_token", CommonTools.getToken(paramContext));
        params.put("phone_id", CommonTools.getPhoneId(paramContext));
        params.put("version", ZZQSApplication.getVersion());
        params.put("platform", "android");
        params.put("username", ZZQSApplication.getInstance().getUser().getUsername());
        return client.get(BASE_URL + "/driver/version", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject json) {
                if (json.has("err")) {
                    JSONObject errJsonObject = json.optJSONObject("err");
                    String type = errJsonObject.optString("type");
                    if (type.equals("internal_system_error")) {
                        restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                    } else if (type.equals("undefined_access_token") || type.equals("invalid_access_token") || type.equals("account_not_exist")) {
                        CommonTools.restLogin(paramContext, new RestResponse() {
                            @Override
                            public void onSuccess(Object object) {
                                upgrade(restResponse);
                            }

                            @Override
                            public void onFailure(Object object) {
                                restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                            }
                        });
                    } else if (type.equals("account_disconnected")) {
                        restResponse.onFailure(errMessage(DISCONNECTED));
                    }
                } else {
                    Upgrade upgrade = new Upgrade();
                    try {
                        upgrade.setVersion(json.getInt("version"));
                        upgrade.setUrl(json.optString("app_url"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ZZQSApplication.getInstance().CrashToLogin();
                    }
                    restResponse.onSuccess(upgrade);
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONArray errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }
        });
    }

    public RequestHandle getEvaluationNumber(final RestResponse restResponse) {
        RequestParams params = new RequestParams();
        params.put("access_token", CommonTools.getToken(paramContext));
        return client.get(BASE_URL + "/driver/evaluation/count/all", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                if (response.has("err")) {
                    JSONObject errJsonObject = response.optJSONObject("err");
                    String type = errJsonObject.optString("type");
                    if (type.equals("internal_system_error")) {
                        restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                    } else if (type.equals("undefined_access_token") || type.equals("invalid_access_token") || type.equals("account_not_exist")) {
                        CommonTools.restLogin(paramContext, new RestResponse() {
                            @Override
                            public void onSuccess(Object object) {
                                getEvaluationNumber(restResponse);
                            }

                            @Override
                            public void onFailure(Object object) {
                                restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                            }
                        });
                    } else if (type.equals("account_disconnected")) {
                        restResponse.onFailure(errMessage(DISCONNECTED));
                    }
                } else {
                    restResponse.onSuccess(response);
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONArray errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }
        });
    }

    /**
     * 获取评论
     */
    public RequestHandle getEvaluations(final int limit, final String createTime, final int level, final RestResponse restResponse) {
        RequestParams params = new RequestParams();
        params.put("access_token", CommonTools.getToken(paramContext));
        params.put("limit", limit);
        params.put("level", level);
        if (!StringTools.isEmp(createTime)) {
            params.put("from_time", createTime);
        }
        return client.get(BASE_URL + "/driver/evaluation/list/simple", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                if (response.has("err")) {
                    JSONObject errJsonObject = response.optJSONObject("err");
                    String type = errJsonObject.optString("type");
                    if (type.equals("internal_system_error")) {
                        restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                    } else if (type.equals("undefined_access_token") || type.equals("invalid_access_token") || type.equals("account_not_exist")) {
                        CommonTools.restLogin(paramContext, new RestResponse() {
                            @Override
                            public void onSuccess(Object object) {
                                getEvaluations(limit, createTime, level, restResponse);
                            }

                            @Override
                            public void onFailure(Object object) {
                                restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                            }
                        });
                    } else if (type.equals("account_disconnected")) {
                        restResponse.onFailure(errMessage(DISCONNECTED));
                    }
                }
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                if (null != response) {
                    List<Evaluation> evaluations = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.optJSONObject(i);
                        Evaluation evaluation = new Evaluation();
                        evaluation.setCreateTime(obj.optString("created"));
                        evaluation.setUpdateTime(obj.optString("create_time_format"));
                        evaluation.setType(obj.optInt("level"));
                        evaluation.setContent(obj.optString("content_text"));
                        evaluation.setSerialNo(obj.optJSONObject("order").optJSONObject("order_details").optString("order_number"));
                        if (obj.has("user")) {
                            evaluation.setCompanyName(obj.optJSONObject("user").optJSONObject("company").optString("name"));
                        } else {
                            evaluation.setCompanyName("系统默认");
                        }

                        evaluations.add(evaluation);
                    }
                    restResponse.onSuccess(evaluations);
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONArray errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }
        });
    }

    /**
     * 实提数量
     */
    public RequestHandle uploadActualGoods(final String orderId, final List goods, final RestResponse restResponse) {
        RequestParams params = new RequestParams();
        params.put("access_token", CommonTools.getToken(paramContext));
        params.put("order_id", orderId);
        params.put("goods", goods);
        return client.post(BASE_URL + "/transport_event/actual_goods/detail", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                if (response.has("err")) {
                    JSONObject errJsonObject = response.optJSONObject("err");
                    String type = errJsonObject.optString("type");
                    if (type.equals("internal_system_error")) {
                        restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                    } else if (type.equals("undefined_access_token") || type.equals("invalid_access_token") || type.equals("account_not_exist")) {
                        CommonTools.restLogin(paramContext, new RestResponse() {
                            @Override
                            public void onSuccess(Object object) {
                                uploadActualGoods(orderId, goods, restResponse);
                            }

                            @Override
                            public void onFailure(Object object) {
                                restResponse.onFailure("服务器出错，请稍后再试或与客服人员联系");
                            }
                        });
                    } else if (type.equals("account_disconnected")) {
                        restResponse.onFailure(errMessage(DISCONNECTED));
                    }
                } else {

                    restResponse.onSuccess(true);
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONArray errorResponse) {
                if (throwable.getCause() instanceof ConnectTimeoutException) {
                    restResponse.onFailure(errMessage(TIMEOUT));
                } else
                    restResponse.onFailure(errMessage(statusCode));
            }
        });
    }

    public static final int TIMEOUT = 101;
    public static final int DISCONNECTED = 102;

    public static String errMessage(int statusCode) {
        String errMsg = String.valueOf(statusCode);
        switch (statusCode) {
            case TIMEOUT:
                errMsg = "连接超时，请检查网络" + "  " + statusCode;
                break;
            case 0:
            case 408:
            case 400:
                errMsg = "与服务器连接失败，请检查网络" + "  " + statusCode;
                break;
            case 102:
                errMsg = "disconnected" + "  " + statusCode;
                break;
        }
        return errMsg;
    }

    public static Order orderParse(Context context, JSONObject orderObject) {
        Order order = new Order();
        if (orderObject.optString("delete_status").equals("true")) {
            order.setStatus(Order.STATUS_INVALID);
        } else {
            order.setStatus(orderObject.optString("status", Order.UN_PICKUP_ENTRANCE));
        }

        order.setOrderId(orderObject.optString("_id"));
        order.setConfirmStatus(orderObject.optString("confirm_status", "un_confirmed"));
        order.setPickupTimeStart(orderObject.optString("pickup_start_time_format"));
        order.setPickupTimeEnd(orderObject.optString("pickup_end_time_format"));
        order.setDeliverTimeStart(orderObject.optString("delivery_start_time_format"));
        order.setDeliverTimeEnd(orderObject.optString("delivery_end_time_format"));
        order.setOrderType(orderObject.optString("type", Order.DRIVER_ORDER));
        order.setUpdateTime(orderObject.optString("updated"));
        order.setReceiverName(orderObject.optString("receiver_name"));
        order.setSenderName(orderObject.optString("sender_name"));
        order.setDamaged(orderObject.optString("damaged"));
        order.setDescription(orderObject.optString("description"));

        order.setPickupEntranceForce(orderObject.optString("pickup_entrance_force", "false"));
        order.setPickupPhotoForce(orderObject.optString("pickup_photo_force", "false"));
        order.setDeliveryEntranceForce(orderObject.optString("delivery_entrance_force", "false"));
        order.setDeliveryPhotoForce(orderObject.optString("delivery_photo_force", "true"));

        if (orderObject.has("pickup_contact")) {
            JSONObject pickUpObject = orderObject.optJSONObject("pickup_contact");
            if (null != pickUpObject) {
                order.setFromMobilePhone(pickUpObject.optString("mobile_phone"));
                order.setFromPhone(pickUpObject.optString("phone"));
                order.setFromAddress(pickUpObject.optString("address"));
                order.setFromContact(pickUpObject.optString("name"));
            }
        }
        if (orderObject.has("delivery_contact")) {
            JSONObject deliveryObject = orderObject.optJSONObject("delivery_contact");
            if (null != deliveryObject) {
                order.setToMobilePhone(deliveryObject.optString("mobile_phone"));
                order.setToPhone(deliveryObject.optString("phone"));
                order.setToAddress(deliveryObject.optString("address"));
                order.setToContact(deliveryObject.optString("name"));
            }
        }
        if (orderObject.has("order_details")) {
            JSONObject detailObject = orderObject.optJSONObject("order_details");
            if (null != detailObject) {
                order.setOrderDetailId(detailObject.optString("_id"));
                order.setSerialNo(detailObject.optString("order_number"));
                order.setTotalWeight(detailObject.optString("weight"));
                order.setTotalQuantity(detailObject.optString("count"));
                order.setTotalVolume(detailObject.optString("volume"));
                order.setWeightUnit(detailObject.optString("weight_unit"));
                order.setQuantityUnit(detailObject.optString("count_unit"));
                order.setVolumeUnit(detailObject.optString("volume_unit"));
                order.setGoodsName(detailObject.optString("goods_name"));
                order.setRefNum(detailObject.optString("refer_order_number"));
                order.setCreateTime(detailObject.optString("create_time"));
                order.setRemark(detailObject.optString("details"));
            }
            if (detailObject.has("goods")) {
                JSONArray goodsArray = detailObject.optJSONArray("goods");
                if (null != goodsArray) {
                    /**
                     * goodsUnit，goodsUnit2,goodsUnit3:件重体单位
                     * goodsCount，goodsCount2，goodsCount3：件重体的值
                     * */
                    StringBuffer goodsId = new StringBuffer();
                    StringBuffer goodsName = new StringBuffer();
                    StringBuffer goodsUnit = new StringBuffer();
                    StringBuffer goodsCount = new StringBuffer();
                    StringBuffer goodsPrice = new StringBuffer();
                    StringBuffer goodsUnit2 = new StringBuffer();
                    StringBuffer goodsUnit3 = new StringBuffer();
                    StringBuffer goodsCount2 = new StringBuffer();
                    StringBuffer goodsCount3 = new StringBuffer();
                    for (int i = 0; i < goodsArray.length(); i++) {
                        try {
                            JSONObject jsonObject = goodsArray.getJSONObject(i);
                            if (StringTools.isEmp(jsonObject.optString("_id"))) {
                                goodsId.append("null");
                            } else {
                                goodsId.append(jsonObject.optString("_id"));
                            }

                            if (StringTools.isEmp(jsonObject.optString("name"))) {
                                goodsName.append("null");
                            } else {
                                goodsName.append(jsonObject.optString("name"));
                            }

                            if (StringTools.isEmp(jsonObject.optString("unit"))) {
                                goodsUnit.append("null");
                            } else {
                                goodsUnit.append(jsonObject.optString("unit"));
                            }

                            if (StringTools.isEmp(jsonObject.optString("unit2"))) {
                                goodsUnit2.append("null");
                            } else {
                                goodsUnit2.append(jsonObject.optString("unit2"));
                            }

                            if (StringTools.isEmp(jsonObject.optString("unit3"))) {
                                goodsUnit3.append("null");
                            } else {
                                goodsUnit3.append(jsonObject.optString("unit3"));
                            }

                            if (StringTools.isEmp(jsonObject.optString("count"))) {
                                goodsCount.append("0");
                            } else {
                                goodsCount.append(jsonObject.optString("count"));
                            }

                            if (StringTools.isEmp(jsonObject.optString("count2"))) {
                                goodsCount2.append("0");
                            } else {
                                goodsCount2.append(jsonObject.optString("count2"));
                            }

                            if (StringTools.isEmp(jsonObject.optString("count3"))) {
                                goodsCount3.append("0");
                            } else {
                                goodsCount3.append(jsonObject.optString("count3"));
                            }

                            if (StringTools.isEmp(jsonObject.optString("price"))) {
                                goodsPrice.append("null");
                            } else {
                                goodsPrice.append(jsonObject.optString("price"));
                            }


                            if (i != goodsArray.length() - 1) {
                                goodsId.append("/zzqs/");
                                goodsName.append("/zzqs/");
                                goodsUnit.append("/zzqs/");
                                goodsUnit2.append("/zzqs/");
                                goodsUnit3.append("/zzqs/");
                                goodsCount.append("/zzqs/");
                                goodsCount2.append("/zzqs/");
                                goodsCount3.append("/zzqs/");
                                goodsPrice.append("/zzqs/");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ZZQSApplication.getInstance().CrashToLogin();
                        }
                    }
                    order.setActualGoodsId(goodsId.toString());
                    order.setActualGoodsName(goodsName.toString());
                    order.setActualGoodsCount(goodsCount.toString());
                    order.setActualGoodsCount2(goodsCount2.toString());
                    order.setActualGoodsCount3(goodsCount3.toString());
                    order.setActualGoodsUnit(goodsUnit.toString());
                    order.setActualGoodsUnit2(goodsUnit2.toString());
                    order.setActualGoodsUnit3(goodsUnit3.toString());
                    order.setActualPrice(goodsPrice.toString());
                }
            }
        }
        if (orderObject.has("road_order")) {
            JSONObject roadOrderObject = orderObject.optJSONObject("road_order");
            if (null != roadOrderObject) {
                order.setRoadOrderName(roadOrderObject.optString("name"));
            }
        }
        if (orderObject.has("company_configuration")) {
            JSONObject configObj = orderObject.optJSONObject("company_configuration");
            if (null != configObj) {
                order.setConfigId(configObj.optString("update_id", "null"));
                order.setCommitPickupConfigDetail("false");
                order.setCommitDeliveryConfigDetail("false");
                if (configObj.has("pickup_option")) {
                    JSONObject pickupOptObj = configObj.optJSONObject("pickup_option");
                    if (null != pickupOptObj) {
                        order.setPickupEntranceForce(pickupOptObj.optString("must_entrance", "false"));
                        if (pickupOptObj.has("entrance_photos")) {
                            JSONArray entrancePhotos = pickupOptObj.optJSONArray("entrance_photos");
                            if (null != entrancePhotos && entrancePhotos.length() > 0) {
                                StringBuffer sb = new StringBuffer();
                                for (int i = 0; i < entrancePhotos.length(); i++) {
                                    try {
                                        JSONObject obj = entrancePhotos.getJSONObject(i);
                                        if (!StringTools.isEmp(obj.optString("name"))) {
                                            sb.append(obj.optString("name"));
                                        }
                                        if (i != entrancePhotos.length() - 1) {
                                            sb.append("/zzqs/");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        ZZQSApplication.getInstance().CrashToLogin();
                                    }
                                }
                                order.setPickupEntrancePhotos(sb.toString());
                            }
                        }
                        order.setPickupPhotoForce(pickupOptObj.optString("must_take_photo", "false"));
                        if (pickupOptObj.has("take_photos")) {
                            JSONArray takePhotos = pickupOptObj.optJSONArray("take_photos");
                            if (null != takePhotos && takePhotos.length() > 0) {
                                StringBuffer sb = new StringBuffer();
                                for (int i = 0; i < takePhotos.length(); i++) {
                                    try {
                                        JSONObject obj = takePhotos.getJSONObject(i);
                                        if (!StringTools.isEmp(obj.optString("name"))) {
                                            sb.append(obj.optString("name"));
                                        }
                                        if (i != takePhotos.length() - 1) {
                                            sb.append("/zzqs/");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        ZZQSApplication.getInstance().CrashToLogin();
                                    }
                                }
                                order.setPickupTakePhotos(sb.toString());
                            }
                        }
                        order.setPickupMustConfirmDetail(pickupOptObj.optString("must_confirm_detail", "false"));
                    }
                }
                if (configObj.has("delivery_option")) {
                    JSONObject deliveryOptObj = configObj.optJSONObject("delivery_option");
                    order.setDeliveryEntranceForce(deliveryOptObj.optString("must_entrance", "false"));
                    if (deliveryOptObj.has("entrance_photos")) {
                        JSONArray entrancePhotos = deliveryOptObj.optJSONArray("entrance_photos");
                        if (null != entrancePhotos && entrancePhotos.length() > 0) {
                            StringBuffer sb = new StringBuffer();
                            for (int i = 0; i < entrancePhotos.length(); i++) {
                                try {
                                    JSONObject obj = entrancePhotos.getJSONObject(i);
                                    if (!StringTools.isEmp(obj.optString("name"))) {
                                        sb.append(obj.optString("name"));
                                    }
                                    if (i != entrancePhotos.length() - 1) {
                                        sb.append("/zzqs/");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    ZZQSApplication.getInstance().CrashToLogin();
                                }
                            }
                            order.setDeliveryEntrancePhotos(sb.toString());
                        }
                    }
                    order.setDeliveryPhotoForce(deliveryOptObj.optString("must_take_photo", "false"));
                    if (deliveryOptObj.has("take_photos")) {
                        JSONArray takePhotos = deliveryOptObj.optJSONArray("take_photos");
                        if (null != takePhotos && takePhotos.length() > 0) {
                            StringBuffer sb = new StringBuffer();
                            for (int i = 0; i < takePhotos.length(); i++) {
                                try {
                                    JSONObject obj = takePhotos.getJSONObject(i);
                                    if (!StringTools.isEmp(obj.optString("name"))) {
                                        sb.append(obj.optString("name"));
                                    }
                                    if (i != takePhotos.length() - 1) {
                                        sb.append("/zzqs/");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            }
                            order.setDeliveryTakePhotos(sb.toString());
                        }
                    }
                    order.setDeliveryMustConfirmDetail(deliveryOptObj.optString("must_confirm_detail", "false"));
                }
            }
        }
        order.setIsNew(Order.EXIST);
        if (!order.getStatus().equals(Order.UN_PICKUP_ENTRANCE)) {
            order.setConfirmStatus(Order.CONFIRMED);
        }
        if (!StringTools.isEmp(order.getActualGoodsId())) {//新类型运单
            if (!StringTools.isEmp(order.getActualGoodsName())) {
                order.setOperationId(order.getActualGoodsId());
                order.setOperationGoodsName(order.getActualGoodsName());
                order.setOperationGoodsUnit(order.getActualGoodsUnit());
                order.setOperationPrice(order.getActualPrice());
                List<Order> list = DaoManager.getOrderDao(context).find(null, "order_id=?", new String[]{order.getOrderId()}, null, null, null, null);
                if (list != null && list.size() == 1) {
                    Order dbOrder = list.get(0);
                    order.setOperationGoodsCount(dbOrder.getOperationGoodsCount());
                    order.setOperationHasDamage(dbOrder.getOperationHasDamage());
                    order.setOperationHasLack(dbOrder.getOperationHasLack());
                } else {
                    String[] names = order.getActualGoodsName().split("/zzqs/");
                    StringBuffer damages = new StringBuffer();
                    StringBuffer lacks = new StringBuffer();
                    StringBuffer counts = new StringBuffer();
                    for (int i = 0; i < names.length; i++) {
                        damages.append("false");
                        lacks.append("false");
                        counts.append("0");
                        if (i != names.length - 1) {
                            damages.append("/zzqs/");
                            lacks.append("/zzqs/");
                            counts.append("/zzqs/");
                        }
                    }
                    order.setOperationGoodsCount(counts.toString());
                    order.setOperationHasDamage(damages.toString());
                    order.setOperationHasLack(lacks.toString());
                }
            }
        }
        return order;
    }
    public static void show(String content) {
        int p = 2048;
        long length = content.length();
        if (length < p || length == p)
        System.out.println(content);
        else {
            while (content.length() > p) {
                String logContent = content.substring(0, p);
                content = content.replace(logContent, "");
                System.out.println(logContent);
            }
            System.out.println(content);
        }
    }
}
