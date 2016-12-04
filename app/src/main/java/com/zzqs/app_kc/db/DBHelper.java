package com.zzqs.app_kc.db;

import android.content.Context;

import com.zzqs.app_kc.db.hibernate.sql.SQLHelper;
import com.zzqs.app_kc.entity.Company;
import com.zzqs.app_kc.entity.DriverTrace;
import com.zzqs.app_kc.entity.EventFile;
import com.zzqs.app_kc.entity.LogInfo;
import com.zzqs.app_kc.entity.Order;
import com.zzqs.app_kc.entity.OrderEvent;
import com.zzqs.app_kc.entity.User;

public class DBHelper extends SQLHelper {
    private final static String DBNAME = "zhuzhuqskc.db";
    private final static int DBVERSION = 1;
    private static DBHelper instance = null;
    public static final Object dbLock = new Object();
    private static final Class<?>[] clazz = {
            EventFile.class, OrderEvent.class, User.class, Order.class, DriverTrace.class, Company.class, LogInfo.class};// 要初始化的表

    public DBHelper(Context context) {
        super(context, DBNAME, null, DBVERSION, clazz);
    }
    public static DBHelper getInstance(Context context){
        if (instance == null){
            instance = new DBHelper(context);
        }
        return instance;
    }
}
