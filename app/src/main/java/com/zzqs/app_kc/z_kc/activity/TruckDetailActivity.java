package com.zzqs.app_kc.z_kc.activity;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zzqs.app_kc.R;
import com.zzqs.app_kc.utils.CommonFiled;
import com.zzqs.app_kc.utils.CommonTools;
import com.zzqs.app_kc.utils.ImageUtil;
import com.zzqs.app_kc.z_kc.entitiy.Driver;
import com.zzqs.app_kc.z_kc.entitiy.ErrorInfo;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;
import com.zzqs.app_kc.z_kc.network.DriverApiImpl;

import rx.Subscriber;

/**
 * Created by lance on 2016/12/6.
 */

public class TruckDetailActivity extends BaseActivity {
    TextView tvLeft, tvTitle, tvRight, tvPlateNumber, tvCarType, tvDriverName, tvDriverPhone;
    SimpleDraweeView sdCarPhoto;
    private Driver driver;
    private ImageView ivIdCard, ivBankCard, ivDriverCard, ivTravelCard, ivLoadingCar, ivDriverPhoto, ivCarPhoto, ivCarNumberPhoto;

    @Override
    public void initVariables() {
        driver = getIntent().getParcelableExtra(Driver.DRIVER);
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.z_kc_act_car_detail);
        tvLeft = (TextView) findViewById(R.id.head_back);
        tvLeft.setText("");
        tvLeft.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                finish();
            }
        });
        tvTitle = (TextView) findViewById(R.id.head_title);
        tvTitle.setText(R.string.driver_detail);
        tvRight = (TextView) findViewById(R.id.head_right);
        tvRight.setText(R.string.add);
        if (driver != null) {
            if (driver.getType().equals(Driver.OWN)) {
                tvRight.setVisibility(View.INVISIBLE);
            } else if (driver.getType().equals(Driver.SEARCHE)) {
                tvRight.setVisibility(View.VISIBLE);
            }
        }
        tvRight.setOnClickListener(new MyOnClickListener() {//添加司机
            @Override
            public void OnceOnClick(View view) {
                addDriverToOwner();
            }
        });
        tvPlateNumber = (TextView) findViewById(R.id.tvPlateNumber);
        tvCarType = (TextView) findViewById(R.id.tvCarType);
        tvDriverName = (TextView) findViewById(R.id.tvDriverName);
        tvDriverPhone = (TextView) findViewById(R.id.tvDriverPhone);
        sdCarPhoto = (SimpleDraweeView) findViewById(R.id.sdCarPhoto);
        ivIdCard = (ImageView) findViewById(R.id.ivIdCard);
        ivBankCard = (ImageView) findViewById(R.id.ivBankCard);
        ivDriverCard = (ImageView) findViewById(R.id.ivDriverCard);
        ivTravelCard = (ImageView) findViewById(R.id.ivTravelCard);
        ivLoadingCar = (ImageView) findViewById(R.id.ivLoadingCar);
        ivDriverPhoto = (ImageView) findViewById(R.id.ivDriverPhoto);
        ivCarPhoto = (ImageView) findViewById(R.id.ivCarPhoto);
        ivCarNumberPhoto = (ImageView) findViewById(R.id.ivCarNumberPhoto);

    }


    @Override
    public void loadData() {
        initViewData();
    }

    private void initViewData() {
        tvPlateNumber.setText(TextUtils.isEmpty(driver.getTruck_number()) ? getString(R.string.un_write) : driver.getTruck_number());
        tvDriverName.setText(TextUtils.isEmpty(driver.getNickname()) ? getString(R.string.un_write) : driver.getNickname());
        tvDriverPhone.setText(TextUtils.isEmpty(driver.getUsername()) ? getString(R.string.un_write) : driver.getUsername());
        tvCarType.setText(TextUtils.isEmpty(driver.getTruck_type()) ? getString(R.string.un_write) : driver.getTruck_type());
        if (!TextUtils.isEmpty(driver.getPhoto())) {
            Uri uri = Uri.parse(CommonFiled.QINIU_ZOOM + driver.getPhoto());
            sdCarPhoto.setImageURI(uri);
            ImageUtil.showImage(driver.getPhoto(), ivDriverPhoto, true);

        }
        if (!TextUtils.isEmpty(driver.getId_card_photo())) {
            ImageUtil.showImage(driver.getId_card_photo(), ivIdCard, true);
        }
        if (!TextUtils.isEmpty(driver.getBank_number_photo())) {
            ImageUtil.showImage(driver.getBank_number_photo(), ivBankCard, true);
        }
        if (!TextUtils.isEmpty(driver.getDriving_id_photo())) {
            ImageUtil.showImage(driver.getDriving_id_photo(), ivDriverCard, true);
        }
        if (!TextUtils.isEmpty(driver.getTravel_id_photo())) {
            ImageUtil.showImage(driver.getTravel_id_photo(), ivTravelCard, true);
        }
        if (!TextUtils.isEmpty(driver.getTruck_photo())) {
            ImageUtil.showImage(driver.getTruck_photo(), ivCarPhoto, true);
        }
        if (!TextUtils.isEmpty(driver.getPlate_photo())) {
            ImageUtil.showImage(driver.getPlate_photo(), ivCarNumberPhoto, true);
        }
        if (!TextUtils.isEmpty(driver.getTruck_list_photo())) {
            ImageUtil.showImage(driver.getTruck_list_photo(), ivLoadingCar, true);
        }
    }

    private void addDriverToOwner() {
        safePd.setMessage(getString(R.string.adding));
        safePd.show();
        DriverApiImpl.getDriverApiImpl().addDriversToOwner(CommonTools.getToken(this), driver.getDriver_id(), new Subscriber<ErrorInfo>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                safePd.dismiss();
            }

            @Override
            public void onNext(ErrorInfo errorInfo) {
                safePd.dismiss();
                if (errorInfo.getType().equals(ErrorInfo.SUCCESS)) {
                    showToast(getString(R.string.add_success), Toast.LENGTH_LONG);
                    finish();
                } else {
                    Toast.makeText(mContext, errorInfo.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
