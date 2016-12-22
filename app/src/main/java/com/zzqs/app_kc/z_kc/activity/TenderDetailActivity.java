package com.zzqs.app_kc.z_kc.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.utils.CommonTools;
import com.zzqs.app_kc.z_kc.entitiy.ErrorInfo;
import com.zzqs.app_kc.z_kc.entitiy.Goods;
import com.zzqs.app_kc.z_kc.entitiy.OilCard;
import com.zzqs.app_kc.z_kc.entitiy.Tender;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;
import com.zzqs.app_kc.z_kc.network.OilCardApiImpl;
import com.zzqs.app_kc.z_kc.util.NumberUtil;
import com.zzqs.app_kc.z_kc.util.TimeUtil;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.http.HEAD;
import rx.Subscriber;

/**
 * Created by lance on 2016/12/4.
 */

public class TenderDetailActivity extends BaseActivity {
    TextView tvLeft, tvTitle, tvRight, tvRemainingTime, tvMaxPrince, tvStartCity, tvStartDistrict, tvEndCity, tvEndDistrict, tvNeedCars, tvGoodsInfo, tvRemark, tvInitiator, tvInitiatorName, tvInitiatorPhone, tvPickupTime, tvPickupAddress, tvDeliveryTime, tvDeliveryAddress, tvPayWay, tvFirstPay, tvLastPay, tvReceipt, tvFreight, tvGrabOrder, tvBiddingOrder;
    LinearLayout llBiddingOrderHead, llGrabOrder, llBiddingOrder, llFirstPay, llLastPay, llReceiptPay;
    EditText etBiddingPrince;

    private Tender tender;
    private Timer timer;
    private long remainingTime;


