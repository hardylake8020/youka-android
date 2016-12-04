package com.zzqs.app_kc.z_kc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.z_kc.entitiy.KCOrder;
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
  private List<KCOrder> kcOrderList;
  private DecimalFormat df;

  public KCOrderAdapter(Context context, List<KCOrder> kcOrderList) {
    this.context = context;
    inflater = LayoutInflater.from(context);
    this.kcOrderList = kcOrderList;
    df = new DecimalFormat("######0.00");
  }

  @Override
  public int getCount() {
    return kcOrderList != null ? kcOrderList.size() : 0;
  }

  @Override
  public Object getItem(int position) {
    return kcOrderList != null ? kcOrderList.get(position) : null;
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }

  @Override
  public View getView(int position, View view, ViewGroup parent) {
    ViewHolder holder;
    KCOrder order = kcOrderList.get(position);
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
    if (order.getType().equals(KCOrder.NORMAL_ORDER)) {
      holder.tvType.setText(R.string.grab);
      holder.tvType.setBackgroundResource(R.drawable.round_red);
      holder.tvPrince.setText(df.format(order.getFixed_prince()) + context.getString(R.string.prince_unit));
    } else {
      holder.tvType.setText(R.string.compare);
      holder.tvType.setBackgroundResource(R.drawable.round_green);
      holder.tvPrince.setText(df.format(order.getCurrent_prince()) + context.getString(R.string.prince_unit));
    }
    holder.tvStartAddress.setText(order.getStart_city() + order.getStart_district());
    holder.tvEndAddress.setText(order.getEnd_city() + order.getEnd_district());
    String msg = "";
    if (order.getTotal_quantity() != 0) {
      msg = NumberUtil.doubleTrans(order.getTotal_quantity()) + order.getQuantity_unit();
    } else if (order.getTotal_volume() != 0) {
      msg = NumberUtil.doubleTrans(order.getTotal_volume()) + order.getVolume_unit();
    } else if (order.getTotal_weight() != 0) {
      msg = NumberUtil.doubleTrans(order.getTotal_weight()) + order.getWeight_unit();
    }
    holder.tvGoodsDescription.setText(context.getString(R.string.order_item_description, order.getTransport_type(), order.getInitiator(), msg));
    holder.tvCreateTime.setText(context.getString(R.string.order_item_create_time, TimeUtil.convertDateStringFormat(order.getCreate_time(), TimeUtil.SERVER_TIME_FORMAT, "MM-dd HH:mm"), order.getDistance() + context.getString(R.string.distance_unit)));
    return view;
  }

  private class ViewHolder {
    TextView tvType, tvStartAddress, tvEndAddress, tvPrince, tvGoodsDescription, tvCreateTime;
  }
}
