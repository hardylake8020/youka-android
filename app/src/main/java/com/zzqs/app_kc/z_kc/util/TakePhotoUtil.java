package com.zzqs.app_kc.z_kc.util;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.app.ContentData;
import com.zzqs.app_kc.z_kc.activity.BaseActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ray on 2017/3/23.
 * Class name : TakePhotoUtil
 * Description :
 */
public class TakePhotoUtil {
    /**
     * 拍照
     */
    public static File takePhoto(BaseActivity activity, String name, boolean checkConnect) {
        PackageManager packageManager = activity.getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) == false) {
            activity.showToast(activity.getString(R.string.no_camera), Toast.LENGTH_SHORT);
            return null;
        }
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            activity.showToast(activity.getString(R.string.prompt_no_sdcard), Toast.LENGTH_SHORT);
            return null;
        }
        File tmpFile = null;

        try {
            tmpFile = TakePhotoUtil.createImageFile(name);
            //tmpFile = createImageFile(type);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tmpFile;
    }

    public static void openCamera(BaseActivity activity, File file, int type) {
        if (file != null) {
            Uri uri = Uri.fromFile(file);
            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePhotoIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            activity.startActivityForResult(takePhotoIntent, type);
        }
    }

    public static File createImageFile(String name) throws IOException {//创建文件
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        File storageDir = new File(ContentData.BASE_PICS);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        String photoName = name + timeStamp;
        File file = new File(ContentData.BASE_PICS + "/" + photoName + ".jpg");
        return file;
    }
}
