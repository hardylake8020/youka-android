package com.zzqs.app_kc.z_kc.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.StandaloneActionMode;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.utils.CommonTools;
import com.zzqs.app_kc.z_kc.activity.MyCarsActivity;
import com.zzqs.app_kc.z_kc.activity.TenderDetailActivity;
import com.zzqs.app_kc.z_kc.activity.TenderTimeAxisActivity;
import com.zzqs.app_kc.z_kc.entitiy.ErrorInfo;
import com.zzqs.app_kc.z_kc.entitiy.Goods;
import com.zzqs.app_kc.z_kc.entitiy.Tender;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;
import com.zzqs.app_kc.z_kc.network.TenderApiImpl;
import com.zzqs.app_kc.z_kc.util.NumberUtil;
import com.zzqs.app_kc.z_kc.util.TimeUtil;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import rx.Subscriber;

/**
 * Created by lance on 2016/12/3.
 */

public class TenderAdapter extends BaseAdapter {
  private Context context;
  private LayoutInflater inflater;
  private List<Tender> kcOrderList;
  private DecimalFormat doubleDF;
  private boolean isMyTender;
  private SimpleDateFormat timeDF;

  public TenderAdapter(Context context, List<Tender> kcOrderList, boolean isMyTender) {
    this.context = context;
    inflater = LayoutInflater.from(context);
    this.kcOrderList = kcOrderList;
    this.isMyTender = isMyTender;
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
    final Tender tender = kcOrderList.get(position);
    if (view == null) {
      view = inflater.inflate(R.layout.z_kc_item_kc_tender, null);
      holder = new ViewHolder();
      holder.ivMarginTop = (ImageView) view.findViewById(R.id.ivMarginTop);
      holder.tvType = (TextView) view.findViewById(R.id.tvType);
      holder.tvPickupProvince = (TextView) view.findViewById(R.id.tvPickupProvince);
      holder.tvPickupCity = (TextView) view.findViewById(R.id.tvPickupCity);
      holder.tvDeliveryProvince = (TextView) view.findViewById(R.id.tvDeliveryProvince);
      holder.tvDeliveryCity = (TextView) view.findViewById(R.id.tvDeliveryCity);
      holder.tvImgAbove = (TextView) view.findViewById(R.id.tvImgAbove);
      holder.tvImgBelow = (TextView) view.findViewById(R.id.tvImgBelow);
      holder.tvGoodsDescription = (TextView) view.findViewById(R.id.tvGoodsDescription);
      holder.tvCreateTime = (TextView) view.findViewById(R.id.tvCreateTime);
      holder.rlTop = (RelativeLayout) view.findViewById(R.id.rlTop);
      holder.llBottom = (LinearLayout) view.findViewById(R.id.llBottom);
      holder.tvBottom = (TextView) view.findViewById(R.id.tvBottom);
      holder.tvTenderNumber = (TextView) view.findViewById(R.id.tvTenderNumber);

      holder.rlTop.setOnClickListener(new MyOnClickListener() {
        @Override
        public void OnceOnClick(View view) {
          Intent intent = new Intent(context, TenderDetailActivity.class);
          intent.putExtra(Tender.TENDER, tender);
          context.startActivity(intent);
        }
      });

      holder.llBottom.setOnClickListener(new MyOnClickListener() {
        @Override
        public void OnceOnClick(View view) {
          switch (tender.getStatus()) {
            case Tender.UN_ASSIGNED:
              //去分配车辆的页面
              Intent intent = new Intent(context, MyCarsActivity.class);
              intent.putExtra(MyCarsActivity.IS_SELECT_CAR, true);
              intent.putExtra(Tender.TENDER, tender);
              context.startActivity(intent);
              break;
            case Tender.IN_PROGRESS:
            case Tender.COMPLETED:
              Intent intent1 = new Intent(context, TenderTimeAxisActivity.class);
              intent1.putExtra(Tender.TENDER_ID, tender.getTender_id());
              context.startActivity(intent1);
              break;
          }

        }
      });

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

    if (isMyTender) {
      holder.ivMarginTop.setVisibility(View.VISIBLE);
      holder.llBottom.setVisibility(View.VISIBLE);
      holder.tvType.setVisibility(View.GONE);
      holder.tvImgAbove.setText(R.string.grab_success);
      holder.tvImgAbove.setTextColor(ContextCompat.getColor(context, R.color.green));
      holder.tvTenderNumber.setText(context.getString(R.string.tender_number, tender.getTender_number()));
      switch (tender.getStatus()) {
        case Tender.UN_ASSIGNED:
          holder.tvImgBelow.setText(R.string.un_distribution_car);
          holder.tvBottom.setText(R.string.distribution_car);
          holder.tvTenderNumber.setVisibility(View.GONE);
          break;
        case Tender.IN_PROGRESS:
          holder.tvImgBelow.setText(R.string.transporting);
          holder.tvBottom.setText(tender.getTruck_number());
          holder.tvTenderNumber.setVisibility(View.VISIBLE);
          break;
        case Tender.COMPLETED:
          holder.tvImgBelow.setText(R.string.completed);
          holder.tvBottom.setText(tender.getTruck_number());
          holder.tvTenderNumber.setVisibility(View.VISIBLE);
          break;
      }
    } else {
      holder.tvTenderNumber.setVisibility(View.GONE);
      holder.ivMarginTop.setVisibility(View.GONE);
      holder.llBottom.setVisibility(View.GONE);
      holder.tvType.setVisibility(View.VISIBLE);
      holder.tvImgAbove.setTextColor(ContextCompat.getColor(context, R.color.red_5));
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
    if (position == 0) {
      holder.ivMarginTop.setVisibility(View.VISIBLE);
    }
    return view;
  }

  private class ViewHolder {
    ImageView ivMarginTop;
    TextView tvType, tvPickupProvince, tvPickupCity, tvDeliveryProvince, tvDeliveryCity, tvImgAbove, tvImgBelow, tvCreateTime, tvGoodsDescription, tvBottom, tvTenderNumber;
    RelativeLayout rlTop;
    LinearLayout llBottom;
  }
}
