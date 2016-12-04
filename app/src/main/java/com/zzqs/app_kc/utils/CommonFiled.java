package com.zzqs.app_kc.utils;

/**
 * Created by lance on 15/3/21.
 */
public class CommonFiled {
    public static final String DEFAULT = "default";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String TOKENID = "tokenId";
    public static final String NEW_MESSAGE = "newMessage";
    public static final String COMPANY_MSG = "company";
    public static final String SETTING_MSG = "setting";
    public static final String HELP_MSG = "help";
    public static final String VERSION_CODE = "versionCode";
    public static final String QINIU_ZOOM = "http://7xiwrb.com1.z0.glb.clouddn.com/@";
    public static final String IS_FIRST_INSTALL_APP = "isFirstInstallApp";
    public static final int MAX_INTERVAL = 30 * 60 * 1000;//前台页面提交事件时定位失败向后台服务拿定位信息的最大时间间隔
    public static final int LOCATION_REQUEST_INTERVAL = 20 * 1000;//后台定位服务每次取点间隔
    public static final int LOCATION_REPORT_INTERVAL = 3 * 60 * 1000;//定位信息上传线程每次上传成功后的时间间隔
    public static final int FILE_UPLOAD_FILE_NOT_EXIST = 5 * 1000;//上传文件线程当发现不存在的文件时的时间间隔
    public static final int FILE_UPLOAD_FILE_NUMBER_IS_0 = 5 * 1000;//上传文件线程当发现本地数据库中没有未上传文件时的时间间隔
    public static final int FILE_UPLOAD_GET_QINIU_TOKEN_FAILED = 5 * 1000;//上传文件线程当发现获取七牛上传token失败时的时间间隔
    public static final int FILE_UPLOAD_FAILED = 5 * 1000;//上传文件线程当上传文件失败时的时间间隔
    public static final String NEW_INVITE_COMPANY = "new_invite_company";
}
