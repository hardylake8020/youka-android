package com.zzqs.app_kc.z_kc.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import com.zzqs.app_kc.utils.CommonTools;
import com.zzqs.app_kc.z_kc.entitiy.ErrorInfo;
import com.zzqs.app_kc.z_kc.entitiy.Truck;
import com.zzqs.app_kc.z_kc.entitiy.OilCard;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;
import com.zzqs.app_kc.z_kc.network.OilCardApiImpl;
import com.zzqs.app_kc.z_kc.network.TruckApiImpl;

import rx.Subscriber;

/**
 * Created by lance on 2016/12/6.
 */

public class TruckDetailActivity extends BaseActivity {
  TextView tvLeft, tvTitle, tvRight, tvPlateNumber, tvCarType, tvOilCard, tvDriverName, tvDriverPhone, tvCarStatus, tvCarLocation;
  SimpleDraweeView sdCarPhoto;
  private Truck truck;
  private String truckId;
  private OilCard card;

  @Override
  public void initVariables() {
    truckId = getIntent().getStringExtra(Truck.TRUCK_ID);
    if (TextUtils.isEmpty(truckId)) {
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
    tvCarType = (TextView) findViewById(R.id.tvCarType);
    tvOilCard = (TextView) findViewById(R.id.tvOilCard);
    tvDriverName = (TextView) findViewById(R.id.tvDriverName);
    tvDriverPhone = (TextView) findViewById(R.id.tvDriverPhone);
    tvCarStatus = (TextView) findViewById(R.id.tvCarStatus);
    tvCarLocation = (TextView) findViewById(R.id.tvCarLocation);
    sdCarPhoto = (SimpleDraweeView) findViewById(R.id.sdCarPhoto);
  }


  @Override
  public void loadData() {
      safePd.show();
      TruckApiImpl.getTruckApiImpl().getTuckById(CommonTools.getToken(this), truckId, new Subscriber<ErrorInfo>() {
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
          if (errorInfo.getType().equals(ErrorInfo.SUCCESS)) {
            truck = (Truck) errorInfo.object;
            initViewData();
            getOilCard();
          } else {
            safePd.dismiss();
            Toast.makeText(mContext, errorInfo.getMessage(), Toast.LENGTH_SHORT).show();
          }
        }
      });
  }

  private void getOilCard() {
    OilCardApiImpl.getOilCardApiImpl().getCardById(CommonTools.getToken(this), truck.getCard_id(), new Subscriber<ErrorInfo>() {
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
          card = (OilCard) errorInfo.object;
          initViewData();
        } else {
          Toast.makeText(mContext, errorInfo.getMessage(), Toast.LENGTH_SHORT).show();
        }
      }
    });
  }

  private void initViewData() {
    tvPlateNumber.setText(truck.getTruck_number());
    tvDriverName.setText(truck.getDriver_name());
    tvDriverPhone.setText(truck.getDriver_number());
    tvCarType.setText(truck.getTruck_type());
    if (card != null && !TextUtils.isEmpty(card.getNumber())) {
      tvOilCard.setText(card.getNumber());
      tvCarStatus.setText(R.string.transporting);
    } else {
      tvCarStatus.setText(R.string.un_transport);
      tvCarStatus.setText(R.string.un_transport);
    }
    if (!TextUtils.isEmpty(truck.getTruck_photo())) {
      Uri uri = Uri.parse(CommonFiled.QINIU_ZOOM + truck.getTruck_photo());
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
    if (truck.getLocation() != null && truck.getLocation().size() == 2) {
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
      LatLng latLng = new LatLng(truck.getLocation().get(1), truck.getLocation().get(0));
      mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
    }
  }

}
