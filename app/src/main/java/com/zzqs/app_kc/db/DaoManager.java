package com.zzqs.app_kc.db;

import android.content.Context;

import com.zzqs.app_kc.db.hibernate.dao.BaseDao;
import com.zzqs.app_kc.db.hibernate.dao.impl.BaseDaoImpl;
import com.zzqs.app_kc.entity.Company;
import com.zzqs.app_kc.entity.DriverTrace;
import com.zzqs.app_kc.entity.EventFile;
import com.zzqs.app_kc.entity.LogInfo;
import com.zzqs.app_kc.entity.Order;
import com.zzqs.app_kc.entity.OrderEvent;
import com.zzqs.app_kc.entity.User;

import java.util.List;


public class DaoManager {
    private static BaseDao<DriverTrace> driverTraceDao;
    private static BaseDao<Order> orderDao;
    private static BaseDao<User> userDao;
    private static BaseDao<OrderEvent> orderEventDao;
    private static BaseDao<EventFile> eventFileDao;
    private static BaseDao<Company> companyDao;
    private static BaseDao<LogInfo> logInfoDao;
    private static DBHelper dbHelper;

    public static void clearAll(Context context) {
        try {
            getEventFileDao(context).execSql("DELETE FROM event_file where 1=1", null);
            getOrderDao(context).execSql("DELETE FROM zz_order where 1=1", null);
            getDriverTraceDao(context).execSql("DELETE FROM driver_trace where 1=1", null);
            getOrderEventDao(context).execSql("DELETE FROM order_event where 1=1", null);
            getUserDao(context).execSql("DELETE FROM user where 1=1", null);
            getCompanyDao(context).execSql("DELETE FROM company where 1=1", null);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }


    }

    public static BaseDao<DriverTrace> getDriverTraceDao(Context context) {
        if (context == null) {
            throw new IllegalStateException("Context can't be null");
        }
        if (dbHelper == null) {
            dbHelper = new DBHelper(context);
        }
        return driverTraceDao != null ? driverTraceDao
                : (driverTraceDao = new BaseDaoImpl<DriverTrace>(dbHelper) {
        });
    }

    public static BaseDao<Order> getOrderDao(Context context) {
        if (context == null) {
            throw new IllegalStateException("Context can't be null");
        }
        if (dbHelper == null) {
            dbHelper = new DBHelper(context);
        }
        return orderDao != null ? orderDao
                : (orderDao = new BaseDaoImpl<Order>(dbHelper) {
        });
    }

    public static BaseDao<User> getUserDao(Context context) {
        if (context == null) {
            throw new IllegalStateException("Context can't be null");
        }
        if (dbHelper == null) {
            dbHelper = new DBHelper(context);
        }
        return userDao != null ? userDao
                : (userDao = new BaseDaoImpl<User>(dbHelper) {
        });
    }

    public static BaseDao<OrderEvent> getOrderEventDao(Context context) {
        if (context == null) {
            throw new IllegalStateException("Context can't be null");
        }
        if (dbHelper == null) {
            dbHelper = new DBHelper(context);
        }
        return orderEventDao != null ? orderEventDao
                : (orderEventDao = new BaseDaoImpl<OrderEvent>(dbHelper) {
        });
    }

    public static BaseDao<EventFile> getEventFileDao(Context context) {
        if (context == null) {
            throw new IllegalStateException("Context can't be null");
        }
        if (dbHelper == null) {
            dbHelper = new DBHelper(context);
        }
        return eventFileDao != null ? eventFileDao
                : (eventFileDao = new BaseDaoImpl<EventFile>(dbHelper) {
        });
    }

    public static BaseDao<Company> getCompanyDao(Context context) {
        if (context == null) {
            throw new IllegalStateException("Context can't be null");
        }
        if (dbHelper == null) {
            dbHelper = new DBHelper(context);
        }
        return companyDao != null ? companyDao
                : (companyDao = new BaseDaoImpl<Company>(dbHelper) {
        });
    }

    public static BaseDao<LogInfo> getLogInfoDao(Context context) {
        if (context == null) {
            throw new IllegalStateException("Context can't be null");
        }
        if (dbHelper == null) {
            dbHelper = new DBHelper(context);
        }

        return logInfoDao != null ? logInfoDao
                : (logInfoDao = new BaseDaoImpl<LogInfo>(dbHelper) {
        });
    }

    /**
     * 是否有非完成的运单
     */
    public static boolean isHaveOrderExceptCommit(Context context) {
        if (null == context) {
            return false;
        }
        List<Order> orderList = getOrderDao(context).find(null, "status not in(?,?)", new String[]{Order.STATUS_COMMIT,Order.STATUS_INVALID}, null, null, null, null);
        if (orderList.size() > 0) {
            return true;
        }
        return false;
    }

}
