package com.zzqs.app_kc.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.activities.MainActivity;
import com.zzqs.app_kc.activities.UploadEvent.BaseUploadEventActivity;
import com.zzqs.app_kc.activities.UploadEvent.SingleEntranceAndMidwayEventActivity;
import com.zzqs.app_kc.activities.UploadEvent.SinglePickAndDeliveryEventActivity;
import com.zzqs.app_kc.db.DaoManager;
import com.zzqs.app_kc.db.hibernate.dao.BaseDao;
import com.zzqs.app_kc.entity.Order;
import com.zzqs.app_kc.entity.OrderEvent;
import com.zzqs.app_kc.utils.StringTools;
import com.zzqs.app_kc.widgets.DialogView;

import java.util.ArrayList;

/**
 * Created by lance on 15/3/24.
 */
public class OrdersAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<Order> list;
    private Handler handler;

    public OrdersAdapter(ArrayList<Order> list, Activity activity) {
        this.list = list;
        this.activity = activity;
    }

    public OrdersAdapter(ArrayList<Order> list, Activity activity, Handler handler) {
        this.list = list;
        this.activity = activity;
        this.handler = handler;
    }


    public ArrayList<Order> getList() {
        return list;
    }

    public void setList(ArrayList<Order> list) {
        this.list = list;
    }


    @Override
    public int getCount() {
        if (list == null)
            return 0;
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (i > list.size()) {
            return null;
        }
        final Order order = list.get(i);
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_order, null);
            holder = new ViewHolder();
            holder.serialNo = (TextView) convertView.findViewById(R.id.serialNo);
            holder.goodsName = (TextView) convertView.findViewById(R.id.goods_name);
            holder.quantity = (TextView) convertView.findViewById(R.id.quantity);
            holder.weight = (TextView) convertView.findViewById(R.id.weight);
            holder.volume = (TextView) convertView.findViewById(R.id.volume);
            holder.address = (TextView) convertView.findViewById(R.id.address);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.isNew = (TextView) convertView.findViewById(R.id.is_new);
            holder.changeTime = (TextView) convertView.findViewById(R.id.changeTime);
            holder.receiverName = (TextView) convertView.findViewById(R.id.receiver_name);
            holder.content = (RelativeLayout) convertView.findViewById(R.id.content);
            holder.header = (RelativeLayout) convertView.findViewById(R.id.item_header);
            holder.wayOrder = (TextView) convertView.findViewById(R.id.way_order);
            holder.entrance = (TextView) convertView.findViewById(R.id.entrance);
            holder.shootPhoto = (TextView) convertView.findViewById(R.id.pickup_or_delivery);
            holder.tv_new_goods_name = (TextView) convertView.findViewById(R.id.tv_new_goods_name);
            holder.ll_old = (LinearLayout) convertView.findViewById(R.id.ll_old);
            holder.isConfirm = (TextView) convertView.findViewById(R.id.is_confirm);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        if (order.getStatus().equals(Order.STATUS_COMMIT)) {
            holder.shootPhoto.setEnabled(false);
            holder.shootPhoto.setText(R.string.view_tv_completed);
            holder.entrance.setVisibility(View.GONE);
        } else if (order.getStatus().equals(Order.STATUS_INVALID)) {
            holder.shootPhoto.setEnabled(false);
            holder.shootPhoto.setText(R.string.view_tv_undone);
            holder.entrance.setVisibility(View.GONE);
        } else if (order.getStatus().equals(Order.UN_PICKUP) || order.getStatus().equals(Order.UN_PICKUP_ENTRANCE)) {//待提货
            holder.shootPhoto.setText(R.string.view_bt_to_pickup);
            holder.entrance.setVisibility(View.VISIBLE);
        } else if (order.getStatus().equals(Order.UN_DELIVERY) || order.getStatus().equals(Order.UN_DELIVERY_ENTRANCE)) {
            holder.shootPhoto.setText(R.string.view_bt_to_delivery);
            holder.entrance.setVisibility(View.VISIBLE);
        }

        if (null != order.getRoadOrderName()) {
            holder.content.setBackgroundColor(activity.getResources().getColor(R.color.way_order_blue));
            holder.shootPhoto.setBackgroundColor(activity.getResources().getColor(R.color.way_order_title_blue));
            holder.entrance.setBackgroundColor(activity.getResources().getColor(R.color.way_order_enter));
            convertView.findViewById(R.id.line1).setBackgroundColor(activity.getResources().getColor(R.color.line_blue));
            convertView.findViewById(R.id.line2).setBackgroundColor(activity.getResources().getColor(R.color.line_blue));
        } else {
            holder.content.setBackgroundColor(Color.rgb(255, 255, 255));
            holder.shootPhoto.setBackgroundColor(activity.getResources().getColor(R.color.bg_gray));
            holder.entrance.setBackgroundColor(activity.getResources().getColor(R.color.second_bg_gray));
            convertView.findViewById(R.id.line1).setBackgroundColor(activity.getResources().getColor(R.color.line_gray));
            convertView.findViewById(R.id.line2).setBackgroundColor(activity.getResources().getColor(R.color.line_gray));
        }
        if (!StringTools.isEmp(order.getRoadOrderName())) {
            String wayOrder = order.getRoadOrderName();
            if (i == 0) {
                holder.header.setVisibility(View.VISIBLE);
            } else {
                String lastWayOrder = list.get(i - 1).getRoadOrderName();
                if (null != lastWayOrder && lastWayOrder.equals(wayOrder)) {
                    holder.header.setVisibility(View.GONE);
                } else {
                    holder.header.setVisibility(View.VISIBLE);
                }

            }
            if (holder.header.getVisibility() == View.VISIBLE) {
                holder.wayOrder.setText("路单号：" + order.getRoadOrderName());
            }
        } else {
            holder.header.setVisibility(View.GONE);
        }

        if (!StringTools.isEmp(order.getSerialNo())) {
            if (!StringTools.isEmp(order.getRefNum())) {
                holder.serialNo.setText(order.getSerialNo() + "(" + order.getRefNum() + ")");
            } else {
                holder.serialNo.setText(order.getSerialNo());
            }
        }

        if (!StringTools.isEmp(order.getActualGoodsName())) {
            holder.ll_old.setVisibility(View.GONE);
            holder.tv_new_goods_name.setVisibility(View.VISIBLE);
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
                        if (StringTools.isEmp(units2[j])) {
                            goods.append(names[j]);
                            if (!StringTools.isEmp(counts[j])) {
                                goods.append(counts[j]);
                            }
                            if (!StringTools.isEmp(units[j])) {
                                goods.append(units[j]);
                            }
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
                        if (!StringTools.isEmp(names[j])) {
                            goods.append(names[j]);
                            if (!StringTools.isEmp(counts[j])) {
                                goods.append(counts[j]);
                            }
                            if (!StringTools.isEmp(units[j])) {
                                goods.append(units[j]);
                            }
                        }
                    }
                    if (j != names.length - 1) {
                        goods.append("  |  ");
                    }
                }
            }
            holder.tv_new_goods_name.setText(goods);
        } else {
            holder.tv_new_goods_name.setText(R.string.view_tv_no_goods_name);
        }

        if (!StringTools.isEmp(order.getToContact())) {
            holder.receiverName.setText(order.getToContact());
        } else {
            holder.receiverName.setText("");
        }


        if (order.getIsNew() == Order.NEW) {
            if (!order.getStatus().equals(Order.STATUS_COMMIT)) {
                holder.isNew.setVisibility(View.VISIBLE);
            }
            holder.isNew.setText(R.string.view_tv_new_order);
            holder.isNew.setBackgroundResource(R.drawable.bg_red_corners);
            holder.changeTime.setVisibility(View.GONE);
        } else if (order.getIsNew() == Order.UPDATE) {
            holder.isNew.setVisibility(View.VISIBLE);
            holder.isNew.setText(R.string.view_tv_modified);
            holder.isNew.setBackgroundResource(R.drawable.bg_green_corners);
        } else if (order.getStatus().equals(Order.STATUS_INVALID)) {
            holder.isNew.setVisibility(View.GONE);
            holder.changeTime.setVisibility(View.VISIBLE);
            holder.changeTime.setText(R.string.view_tv_undone);
            holder.changeTime.setTextColor(activity.getResources().getColor(R.color.red_live_2));
            holder.serialNo.setTextColor(activity.getResources().getColor(R.color.red_live_2));
        } else {
            holder.isNew.setVisibility(View.GONE);
            holder.changeTime.setVisibility(View.GONE);
            holder.serialNo.setTextColor(activity.getResources().getColor(R.color.black));
        }


        if (order.getOrderType().equals(Order.DRIVER_ORDER)) {
            if (!order.getStatus().equals(Order.STATUS_INVALID)) {
                holder.shootPhoto.setVisibility(View.VISIBLE);
                if (order.getStatus().equals(Order.UN_PICKUP_ENTRANCE) || order.getStatus().equals(Order.UN_DELIVERY_ENTRANCE)) {
                    holder.entrance.setText(activity.getResources().getString(R.string.view_tv_enter_site));
                    holder.entrance.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //已更新和新运单显示隐藏逻辑一致
                            if (order.getIsNew() == Order.NEW || order.getIsNew() == Order.UPDATE) {
                                order.setIsNew(Order.EXIST);
                            }
                            holder.isNew.setVisibility(View.GONE);
                            Intent intent = new Intent(activity, SingleEntranceAndMidwayEventActivity.class);
                            intent.putExtra(Order.ORDER_ID, order.getOrderId());
                            if (order.getStatus().equals(Order.UN_PICKUP_ENTRANCE)) {
                                intent.putExtra(BaseUploadEventActivity.EVENT_TYPE, OrderEvent.MOLD_PICKUP_ENTRANCE);
                            } else if (order.getStatus().equals(Order.UN_DELIVERY_ENTRANCE)) {
                                intent.putExtra(BaseUploadEventActivity.EVENT_TYPE, OrderEvent.MOLD_DELIVERY_ENTRANCE);
                            }
                            activity.startActivity(intent);
                        }
                    });
                } else {
                    holder.entrance.setText(activity.getResources().getString(R.string.view_tv_entered));
                    holder.entrance.setOnClickListener(null);
                }
                BaseDao<Order> orderDao = DaoManager.getOrderDao(activity.getApplication());
                orderDao.update(order);
            }
            if (order.getStatus().equals(Order.UN_PICKUP_ENTRANCE) || order.getStatus().equals(order.UN_PICKUP)) {
                holder.address.setVisibility(View.VISIBLE);
                holder.time.setVisibility(View.VISIBLE);
                if (!StringTools.isEmp(order.getFromAddress())) {
                    holder.address.setText(order.getFromAddress());
                } else {
                    holder.address.setText(R.string.view_tv_no_pickup_address);
                }
                if (!StringTools.isEmp(StringTools.outputDate(order.getPickupTimeStart(), order.getPickupTimeEnd()))) {
                    holder.time.setText(StringTools.outputDate(order.getPickupTimeStart(), order.getPickupTimeEnd()));
                } else {
                    holder.time.setText(R.string.view_tv_no_pickup_time);
                }
            } else if (order.getStatus().equals(Order.UN_DELIVERY_ENTRANCE) || order.getStatus().equals(order.UN_DELIVERY)) {
                holder.address.setVisibility(View.VISIBLE);
                holder.time.setVisibility(View.VISIBLE);
                if (!StringTools.isEmp(order.getToAddress())) {
                    holder.address.setText(order.getToAddress());
                } else {
                    holder.address.setText(R.string.view_tv_no_delivery_address);
                }
                if (!StringTools.isEmp(StringTools.outputDate(order.getDeliverTimeStart(), order.getDeliverTimeEnd()))) {
                    holder.time.setText(StringTools.outputDate(order.getDeliverTimeStart(), order.getDeliverTimeEnd()));
                } else {
                    holder.time.setText(R.string.view_tv_no_delivery_time);
                }
            } else {
                holder.address.setVisibility(View.GONE);
                holder.time.setVisibility(View.GONE);
            }
        } else if (order.getOrderType().equals(Order.WAREHOUSE_ORDER)) {
            holder.entrance.setVisibility(View.GONE);
            holder.entrance.setVisibility(View.GONE);
            if (order.getStatus().equals(Order.UN_DELIVERY)) {
                holder.address.setVisibility(View.VISIBLE);
                holder.time.setVisibility(View.VISIBLE);
                if (!StringTools.isEmp(order.getToAddress())) {
                    holder.address.setText(order.getToAddress());
                } else {
                    holder.address.setText(R.string.view_tv_no_address);
                }
                if (!StringTools.isEmp(StringTools.outputDate(order.getDeliverTimeStart(), order.getDeliverTimeEnd()))) {
                    holder.time.setText(StringTools.outputDate(order.getDeliverTimeStart(), order.getDeliverTimeEnd()));
                } else {
                    holder.time.setText(R.string.view_tv_no_time);
                }
            }
        } else {
            holder.address.setVisibility(View.GONE);
            holder.time.setVisibility(View.GONE);
        }

        holder.shootPhoto.setOnClickListener(new View.OnClickListener() {//去提货／去交货
            @Override
            public void onClick(View view) {
                if (order.getIsNew() == Order.NEW || order.getIsNew() == Order.UPDATE) {
                    order.setIsNew(Order.EXIST);
                }
                boolean mustPickupEntrance = order.getStatus().equals(Order.UN_PICKUP_ENTRANCE) && order.getPickupEntranceForce().equals("true");
                boolean mustDeliveryEntrance = order.getStatus().equals(Order.UN_DELIVERY_ENTRANCE) && order.getDeliveryEntranceForce().equals("true");
                if (mustPickupEntrance || mustDeliveryEntrance) {
                    String msg = null;
                    if (mustPickupEntrance) {
                        msg = activity.getString(R.string.prompt_must_pickup_enter);
                    } else if (mustDeliveryEntrance) {
                        msg = activity.getString(R.string.prompt_must_delivery_enter);
                    }
                    DialogView.showChoiceDialog(activity, DialogView.SINGLE_BTN, activity.getString(R.string.prompt_dl_title_1), msg, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            if (msg.what == DialogView.ACCEPT) {
                                toUploadEventActivity(activity, i, true);//有强制进场
                            }
                        }
                    });
                } else {
                    toUploadEventActivity(activity, i, false);//没有强制进场
                }

            }
        });


        if (order.getConfirmStatus().equals(Order.CONFIRMED)) {
            holder.isConfirm.setVisibility(View.GONE);
        } else {
            holder.isConfirm.setVisibility(View.VISIBLE);
            holder.isConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message msg = handler.obtainMessage();
                    msg.what = MainActivity.ORDER_CONFIRM;
                    msg.obj = order;
                    handler.sendMessage(msg);
                }
            });
        }

        return convertView;

    }

    public class ViewHolder {
        TextView serialNo, goodsName, quantity, weight, volume, address, time, isNew, changeTime, receiverName, wayOrder, entrance, shootPhoto, isConfirm, tv_new_goods_name;
        RelativeLayout header, content;
        LinearLayout ll_old;
    }


    /**
     * 提货前检查是否有强制进场
     */
    public void toUploadEventActivity(Activity activity, int i, boolean hasForcibly) {
        Intent intent;
        if (hasForcibly) {
            if (list.get(i).getStatus().equals(Order.UN_PICKUP_ENTRANCE) || list.get(i).getStatus().equals(Order.UN_DELIVERY_ENTRANCE)) {
                intent = new Intent(activity.getApplication(), SingleEntranceAndMidwayEventActivity.class);
                intent.putExtra(Order.ORDER_ID, list.get(i).getOrderId());
                if (list.get(i).getStatus().equals(Order.UN_PICKUP_ENTRANCE)) {
                    intent.putExtra(BaseUploadEventActivity.EVENT_TYPE, OrderEvent.MOLD_PICKUP_ENTRANCE);
                } else if (list.get(i).getStatus().equals(Order.UN_DELIVERY_ENTRANCE)) {
                    intent.putExtra(BaseUploadEventActivity.EVENT_TYPE, OrderEvent.MOLD_DELIVERY_ENTRANCE);
                }
            } else {
                Toast.makeText(activity, R.string.prompt_cannot_repeat_enter, Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            intent = new Intent(activity.getApplication(), SinglePickAndDeliveryEventActivity.class);
            intent.putExtra(Order.ORDER_ID, list.get(i).getOrderId());
            String type;
            if (list.get(i).getStatus().equals(Order.UN_PICKUP_ENTRANCE) || list.get(i).getStatus().equals(Order.UN_PICKUP)) {
                type = OrderEvent.MOLD_PICKUP;
            } else {
                type = OrderEvent.MOLD_DELIVERY;
            }
            intent.putExtra(BaseUploadEventActivity.EVENT_TYPE, type);
        }
        activity.startActivity(intent);
    }
}
