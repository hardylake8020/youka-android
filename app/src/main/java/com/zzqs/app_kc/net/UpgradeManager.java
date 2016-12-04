package com.zzqs.app_kc.net;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.activities.LoginActivity;
import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.entity.Upgrade;
import com.zzqs.app_kc.utils.DownloadManagerUtils;
import com.zzqs.app_kc.widgets.DialogView;

import java.io.File;


public class UpgradeManager {
    private static final String TAG = "UpgradeManager";
    public static int NO_NEW_VERSION = 200;

    public static UpgradeManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new UpgradeManager(context);
        }

        return mInstance;
    }

    static UpgradeManager mInstance;

    protected UpgradeManager(Context context) {
        mContext = context;
        mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        mDownloadManagerUtils = new DownloadManagerUtils(mDownloadManager);
    }

    public static final String UPGRADE_STATUS_BROADCAST = "UPGRADE_STATUS_BROADCAST";
    public static final String UPGRADE_STATUS = "UPGRADE_STATUS";
    public static final int UPGRADE_SUCCESS = 0;
    public static final int UPGRADE_FAIL = 1;

    Context mContext;
    DownloadManager mDownloadManager;
    DownloadManagerUtils mDownloadManagerUtils;
    DownloadCompleteReceiver mDownloadCompleteReceiver = new DownloadCompleteReceiver();
    String mDownloadUrl;
    long mDownloadId = 0;
    String mDownloadDir = "zzqs";
    String mDownloadFileName;
    String mNewApkName;

    OnUpgradeComplete mOnUpgradeComplete = new OnUpgradeComplete() {
        @Override
        public void onUpgradeSuccess() {
            Intent intent = new Intent(UPGRADE_STATUS_BROADCAST);
            intent.putExtra(UPGRADE_STATUS, UPGRADE_SUCCESS);

            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }

        @Override
        public void onUpgradeFail() {
            Intent intent = new Intent(UPGRADE_STATUS_BROADCAST);
            intent.putExtra(UPGRADE_STATUS, UPGRADE_FAIL);

            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }
    };

    boolean mUpgrading = false;

    public boolean isUpgrading() {
        return mUpgrading;
    }

    public UpgradeManager setUrl(String url) {
        if (isUpgrading()) {
            throw new IllegalStateException();
        }

        mDownloadUrl = url;
        return this;
    }

    public UpgradeManager setDir(String dir) {
        if (isUpgrading()) {
            throw new IllegalStateException();
        }

        mDownloadDir = dir;
        return this;
    }

    public UpgradeManager setFileName(String name) {
        if (isUpgrading()) {
            throw new IllegalStateException();
        }

        mDownloadFileName = name;
        return this;
    }

    public void beginUpgrade() {
        if (mUpgrading) return;

        mUpgrading = true;

        File folder = Environment.getExternalStoragePublicDirectory(mDownloadDir);
        if (!folder.exists() || !folder.isDirectory()) {
            folder.mkdirs();
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mDownloadUrl));
        request.setDestinationInExternalPublicDir(mDownloadDir, mDownloadFileName);
        request.setTitle(mContext.getString(R.string.app_name));
        request.setDescription(mContext.getString(R.string.prompt_dl_download_apk_notification_desc));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        }
        request.setVisibleInDownloadsUi(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
        }
        try {
            mDownloadId = mDownloadManager.enqueue(request);
            mContext.registerReceiver(mDownloadCompleteReceiver,
                    new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        } catch (Exception e) {
            Toast.makeText(mContext, R.string.prompt_download_app_failed,Toast.LENGTH_LONG).show();
        }
    }

    protected boolean install(String filePath) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        File file = new File(filePath);
        if (file != null && file.length() > 0 && file.exists() && file.isFile()) {
            i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setAction(Intent.ACTION_VIEW);
            mContext.startActivity(i);

            Log.d(TAG, "install new version apk");

            if (mOnUpgradeComplete != null) {
                mOnUpgradeComplete.onUpgradeSuccess();
            }
            mUpgrading = false;
            return true;
        }
        return false;
    }

    class DownloadCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            /**
             * get the id of download which have download success, if the id is my id and it's status is successful,
             * then install it
             **/
            long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (completeDownloadId == mDownloadId) {
                // if download successful, install apk
                if (mDownloadManagerUtils.getStatusById(mDownloadId) == DownloadManager.STATUS_SUCCESSFUL) {
                    String apkFilePath = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath())
                            .append(File.separator).append(mDownloadDir).append(File.separator)
                            .append(mDownloadFileName).toString();
                    Log.d(TAG, "download apk success");
                    if (!install(apkFilePath)) {
                        mUpgrading = false;
                        if (mOnUpgradeComplete != null) {
                            mOnUpgradeComplete.onUpgradeFail();
                        }
                    }
                } else {
                    mUpgrading = false;
                    if (mOnUpgradeComplete != null) {
                        mOnUpgradeComplete.onUpgradeFail();
                    }
                }

            }
        }
    }

    ;

    public interface OnUpgradeComplete {
        public void onUpgradeSuccess();

        public void onUpgradeFail();
    }

    public void upgrade(final int currentVersion, final Context context, final Handler handler) {
        RestAPI.getInstance(context).upgrade(new RestAPI.RestResponse() {
            @Override
            public void onSuccess(Object object) {
                if (object == null) {
                    return;
                }
                final Upgrade upgrade = (Upgrade) object;
                if (upgrade.getVersion() > currentVersion) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.prompt_dl_zzqs_upgrade)
                            .setMessage(R.string.prompt_dl_confirm_upgrade)
                            .setPositiveButton(R.string.prompt_dl_upgrade_yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mNewApkName = "zzqs" + upgrade.getVersion() + ".apk";
                                    String downloadUrl = upgrade.getUrl();
                                    UpgradeManager.this.setUrl(downloadUrl)
                                            .setDir(mDownloadDir)
                                            .setFileName(mNewApkName)
                                            .beginUpgrade();
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    handler.sendEmptyMessage(NO_NEW_VERSION);
                }
            }

            @Override
            public void onFailure(Object object) {
                if (object.toString().equals("disconnected")) {
                    DialogView.showChoiceDialog(ZZQSApplication.getInstance().getCurrentContext(), DialogView.SINGLE_BTN, context.getResources().getString(R.string.prompt_dl_other_equipment_login_title), context.getResources().getString(R.string.prompt_dl_other_equipment_login_msg), new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            ZZQSApplication.getInstance().clearUser(context);
                            ZZQSApplication.getInstance().cleanAllActivity();
                            context.startActivity(new Intent(context, LoginActivity.class));
                        }
                    });
                }
            }
        });
    }
}
