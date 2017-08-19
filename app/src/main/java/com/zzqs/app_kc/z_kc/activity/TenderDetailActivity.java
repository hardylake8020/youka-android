package com.zzqs.app_kc.z_kc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.entity.User;
import com.zzqs.app_kc.utils.CommonTools;
import com.zzqs.app_kc.widgets.DialogView;
import com.zzqs.app_kc.z_kc.entitiy.ErrorInfo;
import com.zzqs.app_kc.z_kc.entitiy.Goods;
import com.zzqs.app_kc.z_kc.entitiy.Tender;
import com.zzqs.app_kc.z_kc.entitiy.TenderRecord;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;
import com.zzqs.app_kc.z_kc.network.TenderApiImpl;
import com.zzqs.app_kc.z_kc.util.NumberUtil;
import com.zzqs.app_kc.z_kc.util.TimeUtil;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import rx.Subscriber;

/**
 * Created by lance on 2016/12/4.
 */

public class TenderDetailActivity extends BaseActivity {
    TextView tvLeft, tvTitle, tvRight, tvRemainingTime, tvMaxPrince, tvStartCity, tvStartDistrict, tvEndCity,
            tvEndDistrict, tvNeedCars, tvGoodsInfo, tvRemark, tvInitiator, tvInitiatorName, tvInitiatorPhone,
            tvPickupTime, tvPickupAddress, tvDeliveryTime, tvDeliveryAddress, tvPayWay, tvFirstPay, tvLastPay,
            tvReceipt, tvFreight, tvGrabOrder, tvBiddingOrder, tvCompareResult, tvComparePrice, tvTon, tvTonOder;
    LinearLayout llBiddingOrderHead, llGrabOrder, llBiddingOrder, llFirstPay, llLastPay, llReceiptPay, llTon, llTonOperation;
    EditText etBiddingPrince, editTonPrice, editTonOverPrice;

    private Tender tender;
    private Timer timer;
    private long remainingTime;
    private User user;

