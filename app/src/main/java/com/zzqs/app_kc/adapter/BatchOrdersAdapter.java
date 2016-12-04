package com.zzqs.app_kc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.entity.Order;
import com.zzqs.app_kc.utils.StringTools;

import java.util.List;

/**
 * Created by lance on 15/5/17.
 */
public class BatchOrdersAdapter extends BaseAdapter {
    private Context context;
    private List<Order> orders = null;

    public BatchOrdersAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @Override
    public int getCount() {
        return orders != null ? orders.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return orders.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        Order order = orders.get(i);
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_batch_orders, null);
            holder.serialNo = (TextView) view.findViewById(R.id.serialNo);
            holder.goodsName = (TextView) view.findViewById(R.id.goods_name);
            holder.quantity = (TextView) view.findViewById(R.id.quantity);
            holder.weight = (TextView) view.findViewById(R.id.weight);
            holder.volume = (TextView) view.findViewById(R.id.volume);
            holder.tv_pickup_place = (TextView) view.findViewById(R.id.tv_pickup_place);
            holder.tv_pickup_time = (TextView) view.findViewById(R.id.tv_pickup_time);
            holder.tv_new_goods = (TextView) view.findViewById(R.id.tv_new_goods);
            holder.ll_batch = (LinearLayout) view.findViewById(R.id.ll_batch);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (!StringTools.isEmp(order.getSerialNo())) {
            holder.serialNo.setText(order.getSerialNo());
        }

        if (!StringTools.isEmp(order.getActualGoodsId())) {//新版本创建的运单
            holder.ll_batch.setVisibility(View.GONE);
            holder.tv_new_goods.setVisibility(View.VISIBLE);
            if (!StringTools.isEmp(order.getActualGoodsId())) {
                StringBuffer goods = new StringBuffer();
                String[] names = order.getActualGoodsName().split("/zzqs/");
                String[] units = order.getActualGoodsUnit().split("/zzqs/");
                String[] units2 = order.getActualGoodsUnit2().split("/zzqs/");
                String[] units3 = order.getActualGoodsUnit3().split("/zzqs/");
                String[] counts = order.getActualGoodsCount().split("/zzqs/");
                String[] counts2 = order.getActualGoodsCount2().split("/zzqs/");
                String[] counts3 = order.getActualGoodsCount3().split("/zzqs/");
                for (int j = 0; j < names.length; j++) {
                    if (!StringTools.isEmp(names[j])) {
                        if (null != units2 && units2.length >= j + 1) {
                            if (units2[j].equals("null")) {
                                goods.append(names[j] + " " + counts[j] + units[j]);
                            } else {
                                String count = "", count2 = "", count3 = "", unit = "", unit2 = "", unit3 = "";
                                if (!StringTools.isEmp(counts[j])) {
                                    count = counts[j].toString();
                                    unit = units[j].toString();
                                }
                                if (!StringTools.isEmp(counts2[j])) {
                                    count2 = counts2[j].toString();
                                    unit2 = units2[j].toString();
                                }
                                if (!StringTools.isEmp(counts3[j])) {
                                    count3 = counts3[j].toString();
                                    unit3 = units3[j].toString();
                                }
                                goods.append(names[j] + " " + count + unit + " " + count2 + unit2 + " " + count3 + unit3);
                            }
                        } else {
                            goods.append(names[j] + " " + counts[j] + units[j]);
                        }
                        if (j != names.length - 1) {
                            goods.append("  |  ");
                        }
                    }
                }
                holder.tv_new_goods.setText(goods);
            } else {
                holder.tv_new_goods.setText(R.string.view_tv_no_goods_name);
            }
        } else {
            holder.ll_batch.setVisibility(View.VISIBLE);
            holder.tv_new_goods.setVisibility(View.GONE);
            if (!StringTools.isEmp(order.getGoodsName())) {
                holder.goodsName.setText(order.getGoodsName());
            } else {
                holder.goodsName.setText(R.string.view_tv_no_goods_name);
            }

            if (!StringTools.isEmp(order.getTotalQuantity())) {
                holder.quantity.setText(" " + order.getTotalQuantity() + " " + order.getQuantityUnit());
            } else {
                holder.quantity.setText(" / " + order.getQuantityUnit());
            }
            if (!StringTools.isEmp(order.getTotalWeight())) {
                holder.weight.setText(" " + order.getTotalWeight() + " " + order.getWeightUnit());
            } else {
                holder.weight.setText(" / " + order.getWeightUnit());
            }
            if (!StringTools.isEmp(order.getTotalVolume())) {
                holder.volume.setText(" " + order.getTotalVolume() + " " + order.getVolumeUnit());
            } else {
                holder.volume.setText(" / " + order.getVolumeUnit());
            }
        }
        if (!StringTools.isEmp(StringTools.outputDate(order.getPickupTimeStart(), order.getPickupTimeEnd()))) {
            holder.tv_pickup_time.setText(StringTools.outputDate(order.getPickupTimeStart(), order.getPickupTimeEnd()));
        } else {
            holder.tv_pickup_time.setText(R.string.view_tv_no_pickup_time);
        }
        if (!StringTools.isEmp(order.getFromAddress())) {
            holder.tv_pickup_place.setText(order.getFromAddress());
        } else {
            holder.tv_pickup_place.setText(R.string.view_tv_no_pickup_address);
        }
        return view;
    }

    class ViewHolder {
        TextView serialNo, goodsName, quantity, weight, volume, tv_pickup_place, tv_pickup_time, tv_new_goods;
        LinearLayout ll_batch;
    }
}
