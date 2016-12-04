package com.zzqs.app_kc.activities.OrderOperation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.activities.UploadEvent.BaseUploadEventActivity;
import com.zzqs.app_kc.activities.UploadEvent.SingleEntranceAndMidwayEventActivity;
import com.zzqs.app_kc.activities.UploadEvent.SinglePickAndDeliveryEventActivity;
import com.zzqs.app_kc.entity.Order;
import com.zzqs.app_kc.entity.OrderEvent;
import com.zzqs.app_kc.utils.StringTools;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lance on 15/11/19.
 */
public class SingleOperationActivity extends BaseOperationActivity {
  TextView orderNo, refNo, goodsName, parameters, remark, sendName, receiverName, pickupAddress, pickupTime, pickupContact, pickupPhone, pickup_telephone, delivery_telephone, deliveryAddress, deliveryTime, deliveryContact, deliveryPhone, combined;
  LinearLayout ll_old_data;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    init();
  }


  private void init() {
    setOrder(order);
    ScrollView orderDetailView = (ScrollView) inflater.inflate(R.layout.view_order_detail, null);
    insertView.addView(orderDetailView);
    orderNo = (TextView) findViewById(R.id.serialNo);
    refNo = (TextView) findViewById(R.id.ref_num);
    goodsName = (TextView) findViewById(R.id.goods_name);
    parameters = (TextView) findViewById(R.id.parameters);
    remark = (TextView) findViewById(R.id.remark);
    sendName = (TextView) findViewById(R.id.sender_name);
    receiverName = (TextView) findViewById(R.id.receiver_name);
    pickupAddress = (TextView) findViewById(R.id.pickup_address);
    pickupTime = (TextView) findViewById(R.id.pickup_time);
    pickupContact = (TextView) findViewById(R.id.pickup_contact);
    pickupPhone = (TextView) findViewById(R.id.pickup_phone);
    pickup_telephone = (TextView) findViewById(R.id.pickup_telephone);
    deliveryAddress = (TextView) findViewById(R.id.delivery_address);
    deliveryTime = (TextView) findViewById(R.id.delivery_time);
    deliveryContact = (TextView) findViewById(R.id.delivery_contact);
    deliveryPhone = (TextView) findViewById(R.id.delivery_phone);
    delivery_telephone = (TextView) findViewById(R.id.delivery_telephone);
    combined = (TextView) findViewById(R.id.combined);
    ll_old_data = (LinearLayout) findViewById(R.id.ll_old_data);
    pickupPhone.setOnClickListener(this);
    pickup_telephone.setOnClickListener(this);
    deliveryPhone.setOnClickListener(this);
    delivery_telephone.setOnClickListener(this);
    timeLine.setVisibility(View.VISIBLE);
    setOrderInfoText(order);
  }

  @Override
  public void toShootActivity() {
    Intent intent = new Intent(getApplicationContext(), SinglePickAndDeliveryEventActivity.class);
    intent.putExtra(Order.ORDER_ID, order.getOrderId());
    String type;
    if (order.getStatus().equals(Order.UN_PICKUP_ENTRANCE) || order.getStatus().equals(Order.UN_PICKUP)) {
      type = OrderEvent.MOLD_PICKUP;
    } else {
      type = OrderEvent.MOLD_DELIVERY;
    }
    intent.putExtra(BaseUploadEventActivity.EVENT_TYPE, type);
    startActivity(intent);
  }

  @Override
  public void toUploadEventActivity(int type) {
    Intent intent = null;
    if (type == TO_ENTRANCE) {
      if (order.getStatus().equals(Order.UN_PICKUP_ENTRANCE) || order.getStatus().equals(Order.UN_DELIVERY_ENTRANCE)) {
        intent = new Intent(getApplicationContext(), SingleEntranceAndMidwayEventActivity.class);
        intent.putExtra(Order.ORDER_ID, order.getOrderId());
        if (order.getStatus().equals(Order.UN_PICKUP_ENTRANCE)) {
          intent.putExtra(BaseUploadEventActivity.EVENT_TYPE, OrderEvent.MOLD_PICKUP_ENTRANCE);
        } else if (order.getStatus().equals(Order.UN_DELIVERY_ENTRANCE)) {
          intent.putExtra(BaseUploadEventActivity.EVENT_TYPE, OrderEvent.MOLD_DELIVERY_ENTRANCE);
        }
      } else {
        Toast.makeText(this, R.string.prompt_unable_repeat_enter, Toast.LENGTH_SHORT).show();
        return;
      }
    } else if (type == TO_HALF_EVENT) {
      intent = new Intent(getApplicationContext(), SingleEntranceAndMidwayEventActivity.class);
      intent.putExtra(Order.ORDER_ID, order.getOrderId());
      intent.putExtra(BaseUploadEventActivity.EVENT_TYPE, OrderEvent.MOLD_HALFWAY);
      startActivityForResult(intent, UPLOAD_EVENT);
      return;
    }
    startActivity(intent);
  }

  private void setOrderInfoText(Order order) {
    StringBuffer sb = new StringBuffer();
    orderNo.setText(order.getSerialNo() + "");
    refNo.setText(checkFiled(order.getRefNum()) + "");
    if (!StringTools.isEmp(order.getActualGoodsId())) {//新版本创建的运单
      ll_old_data.setVisibility(View.GONE);
      if (!StringTools.isEmp(order.getActualGoodsName())) {
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
                goods.append(names[j] + "\n" + counts[j] + " " + units[j]);
                addToTotal(units[j], Double.valueOf(counts[j]));
              } else {
                String count = "", count2 = "", count3 = "", unit = "", unit2 = "", unit3 = "";
                if (!StringTools.isEmp(counts[j])) {
                  count = counts[j].toString();
                  unit = units[j].toString();
                  addToTotal(unit, Double.valueOf(count));
                }
                if (!StringTools.isEmp(counts2[j])) {
                  count2 = counts2[j].toString();
                  unit2 = units2[j].toString();
                  addToTotal(unit2, Double.valueOf(count2));
                }
                if (!StringTools.isEmp(counts3[j])) {
                  count3 = counts3[j].toString();
                  unit3 = units3[j].toString();
                  addToTotal(unit3, Double.valueOf(count3));
                }
                goods.append(names[j] + "\n" + count + unit + "  " + count2 + " " + unit2 + "  " + count3 + " " + unit3);
              }
            } else {
              goods.append(names[j] + "\n" + counts[j] + units[j]);
            }
            if (j != names.length - 1) {
              goods.append("\n");
            }
          }
        }
        goodsName.setText(goods.toString());
        if (total != null) {
          sb.setLength(0);
          for (Map.Entry<String, Double> entry : total.entrySet()) {
            sb.append(entry.getValue() + " " + entry.getKey() + "  ");
          }
          if (sb.length() > 0) {
            combined.setText(sb.toString());
            sb.setLength(0);
          }
        }
      }
    } else {
      goodsName.setText(checkFiled(order.getGoodsName()) + "");
    }
    if (parameters.getVisibility() == View.VISIBLE) {
      sb.append(!StringTools.isEmp(order.getTotalQuantity()) ? order.getTotalQuantity() + " " : "-");
      sb.append(!StringTools.isEmp(order.getQuantityUnit()) ? order.getQuantityUnit() : "");
      sb.append(!StringTools.isEmp(order.getTotalWeight()) ? "/" + order.getTotalWeight() + " " : "-");
      sb.append(!StringTools.isEmp(order.getWeightUnit()) ? order.getWeightUnit() : "");
      sb.append(!StringTools.isEmp(order.getTotalVolume()) ? "/" + order.getTotalVolume() + " " : "-");
      sb.append(!StringTools.isEmp(order.getVolumeUnit()) ? order.getVolumeUnit() : "");
      if (sb.length() != 0 && !sb.toString().trim().equals("箱吨立方")) {
        if (StringTools.isEmp(order.getTotalQuantity()) && StringTools.isEmp(order.getTotalWeight()) && StringTools.isEmp(order.getTotalVolume())) {
          parameters.setText("");

        } else {
          parameters.setText(sb.toString());
        }
      }
      sb.setLength(0);
    }

    remark.setText(checkFiled(order.getDescription()) + "");
    sendName.setText(checkFiled(order.getSenderName()) + "");
    receiverName.setText(checkFiled(order.getReceiverName()) + "");

    pickupAddress.setText(checkFiled(order.getFromAddress()) + "");

    sb.append(checkFiled(order.getPickupTimeStart()));
    if (sb.length() > 0) {
      if (checkFiled(order.getPickupTimeEnd()).length() > 0) {
        sb.append(" ～ ");
        sb.append(checkFiled(order.getPickupTimeEnd()));
      }
    } else {
      if (checkFiled(order.getPickupTimeEnd()).length() > 0) {
        sb.append(checkFiled(order.getPickupTimeEnd()));
      }
    }

    pickupTime.setText(sb.toString());
    sb.setLength(0);

    pickupContact.setText(checkFiled(order.getFromContact()));

    sb.append(checkFiled(order.getFromMobilePhone()));
    pickupPhone.setTextColor(getResources().getColor(R.color.primary_colors));
    pickupPhone.setText(sb.toString());
    sb.setLength(0);


    deliveryAddress.setText(checkFiled(order.getToAddress()) + "");

    sb.append(checkFiled(order.getDeliverTimeStart()));
    if (sb.length() > 0) {
      if (checkFiled(order.getDeliverTimeEnd()).length() > 0) {
        sb.append(" ～ ");
        sb.append(checkFiled(order.getDeliverTimeEnd()));
      }
    } else {
      if (checkFiled(order.getDeliverTimeEnd()).length() > 0) {
        sb.append(checkFiled(order.getDeliverTimeEnd()));
      }
    }
    deliveryTime.setText(sb.toString());
    sb.setLength(0);

    deliveryContact.setText(checkFiled(order.getToContact()));

    sb.append(checkFiled(order.getToMobilePhone()));
    deliveryPhone.setTextColor(getResources().getColor(R.color.primary_colors));
    deliveryPhone.setText(sb.toString());
    sb.setLength(0);
    if (!StringTools.isEmp(order.getFromPhone())) {
      pickup_telephone.setTextColor(getResources().getColor(R.color.primary_colors));
      pickup_telephone.setText(order.getFromPhone());
    }
    if (!StringTools.isEmp(order.getToPhone())) {
      delivery_telephone.setTextColor(getResources().getColor(R.color.primary_colors));
      delivery_telephone.setText(order.getToPhone());
    }

  }

  private HashMap<String, Double> total = new HashMap<>();
  private void addToTotal(String key, double count) {
    if (total.containsKey(key)) {
      double oldCount = total.get(key);
      oldCount += count;
      total.put(key, oldCount);
    } else {
      total.put(key, count);
    }
  }

  private String checkFiled(String filed) {
    if (!StringTools.isEmp(filed)) {
      return filed;
    } else {
      return "";
    }
  }

  @Override
  public void onClick(View view) {
    super.onClick(view);
    Intent intent = new Intent(Intent.ACTION_DIAL);
    switch (view.getId()) {
      case R.id.pickup_phone:
        if (!StringTools.isEmp(order.getFromMobilePhone())) {
          Uri data = Uri.parse("tel:" + order.getFromMobilePhone());
          intent.setData(data);
          startActivity(intent);
        }
        break;
      case R.id.pickup_telephone:
        if (!StringTools.isEmp(order.getFromPhone())) {
          Uri data = Uri.parse("tel:" + order.getFromPhone());
          intent.setData(data);
          startActivity(intent);
        }
        break;
      case R.id.delivery_phone:
        if (!StringTools.isEmp(order.getToMobilePhone())) {
          Uri data = Uri.parse("tel:" + order.getToMobilePhone());
          intent.setData(data);
          startActivity(intent);
        }
        break;
      case R.id.delivery_telephone:
        if (!StringTools.isEmp(order.getToPhone())) {
          Uri data = Uri.parse("tel:" + order.getToPhone());
          intent.setData(data);
          startActivity(intent);
        }
        break;
      default:
        break;
    }
  }
}