    @Override
    public void initVariables() {
        tender = getIntent().getParcelableExtra(Tender.TENDER);
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.z_kc_act_tender_detail);
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
        if (tender.getTender_type().equals(Tender.GRAB)) {
            tvTitle.setText(R.string.grab_details);
            llBiddingOrderHead.setVisibility(View.GONE);
            llBiddingOrder.setVisibility(View.GONE);
            DecimalFormat df = new DecimalFormat("######0.00");
            tvFreight.setText(df.format(tender.getHighest_protect_price()));
            tvGrabOrder.setOnClickListener(new MyOnClickListener() {
                @Override
                public void OnceOnClick(View view) {
                    //抢单
                    grabTender();
                }
            });
        } else {
            tvTitle.setText(R.string.bidding_details);
            llGrabOrder.setVisibility(View.GONE);
            long startTime = System.currentTimeMillis();
            long endTime = TimeUtil.UTCTimeToTimeMillis(tender.getEnd_time());
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
                tvMaxPrince.setText(tender.getHighest_grab_price() + getString(R.string.prince_unit));
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

            tvStartCity.setText(tender.getPickup_city());
            tvStartDistrict.setText(tender.getPickup_region());
            tvEndCity.setText(tender.getDelivery_city());
            tvEndDistrict.setText(tender.getDelivery_region());
            tvNeedCars.setText(tender.getTruck_type() + "  " + tender.getTruck_count() + "辆");
            StringBuilder sb = new StringBuilder();
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
                    sb.append(quantity + quantityUnit);
                }
                if (!TextUtils.isEmpty(volumeUnit)) {
                    if (sb.length() > 0) {
                        sb.append("/");
                    }
                    sb.append(volume + volumeUnit);
                }
                if (!TextUtils.isEmpty(weightUnit)) {
                    if (sb.length() > 0) {
                        sb.append("/");
                    }
                    sb.append(weight + weightUnit);
                }
                tvGoodsInfo.setText(sb.toString());
                sb.setLength(0);
            }
            tvRemark.setText(TextUtils.isEmpty(tender.getRemark()) ? "-" : tender.getRemark());
            tvInitiator.setText(tender.getSender_company());
            tvInitiatorName.setText(tender.getPickup_name());
            tvInitiatorPhone.setText(tender.getPickup_mobile_phone());
            tvPickupTime.setText(TimeUtil.convertDateStringNormalTimeFormat(tender.getPickup_start_time_format(), "MM-dd HH:mm") + "~" + TimeUtil.convertDateStringNormalTimeFormat(tender.getPickup_end_time_format(), "MM-dd HH:mm"));
            tvPickupAddress.setText(tender.getPickup_address());
            tvDeliveryTime.setText(TimeUtil.convertDateStringNormalTimeFormat(tender.getDelivery_start_time_format(), "MM-dd HH:mm") + "~" + TimeUtil.convertDateStringNormalTimeFormat(tender.getDelivery_end_time_format(), "MM-dd HH:mm"));
            tvDeliveryAddress.setText(tender.getDelivery_address());
            if (tender.getPayment_top_rate() > 0) {
                sb.append(getString(R.string.payment_top_rate));
            } else if (tender.getPayment_last_rate() > 0) {
                sb.append("+" + getString(R.string.payment_last_rate));
            }
            if (tender.getPayment_tail_rate() > 0) {
                sb.append("+" + getString(R.string.receipt));
            }
            tvPayWay.setText(sb.toString());
            sb.setLength(0);

            if (tender.getPayment_top_rate() > 0) {
                sb.append(NumberUtil.doubleTrans(tender.getPayment_top_rate()) + "%   ");
                if (tender.getPayment_top_cash_rate() == 100) {
                    sb.append(getString(R.string.cash_pay));
                } else if (tender.getPayment_top_card_rate() == 100) {
                    sb.append(getString(R.string.oil_card_pay));
                } else {
                    sb.append("(" + NumberUtil.doubleTrans(tender.getPayment_top_cash_rate()) + "%" + getString(R.string.cash) + "  +  " + NumberUtil.doubleTrans(tender.getPayment_top_card_rate()) + "%" + getString(R.string.oil_card) + ")");
                }
                tvFirstPay.setText(sb.toString());
                sb.setLength(0);
            } else {
                llFirstPay.setVisibility(View.GONE);
            }

            if (tender.getPayment_last_rate() > 0) {
                sb.append(NumberUtil.doubleTrans(tender.getPayment_last_rate()) + "%   ");
                if (tender.getPayment_last_cash_rate() == 100) {
                    sb.append("(" + getString(R.string.cash_pay) + ")");
                } else if (tender.getPayment_last_card_rate() == 100) {
                    sb.append("(" + getString(R.string.oil_card_pay) + ")");
                } else {
                    sb.append("(" + NumberUtil.doubleTrans(tender.getPayment_last_cash_rate()) + "%" + getString(R.string.cash) + "  +  " + NumberUtil.doubleTrans(tender.getPayment_last_card_rate()) + "%" + getString(R.string.oil_card) + ")");
                }
                tvLastPay.setText(sb.toString());
                sb.setLength(0);
            } else {
                llLastPay.setVisibility(View.GONE);
            }

            if (tender.getPayment_tail_rate() > 0) {
                sb.append(NumberUtil.doubleTrans(tender.getPayment_tail_rate()) + "%   ");
                if (tender.getPayment_tail_cash_rate() == 100) {
                    sb.append("(" + getString(R.string.cash_pay) + ")");
                } else if (tender.getPayment_tail_card_rate() == 100) {
                    sb.append("(" + getString(R.string.oil_card_pay) + ")");
                } else {
                    sb.append("(" + NumberUtil.doubleTrans(tender.getPayment_tail_cash_rate()) + "%" + getString(R.string.cash) + "  +  " + NumberUtil.doubleTrans(tender.getPayment_tail_card_rate()) + "%" + getString(R.string.oil_card) + ")");
                }
                tvReceipt.setText(sb.toString());
                sb.setLength(0);
            } else {
                llReceiptPay.setVisibility(View.GONE);
            }
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

    private void grabTender() {
//        TenderApiImpl.getUserApiImpl().grabTender(CommonTools.getToken(this), tender.getTender_id(), new Subscriber<ErrorInfo>() {
//            @Override
//            public void onCompleted() {
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Toast.makeText(TenderDetailActivity.this, "失败", Toast.LENGTH_LONG).show();
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onNext(ErrorInfo errorInfo) {
//                Toast.makeText(TenderDetailActivity.this, "成功", Toast.LENGTH_LONG).show();
//                finish();
//            }
//        });

        //测试
        OilCard oilCard = new OilCard();
        oilCard.setNumber("abc123abc");
        oilCard.setType(OilCard.ETC);
        OilCardApiImpl.getOilCardApiImpl().addOilCardByDriver(CommonTools.getToken(this), oilCard, new Subscriber<ErrorInfo>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(TenderDetailActivity.this, "失败", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            @Override
            public void onNext(ErrorInfo errorInfo) {
                Toast.makeText(TenderDetailActivity.this, "成功", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}

