package com.zzqs.app_kc.z_kc.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.z_kc.entitiy.Tender;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;
import com.zzqs.app_kc.z_kc.util.NumberUtil;
import com.zzqs.app_kc.z_kc.util.TimeUtil;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lance on 2016/12/4.
 */

public class ZCOrderDetailActivity extends BaseActivity {
  TextView tvLeft, tvTitle, tvRight, tvRemainingTime, tvMaxPrince, tvStartCity, tvStartDistrict, tvEndCity, tvEndDistrict, tvNeedCars, tvGoodsInfo, tvRemark, tvInitiator, tvInitiatorName, tvInitiatorPhone, tvPickupTime, tvPickupAddress, tvDeliveryTime, tvDeliveryAddress, tvPayWay, tvFirstPay, tvLastPay, tvReceipt, tvFreight, tvGrabOrder, tvBiddingOrder;
  LinearLayout llBiddingOrderHead, llGrabOrder, llBiddingOrder, llFirstPay, llLastPay, llReceiptPay;
  EditText etBiddingPrince;

  private Tender order;
  private Timer timer;
  private long remainingTime;


  @Override
  public void initVariables() {
    order = getIntent().getParcelableExtra(Tender.TENDER);
  }

  @Override
  public void initViews(Bundle savedInstanceState) {
    setContentView(R.layout.z_kc_act_order_detail);
    tvLeft = (TextView) findViewById(R.id.head_back);
    tvLeft.setText("");
    tvLeft.setOnClickListener(new MyOnClickListener() {
      @Override
      public void OnceOnClick(View view) {
        finish();
      }
    });
    tvTitle = (TextView) findViewById(R.id.head_title);
    tvRight = (TextView) findViewById(R.id.head_right);
    tvRight.setText(R.string.contact_customer_service);
    tvRight.setVisibility(View.VISIBLE);

    tvRemainingTime = (TextView) findViewById(R.id.tvRemainingTime);
    tvMaxPrince = (TextView) findViewById(R.id.tvMaxPrince);
    tvStartCity = (TextView) findViewById(R.id.tvStartCity);
    tvStartDistrict = (TextView) findViewById(R.id.tvStartDistrict);
    tvEndCity = (TextView) findViewById(R.id.tvEndCity);
    tvEndDistrict = (TextView) findViewById(R.id.tvEndDistrict);
    tvNeedCars = (TextView) findViewById(R.id.tvNeedCars);
    tvGoodsInfo = (TextView) findViewById(R.id.tvGoodsInfo);
    tvRemark = (TextView) findViewById(R.id.tvRemark);
    tvInitiator = (TextView) findViewById(R.id.tvInitiator);
    tvInitiatorName = (TextView) findViewById(R.id.tvInitiatorName);
    tvInitiatorPhone = (TextView) findViewById(R.id.tvInitiatorPhone);
    tvPickupTime = (TextView) findViewById(R.id.tvPickupTime);
    tvPickupAddress = (TextView) findViewById(R.id.tvPickupAddress);
    tvDeliveryTime = (TextView) findViewById(R.id.tvDeliveryTime);
    tvDeliveryAddress = (TextView) findViewById(R.id.tvDeliveryAddress);
    tvPayWay = (TextView) findViewById(R.id.tvPayWay);
    tvFirstPay = (TextView) findViewById(R.id.tvFirstPay);
    tvLastPay = (TextView) findViewById(R.id.tvLastPay);
    tvReceipt = (TextView) findViewById(R.id.tvReceipt);
    tvFreight = (TextView) findViewById(R.id.tvFreight);

    tvGrabOrder = (TextView) findViewById(R.id.tvGrabOrder);
    tvBiddingOrder = (TextView) findViewById(R.id.tvBiddingOrder);

    llBiddingOrderHead = (LinearLayout) findViewById(R.id.llBiddingOrderHead);
    llGrabOrder = (LinearLayout) findViewById(R.id.llGrabOrder);
    llBiddingOrder = (LinearLayout) findViewById(R.id.llBiddingOrder);
    etBiddingPrince = (EditText) findViewById(R.id.etBiddingPrince);
    llFirstPay = (LinearLayout) findViewById(R.id.llFirstPay);
    llLastPay = (LinearLayout) findViewById(R.id.llLastPay);
    llReceiptPay = (LinearLayout) findViewById(R.id.llReceiptPay);

    if (order.getTender_type().equals(Tender.NORMAL_TENDER)) {
      tvTitle.setText(R.string.grab_details);
      llBiddingOrderHead.setVisibility(View.GONE);
      llBiddingOrder.setVisibility(View.GONE);
      DecimalFormat df = new DecimalFormat("######0.00");
      tvFreight.setText(df.format(order.getFixed_prince()));
      tvGrabOrder.setOnClickListener(new MyOnClickListener() {
        @Override
        public void OnceOnClick(View view) {
          //抢单
        }
      });
    } else {
      tvTitle.setText(R.string.bidding_details);
      llGrabOrder.setVisibility(View.GONE);
      long startTime = System.currentTimeMillis();
      long endTime = TimeUtil.UTCTimeToTimeMillis(order.getEnd_time());
      remainingTime = endTime - startTime;
      if (remainingTime > 0) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
          @Override
          public void run() {
            remainingTime -= 1000;
            setRemainingTime();
          }
        }, 0, 1000);
        tvMaxPrince.setText(order.getMax_prince() + getString(R.string.prince_unit));
        tvBiddingOrder.setOnClickListener(new MyOnClickListener() {
          @Override
          public void OnceOnClick(View view) {
            //出价
          }
        });
      } else {
        tvRemainingTime.setText("已结束");
        llBiddingOrder.setVisibility(View.GONE);
      }
    }

    tvStartCity.setText(order.getPickup_city());
    tvStartDistrict.setText(order.getPickup_region());
    tvEndCity.setText(order.getEnd_city());
    tvEndDistrict.setText(order.getEnd_district());
    tvNeedCars.setText(order.getTruck_type() + "  "  + "1 辆");
    StringBuilder sb = new StringBuilder();
    if (order.getTotal_quantity() != 0) {
      sb.append(order.getTotal_quantity() + order.getQuantity_unit());
    }
    if (order.getTotal_volume() != 0) {
      if (sb.length() > 0) {
        sb.append("/");
      }
      sb.append(order.getTotal_volume() + order.getVolume_unit());
    }
    if (order.getTotal_weight() != 0) {
      if (sb.length() > 0) {
        sb.append("/");
      }
      sb.append(order.getTotal_weight() + order.getWeight_unit());
    }
    tvGoodsInfo.setText(sb.toString());
    sb.setLength(0);

    tvRemark.setText(TextUtils.isEmpty(order.getRemark()) ? "-" : order.getRemark());
    tvInitiator.setText(order.getInitiator());
    tvInitiatorName.setText(order.getInitiator_name());
    tvInitiatorPhone.setText(order.getInitiator_phone());
    tvPickupTime.setText(TimeUtil.convertDateStringFormat(order.getPickup_start_time(), TimeUtil.SERVER_TIME_FORMAT, "MM-dd HH:mm") + "~" + TimeUtil.convertDateStringFormat(order.getPickup_end_time(), TimeUtil.SERVER_TIME_FORMAT, "MM-dd HH:mm"));
    tvPickupAddress.setText(order.getPickup_street());
    tvDeliveryTime.setText(TimeUtil.convertDateStringFormat(order.getDelivery_start_time(), TimeUtil.SERVER_TIME_FORMAT, "MM-dd HH:mm") + "~" + TimeUtil.convertDateStringFormat(order.getDelivery_end_time(), TimeUtil.SERVER_TIME_FORMAT, "MM-dd HH:mm"));
    tvDeliveryAddress.setText(order.getEnd_address());
    if (order.getFirst_pay() > 0) {
      sb.append(getString(R.string.first_pay));
    } else if (order.getLast_pay() > 0) {
      sb.append("+" + getString(R.string.last_pay));
    }
    if (order.getReceipt_pay() > 0) {
      sb.append("+" + getString(R.string.receipt));
    }
    tvPayWay.setText(sb.toString());
    sb.setLength(0);

    if (order.getFirst_pay() > 0) {
      sb.append(NumberUtil.doubleTrans(order.getFirst_pay()) + "%   ");
      if (order.getFirst_pay_cash() == 100) {
        sb.append(getString(R.string.cash_pay));
      } else if (order.getFirst_pay_oil_card() == 100) {
        sb.append(getString(R.string.oil_card_pay));
      } else {
        sb.append("(" + NumberUtil.doubleTrans(order.getFirst_pay_cash()) + "%" + getString(R.string.cash) + "  +  " + NumberUtil.doubleTrans(order.getFirst_pay_oil_card()) + "%" + getString(R.string.oil_card) + ")");
      }
      tvFirstPay.setText(sb.toString());
      sb.setLength(0);
    } else {
      llFirstPay.setVisibility(View.GONE);
    }

    if (order.getLast_pay() > 0) {
      sb.append(NumberUtil.doubleTrans(order.getLast_pay()) + "%   ");
      if (order.getLast_pay_cash() == 100) {
        sb.append("(" + getString(R.string.cash_pay) + ")");
      } else if (order.getLast_pay_oil_card() == 100) {
        sb.append("(" + getString(R.string.oil_card_pay) + ")");
      } else {
        sb.append("(" + NumberUtil.doubleTrans(order.getLast_pay_cash()) + "%" + getString(R.string.cash) + "  +  " + NumberUtil.doubleTrans(order.getLast_pay_oil_card()) + "%" + getString(R.string.oil_card) + ")");
      }
      tvLastPay.setText(sb.toString());
      sb.setLength(0);
    } else {
      llLastPay.setVisibility(View.GONE);
    }

    if (order.getReceipt_pay() > 0) {
      sb.append(NumberUtil.doubleTrans(order.getReceipt_pay()) + "%   ");
      if (order.getReceipt_pay_cash() == 100) {
        sb.append("(" + getString(R.string.cash_pay) + ")");
      } else if (order.getReceipt_pay_oil_card() == 100) {
        sb.append("(" + getString(R.string.oil_card_pay) + ")");
      } else {
        sb.append("(" + NumberUtil.doubleTrans(order.getReceipt_pay_cash()) + "%" + getString(R.string.cash) + "  +  " + NumberUtil.doubleTrans(order.getReceipt_pay_oil_card()) + "%" + getString(R.string.oil_card) + ")");
      }
      tvReceipt.setText(sb.toString());
      sb.setLength(0);
    } else {
      llReceiptPay.setVisibility(View.GONE);
    }
  }

  @Override
  public void loadData() {
  }

  private Handler changeTimeHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      tvRemainingTime.setText(msg.obj.toString());
    }
  };
  private StringBuilder timeStringBuilder = new StringBuilder();

  private void setRemainingTime() {
    long days = remainingTime / (1000 * 60 * 60 * 24);
    long hours = (remainingTime % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
    long minutes = (remainingTime % (1000 * 60 * 60)) / (1000 * 60);
    long seconds = (remainingTime % (1000 * 60)) / 1000;
    if (days < 10) {
      timeStringBuilder.append("0" + days + "天");
    } else {
      timeStringBuilder.append(days + "天");
    }
    if (hours < 10) {
      timeStringBuilder.append("0" + hours + "小时");
    } else {
      timeStringBuilder.append(hours + "小时");
    }
    if (minutes < 10) {
      timeStringBuilder.append("0" + minutes + "分");
    } else {
      timeStringBuilder.append(minutes + "分");
    }
    if (seconds < 10) {
      timeStringBuilder.append("0" + seconds + "秒");
    } else {
      timeStringBuilder.append(seconds + "秒");
    }
    Message msMessage = changeTimeHandler.obtainMessage();
    msMessage.obj = timeStringBuilder.toString();
    timeStringBuilder.setLength(0);
    changeTimeHandler.sendMessage(msMessage);
  }
}
