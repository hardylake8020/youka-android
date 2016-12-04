package com.zzqs.app_kc.widgets;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by lance on 16/3/16.
 */
public class SafeProgressDialog extends ProgressDialog {
    private Activity parentActivity;

    public SafeProgressDialog(Context context) {
        super(context);
        parentActivity = (Activity) context;
    }

    @Override
    public void dismiss() {
        if (parentActivity != null && !parentActivity.isFinishing()) {
            super.dismiss();
        }
    }
}
