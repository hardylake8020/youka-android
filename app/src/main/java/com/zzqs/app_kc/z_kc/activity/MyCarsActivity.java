package com.zzqs.app_kc.z_kc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.z_kc.adapter.CarAdapter;
import com.zzqs.app_kc.z_kc.entitiy.Car;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lance on 2016/12/4.
 */

public class MyCarsActivity extends BaseActivity {
  TextView tvLeft, tvTitle, tvRight, tvAddCar;
  ListView lvCars;
  CarAdapter carAdapter;
  List<Car> carList;

  @Override
  public void initVariables() {
    carList = new ArrayList<>();
  }

  @Override
  public void initViews(Bundle savedInstanceState) {
    setContentView(R.layout.z_kc_my_cars);
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
    tvRight.setText(R.string.find_good_by_map);
    tvRight.setVisibility(View.VISIBLE);
    tvAddCar = (TextView) findViewById(R.id.tvAddCar);
    tvAddCar.setOnClickListener(new MyOnClickListener() {
      @Override
      public void OnceOnClick(View view) {

      }
    });
    lvCars = (ListView) findViewById(R.id.lvCars);
    carAdapter = new CarAdapter(this, carList);
    lvCars.setAdapter(carAdapter);
    lvCars.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Car car = carList.get(position);
        Intent intent = new Intent(mContext,CarDetailActivity.class);
        intent.putExtra(Car.CAR,car);
        startActivity(intent);
      }
    });
  }

  @Override
  public void loadData() {
    Car car1 = new Car();
    car1.setCar_type("金杯");
    car1.setDriver_name("张三");
    car1.setDriver_phone("18721850339");
    car1.setPlate_number("沪A12345");
    car1.setOil_card("1234 1234 1234 1234");
    car1.setStatus(Car.TRANSPORTING);
    List<Double> location = new ArrayList<>();
    location.add(121.48789949);
    location.add(31.24916171);
    car1.setLocation(location);
    carList.add(car1);

    Car car2 = new Car();
    car2.setCar_type("五菱之光");
    car2.setDriver_name("李四");
    car2.setDriver_phone("13927499609");
    car2.setPlate_number("沪B54321");
    car2.setOil_card("4321 4321 4321 4321");
    car2.setStatus(Car.UN_TRANSPORT);
    List<Double> location2 = new ArrayList<>();
    location2.add(120.48789949);
    location2.add(29.24916171);
    car2.setLocation(location2);
    carList.add(car2);

    carAdapter.notifyDataSetChanged();
  }
}
