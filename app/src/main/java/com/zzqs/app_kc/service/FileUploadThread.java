package com.zzqs.app_kc.service;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.zzqs.app_kc.db.DaoManager;
import com.zzqs.app_kc.db.hibernate.dao.BaseDao;
import com.zzqs.app_kc.entity.EventFile;
import com.zzqs.app_kc.entity.LogInfo;
import com.zzqs.app_kc.net.RestAPI;
import com.zzqs.app_kc.utils.CommonFiled;

import org.json.JSONObject;

import java.io.File;
import java.util.List;

/**
 * Created by lance on 15/9/17.
 */
public class FileUploadThread extends Thread {
    private Context context;
    private BaseDao<EventFile> eventFileDao;
    private List<EventFile> fileList;
    private UploadManager uploadManager;
    private Handler uploadHandler;
    private RestAPI restAPI;
    private BaseDao<LogInfo> logInfoDao;
    private static FileUploadThread fileUploadThread;

    private FileUploadThread(Context context) {
        this.context = context;
    }

    public static FileUploadThread getInstance(Context context) {
        if (fileUploadThread == null) {
            fileUploadThread = new FileUploadThread(context);
        }
        return fileUploadThread;
    }

    @Override
    public void run() {
        logInfoDao = DaoManager.getLogInfoDao(context);
        LogInfo logInfo = new LogInfo();
        logInfo.setType(LogInfo.TYPE_OF_NORMAL);
        logInfo.setContent("文件上传线程开始运行");
        logInfo.setTime(System.currentTimeMillis());
        logInfoDao.insert(logInfo);
        uploadManager = new UploadManager();
        eventFileDao = DaoManager.getEventFileDao(context);
        restAPI = RestAPI.getInstance(context);
        Looper.prepare();
        uploadHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                fileList = eventFileDao.find(null, "status=? and file_path<>? and event_id>?", new String[]{EventFile.STATUS_NEW + "", EventFile.ZZQS_CONFIG_PHOTO, 0 + ""}, null, null, null, 1 + "");
                if (fileList.size() > 0) {
                    final EventFile eventFile = fileList.get(0);
                    final File file;
                    if (!TextUtils.isEmpty(eventFile.getBigFilePath())) {
                        file = new File(eventFile.getBigFilePath());
                    } else {
                        file = new File(eventFile.getFilePath());
                    }
                    if (file.exists()) {
                        final String key = eventFile.getFilePath();
                        String keyType = key.substring(key.length() - 4, key.length());
                        if (keyType.equals(".amr")) {
                            LogInfo logInfo = new LogInfo();
                            logInfo.setType(LogInfo.TYPE_OF_NORMAL);
                            logInfo.setContent("发现一个音频文件需要上传");
                            logInfo.setTime(System.currentTimeMillis());
                            logInfoDao.insert(logInfo);
                            String voiceKey = key.substring(0, key.lastIndexOf('.')) + ".mp3";
                            getQiniuToken(voiceKey, eventFile, file);
                        } else {
                            LogInfo logInfo = new LogInfo();
                            logInfo.setType(LogInfo.TYPE_OF_NORMAL);
                            logInfo.setContent("发现一个图片文件需要上传");
                            logInfo.setTime(System.currentTimeMillis());
                            logInfoDao.insert(logInfo);
                            getQiniuToken(null, eventFile, file);
                        }
                    } else {
                        if (!eventFile.getFilePath().equals(EventFile.ZZQS_CONFIG_PHOTO)) {
                            try {
                                LogInfo logInfo = new LogInfo();
                                logInfo.setType(LogInfo.TYPE_OF_ERR);
                                logInfo.setContent("发现有不存在的文件存在于数据库中，名字为" + eventFile.getFilePath());
                                logInfo.setTime(System.currentTimeMillis());
                                logInfoDao.insert(logInfo);
                                eventFile.setStatus(EventFile.STATUS_UNFIND);
                                eventFileDao.update(eventFile);
                                sleep(CommonFiled.FILE_UPLOAD_FILE_NOT_EXIST);
                                Message obtainMessage = uploadHandler.obtainMessage();
                                obtainMessage.what = 1;
                                obtainMessage.sendToTarget();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    try {
                        sleep(CommonFiled.FILE_UPLOAD_FILE_NUMBER_IS_0);
                        Message obtainMessage = uploadHandler.obtainMessage();
                        obtainMessage.what = 1;
                        obtainMessage.sendToTarget();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Message msg = uploadHandler.obtainMessage();
        msg.what = 1;
        msg.sendToTarget();
        Looper.loop();
    }

    private void getQiniuToken(final String voiceKey, final EventFile eventFile, final File file) {
        if (null != voiceKey) {
            restAPI.getQiniuVoiceToken(voiceKey, new RestAPI.RestResponse() {
                @Override
                public void onSuccess(Object object) {
                    String token = (String) object;
                    uploadFileToQiniu(eventFile, token, file);
                }

                @Override
                public void onFailure(Object object) {
                    try {
                        LogInfo logInfo = new LogInfo();
                        logInfo.setType(LogInfo.TYPE_OF_ERR);
                        logInfo.setContent("获取音频文件的七牛token失败了，等待5秒后继续获取" + "\n" + object.toString());
                        logInfo.setTime(System.currentTimeMillis());
                        logInfoDao.insert(logInfo);
                        sleep(CommonFiled.FILE_UPLOAD_GET_QINIU_TOKEN_FAILED);
                        getQiniuToken(voiceKey, eventFile, file);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            restAPI.getQiniuImgToken(new RestAPI.RestResponse() {
                @Override
                public void onSuccess(Object object) {
                    String token = (String) object;
                    uploadFileToQiniu(eventFile, token, file);
                }

                @Override
                public void onFailure(Object object) {
                    try {
                        LogInfo logInfo = new LogInfo();
                        logInfo.setType(LogInfo.TYPE_OF_ERR);
                        logInfo.setContent("获取音频文件的七牛token失败了，等待5秒后继续获取" + "\n" + object.toString());
                        logInfo.setTime(System.currentTimeMillis());
                        logInfoDao.insert(logInfo);
                        sleep(CommonFiled.FILE_UPLOAD_GET_QINIU_TOKEN_FAILED);
                        getQiniuToken(voiceKey, eventFile, file);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    private void uploadFileToQiniu(final EventFile eventFile, final String token, final File file) {
        String filePath;
        if (!TextUtils.isEmpty(eventFile.getBigFilePath())) {
            filePath = eventFile.getBigFilePath();
            System.out.println("uploadFileToQiniu:" + filePath);
        } else {
            filePath = eventFile.getFilePath();
        }
        uploadManager.put(file, filePath, token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo responseInfo, JSONObject jsonObject) {
                if (responseInfo.isOK()) {
                    LogInfo logInfo = new LogInfo();
                    logInfo.setType(LogInfo.TYPE_OF_NORMAL);
                    logInfo.setContent("向七牛传输文件成功");
                    logInfo.setTime(System.currentTimeMillis());
                    logInfoDao.insert(logInfo);
                    eventFile.setStatus(EventFile.STATUS_COMMIT);
                    eventFileDao.update(eventFile);
                    Message msg = uploadHandler.obtainMessage();
                    msg.what = 1;
                    msg.sendToTarget();
                } else {
                    Log.e("upload failed", responseInfo.toString());
                    try {
                        LogInfo logInfo = new LogInfo();
                        logInfo.setType(LogInfo.TYPE_OF_ERR);
                        logInfo.setContent("向七牛传输文件失败:\n" + responseInfo.toString() + "\n" + eventFile.toString());
                        logInfo.setTime(System.currentTimeMillis());
                        logInfoDao.insert(logInfo);
                        sleep(CommonFiled.FILE_UPLOAD_FAILED);
                        uploadFileToQiniu(eventFile, token, file);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, null);
    }

}
