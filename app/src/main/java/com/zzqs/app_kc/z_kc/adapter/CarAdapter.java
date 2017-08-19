package com.zzqs.app_kc.z_kc.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zzqs.app_kc.R;
import com.zzqs.app_kc.utils.CommonFiled;
import com.zzqs.app_kc.z_kc.entitiy.Truck;

import java.util.List;

/**
 * Created by lance on 2016/12/4.
 */

public class CarAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Truck> truckList;
    private boolean isSelectCar;

    public CarAdapter(Context context, List<Truck> truckList, boolean isSelectCar) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.truckList = truckList;
        this.isSelectCar = isSelectCar;
    }

    @Override
    public int getCount() {
        return truckList != null ? truckList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return truckList != null ? truckList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        Truck truck = truckList.get(position);
        if (view == null) {
            view = inflater.inflate(R.layout.z_kc_item_car, null);
            holder = new ViewHolder();
            holder.sdCarPhoto = (SimpleDraweeView) view.findViewById(R.id.sdCarPhoto);
            holder.tvPlateNumber = (TextView) view.findViewById(R.id.tvPlateNumber);
            holder.tvCarType = (TextView) view.findViewById(R.id.tvCarType);
            holder.tvDriverPhone = (TextView) view.findViewById(R.id.tvDriverPhone);
            holder.tvDriverName = (TextView) view.findViewById(R.id.tvDriverName);
            holder.ivSelect = (ImageView) view.findViewById(R.id.ivSelect);
            if (isSelectCar) {
                holder.ivSelect.setVisibility(View.VISIBLE);
            } else {
                holder.ivSelect.setVisibility(View.GONE);
            }
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (!TextUtils.isEmpty(truck.getDriver().getPhoto())) {
            Uri uri = Uri.parse(CommonFiled.QINIU_ZOOM + truck.getDriver().getPhoto());
            holder.sdCarPhoto.setImageURI(uri);
        } else {
            holder.sdCarPhoto.setImageURI("");
        }
        holder.tvPlateNumber.setText(truck.getTruck_number() + "");
        holder.tvCarType.setText(context.getString(R.string.car_describe) + truck.getTruck_type() + "");
        holder.tvDriverName.setText(context.getString(R.string.car_driver) + (TextUtils.isEmpty(truck.getDriver_name()) ? context.getString(R.string.un_write) : truck.getDriver_name()));
        holder.tvDriverPhone.setText(context.getString(R.string.driver_phone) + truck.getDriver_number() + "");
        if (isSelectCar) {
            if (truck.isSelect()) {
                holder.ivSelect.setBackgroundResource(R.drawable.z_kc_ic_select);
            } else {
                holder.ivSelect.setBackgroundResource(R.drawable.z_kc_ic_un_select);
            }
        }
        return view;
    }

    private class ViewHolder {
        SimpleDraweeView sdCarPhoto;
        TextView tvPlateNumber, tvCarType, tvDriverPhone, tvDriverName;
        ImageView ivSelect;
    }
}
