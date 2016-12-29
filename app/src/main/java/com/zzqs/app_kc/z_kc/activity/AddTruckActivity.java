package com.zzqs.app_kc.z_kc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.utils.CommonTools;
import com.zzqs.app_kc.utils.StringTools;
import com.zzqs.app_kc.z_kc.entitiy.Truck;
import com.zzqs.app_kc.z_kc.entitiy.ErrorInfo;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;
import com.zzqs.app_kc.z_kc.network.TruckApiImpl;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

/**
 * Created by lance on 2016/12/24.
 */

public class AddTruckActivity extends BaseActivity {
  TextView tvLeft, tvTitle, tvAddCar;
  EditText etCarNumber, etDriverPhone;
  Spinner spinnerTruckType;
  private List<String> truckTypeList;
  private ArrayAdapter<String> adapter;

  @Override
  public void initVariables() {
    String[] truckTypes = getResources().getStringArray(R.array.truck_type);
    truckTypeList = new ArrayList<>();
    for (int i = 0; i < truckTypes.length; i++) {
      truckTypeList.add(truckTypes[i]);
    }
  }

  @Override
  public void initViews(Bundle savedInstanceState) {
    setContentView(R.layout.z_kc_act_add_car);
    tvLeft = (TextView) findViewById(R.id.head_back);
    tvLeft.setText("");
    tvLeft.setOnClickListener(new MyOnClickListener() {
      @Override
      public void OnceOnClick(View view) {
        finish();
      }
    });
    tvTitle = (TextView) findViewById(R.id.head_title);
    tvTitle.setText(R.string.add_car);
    tvAddCar = (TextView) findViewById(R.id.tvAddCar);
    tvAddCar.setOnClickListener(new MyOnClickListener() {
      @Override
      public void OnceOnClick(View view) {
        addCar();
      }
    });
    etCarNumber = (EditText) findViewById(R.id.etCarNumber);
    etDriverPhone = (EditText) findViewById(R.id.etDriverPhone);
    spinnerTruckType = (Spinner) findViewById(R.id.spinnerTruckType);
    adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, truckTypeList);
    spinnerTruckType.setAdapter(adapter);
    spinnerTruckType.setSelection(0);
  }

  @Override
  public void loadData() {

  }

  private void addCar() {
    String carNumber = etCarNumber.getText().toString().trim();
    if (TextUtils.isEmpty(carNumber)) {
      Toast.makeText(this, R.string.input_driver_phone, Toast.LENGTH_SHORT).show();
      return;
    }
    if (!StringTools.isCarnumberNO(carNumber)) {
      Toast.makeText(this, R.string.prompt_input_no_plate, Toast.LENGTH_SHORT).show();
      return;
    }
    String driverPhone = etDriverPhone.getText().toString().trim();
    if (TextUtils.isEmpty(driverPhone)) {
      Toast.makeText(this, R.string.input_driver_phone, Toast.LENGTH_SHORT).show();
      return;
    }
    if (!StringTools.isMobileNumber(driverPhone)) {
      Toast.makeText(this, R.string.prompt_phone_is_error, Toast.LENGTH_SHORT).show();
      return;
    }

    String truckType = spinnerTruckType.getSelectedItem().toString();
    System.out.println(carNumber + "--" + driverPhone + "--" + truckType);
    safePd.show();
    TruckApiImpl.getTruckApiImpl().createCar(CommonTools.getToken(this), driverPhone, carNumber, truckType, new Subscriber<ErrorInfo>() {
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
          Truck truck = (Truck) errorInfo.object;
          System.out.println(truck.toString());
          Toast.makeText(mContext, R.string.add_success, Toast.LENGTH_LONG).show();
          Intent data = new Intent();
          data.putExtra(Truck.TRUCK, truck);
          setResult(RESULT_OK, data);
          finish();
        } else {
          Toast.makeText(mContext, errorInfo.getMessage(), Toast.LENGTH_LONG).show();
        }
      }
    });
  }
}