    @Override
    public void initVariables() {
        tender = getIntent().getParcelableExtra(Tender.TENDER);
        user = ZZQSApplication.getInstance().getUser();

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
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("查看地图");
        tvRight.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                Intent intent = new Intent(TenderDetailActivity.this, MapViewActivity.class);
                intent.putExtra("tender", tender);
                startActivity(intent);
            }
        });

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

        tvCompareResult = (TextView) findViewById(R.id.tvCompareResult);
        tvComparePrice = (TextView) findViewById(R.id.tvComparePrice);

        tvGrabOrder = (TextView) findViewById(R.id.tvGrabOrder);
        tvBiddingOrder = (TextView) findViewById(R.id.tvBiddingOrder);

        llBiddingOrderHead = (LinearLayout) findViewById(R.id.llBiddingOrderHead);
        llGrabOrder = (LinearLayout) findViewById(R.id.llGrabOrder);
        llBiddingOrder = (LinearLayout) findViewById(R.id.llBiddingOrder);
        etBiddingPrince = (EditText) findViewById(R.id.etBiddingPrince);
        llFirstPay = (LinearLayout) findViewById(R.id.llFirstPay);
        llLastPay = (LinearLayout) findViewById(R.id.llLastPay);
        llReceiptPay = (LinearLayout) findViewById(R.id.llReceiptPay);
        llTon = (LinearLayout) findViewById(R.id.llTon);
        tvTon = (TextView) findViewById(R.id.tvTon);
        llTonOperation = (LinearLayout) findViewById(R.id.llTonOperation);
        editTonPrice = (EditText) findViewById(R.id.editTonPrice);
        editTonOverPrice = (EditText) findViewById(R.id.editTonOverPrice);
        tvTonOder = (TextView) findViewById(R.id.tvTonOder);

        if (tender.getTender_type().equals(Tender.GRAB) || tender.getTender_type().equals(Tender.ASSIGN)) {
            if (tender.getTender_type().equals(Tender.ASSIGN)) {
                tvTitle.setText("派单详情");
            } else {
                tvTitle.setText(R.string.grab_details);
            }
            llBiddingOrderHead.setVisibility(View.GONE);
            llBiddingOrder.setVisibility(View.GONE);
            llTonOperation.setVisibility(View.GONE);
            DecimalFormat df = new DecimalFormat("######0.00");
            tvFreight.setText(df.format(tender.getLowest_grab_price()));
            if (tender.getStatus().equals(Tender.UN_STARTED)) {
                tvGrabOrder.setText(R.string.grab_order);
                tvGrabOrder.setBackgroundResource(R.color.red_5);
                tvGrabOrder.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                tvGrabOrder.setOnClickListener(new MyOnClickListener() {
                    @Override
                    public void OnceOnClick(View view) {
                        //抢单
                        grabTender();
                    }
                });
            } else {
                if (tender.getTender_type().equals(Tender.ASSIGN)) {
                    tvGrabOrder.setText("已接收");
                } else {
                    tvGrabOrder.setText(R.string.grabbed);
                }
                tvGrabOrder.setBackgroundResource(R.color.tender_primary_color);
                tvGrabOrder.setTextColor(ContextCompat.getColor(this, R.color.text_gray));
            }
        } else {
            tvTitle.setText(R.string.bidding_details);
            llGrabOrder.setVisibility(View.GONE);
            if (tender.getTender_type().equals(Tender.COMPARE)) {
                llTon.setVisibility(View.GONE);
                llTonOperation.setVisibility(View.GONE);
            } else {
                llBiddingOrder.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(tender.getLowest_tons_count())) {
                tvTon.setText(tender.getLowest_tons_count() + "吨");
                tvComparePrice.setText("保底价不高于");
            }
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
                if (tender.getTender_records().size() > 0) {
                    for (TenderRecord record : tender.getTender_records()) {
                        if (record.getDriver().equals(user.getDriver_id())) {
                            etBiddingPrince.setHint(Html.fromHtml("出价：" + "<font color = #ED6250>" + new DecimalFormat("#").format(record.getPrice()) + "</font>元 点击修改"));
                            editTonPrice.setHint(Html.fromHtml("保底价：" + "<font color = #ED6250>" + new DecimalFormat("#").format(record.getPrice()) + "</font>元 点击修改"));
                            editTonOverPrice.setHint(Html.fromHtml("超额单价：" + "<font color = #ED6250>" + new DecimalFormat("#").format(record.getPrice_per_ton()) + "</font>元 点击修改"));
                            break;
                        }
                    }
                }
                tvMaxPrince.setText(tender.getHighest_protect_price() + getString(R.string.prince_unit));
                tvBiddingOrder.setOnClickListener(new MyOnClickListener() {
                    @Override
                    public void OnceOnClick(View view) {
                        if (TextUtils.isEmpty(etBiddingPrince.getText().toString())) {
                            Toast.makeText(TenderDetailActivity.this, "请输入价格", Toast.LENGTH_LONG).show();
                            return;
                        }
                        int price = Integer.parseInt(etBiddingPrince.getText().toString());
                        int highProtectPrice = (int) tender.getHighest_protect_price();
                        int lowestProtectPrice = (int) tender.getLowest_protect_price();
                        if (price > highProtectPrice) {
                            Toast.makeText(TenderDetailActivity.this, "出价不能高于" + tender.getHighest_protect_price() + "元", Toast.LENGTH_LONG).show();
                            etBiddingPrince.setText("");
                            return;
                        }
                        if (price < lowestProtectPrice) {
                            Toast.makeText(TenderDetailActivity.this, "出价不能低于" + tender.getLowest_protect_price() + "元", Toast.LENGTH_LONG).show();
                            etBiddingPrince.setText("");
                            return;
                        }
                        //出价
                        compareTender();
                    }
                });
                tvTonOder.setOnClickListener(new MyOnClickListener() {
                    @Override
                    public void OnceOnClick(View view) {
                        if (TextUtils.isEmpty(editTonPrice.getText().toString())) {
                            Toast.makeText(TenderDetailActivity.this, "请输入保底价", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (TextUtils.isEmpty(editTonOverPrice.getText().toString())) {
                            Toast.makeText(TenderDetailActivity.this, "请输入超额单价", Toast.LENGTH_LONG).show();
                            return;
                        }
                        int price = Integer.parseInt(editTonPrice.getText().toString());
                        int highProtectPrice = (int) tender.getHighest_protect_price();
                        if (price > highProtectPrice) {
                            Toast.makeText(TenderDetailActivity.this, "出价不能高于" + tender.getHighest_protect_price() + "元", Toast.LENGTH_LONG).show();
                            editTonPrice.setText("");
                            return;
                        }
                        compareTender();
                    }
                });
            } else {
                tvComparePrice.setText("中标情况");
                tvCompareResult.setText("比价结果");
                switch (tender.getStatus()) {
                    case Tender.COMPARING:
                        break;
                    case Tender.UN_ASSIGNED:
                    case Tender.IN_PROGRESS:
                        if (!TextUtils.isEmpty(tender.getDriver_winner().getDriver_id()) && tender.getDriver_winner().getDriver_id().equals(user.getDriver_id())) {
                            tvRemainingTime.setText("恭喜您中标了");
                            tvRemainingTime.setTextColor(ContextCompat.getColor(this, R.color.green));
                            tvMaxPrince.setText(new DecimalFormat("#").format(tender.getWinner_price()) + "元");
                            tvMaxPrince.setTextColor(ContextCompat.getColor(this, R.color.green));
                            tvComparePrice.setTextColor(ContextCompat.getColor(this, R.color.green));
                            tvCompareResult.setTextColor(ContextCompat.getColor(this, R.color.green));

                        } else {
                            tvRemainingTime.setText("很遗憾您未中标");
                            tvRemainingTime.setTextColor(ContextCompat.getColor(this, R.color.red));
                            tvMaxPrince.setTextColor(ContextCompat.getColor(this, R.color.red));
                            tvComparePrice.setTextColor(ContextCompat.getColor(this, R.color.red));
                            tvCompareResult.setTextColor(ContextCompat.getColor(this, R.color.red));
                            String phone = tender.getDriver_winner().getUsername();
                            phone = hidePhoneNumber(phone);
                            tvMaxPrince.setText(new DecimalFormat("#").format(tender.getWinner_price()) + "元(" + phone + ")");
                        }
                        break;
                }

//                tvRemainingTime.setText(R.string.is_over);
//                llBiddingOrder.setVisibility(View.GONE);
            }
            if (tender.getTender_records().size() > 0) {
                for (TenderRecord record : tender.getTender_records()) {
                    if (record.getDriver().equals(user.getDriver_id()) && remainingTime <= 0) {
                        tvBiddingOrder.setText("已出价");
                        tvBiddingOrder.setTextColor(ContextCompat.getColor(this, R.color.text_gray));
                        tvBiddingOrder.setBackgroundResource(R.color.tender_primary_color);
                        tvBiddingOrder.setClickable(false);
                        etBiddingPrince.setText(Html.fromHtml("出价：" + "<font color = #ED6250>" + new DecimalFormat("#").format(record.getPrice()) + "</font>元"));
                        etBiddingPrince.setTextColor(ContextCompat.getColor(this, R.color.text_gray));
                        etBiddingPrince.setEnabled(false);

                        tvTonOder.setText("已出价");
                        tvTonOder.setTextColor(ContextCompat.getColor(this, R.color.text_gray));
                        tvTonOder.setBackgroundResource(R.color.tender_primary_color);
                        tvTonOder.setClickable(false);
                        editTonPrice.setText(Html.fromHtml("保底价：" + "<font color = #ED6250>" + new DecimalFormat("#").format(record.getPrice()) + "</font>元"));
                        editTonPrice.setTextColor(ContextCompat.getColor(this, R.color.text_gray));
                        editTonPrice.setEnabled(false);
                        editTonOverPrice.setText(Html.fromHtml("超额单价：" + "<font color = #ED6250>" + new DecimalFormat("#").format(record.getPrice_per_ton()) + "</font>元"));
                        editTonOverPrice.setTextColor(ContextCompat.getColor(this, R.color.text_gray));
                        editTonOverPrice.setEnabled(false);
                        break;
                    }
                }
            }
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
                    weightUnit = goods.getUnit2();
                }
                if (!TextUtils.isEmpty(goods.getUnit3())) {
                    volumeUnit = goods.getUnit3();
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
        }
        if (tender.getPayment_last_rate() > 0) {
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
        DialogView.showConfirmDialog(this, "抢单确认", "您确定要抢单吗？抢单成功后若违约讲扣除您的保证金", true, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == DialogView.ACCEPT) {
                    if (!checkConnected()) {
                        return;
                    }
                    safePd.setMessage(getString(R.string.grabbing));
                    safePd.show();
                    TenderApiImpl.getUserApiImpl().grabTender(CommonTools.getToken(mContext), tender.getTender_id(), new Subscriber<ErrorInfo>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            safePd.dismiss();
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(ErrorInfo errorInfo) {
                            safePd.dismiss();
                            if (errorInfo.getType().equals(ErrorInfo.SUCCESS)) {
                                tender.setStatus(Tender.UN_STARTED);
                                DialogView.showConfirmDialog(mContext, "抢单成功", "恭喜您抢单，请选择车辆与绑定油卡的操作，\"取消\"则可稍后操作", true, new Handler() {
                                    @Override
                                    public void handleMessage(Message msg) {
                                        if (msg.what == DialogView.ACCEPT) {
                                            Intent intent = new Intent(mContext, MyCarsActivity.class);
                                            intent.putExtra(MyCarsActivity.IS_SELECT_CAR, true);
                                            intent.putExtra(Tender.TENDER, tender);
                                            startActivity(intent);
                                        } else if (msg.what == DialogView.CANCEL) {
                                            startActivity(new Intent(mContext, MyTendersActivity.class));
                                        }
                                        finish();
                                    }
                                });

                            } else {
                                Toast.makeText(mContext, errorInfo.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
    }

    private void compareTender() {
        DialogView.showConfirmDialog(this, "出价确认", "您确定要出价吗？出价中标后若违约讲扣除您的保证金", true, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == DialogView.ACCEPT) {
                    if (!checkConnected()) {
                        return;
                    }
                    safePd.setMessage(getString(R.string.comparing));
                    safePd.show();
                    int price = 0;
                    String price_per_ton = "";
                    if (tender.getTender_type().equals(Tender.COMPARE)) {
                        price = Integer.parseInt(etBiddingPrince.getText().toString());
                    } else {
                        price = Integer.parseInt(editTonPrice.getText().toString());
                        price_per_ton = editTonOverPrice.getText().toString();
                    }
                    TenderApiImpl.getUserApiImpl().compareTender(CommonTools.getToken(mContext), tender.getTender_id(), price, price_per_ton, new Subscriber<ErrorInfo>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            safePd.dismiss();
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(ErrorInfo errorInfo) {
                            safePd.dismiss();
                            if (errorInfo.getType().equals(ErrorInfo.SUCCESS)) {
                                tender.setStatus(Tender.UN_STARTED);
                                DialogView.showConfirmDialog(mContext, "出价成功", "恭喜您出价成功，请耐心等待比价截止，系统会即使通知您比价结果", false, new Handler() {
                                    @Override
                                    public void handleMessage(Message msg) {
                                        if (msg.what == DialogView.ACCEPT) {
                                            Intent intent = new Intent(mContext, MyTendersActivity.class);
                                            startActivity(intent);
                                        } else if (msg.what == DialogView.CANCEL) {
                                            startActivity(new Intent(mContext, MyTendersActivity.class));
                                        }
                                        finish();
                                    }
                                });

                            } else {
                                Toast.makeText(mContext, errorInfo.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
    }

    private String hidePhoneNumber(String phone) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < phone.length(); i++) {
            char c = phone.charAt(i);
            if (i >= 3 && i <= 6) {
                sb.append('*');
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}

