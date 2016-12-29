package com.zzqs.app_kc.z_kc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.utils.CommonTools;
import com.zzqs.app_kc.z_kc.adapter.CarAdapter;
import com.zzqs.app_kc.z_kc.entitiy.Truck;
import com.zzqs.app_kc.z_kc.entitiy.ErrorInfo;
import com.zzqs.app_kc.z_kc.entitiy.Tender;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;
import com.zzqs.app_kc.z_kc.network.TruckApiImpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import rx.Subscriber;

/**
 * Created by lance on 2016/12/4.
 */

public class MyCarsActivity extends BaseActivity {
  TextView tvLeft, tvTitle, tvRight, tvBottom;
  ListView lvCars;
  CarAdapter carAdapter;
  List<Truck> truckList;
  public static final String IS_SELECT_CAR = "isSelectCar";
  private boolean isSelectCar;
  public static final int TO_ADD_CAR = 100;
  private Tender tender;

  @Override
  public void initVariables() {
    isSelectCar = getIntent().getBooleanExtra(IS_SELECT_CAR, false);
    tender = getIntent().getParcelableExtra("tender");
    truckList = new ArrayList<>();

  }

  @Override
  public void initViews(Bundle savedInstanceState) {
    setContentView(R.layout.z_kc_act_my_cars);
    tvLeft = (TextView) findViewById(R.id.head_back);
    tvLeft.setText("");
    tvLeft.setOnClickListener(new MyOnClickListener() {
      @Override
      public void OnceOnClick(View view) {
        finish();
      }
    });
    tvTitle = (TextView) findViewById(R.id.head_title);
    tvRight = (TextView) findViewById(R.id.head_right);
    tvBottom = (TextView) findViewById(R.id.tvBottom);
    if (isSelectCar) {
      tvTitle.setText(R.string.distribution_car);
      tvRight.setText(R.string.add_car);
      tvBottom.setText(R.string.distribution_car);
    } else {
      tvRight.setText("");
      tvTitle.setText(R.string.my_cars);
      tvBottom.setText(R.string.add_car);
    }
    tvRight.setVisibility(View.VISIBLE);
    tvRight.setOnClickListener(new MyOnClickListener() {
      @Override
      public void OnceOnClick(View view) {
        startActivityForResult(new Intent(mContext, AddTruckActivity.class), TO_ADD_CAR);
      }
    });

    tvBottom.setOnClickListener(new MyOnClickListener() {
      @Override
      public void OnceOnClick(View view) {
        if (isSelectCar) {//分配车辆
          if (!hasCarChoise()) {
            Toast.makeText(MyCarsActivity.this, getString(R.string.need_choice_car), Toast.LENGTH_LONG).show();
            return;
          }
          Intent intent = new Intent(mContext, ChoiceOilCardActivity.class);
          Truck truck = new Truck();
          for (Truck truck1 : truckList) {
            if (truck1.isSelect()) {
              truck = truck1;
            }
          }
          intent.putExtra("truck", truck);
          intent.putExtra("tender", tender);
          startActivity(intent);
        } else {//添加车辆
          startActivityForResult(new Intent(mContext, AddTruckActivity.class), TO_ADD_CAR);
        }
      }
    });
    lvCars = (ListView) findViewById(R.id.lvCars);
    carAdapter = new CarAdapter(this, truckList, isSelectCar);
    lvCars.setAdapter(carAdapter);
    lvCars.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Truck truck = truckList.get(position);
        if (truck.isSelect()) {
          return;
        }
        if (isSelectCar) {
          for (Truck truck1 : truckList) {
            truck1.setSelect(false);
          }
          truck.setSelect(true);
          carAdapter.notifyDataSetChanged();
        } else {
          Intent intent = new Intent(mContext, TruckDetailActivity.class);
          intent.putExtra(Truck.TRUCK_ID, truck.getTruck_id());
          startActivity(intent);
        }
      }
    });
  }

  @Override
  public void loadData() {
    getTrucks();
  }

  private void getTrucks() {
    safePd.show();
    TruckApiImpl.getTruckApiImpl().getListByDriver(CommonTools.getToken(this), new Subscriber<ErrorInfo>() {
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
          List<Truck> list = (List<Truck>) errorInfo.object;
          if (isSelectCar) {
            Iterator<Truck> it = list.iterator();
            while (it.hasNext()) {
              Truck truck = it.next();
              if (!TextUtils.isEmpty(truck.getCard_id())) {
                it.remove();
              }
            }
          }
          truckList.addAll(list);
          carAdapter.notifyDataSetChanged();
        } else {
          Toast.makeText(mContext, errorInfo.getMessage(), Toast.LENGTH_LONG).show();
        }
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == RESULT_OK) {
      if (requestCode == TO_ADD_CAR) {
        Truck truck = data.getParcelableExtra(Truck.TRUCK);
        if (truck == null) {
          return;
        }
        truckList.add(0, truck);
        carAdapter.notifyDataSetChanged();
      }
    }
  }

  private boolean hasCarChoise() {
    boolean result = false;
    for (Truck truck : truckList) {
      if (truck.isSelect()) {
        result = true;
      }
    }
    return result;
  }
}
