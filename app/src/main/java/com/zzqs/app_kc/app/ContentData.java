package com.zzqs.app_kc.app;

import android.os.Environment;

import java.io.File;


public class ContentData {

    private static final String BASE = Environment.getExternalStorageDirectory() + "/zzqs_kc";

    public static final String BASE_DIR = BASE + "/info";
    public static final String BASE_PICS = BASE + "/pics";
    public static final String BASE_LOG = BASE + "/log";
    public static final String BASE_FIN = BASE + "/finger";
    public static final String BASE_SOUNDS = BASE + "/sounds";
    public static final String BASE_BIG_PICS = BASE + "/big_pics";

    /**
     * 创建所有文件夹
     */
    public static void createMKDir() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File f = new File(BASE_DIR);
            if (!f.exists()) {
                f.mkdirs();
            }
            File f1 = new File(BASE_LOG);
            if (!f1.exists()) {
                f1.mkdirs();
            }
            File f2 = new File(BASE_PICS);
            if (!f2.exists()) {
                f2.mkdirs();
            }
            File f3 = new File(BASE_FIN);
            if (!f3.exists()) {
                f3.mkdirs();
            }
            File f4 = new File(BASE_SOUNDS);
            if (!f4.exists()) {
                f4.mkdir();
            }

            File f5 = new File(BASE_BIG_PICS);
            if (!f5.exists()) {
                f5.mkdir();
            }
        }

    }
}
