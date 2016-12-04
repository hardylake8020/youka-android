package com.zzqs.app_kc.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.testin.agent.TestinAgent;
import com.testin.agent.TestinAgentConfig;
import com.zzqs.app_kc.BuildConfig;
import com.zzqs.app_kc.R;
import com.zzqs.app_kc.activities.LoginActivity;
import com.zzqs.app_kc.activities.MainActivity;
import com.zzqs.app_kc.db.DBHelper;
import com.zzqs.app_kc.db.DaoManager;
import com.zzqs.app_kc.db.hibernate.dao.BaseDao;
import com.zzqs.app_kc.entity.LogInfo;
import com.zzqs.app_kc.entity.User;
import com.zzqs.app_kc.utils.CrashHandler;
import com.zzqs.app_kc.utils.StringTools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lance on 15/3/23.
 */
public class ZZQSApplication extends Application {
  public static final String TAG = "ZZQSApplication";
  private static ZZQSApplication instance;
  private User currentUser = null;
  private BaseDao<User> userDao;
  private Activity currentContext;
  public List<Activity> activityList = new ArrayList<Activity>();

  @Override
  public void onCreate() {
    super.onCreate();
    if (getCurProcessName(getApplicationContext()).equals(getPackageName())) {
      ContentData.createMKDir();
      SDKInitializer.initialize(getApplicationContext());
      ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
          .memoryCacheExtraOptions(480, 800)
          .threadPoolSize(3)
          .threadPriority(Thread.NORM_PRIORITY - 2)
          .memoryCacheSize(2 * 1024 * 1024)
          .imageDownloader(new BaseImageDownloader(getApplicationContext(), 5 * 1000, 30 * 1000))
          .build();
      ImageLoader.getInstance().init(config);
      userDao = DaoManager.getUserDao(this);
      List<User> users = userDao.find();
      if (users != null && users.size() > 0) {
        currentUser = users.get(0);
        if (!StringTools.isEmp(currentUser.getPhoto()) && StringTools.isEmp(currentUser.getLocalPhoto())) {
          File file = new File(currentUser.getPhoto());
          if (file.exists()) {
            currentUser.setLocalPhoto(currentUser.getPhoto());
          }
        }
        if (!StringTools.isEmp(currentUser.getIdCardPhoto()) && StringTools.isEmp(currentUser.getLocalIdCardPhoto())) {
          File file = new File(currentUser.getIdCardPhoto());
          if (file.exists()) {
            currentUser.setLocalIdCardPhoto(currentUser.getIdCardPhoto());
          }
        }
        if (!StringTools.isEmp(currentUser.getDrivingIdPhoto()) && StringTools.isEmp(currentUser.getLocalDrivingIdPhoto())) {
          File file = new File(currentUser.getDrivingIdPhoto());
          if (file.exists()) {
            currentUser.setLocalDrivingIdPhoto(currentUser.getDrivingIdPhoto());
          }
        }
        if (!StringTools.isEmp(currentUser.getTradingIdPhoto()) && StringTools.isEmp(currentUser.getLocalTradingIdPhoto())) {
          File file = new File(currentUser.getTradingIdPhoto());
          if (file.exists()) {
            currentUser.setLocalTradingIdPhoto(currentUser.getTradingIdPhoto());
          }
        }
        if (!StringTools.isEmp(currentUser.getTravelIdPhoto()) && StringTools.isEmp(currentUser.getLocalTravelIdPhoto())) {
          File file = new File(currentUser.getTravelIdPhoto());
          if (file.exists()) {
            currentUser.setLocalTravelIdPhoto(currentUser.getTravelIdPhoto());
          }
        }
      }
      DBHelper dbHelper = new DBHelper(getApplicationContext());
      //得到操作数据库的实例
      SQLiteDatabase db = dbHelper.getReadableDatabase();
      // 调用查找书库代码并返回数据源
      Cursor cursor = db.rawQuery("select count(*)from log_info", null);
      //游标移到第一条记录准备获取数据
      cursor.moveToFirst();
      // 获取数据中的LONG类型数据
      Long count = cursor.getLong(0);
      if (count > 10000) {
        BaseDao<LogInfo> logInfoBaseDao = DaoManager.getLogInfoDao(getApplicationContext());
        int deleteCount = (int) (count - 10000);
        logInfoBaseDao.rawQuery("delete from log_info where _id < ?", new String[]{deleteCount + ""});
      }

      CrashHandler crashHandler = CrashHandler.getInstance();
      crashHandler.init(getApplicationContext());
      if (!BuildConfig.DEBUG) {
        TestinAgentConfig testinConfig = new TestinAgentConfig.Builder(this)
            .withDebugModel(true)       // Output the crash log in local if you open debug mode
            .withErrorActivity(true)    // Output the activity info in crash or error log
            .withCollectNDKCrash(false)  // Collect NDK crash or not if you use our NDK
            .withOpenCrash(true)        // Monitor crash if true
            .withReportOnlyWifi(false)   // Report data only on wifi mode
            .withReportOnBack(true)     // allow to report data when application in background
            .build();
        TestinAgent.init(testinConfig);
      }

      Fresco.initialize(this);
    }
  }


