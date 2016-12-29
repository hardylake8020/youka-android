package com.zzqs.app_kc.activities.UploadEvent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.zzqs.app_kc.R;
import com.zzqs.app_kc.activities.MainActivity;
import com.zzqs.app_kc.activities.OrderCompleteActivity;
import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.entity.EventFile;
import com.zzqs.app_kc.entity.Events;
import com.zzqs.app_kc.entity.Order;
import com.zzqs.app_kc.entity.OrderEvent;
import com.zzqs.app_kc.utils.CommonFiled;
import com.zzqs.app_kc.utils.StringTools;

import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by lance on 15/11/22.
 */
public class SinglePickAndDeliveryEventActivity extends BaseUploadEventActivity {

  @Override
  public void initCallbackHandler() {
    callbackHandler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (pd.isShowing()) {
          pd.dismiss();
        }
        Events.OrderEvent event;
        switch (msg.what) {
          case SAVE:
            event = new Events.OrderEvent(order, Events.REFRESH_ORDER);
            EventBus.getDefault().post(event);
            Toast.makeText(SinglePickAndDeliveryEventActivity.this, getString(R.string.prompt_saved_data), Toast.LENGTH_SHORT).show();
            finish();
          case GET_PHOTO:
            adapter.notifyDataSetChanged();
            break;
          case UPLOAD_SUCCESS:
            orderEvent.setStatus(OrderEvent.STATUS_COMMIT);
            if (orderEvent.get_id() > 0) {
              orderEventDao.update(orderEvent);
            } else {
              orderEventDao.insert(orderEvent);
            }
            Toast.makeText(SinglePickAndDeliveryEventActivity.this, getString(R.string.prompt_submitted), Toast.LENGTH_SHORT).show();
            if (mold.equals(OrderEvent.MOLD_DELIVERY)) {
              Intent data = new Intent(getApplicationContext(), OrderCompleteActivity.class);
              data.putExtra(Order.ORDER, order);
              startActivity(data);
            } else {
              startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
            break;
          case ORDER_NOT_EXIST:
            Toast.makeText(SinglePickAndDeliveryEventActivity.this, getString(R.string.prompt_cancelled_order), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            break;
          case ORDER_STATUS_HAS_CHANGED:
            Toast.makeText(SinglePickAndDeliveryEventActivity.this, getString(R.string.prompt_operating_order), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            break;
          case SAVE_FILE_FAILED:
            Toast.makeText(SinglePickAndDeliveryEventActivity.this, getString(R.string.prompt_save_file_failed), Toast.LENGTH_SHORT).show();
            break;
        }
      }
    };
  }

  @Override
  public void subclassView() {
    timeLine.setVisibility(View.VISIBLE);
    boolean oldOrderCountChange = false;
    boolean newOrderCountChange = false;
    //判断是否显示实收入口，实收入口和货损按钮只其中显示一个
//    if (!StringTools.isEmp(order.getOperationGoodsName())) {
//      rlActualDelivery.setVisibility(View.VISIBLE);
//      rlDamage.setVisibility(View.GONE);
//      if (!StringTools.isEmp(order.getActualGoodsId())) {
//        if (!order.getOperationGoodsCount().equals(order.getActualGoodsCount())) {
//          newOrderCountChange = true;
//        }
//      }
//    } else {
//      rlActualDelivery.setVisibility(View.GONE);
//      rlDamage.setVisibility(View.VISIBLE);
//    }
    rlActualDelivery.setVisibility(View.GONE);
    rlDamage.setVisibility(View.VISIBLE);

    if (mold.equals(OrderEvent.MOLD_PICKUP)) {
      headMsg.setText(getString(R.string.view_tv_pickup));
      if (newOrderCountChange || oldOrderCountChange) {
        tvActualInfo.setText(getString(R.string.view_tv_modify_actual_pickup_1));
      } else {
        tvActualInfo.setText(getString(R.string.view_tv_modify_actual_pickup_2));
      }
    } else {
      scanCommit.setVisibility(View.VISIBLE);
      if (order.getOrderType().equals(Order.WAREHOUSE_ORDER)) {
        headMsg.setText(getString(R.string.view_tv_warehouse_delivery));

      } else {
        headMsg.setText(getString(R.string.view_tv_delivery));
      }
      if (newOrderCountChange || oldOrderCountChange) {
        tvActualInfo.setText(getString(R.string.view_tv_modify_actual_delivery_1));
      } else {
        tvActualInfo.setText(getString(R.string.view_tv_modify_actual_delivery_2));
      }
    }

    if (mold.equals(OrderEvent.MOLD_DELIVERY)) {
      rlCompression.setVisibility(View.VISIBLE);
      tvCompression.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          isNeedBigFile = !isNeedBigFile;
          SharedPreferences sharedPreferences = getSharedPreferences(CommonFiled.DEFAULT, MODE_PRIVATE);
          sharedPreferences.edit().putBoolean("isNeedBigFile", isNeedBigFile).commit();
          changeCompression();
        }
      });

