package com.zzqs.app_kc.utils;

import android.content.Context;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.app.ZZQSApplication;


/**
 * Created by lance on 16/3/14.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";
    private Context context;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private static CrashHandler INSTANCE = new CrashHandler();

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CrashHandler();
        }
        return INSTANCE;
    }

    public void init(Context ctx) {
        context = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.e(TAG,"error",ex);
        if (!handlerException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3 * 1000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error:", e);
                return;
            }
            ZZQSApplication.getInstance().cleanAllActivity();
            Process.killProcess(Process.myPid());
            System.exit(1);
        }
    }

    private boolean handlerException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(context, R.string.prompt_crash, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        return true;
    }
}
