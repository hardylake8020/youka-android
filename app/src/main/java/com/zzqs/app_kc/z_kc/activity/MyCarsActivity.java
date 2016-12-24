package com.zzqs.app_kc.z_kc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.utils.CommonTools;
import com.zzqs.app_kc.z_kc.adapter.CarAdapter;
import com.zzqs.app_kc.z_kc.entitiy.Car;
import com.zzqs.app_kc.z_kc.entitiy.ErrorInfo;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;
import com.zzqs.app_kc.z_kc.network.CarApiImpl;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

/**
 * Created by lance on 2016/12/4.
 */

public class MyCarsActivity extends BaseActivity {
  TextView tvLeft, tvTitle, tvRight, tvBottom;
  ListView lvCars;
  CarAdapter carAdapter;
  List<Car> carList;
  public static final String IS_SELECT_CAR = "isSelectCar";
  private boolean isSelectCar;
  public static final int TO_ADD_CAR = 100;

  @Override
  public void initVariables() {
    isSelectCar = getIntent().getBooleanExtra(IS_SELECT_CAR, false);
    carList = new ArrayList<>();
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
    tvTitle.setText(R.string.my_cars);
    tvRight = (TextView) findViewById(R.id.head_right);
    tvBottom = (TextView) findViewById(R.id.tvBottom);
    if (isSelectCar) {
      tvRight.setText(R.string.add_car);
      tvBottom.setText(R.string.distribution_car);
    } else {
      tvRight.setText("");
      tvBottom.setText(R.string.add_car);
    }
    tvRight.setVisibility(View.VISIBLE);
    tvRight.setOnClickListener(new MyOnClickListener() {
      @Override
      public void OnceOnClick(View view) {
        startActivityForResult(new Intent(mContext, AddCarActivity.class), TO_ADD_CAR);
      }
    });

    tvBottom.setOnClickListener(new MyOnClickListener() {
      @Override
      public void OnceOnClick(View view) {
        if (isSelectCar) {

        } else {
          startActivityForResult(new Intent(mContext, AddCarActivity.class), TO_ADD_CAR);
        }
      }
    });
    lvCars = (ListView) findViewById(R.id.lvCars);
    carAdapter = new CarAdapter(this, carList, isSelectCar);
    lvCars.setAdapter(carAdapter);
    lvCars.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Car car = carList.get(position);
        if (car.isSelect()) {
          return;
        }
        if (isSelectCar) {
          for (Car car1 : carList) {
            car1.setSelect(false);
          }
          car.setSelect(true);
          carAdapter.notifyDataSetChanged();
        } else {
          Intent intent = new Intent(mContext, CarDetailActivity.class);
          intent.putExtra(Car.TRUCK, car);
          startActivity(intent);
        }
      }
    });
  }

  @Override
  public void loadData() {
    getCars();
  }

  private void getCars() {
    safePd.show();
    CarApiImpl.getCarApiImpl().getListByDriver(CommonTools.getToken(this), new Subscriber<ErrorInfo>() {
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
          List<Car> list = (List<Car>) errorInfo.object;
          carList.addAll(list);
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
        Car car = data.getParcelableExtra(Car.TRUCK);
        if (car == null) {
          return;
        }
        carList.add(0, car);
        carAdapter.notifyDataSetChanged();
      }
    }
  }
}