  public Activity getCurrentContext() {
    return currentContext;
  }

  public void setCurrentContext(Activity currentContext) {
    this.currentContext = currentContext;
  }


  /**
   * 判断启动的进程是否为主进程，防止因为百度定位的独立进程启动导致onCreate被执行两次
   *
   * @param context
   * @return String
   */
  private String getCurProcessName(Context context) {
    int pid = android.os.Process.myPid();
    ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
      if (appProcess.pid == pid) {
        return appProcess.processName;
      }
    }
    return null;
  }

  // 单例模式中获取唯一的ExitApplication 实例
  public static ZZQSApplication getInstance() {
    if (null == instance) {
      instance = new ZZQSApplication();
    }
    return instance;
  }

  public User getUser() {
    if (userDao == null) {
      userDao = DaoManager.getUserDao(this);
    }
    if (currentUser == null) {
      List<User> users = userDao.find();
      if (users != null && users.size() > 0) {
        currentUser = users.get(0);
      }
    }
    return currentUser;
  }

  public boolean registerUser(User user) {
    if (userDao == null)
      userDao = DaoManager.getUserDao(this);
    if (user == null)
      return false;
    userDao.insert(user);
    currentUser = user;
    return true;
  }

  public boolean unregisterUser(User user) {
    if (userDao == null)
      userDao = DaoManager.getUserDao(this);
    if (user == null)
      return false;
    currentUser = null;
    return true;
  }

  public boolean clearUser(Context context) {
    DaoManager.clearAll(context);
    currentUser = null;
    return true;
  }

  public boolean updateUser(User user) {
    if (userDao == null) {
      userDao = DaoManager.getUserDao(this);
    }
    if (user == null) {
      return false;
    }
    userDao.update(user);
    currentUser = user;
    return true;
  }

  public void addActivity(Activity activity) {
    if (activityList == null) {
      activityList = new ArrayList<>();
    }
    activityList.add(activity);
  }

  public void removeActivity(Activity activity) {
    activityList.remove(activity);
  }

  public void cleanAllActivity() {
    for (Activity activity : activityList) {
      activity.finish();
    }
  }

  public void finishUnMainActivity() {
    for (Activity activity : activityList) {
      if (activity.getClass() != MainActivity.class || activity.getClass() != com.zzqs.app_kc.z_kc.activity.MainActivity.class) {
        activity.finish();
      }
    }
  }

  public void CrashToLogin() {
    Toast.makeText(currentContext, R.string.prompt_date_err, Toast.LENGTH_SHORT).show();
    ZZQSApplication.getInstance().clearUser(this);
    ZZQSApplication.getInstance().cleanAllActivity();
    currentContext.startActivity(new Intent(currentContext, LoginActivity.class));
  }


  public static int getVersion() {
    return BuildConfig.VERSION_CODE;
  }

}
