package com.zzqs.app_kc.widgets;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.zzqs.app_kc.R;
import com.zzqs.app_kc.utils.CommonFiled;
import com.zzqs.app_kc.utils.ScreenUtil;
import com.zzqs.app_kc.utils.StringTools;
import com.zzqs.app_kc.widgets.photoview.PhotoView;
import com.zzqs.app_kc.widgets.photoview.PhotoViewAttacher;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lance on 15/4/23.
 */
public class DialogView {
  public static final int DELETE = 99;
  public static final int ACCEPT = 100;
  public static final int CANCEL = 101;
  public static final int DEFAULT = 102;

  /**
   * 启动一个显示放大图片的dialog
   *
   * @param context 上下文对象
   * @param path    文件路径
   * @param handler 删除的回调
   */
  public static void showBigImageDialog(final Context context, final String path, final String number, final String title, final Handler handler) {
    if (StringTools.isEmp(path)) {
      return;
    }
    final Dialog dialog = new Dialog(context, R.style.Dialog_Fullscreen);
    dialog.setContentView(R.layout.big_image_dialog);
    dialog.findViewById(R.id.ll).setBackgroundResource(R.color.black);
    PhotoView bigImage = (PhotoView) dialog.findViewById(R.id.bigImage);
    TextView delete = (TextView) dialog.findViewById(R.id.head_right);
    delete.setText("删除");
    TextView titleView = (TextView) dialog.findViewById(R.id.head_title);
    titleView.setText(title);
    Button default_plate = (Button) dialog.findViewById(R.id.default_plate);
    TextView close = (TextView) dialog.findViewById(R.id.head_back);
    if (!StringTools.isEmp(number)) {
      default_plate.setVisibility(View.VISIBLE);
    } else {
      default_plate.setVisibility(View.GONE);
    }
    bigImage.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
      @Override
      public void onPhotoTap(View view, float x, float y) {
        dialog.dismiss();
      }
    });
    close.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        dialog.dismiss();
      }
    });
    if (handler == null) {
      delete.setVisibility(View.GONE);
    } else {
      delete.setVisibility(View.VISIBLE);
      delete.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Message msg = handler.obtainMessage();
          msg.what = DELETE;
          msg.obj = path;
          handler.sendMessage(msg);
          dialog.dismiss();
        }
      });
      default_plate.setOnClickListener(new View.OnClickListener() {//设为默认车牌
        @Override
        public void onClick(View view) {
          Message msg = handler.obtainMessage();
          msg.what = DEFAULT;
          handler.sendMessage(msg);
          dialog.dismiss();
        }
      });
    }
    if (path.contains(CommonFiled.QINIU_ZOOM)) {
      DisplayImageOptions options = new DisplayImageOptions.Builder()
          .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
          .bitmapConfig(Bitmap.Config.RGB_565)
          .build();
      ImageLoader.getInstance().displayImage(path, bigImage, options);
    } else {
      File file = new File(path);
      if (!file.exists()) {
        return;
      }
      Uri uri = Uri.fromFile(file);
      ImageLoader.getInstance().displayImage(uri.toString(), bigImage);
    }
    dialog.show();
  }

  /**
   * @param context 上下文对象
   * @param type    显示类型
   * @param handler 回调
   */

  public static final int NETWORK = 1;
  public static final int GPS = 2;
  public static final int NORMAL = 3;
  public static final int SINGLE_BTN = 4;

  public static void showChoiceDialog(final Context context, final int type, String title, String message, final Handler handler) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setCancelable(false);
    builder.setTitle(title);
    builder.setMessage(message);

    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        switch (type) {
          case NETWORK:
            context.startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
            break;
          case GPS:
            context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            break;
          default:
            break;
        }
        if (handler != null)
          handler.sendEmptyMessage(ACCEPT);
      }
    });
    if (type != SINGLE_BTN && type != GPS) {
      builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int i) {
          dialog.dismiss();
          if (null != handler) {
            handler.sendEmptyMessage(CANCEL);
          }
        }
      });
    }
    if (context != null) {
      builder.show();
    }
  }

  /**
   * 获取一个带输入框的弹窗
   */
  public static final int NORMAL_INPUT = 4;
  public static final int INPUT_ONLY_NUMBER = 5;
  public static final int INPUT_MOBILE_PHONE = 6;
  public static final int INPUT_PLATE_NUMBER = 7;

  public static void inputInfo(final Context context, final int type, String hint, final Handler handler) {
    final EditText inputServer = new EditText(context);
    if (type == INPUT_ONLY_NUMBER || type == INPUT_MOBILE_PHONE) {
      inputServer.setInputType(InputType.TYPE_CLASS_NUMBER);
    }
    inputServer.setHint(hint);
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
                     public void run() {
                       InputMethodManager inputManager = (InputMethodManager) inputServer.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                       inputManager.showSoftInput(inputServer, 0);
                     }
                   },
        200);
    builder.setTitle(R.string.prompt_dl_input_info).setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
        .setNegativeButton(R.string.view_tv_cancel, null);
    builder.setPositiveButton(R.string.view_bt_ok, new DialogInterface.OnClickListener() {

      public void onClick(DialogInterface dialog, int which) {
        String content = inputServer.getText().toString();
        if (type == INPUT_MOBILE_PHONE) {
          if (!StringTools.isMobileNumber(content)) {
            Toast.makeText(context, R.string.prompt_phone_is_error, Toast.LENGTH_SHORT).show();
            return;
          }
        } else if (type == INPUT_PLATE_NUMBER) {
          if (StringTools.isEmp(content)) {
            Toast.makeText(context, R.string.prompt_no_input_plate, Toast.LENGTH_SHORT).show();
            return;
          } else if (!StringTools.isCarnumberNO(content)) {
            Toast.makeText(context, R.string.prompt_input_no_plate, Toast.LENGTH_SHORT).show();
            return;
          }
        }
        Message message = handler.obtainMessage();
        message.what = ACCEPT;
        message.obj = content;
        handler.sendMessage(message);
        dialog.dismiss();
      }
    });
    if (context != null) {
      builder.show();
    }
  }

  public static void showConfirmDialog(Context context, String title, String message, final Handler handler) {
    final Dialog dialog = new AlertDialog.Builder(context).create();
    dialog.show();
    dialog.setCancelable(false);

    Window window = dialog.getWindow();
    window.setContentView(R.layout.prompt_dialog);
    window.setBackgroundDrawable(new BitmapDrawable());
    WindowManager.LayoutParams params = window.getAttributes();
    params.width = (int) (ScreenUtil.getScreenWidth(context) * 0.8);
    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
    params.gravity = Gravity.CENTER_HORIZONTAL;
    window.setAttributes(params);

    View view = LayoutInflater.from(context).inflate(R.layout.prompt_dialog, null);
    TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
    TextView tvMsg = (TextView) view.findViewById(R.id.tvMsg);
    TextView btLeft = (TextView) view.findViewById(R.id.tvLeft);
    TextView btRight = (TextView) view.findViewById(R.id.tvRight);
    if (!TextUtils.isEmpty(title)) {
      tvTitle.setText(title + "");
    }
    tvMsg.setText(message + "");
    dialog.setContentView(view);
    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
      @Override
      public void onDismiss(DialogInterface dialog) {
      }
    });
    btLeft.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (handler != null) {
          handler.sendEmptyMessage(CANCEL);
        }
        dialog.dismiss();
      }
    });
    btRight.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (handler != null) {
          handler.sendEmptyMessage(ACCEPT);
        }
        dialog.dismiss();
      }
    });
  }

}
