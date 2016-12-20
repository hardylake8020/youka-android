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
import java.util.List;

/**
 * Created by lance on 2016/12/3.
 */

public class KCOrderAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Tender> kcOrderList;
    private DecimalFormat df;

    public KCOrderAdapter(Context context, List<Tender> kcOrderList) {
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
        Tender order = kcOrderList.get(position);
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
        if (order.getTender_type().equals(Tender.GRAB)) {
            holder.tvType.setText(R.string.grab);
            holder.tvType.setBackgroundResource(R.drawable.round_red);
            holder.tvPrince.setText(df.format(order.getHighest_protect_price()) + context.getString(R.string.prince_unit));
        } else {
            holder.tvType.setText(R.string.compare);
            holder.tvType.setBackgroundResource(R.drawable.round_green);
            holder.tvPrince.setText(df.format(order.getCurrent_grab_price()) + context.getString(R.string.prince_unit));
        }
        holder.tvStartAddress.setText(order.getPickup_city() + order.getPickup_region());
        holder.tvEndAddress.setText(order.getDelivery_city() + order.getDelivery_region());
        String msg = "";
        if (order.getGoods() != null) {
            double quantity = 0, volume = 0, weight = 0;
            String quantityUnit = null, volumeUnit = null, weightUnit = null;
            for (Goods goods : order.getGoods()) {
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
        holder.tvGoodsDescription.setText(context.getString(R.string.order_item_description, order.getTransport_type(), order.getSender_company(), msg));
        holder.tvCreateTime.setText(context.getString(R.string.order_item_create_time, TimeUtil.convertDateStringFormat(order.getStart_time(), TimeUtil.SERVER_TIME_FORMAT, "MM-dd HH:mm"), order.getDistance() + context.getString(R.string.distance_unit)));
        return view;
    }

    private class ViewHolder {
        TextView tvType, tvStartAddress, tvEndAddress, tvPrince, tvGoodsDescription, tvCreateTime;
    }
}
