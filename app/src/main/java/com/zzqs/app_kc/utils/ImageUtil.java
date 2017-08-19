package com.zzqs.app_kc.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zzqs.app_kc.R;
import com.zzqs.app_kc.net.RestAPI;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtil {
    public static ImageLoader imageLoader = ImageLoader.getInstance();


    public static void showImage(String path, ImageView imageView, boolean isServer) {
        if (isServer) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.ic_loading)
                    .showImageOnFail(R.drawable.ic_default_head)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            imageLoader.displayImage(CommonFiled.QINIU_ZOOM + path, imageView, options);
        } else {
            imageLoader.displayImage(path, imageView);
        }
    }

    public static void showImage(String path, ImageView imageView, DisplayImageOptions options) {
        imageLoader.displayImage(path, imageView, options);
    }

    public static void showImage(String path, ImageView imageView) {
        if (!TextUtils.isEmpty(path)) {
            path = "file://" + path;
        } else {
            path = "";
        }
        imageLoader.displayImage(path, imageView);
    }

    /**
     * 质量压缩
     */
    private static Bitmap compressImage(Bitmap image, int maxSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 90;
        image.compress(Bitmap.CompressFormat.JPEG, options, baos);
        while (baos.toByteArray().length / 1024 > maxSize && options > 0) { // 循环判断如果压缩后图片是否大于maxSize,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        return BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length);
    }

    /**
     * 图片按比例大小压缩 根据图片
     */
    public static Bitmap comp(Bitmap image, int maxSize, int ww, int hh) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = newOpts.outWidth / ww;
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = newOpts.outHeight / hh;
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        if (maxSize >= 100) {
            newOpts.inPreferredConfig = Config.ARGB_8888;
        } else {
            newOpts.inPreferredConfig = Config.RGB_565;
        }
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap, maxSize);// 压缩好比例大小后再进行质量压缩
    }

    public static Bitmap comp(String srcPath, int maxSize, int ww, int hh) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = newOpts.outWidth / ww;
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = newOpts.outHeight / hh;
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例

        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        if (maxSize >= 50) {
            newOpts.inPreferredConfig = Config.ARGB_8888;
        } else {
            newOpts.inPreferredConfig = Config.RGB_565;
        }
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap, maxSize);
    }

    /**
     * 保存压缩的图片
     *
     * @param bitmap  需要保存的图片
     * @param path    图片的保存完整路径
     * @param quality 图片质量
     * @return true 保存成功 ，false 否则保存失败
     */
    public static boolean saveCompressBitmap(Bitmap bitmap, String path, int quality) {
        File f = new File(path);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(f);
            return bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        return degree;
    }

    /*
     * 旋转图片
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        return Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 图片下载
     */
    public static final int DOWNLOAD_SUCCESS = 200;
    public static final int DOWNLOAD_FAILURE = 300;

    public interface ImageUtilCallback {
        public void onSuccess(Object object);

        public void onFailure(Object object);
    }

    public static void downloadBitmap(Context context, final String url, final String fileName, final ImageUtilCallback callback) {
        AsyncHttpClient client = RestAPI.getInstance(context).getClient();
        client.get(url, new BinaryHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] binaryData) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(binaryData, 0, binaryData.length);
                File f = new File(fileName);
                if (!f.exists()) {
                    try {
                        f.createNewFile();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        FileOutputStream out = new FileOutputStream(f);
                        out.write(baos.toByteArray());
                        out.flush();
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                }
                callback.onSuccess(DOWNLOAD_SUCCESS);
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] binaryData, Throwable error) {
                Log.d("imageutil", "download image is Failure");
                callback.onFailure(DOWNLOAD_FAILURE);
            }
        });
    }
}
