package com.zzqs.app_kc.z_kc.activity;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.z_kc.adapter.PhotoAdapter;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ray on 2017/3/17.
 * Class name : TakePhotoActivity
 * Description :选择照片页面
 */
public class TakePhotoActivity extends BaseActivity implements PopupWindow.OnDismissListener {
    private TextView tvPhotoType, head_back, head_title, head_right;
    private GridView gvPhotos;
    private String photoType;
    private PhotoAdapter photoAdapter;
    private List<String> photos;
    private PopupWindow popupWindow;
    private int navigationHeight;

    @Override
    public void initVariables() {
        photoType = getIntent().getStringExtra("photoType");
        photos = new ArrayList<>();
        photoAdapter = new PhotoAdapter(this, photos);
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        navigationHeight = getResources().getDimensionPixelSize(resourceId);

    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.z_kc_act_take_photo);
        tvPhotoType = (TextView) findViewById(R.id.tvPhotoType);
        head_back = (TextView) findViewById(R.id.head_back);
        head_title = (TextView) findViewById(R.id.head_title);
        head_right = (TextView) findViewById(R.id.head_right);
        gvPhotos = (GridView) findViewById(R.id.gvPhotos);
        gvPhotos.setAdapter(photoAdapter);
        head_back.setText(R.string.cancel);
        head_back.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                finish();
            }
        });
        head_right.setText(R.string.save);
        head_right.setVisibility(View.VISIBLE);
        head_right.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {

            }
        });
        head_title.setText(photoType);
        tvPhotoType.setText(getString(R.string.photo_type, photoType));
        gvPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openPopupWindow(view);
            }
        });
    }

    @Override
    public void loadData() {

    }

    private void openPopupWindow(View v) {
        //防止重复按按钮
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }
        //设置PopupWindow的View
        View view = LayoutInflater.from(this).inflate(R.layout.view_popupwindow, null);
        popupWindow = new PopupWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        //设置背景,这个没什么效果，不添加会报错
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //设置点击弹窗外隐藏自身
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        //设置动画
        popupWindow.setAnimationStyle(R.style.PopupWindow);
        //设置位置
        popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, navigationHeight);
        //设置消失监听
        popupWindow.setOnDismissListener(this);
        //设置PopupWindow的View点击事件
        setOnPopupViewClick(view);
        //设置背景色
        setBackgroundAlpha(0.5f);
    }

    //设置屏幕背景透明效果
    public void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = alpha;
        if (alpha == 1) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        getWindow().setAttributes(lp);
    }

    private void setOnPopupViewClick(View view) {
        TextView tv_take_photo, tv_pick_photo, tv_cancel;
        tv_take_photo = (TextView) view.findViewById(R.id.tv_take_photo);
        tv_pick_photo = (TextView) view.findViewById(R.id.tv_pick_photo);
        tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        tv_take_photo.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    Toast.makeText(TakePhotoActivity.this, R.string.prompt_no_sdcard, Toast.LENGTH_LONG).show();
                    return;
                }
                popupWindow.dismiss();
            }
        });
        tv_pick_photo.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    Toast.makeText(TakePhotoActivity.this, R.string.prompt_no_sdcard, Toast.LENGTH_LONG).show();
                    return;
                }
                popupWindow.dismiss();
            }
        });
        tv_cancel.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                popupWindow.dismiss();
            }
        });
    }

    @Override
    public void onDismiss() {
        setBackgroundAlpha(1);
    }
}
