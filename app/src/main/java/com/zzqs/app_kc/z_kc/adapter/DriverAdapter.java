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
import com.zzqs.app_kc.z_kc.entitiy.Driver;

import java.util.List;

/**
 * Created by lance on 2016/12/4.
 */

public class DriverAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Driver> driverList;

    public DriverAdapter(Context context, List<Driver> driverList) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.driverList = driverList;
    }

    @Override
    public int getCount() {
        return driverList != null ? driverList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return driverList != null ? driverList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        Driver driver = driverList.get(position);
        if (view == null) {
            view = inflater.inflate(R.layout.z_kc_item_driver, null);
            holder = new ViewHolder();
            holder.sdDriverPhoto = (SimpleDraweeView) view.findViewById(R.id.sdDriverPhoto);
            holder.tvCarNumber = (TextView) view.findViewById(R.id.tvCarNumber);
            holder.tvCarType = (TextView) view.findViewById(R.id.tvCarType);
            holder.tvDriverPhone = (TextView) view.findViewById(R.id.tvDriverPhone);
            holder.tvDriverName = (TextView) view.findViewById(R.id.tvDriverName);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (!TextUtils.isEmpty(driver.getPlate_photo())) {
            Uri uri = Uri.parse(CommonFiled.QINIU_ZOOM + driver.getPlate_photo());
            holder.sdDriverPhoto.setImageURI(uri);
        }
        holder.tvCarNumber.setText(TextUtils.isEmpty(driver.getTruck_number()) ? context.getString(R.string.un_write) : driver.getTruck_number());
        holder.tvCarType.setText(TextUtils.isEmpty(driver.getTruck_type()) ? context.getString(R.string.un_write) : driver.getTruck_type());
        holder.tvDriverName.setText(TextUtils.isEmpty(driver.getNickname()) ? context.getString(R.string.un_write) : driver.getNickname());
        holder.tvDriverPhone.setText(TextUtils.isEmpty(driver.getUsername()) ? context.getString(R.string.un_write) : driver.getUsername());
        return view;
    }

    private class ViewHolder {
        SimpleDraweeView sdDriverPhoto;
        TextView tvCarType, tvDriverPhone, tvDriverName, tvCarNumber;
    }
}
