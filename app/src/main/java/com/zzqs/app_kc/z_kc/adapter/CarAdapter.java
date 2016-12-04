package com.zzqs.app_kc.z_kc.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zzqs.app_kc.R;
import com.zzqs.app_kc.utils.CommonFiled;
import com.zzqs.app_kc.z_kc.entitiy.Car;

import java.util.List;

/**
 * Created by lance on 2016/12/4.
 */

public class CarAdapter extends BaseAdapter {
  private Context context;
  private LayoutInflater inflater;
  private List<Car> carList;

  public CarAdapter(Context context, List<Car> carList) {
    this.context = context;
    inflater = LayoutInflater.from(context);
    this.carList = carList;
  }

  @Override
  public int getCount() {
    return carList != null ? carList.size() : 0;
  }

  @Override
  public Object getItem(int position) {
    return carList != null ? carList.get(position) : null;
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }

  @Override
  public View getView(int position, View view, ViewGroup parent) {
    ViewHolder holder;
    Car car = carList.get(position);
    if (view == null) {
      view = inflater.inflate(R.layout.z_kc_item_car, null);
      holder = new ViewHolder();
      holder.sdCarPhoto = (SimpleDraweeView) view.findViewById(R.id.sdCarPhoto);
      holder.tvPlateNumber = (TextView) view.findViewById(R.id.tvPlateNumber);
      holder.tvCarType = (TextView) view.findViewById(R.id.tvCarType);
      holder.tvDriverPhone = (TextView) view.findViewById(R.id.tvDriverPhone);
      view.setTag(holder);
    } else {
      holder = (ViewHolder) view.getTag();
    }
    if (!TextUtils.isEmpty(car.getCar_photo())) {
      Uri uri = Uri.parse(CommonFiled.QINIU_ZOOM + car.getCar_photo());
      holder.sdCarPhoto.setImageURI(uri);
    }
    holder.tvPlateNumber.setText(car.getPlate_number() + "");
    holder.tvCarType.setText(car.getCar_type() + "");
    holder.tvDriverPhone.setText(car.getDriver_phone() + "");
    return view;
  }

  private class ViewHolder {
    SimpleDraweeView sdCarPhoto;
    TextView tvPlateNumber, tvCarType, tvDriverPhone;
  }
}
