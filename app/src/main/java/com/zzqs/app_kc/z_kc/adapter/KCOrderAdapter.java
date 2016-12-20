package com.zzqs.app_kc.z_kc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.z_kc.entitiy.Tender;
import com.zzqs.app_kc.z_kc.util.NumberUtil;
import com.zzqs.app_kc.z_kc.util.TimeUtil;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by lance on 2016/12/3.
 */

public class KCOrderAdapter extends BaseAdapter {
  private Context context;
  private LayoutInflater inflater;
  private List<Tender> tenderList;
  private DecimalFormat df;

  public KCOrderAdapter(Context context, List<Tender> tenderList) {
    this.context = context;
    inflater = LayoutInflater.from(context);
    this.tenderList = tenderList;
    df = new DecimalFormat("######0.00");
  }

  @Override
  public int getCount() {
    return tenderList != null ? tenderList.size() : 0;
  }

  @Override
  public Object getItem(int position) {
    return tenderList != null ? tenderList.get(position) : null;
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }

  @Override
  public View getView(int position, View view, ViewGroup parent) {
    ViewHolder holder;
    Tender tender = tenderList.get(position);
    if (view == null) {
      view = inflater.inflate(R.layout.z_kc_item_kc_order, null);
      holder = new ViewHolder();
      holder.tvType = (TextView) view.findViewById(R.id.tvType);
      holder.tvStartAddress = (TextView) view.findViewById(R.id.tvStartAddress);
      holder.tvEndAddress = (TextView) view.findViewById(R.id.tvEndAddress);
      holder.tvPrince = (TextView) view.findViewById(R.id.tvPrince);
      holder.tvGoodsDescription = (TextView) view.findViewById(R.id.tvGoodsDescription);
      holder.tvCreateTime = (TextView) view.findViewById(R.id.tvCreateTime);
      view.setTag(holder);
    } else {
      holder = (ViewHolder) view.getTag();
    }
    if (tender.getTender_type().equals(Tender.NORMAL_TENDER)) {
      holder.tvType.setText(R.string.grab);
      holder.tvType.setBackgroundResource(R.drawable.round_red);
      holder.tvPrince.setText(df.format(tender.getFixed_prince()) + context.getString(R.string.prince_unit));
    } else {
      holder.tvType.setText(R.string.compare);
      holder.tvType.setBackgroundResource(R.drawable.round_green);
      holder.tvPrince.setText(df.format(tender.getCurrent_prince()) + context.getString(R.string.prince_unit));
    }
    holder.tvStartAddress.setText(tender.getPickup_city() + tender.getPickup_region());
    holder.tvEndAddress.setText(tender.getEnd_city() + tender.getEnd_district());
    String msg = "";
    if (tender.getTotal_quantity() != 0) {
      msg = NumberUtil.doubleTrans(tender.getTotal_quantity()) + tender.getQuantity_unit();
    } else if (tender.getTotal_volume() != 0) {
      msg = NumberUtil.doubleTrans(tender.getTotal_volume()) + tender.getVolume_unit();
    } else if (tender.getTotal_weight() != 0) {
      msg = NumberUtil.doubleTrans(tender.getTotal_weight()) + tender.getWeight_unit();
    }
    holder.tvGoodsDescription.setText(context.getString(R.string.order_item_description, tender.getInitiator(), msg,"45公里"));
    holder.tvCreateTime.setText(context.getString(R.string.order_item_create_time, TimeUtil.convertDateStringFormat(tender.getStart_time(), TimeUtil.SERVER_TIME_FORMAT, "MM-dd HH:mm"), tender.getDistance() + context.getString(R.string.distance_unit)));
    return view;
  }

  private class ViewHolder {
    TextView tvType, tvStartAddress, tvEndAddress, tvPrince, tvGoodsDescription, tvCreateTime;
  }
}