      SharedPreferences sharedPreferences = getSharedPreferences(CommonFiled.DEFAULT, MODE_PRIVATE);
      isNeedBigFile = sharedPreferences.getBoolean("isNeedBigFile", false);
      changeCompression();
    }
  }

  private void changeCompression() {
    if (isNeedBigFile) {
      tvCompression.setBackgroundResource(R.drawable.off);
    } else {
      tvCompression.setBackgroundResource(R.drawable.on);
    }
  }

  @Override
  public void saveData() {
    String message = et.getText().toString().trim();
    if (!StringTools.isEmp(message)) {
      orderEvent.setRemark(message);
    }
    orderEvent.setIsDamaged(isDamaged ? 1 : 0);
    if (orderEvent.getLatitude() != 0 && orderEvent.getLongitude() != 0) {
      latitude = orderEvent.getLatitude();
      longitude = orderEvent.getLongitude();
      if (!StringTools.isEmp(orderEvent.getCreateTime())) {
        uploadTime = orderEvent.getCreateTime();
      }
      if (!StringTools.isEmp(orderEvent.getAddress())) {
        locationAddress = orderEvent.getAddress();
      } else {
        if (!isSearch) {
          isSearch = !isSearch;
          LatLng latLng = new LatLng(orderEvent.getLatitude(), orderEvent.getLongitude());
          mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
          return;
        }

      }
    } else {
      if (lastTrace != null) {
        latitude = lastTrace.getLatitude();
        longitude = lastTrace.getLongitude();
        uploadTime = lastTrace.getTime();
        if (!StringTools.isEmp(lastTrace.getAddress())) {
          locationAddress = lastTrace.getAddress();
        } else {
          if (!isSearch) {
            isSearch = !isSearch;
            LatLng latLng = new LatLng(orderEvent.getLatitude(), orderEvent.getLongitude());
            mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
            return;
          }
        }
      }
    }
    if (!StringTools.isEmp(uploadTime) && !uploadTime.equals("1970-01-01 00:00:00")) {
      orderEvent.setCreateTime(uploadTime);
    } else {
      orderEvent.setCreateTime(sdf.format(new Date()));
    }
    orderEvent.setAddress(locationAddress);
    orderEvent.setLatitude(latitude);
    orderEvent.setLongitude(longitude);

    if (orderEvent.get_id() > 0) {
      orderEventDao.update(orderEvent);
    } else {
      orderEvent.setStatus(OrderEvent.STATUS_NEW);
      orderEvent.setOrderId(order.getOrderId());
      orderEvent.setMold(mold);
      orderEventDao.insert(orderEvent);
      List<OrderEvent> orderEvents = orderEventDao.rawQuery("select * from order_event where order_id=? and mold=? and status=?", new String[]{orderEvent.getOrderId() + "", orderEvent.getMold(), OrderEvent.STATUS_NEW + ""});
      if (orderEvents.size() > 0) {
        orderEvent.set_id(orderEvents.get(orderEvents.size() - 1).get_id());
      }
    }

    if (null != voiceFile && !StringTools.isEmp(voiceFile.getFilePath())) {
      voiceFile.setOrderId(order.getOrderId());
      voiceFile.setEventId(orderEvent.get_id());
      if (voiceFile.get_id() > 0) {
        eventFileDao.update(voiceFile);
      } else {
        eventFileDao.insert(voiceFile);
      }
    } else {
      voiceFile = new EventFile();
    }

    for (EventFile eventFile : photoList) {
      if (eventFile.get_id() > 0) {
        eventFileDao.update(eventFile);
      } else {
        eventFile.setEventId(orderEvent.get_id());
        eventFileDao.insert(eventFile);
      }
    }
    if (isUpload) {
      isHaveEventId();
      SingleUpload(isScanCode);
    } else {
      pd.setMessage(getString(R.string.prompt_dl_saving_data));
      pd.setCancelable(false);
      pd.show();
      callbackHandler.sendEmptyMessage(SAVE);
    }
  }

  @Override
  public void back() {
    isUpload = false;
    if (photoList.size() > 0 || !StringTools.isEmp(et.getText().toString().trim())) {
      saveData();
    } else {
      finish();
    }
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      isUpload = false;
      if (photoList.size() > 0 || !StringTools.isEmp(et.getText().toString().trim())) {
        saveData();
      } else {
        finish();
      }
      return true;
    } else {
      return super.onKeyDown(keyCode, event);
    }
  }
}
