package com.zzqs.app_kc.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.service.HeartbeatService;
import com.zzqs.app_kc.widgets.DialogView;

/**
 * Created by lance on 15/9/18.
 */
public class UnOpenReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.zhuzhuqs.android.GPS_UNOPEN")) {
            if (null != ZZQSApplication.getInstance().getCurrentContext()) {
                if (!HeartbeatService.isDialogOpen) {
                    HeartbeatService.isDialogOpen = true;
                    DialogView.showChoiceDialog(ZZQSApplication.getInstance().getCurrentContext(), DialogView.GPS, context.getString(R.string.prompt_dl_title_1), context.getString(R.string.prompt_must_open_GPS), new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            HeartbeatService.isDialogOpen = false;
                        }
                    });
                }
            }
        } else if (intent.getAction().equals("com.zhuzhuqs.android.NET_WORK_UNOPEN")) {
            if (ZZQSApplication.getInstance().getCurrentContext() != null)
                Toast.makeText(ZZQSApplication.getInstance().getCurrentContext(), context.getString(R.string.prompt_must_open_network), Toast.LENGTH_LONG).show();
        }
    }
}
