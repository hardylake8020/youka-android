package com.zzqs.app_kc.z_kc.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.z_kc.entitiy.Goods;
import com.zzqs.app_kc.z_kc.entitiy.Tender;
import com.zzqs.app_kc.z_kc.util.NumberUtil;
import com.zzqs.app_kc.z_kc.util.TimeUtil;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by lance on 2016/12/3.
 */

public class TenderAdapter extends BaseAdapter {
  private Context context;
  private LayoutInflater inflater;
  private List<Tender> kcOrderList;
  private DecimalFormat doubleDF;
  private boolean isUnParticipation;
  private SimpleDateFormat timeDF;
  public TenderAdapter(Context context, List<Tender> kcOrderList, boolean isUnParticipation) {
    this.context = context;
    inflater = LayoutInflater.from(context);
    this.kcOrderList = kcOrderList;
    this.isUnParticipation = isUnParticipation;
    doubleDF = new DecimalFormat("######0.00");
    timeDF = new SimpleDateFormat("HH:mm:ss");
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
    Tender tender = kcOrderList.get(position);
    if (view == null) {
      view = inflater.inflate(R.layout.z_kc_item_kc_order, null);
      holder = new ViewHolder();
      holder.tvType = (TextView) view.findViewById(R.id.tvType);
      holder.tvPickupProvince = (TextView) view.findViewById(R.id.tvPickupProvince);
      holder.tvPickupCity = (TextView) view.findViewById(R.id.tvPickupCity);
      holder.tvDeliveryProvince = (TextView) view.findViewById(R.id.tvDeliveryProvince);
      holder.tvDeliveryCity = (TextView) view.findViewById(R.id.tvDeliveryCity);
      holder.tvImgAbove = (TextView) view.findViewById(R.id.tvImgAbove);
      holder.tvImgBelow = (TextView) view.findViewById(R.id.tvImgBelow);
      holder.tvGoodsDescription = (TextView) view.findViewById(R.id.tvGoodsDescription);
      holder.tvCreateTime = (TextView) view.findViewById(R.id.tvCreateTime);
      view.setTag(holder);
    } else {
      holder = (ViewHolder) view.getTag();
    }
    holder.tvPickupProvince.setText(tender.getPickup_province());
    holder.tvPickupCity.setText(tender.getPickup_city());
    holder.tvDeliveryProvince.setText(tender.getDelivery_province());
    holder.tvDeliveryCity.setText(tender.getDelivery_city());
    String msg = "";
    if (tender.getMobile_goods() != null) {
      double quantity = 0, volume = 0, weight = 0;
      String quantityUnit = null, volumeUnit = null, weightUnit = null;
      for (Goods goods : tender.getMobile_goods()) {
        quantity += goods.getCount();
        volume += goods.getCount2();
        weight += goods.getCount3();
        if (!TextUtils.isEmpty(goods.getUnit())) {
          quantityUnit = goods.getUnit();
        }
        if (!TextUtils.isEmpty(goods.getUnit2())) {
          quantityUnit = goods.getUnit2();
        }
        if (!TextUtils.isEmpty(goods.getUnit2())) {
          quantityUnit = goods.getUnit2();
        }
      }
      if (!TextUtils.isEmpty(quantityUnit)) {
        msg = NumberUtil.doubleTrans(quantity) + quantityUnit;
      } else if (!TextUtils.isEmpty(volumeUnit)) {
        msg = NumberUtil.doubleTrans(volume) + volumeUnit;
      } else if (!TextUtils.isEmpty(weightUnit)) {
        msg = NumberUtil.doubleTrans(weight) + weightUnit;
      }
    }
    holder.tvGoodsDescription.setText(context.getString(R.string.order_item_description, tender.getSender_company(), msg, tender.getDistance() + context.getString(R.string.distance_unit)));
    holder.tvCreateTime.setText(context.getString(R.string.order_item_create_time, TimeUtil.convertDateStringFormat(tender.getStart_time(), TimeUtil.SERVER_TIME_FORMAT, "MM-dd HH:mm")));

    if (isUnParticipation) {
      if (tender.getTender_type().equals(Tender.GRAB)) {
        holder.tvType.setText(R.string.grab);
        holder.tvType.setBackgroundResource(R.drawable.round_red);
        holder.tvImgAbove.setText(tender.getLowest_grab_price() + "");
        holder.tvImgBelow.setText(R.string.prince_unit2);
      } else {
        holder.tvType.setText(R.string.compare);
        holder.tvType.setBackgroundResource(R.drawable.round_green);

        long between = TimeUtil.UTCTimeToTimeMillis(tender.getStart_time()) - TimeUtil.UTCTimeToTimeMillis(tender.getEnd_time()) / 1000;//除以1000是为了转换成秒
        String time = timeDF.format(between);
        holder.tvImgAbove.setText(time);
        holder.tvImgBelow.setText(R.string.rest_time);
      }
    }
    return view;
  }

  private class ViewHolder {
    TextView tvType, tvPickupProvince, tvPickupCity, tvDeliveryProvince, tvDeliveryCity, tvImgAbove, tvImgBelow, tvCreateTime, tvGoodsDescription;
  }
}
