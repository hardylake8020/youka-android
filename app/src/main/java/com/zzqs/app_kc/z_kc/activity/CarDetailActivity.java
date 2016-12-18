package com.zzqs.app_kc.z_kc.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.facebook.drawee.view.SimpleDraweeView;
import com.zzqs.app_kc.R;
import com.zzqs.app_kc.utils.CommonFiled;
import com.zzqs.app_kc.z_kc.entitiy.Car;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;

/**
 * Created by lance on 2016/12/6.
 */

public class CarDetailActivity extends BaseActivity {
  TextView tvLeft, tvTitle, tvRight, tvPlateNumber, tvCarType, tvOilCard, tvDriverName, tvDriverPhone, tvCarStatus, tvCarLocation;
  SimpleDraweeView sdCarPhoto;
  private Car car;

  @Override
  public void initVariables() {
    car = getIntent().getParcelableExtra(Car.CAR);
    if (car == null) {
      finish();
    }
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
    tvTitle.setText(R.string.car_detail);
    tvRight = (TextView) findViewById(R.id.head_right);
    tvRight.setText(R.string.editor_car_info);
    tvRight.setVisibility(View.VISIBLE);
    tvRight.setOnClickListener(new MyOnClickListener() {
      @Override
      public void OnceOnClick(View view) {

      }
    });
    tvPlateNumber = (TextView) findViewById(R.id.tvPlateNumber);
    tvPlateNumber.setText(car.getTruck_number());
    tvCarType = (TextView) findViewById(R.id.tvCarType);
    tvCarType.setText(car.getTruck_type());
    tvOilCard = (TextView) findViewById(R.id.tvOilCard);
    tvOilCard.setText(car.getOil_card());
    tvDriverName = (TextView) findViewById(R.id.tvDriverName);
    tvDriverName.setText(car.getDriver_name());
    tvDriverPhone = (TextView) findViewById(R.id.tvDriverPhone);
    tvDriverPhone.setText(car.getDriver_phone());
    tvCarStatus = (TextView) findViewById(R.id.tvCarStatus);
    if (car.getStatus().equals(Car.UN_USAGE)) {
      tvCarStatus.setText(R.string.un_transport);
    } else if (car.getStatus().equals(Car.USAGE)) {
      tvCarStatus.setText(R.string.transporting);
    }
    tvCarLocation = (TextView) findViewById(R.id.tvCarLocation);

    sdCarPhoto = (SimpleDraweeView) findViewById(R.id.sdCarPhoto);
    if (!TextUtils.isEmpty(car.getCar_photo())) {
      Uri uri = Uri.parse(CommonFiled.QINIU_ZOOM + car.getCar_photo());
      sdCarPhoto.setImageURI(uri);
    }
    getCarLocationInfo();
  }

  private static final int NO_LOCATION_RESULT = 1;
  private static final int GET_LOCATION_RESULT = 2;
  private Handler locationInfoHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      if (msg.what == NO_LOCATION_RESULT) {
        tvCarLocation.setText(R.string.no_location_info);
      } else if (msg.what == GET_LOCATION_RESULT) {
        System.out.println(msg.obj.toString());
        tvCarLocation.setText(msg.obj.toString());
        tvCarLocation.setOnClickListener(new MyOnClickListener() {
          @Override
          public void OnceOnClick(View view) {

          }
        });
      }
//      mSearch.destroy();
    }
  };

  private GeoCoder mSearch;

  private void getCarLocationInfo() {
    mSearch = GeoCoder.newInstance();
    OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
      public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
          //没有检索到结果
        }
        //获取地理编码结果
        System.out.println("onGetGeoCodeResult:" + result.getAddress());
      }

      @Override
      public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
          //没有找到检索结果
          locationInfoHandler.sendEmptyMessage(NO_LOCATION_RESULT);
        }
        //获取反向地理编码结果
        System.out.println("onGetReverseGeoCodeResult:" + result.getLocation().toString() + "--" + result.getAddress() + "--" + result.getAddressDetail().city + "--" + result.getAddressDetail().district);
        String info = result.getAddressDetail().city + result.getAddressDetail().district;
        Message msg = locationInfoHandler.obtainMessage();
        msg.what = GET_LOCATION_RESULT;
        msg.obj = info;
        locationInfoHandler.sendMessage(msg);
      }
    };
    mSearch.setOnGetGeoCodeResultListener(listener);
    LatLng latLng = new LatLng(car.getLocation().get(1), car.getLocation().get(0));
    mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
  }

  @Override
  public void loadData() {

  }
}
