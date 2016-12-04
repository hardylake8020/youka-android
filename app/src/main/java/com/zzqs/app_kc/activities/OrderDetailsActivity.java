package com.zzqs.app_kc.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.entity.Order;
import com.zzqs.app_kc.utils.StringTools;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lance on 15/3/25.
 */
public class OrderDetailsActivity extends BaseActivity implements View.OnClickListener {
  TextView head_message, serialNo, goods_name, totalQuantity, totalWeight, totalVolume, quantityUnit, weightUnit, volumeUnit, remark, pickupTime,
      pickupAddress, pickupContact, pickupPhone, pickup_telephone, delivery_telephone, deliveryTime, deliveryAddress, deliveryContact, deliveryPhone, checkTimeAxis, refNum, senderName, receiverName, damaged, combined;
  LinearLayout ll_detail_goods;
  private Order order;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.act_waybill_details);
    initView();
    initData();
  }

  private void initView() {
    findViewById(R.id.head_back).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
      }
    });
    checkTimeAxis = (TextView) findViewById(R.id.check_time_axis);
    checkTimeAxis.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), OrderTimeAxisActivity.class);
        intent.putExtra(Order.ORDER, order);
        startActivity(intent);
      }
    });
    head_message = (TextView) findViewById(R.id.head_title);
    serialNo = (TextView) findViewById(R.id.serialNo);
    goods_name = (TextView) findViewById(R.id.goods_name);
    totalQuantity = (TextView) findViewById(R.id.total_quantity);
    totalWeight = (TextView) findViewById(R.id.total_weight);
    totalVolume = (TextView) findViewById(R.id.total_volume);
    quantityUnit = (TextView) findViewById(R.id.quantity_unit);
    weightUnit = (TextView) findViewById(R.id.weight_unit);
    volumeUnit = (TextView) findViewById(R.id.volume_unit);
    remark = (TextView) findViewById(R.id.remark);
    pickupTime = (TextView) findViewById(R.id.pickup_time);
    pickupAddress = (TextView) findViewById(R.id.pickup_address);
    pickupContact = (TextView) findViewById(R.id.pickup_contact);
    pickupPhone = (TextView) findViewById(R.id.pickup_phone);
    deliveryTime = (TextView) findViewById(R.id.delivery_time);
    deliveryAddress = (TextView) findViewById(R.id.delivery_address);
    deliveryContact = (TextView) findViewById(R.id.delivery_contact);
    deliveryPhone = (TextView) findViewById(R.id.delivery_phone);
    senderName = (TextView) findViewById(R.id.sender_name);
    receiverName = (TextView) findViewById(R.id.receiver_name);
    damaged = (TextView) findViewById(R.id.damaged);
    refNum = (TextView) findViewById(R.id.ref_num);
    pickup_telephone = (TextView) findViewById(R.id.pickup_telephone);
    delivery_telephone = (TextView) findViewById(R.id.delivery_telephone);
    combined = (TextView) findViewById(R.id.combined);
    ll_detail_goods = (LinearLayout) findViewById(R.id.ll_detail_goods);
    delivery_telephone.setOnClickListener(this);
    pickup_telephone.setOnClickListener(this);
    pickupPhone.setOnClickListener(this);
    deliveryPhone.setOnClickListener(this);
  }

  private void initData() {
    head_message.setText(R.string.view_tv_waybill_details);
    StringBuilder stringbuilder = new StringBuilder();
    order = getIntent().getParcelableExtra(Order.ORDER);
    serialNo.setText(order.getSerialNo());

    if (!StringTools.isEmp(order.getActualGoodsId())) {//新版本创建的运单
      ll_detail_goods.setVisibility(View.GONE);
      if (!StringTools.isEmp(order.getActualGoodsName())) {
        StringBuffer sb = new StringBuffer();
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
                sb.append(names[j] + "\n" + counts[j] + " " + units[j]);
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
                sb.append(names[j] + "\n" + count + unit + "  " + count2 + " " + unit2 + "  " + count3 + " " + unit3);
              }
            } else {
              sb.append(names[j] + "\n" + counts[j] + units[j]);
            }
            if (j != names.length - 1) {
              sb.append("\n");
            }
          }
        }
        goods_name.setText(sb.toString());
        sb.setLength(0);
        if (total != null) {
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
      ll_detail_goods.setVisibility(View.VISIBLE);
      if (!StringTools.isEmp(order.getGoodsName())) {
        goods_name.setText(order.getGoodsName());
      }
    }

    if (!StringTools.isEmp(order.getRefNum())) {
      refNum.setText(order.getRefNum());
    }
    if (!StringTools.isEmp(order.getSenderName())) {
      senderName.setText(order.getSenderName());
    }
    if (!StringTools.isEmp(order.getReceiverName())) {
      receiverName.setText(order.getReceiverName());
    }
    if ((!StringTools.isEmp(order.getDamaged()) && order.getDamaged().equals("true"))) {
      damaged.setText(R.string.view_tv_has_damage);
    } else {
      damaged.setText(R.string.view_tv_no_damage);
    }
    quantityUnit.setText("(" + order.getQuantityUnit() + ")/");
    if (!StringTools.isEmp(order.getTotalQuantity())) {
      totalQuantity.setText(order.getTotalQuantity() + "");
    }
    weightUnit.setText("(" + order.getWeightUnit() + ")/");
    if (!StringTools.isEmp(order.getTotalWeight())) {
      totalWeight.setText(order.getTotalWeight() + "");
    }
    volumeUnit.setText("(" + order.getVolumeUnit() + ")");
    if (!StringTools.isEmp(order.getTotalVolume())) {
      totalVolume.setText(order.getTotalVolume() + "");
    }
    if (!StringTools.isEmp(order.getDescription())) {
      remark.setText(order.getDescription());
    }
    if (!StringTools.isEmp(order.getPickupTimeStart())) {
      stringbuilder.append(order.getPickupTimeStart());
    }
    if (!StringTools.isEmp(order.getPickupTimeEnd())) {
      if (stringbuilder.length() > 0) {
        stringbuilder.append(" -- ");
      }
      stringbuilder.append(order.getPickupTimeEnd());
    }
    if (stringbuilder.length() != 0) {
      pickupTime.setText(stringbuilder.toString());
    }
    stringbuilder.setLength(0);
    if (!StringTools.isEmp(order.getFromAddress())) {
      pickupAddress.setText(order.getFromAddress());
    }
    if (!StringTools.isEmp(order.getFromContact())) {
      pickupContact.setText(order.getFromContact());
    }

    if (!StringTools.isEmp(order.getFromMobilePhone())) {
      pickupPhone.setTextColor(getResources().getColor(R.color.primary_colors));
      pickupPhone.setText(order.getFromMobilePhone());
    }

    if (!StringTools.isEmp(order.getFromPhone())) {
      pickup_telephone.setTextColor(getResources().getColor(R.color.primary_colors));
      pickup_telephone.setText(order.getFromPhone());
    }
    if (!StringTools.isEmp(order.getToPhone())) {
      delivery_telephone.setTextColor(getResources().getColor(R.color.primary_colors));
      delivery_telephone.setText(order.getToPhone());
    }
    stringbuilder.setLength(0);

    if (!StringTools.isEmp(order.getDeliverTimeStart())) {
      stringbuilder.append(order.getDeliverTimeStart());
    }
    if (!StringTools.isEmp(order.getDeliverTimeEnd())) {
      if (stringbuilder.length() > 0)
        stringbuilder.append(" -- ");
      stringbuilder.append(order.getDeliverTimeEnd());
    }
    if (stringbuilder.length() != 0) {
      deliveryTime.setText(stringbuilder.toString());
    }
    stringbuilder.setLength(0);
    if (!StringTools.isEmp(order.getToAddress())) {
      deliveryAddress.setText(order.getToAddress());
    }
    if (!StringTools.isEmp(order.getToContact())) {
      deliveryContact.setText(order.getToContact());
    }

    if (!StringTools.isEmp(order.getToMobilePhone())) {
      deliveryPhone.setTextColor(getResources().getColor(R.color.primary_colors));
      deliveryPhone.setText(order.getToMobilePhone());
    }
    stringbuilder.setLength(0);
  }

  @Override
  public void onClick(View view) {
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
}
