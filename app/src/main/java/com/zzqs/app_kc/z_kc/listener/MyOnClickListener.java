package com.zzqs.app_kc.z_kc.listener;

import android.view.View;

/**
 * Created by lance on 16/7/19.
 */
public abstract class MyOnClickListener implements View.OnClickListener {
  private static long lastClickTime;

  @Override
  public void onClick(View v) {
    if (isFastClick()) {
      return;
    }
    OnceOnClick(v);
  }

  public abstract void OnceOnClick(View view);

  public synchronized boolean isFastClick() {
    long time = System.currentTimeMillis();
    if (time - lastClickTime < 500) {
      return true;
    }
    lastClickTime = time;
    return false;
  }
}
