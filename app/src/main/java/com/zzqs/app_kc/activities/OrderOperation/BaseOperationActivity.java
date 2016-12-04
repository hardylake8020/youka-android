package com.zzqs.app_kc.activities.OrderOperation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.activities.BaseActivity;
import com.zzqs.app_kc.activities.MipcaActivityCapture;
import com.zzqs.app_kc.activities.OrderTimeAxisActivity;
import com.zzqs.app_kc.activities.ScanCodeListActivity;
import com.zzqs.app_kc.db.DaoManager;
import com.zzqs.app_kc.db.hibernate.dao.BaseDao;
import com.zzqs.app_kc.entity.EventFile;
import com.zzqs.app_kc.entity.Events;
import com.zzqs.app_kc.entity.Order;
import com.zzqs.app_kc.entity.OrderCode;
import com.zzqs.app_kc.entity.OrderEvent;
import com.zzqs.app_kc.utils.SetTypeFace;
import com.zzqs.app_kc.utils.StringTools;
import com.zzqs.app_kc.widgets.DialogView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

/**
 * Created by lance on 15/11/19.
 */
public abstract class BaseOperationActivity extends BaseActivity implements View.OnClickListener {
    public static final int UPLOAD_EVENT = 101;
    public static final int SCANNIN_GREQUEST_CODE = 103;
    public static final int TO_ENTRANCE = 1;
    public static final int TO_HALF_EVENT = 2;

    LayoutInflater inflater;
    LinearLayout insertView;

    ImageView  haveCodeImg, haveEntrance;
    Button commit;
    TextView title, timeLine, scanCodeTv, entranceTv, back;
    TextView iconHalfEvent, iconEntrance, IconScan;
    RelativeLayout halfEvent, entrance, scan;
    protected boolean haveCode;

    protected Order order;
    protected List<Order> orders;

    protected BaseDao<Order> orderDao;
    protected BaseDao<OrderEvent> orderEventBaseDao;
    protected BaseDao<EventFile> eventFileBaseDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_operation);
        orderDao = DaoManager.getOrderDao(getApplicationContext());
        orderEventBaseDao = DaoManager.getOrderEventDao(getApplicationContext());
        eventFileBaseDao = DaoManager.getEventFileDao(getApplicationContext());
        String orderId = getIntent().getStringExtra(Order.ORDER_ID);
        if (!StringTools.isEmp(orderId)) {
            List<Order> orderList = orderDao.find(null, "order_id=?", new String[]{orderId}, null, null, null, null);
            if (null != orderList && orderList.size() == 1) {
                order = orderList.get(0);
            }
        }
        orders = new ArrayList<>();
        ArrayList<String> orderIdList = getIntent().getStringArrayListExtra(Order.ORDER_IDS);
        if (null != orderIdList && orderIdList.size() > 0) {
            for (String id : orderIdList) {
                List<Order> orderList = orderDao.find(null, "order_id = ?", new String[]{id}, null, null, null, null);
                if (null != orderList && orderList.size() > 0) {
                    orders.add(orderList.get(0));
                }
            }
            order = orders.get(0);
        }
        initView();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(Events.OrderEvent event) {
        if (event.getType() == Events.REFRESH_ORDER) {
            order = event.getOrder();
            changeUI();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void initView() {
        insertView = (LinearLayout) findViewById(R.id.insert_view);
        inflater = LayoutInflater.from(this);

        back = (TextView) findViewById(R.id.head_back);
        haveCodeImg = (ImageView) findViewById(R.id.have_code);
        haveEntrance = (ImageView) findViewById(R.id.have_entrance);
        commit = (Button) findViewById(R.id.submit);
        title = (TextView) findViewById(R.id.head_title);
        timeLine = (TextView) findViewById(R.id.head_right);
        timeLine.setText("时间轴");

        iconHalfEvent = (TextView) findViewById(R.id.icon_half_event);
        iconEntrance = (TextView) findViewById(R.id.icon_entrance);
        IconScan = (TextView) findViewById(R.id.icon_scan);

        scanCodeTv = (TextView) findViewById(R.id.scan_code_text);
        entranceTv = (TextView) findViewById(R.id.entrance_text);

        halfEvent = (RelativeLayout) findViewById(R.id.half_event);
        entrance = (RelativeLayout) findViewById(R.id.entrance_event);
        scan = (RelativeLayout) findViewById(R.id.scan_code);

        SetTypeFace.setTypeFace(this, iconHalfEvent, 0, 0);
        SetTypeFace.setTypeFace(this, iconEntrance, 0, 0);
        SetTypeFace.setTypeFace(this, IconScan, 0, 0);

        back.setOnClickListener(this);
        commit.setOnClickListener(this);
        timeLine.setOnClickListener(this);
        scan.setOnClickListener(this);
    }

    public void setOrder(Order order) {
        this.order = order;
        changeUI();
    }


    protected void changeUI() {
        String mold;
        if (order.getOrderType().equals(Order.DRIVER_ORDER)) {
            SetTypeFace.setTypeFace(this, iconHalfEvent, 0, 0);
            SetTypeFace.setTypeFace(this, iconEntrance, 0, 0);
            SetTypeFace.setTypeFace(this, IconScan, 0, 0);
            if (order.getStatus().equals(Order.UN_PICKUP) || order.getStatus().equals(Order.UN_DELIVERY)) {
                entranceTv.setText(getString(R.string.view_tv_entered));
                SetTypeFace.setTypeFace(this, iconEntrance, getResources().getColor(R.color.dark_gray), 0);
            } else {
                entranceTv.setText(getString(R.string.view_tv_enter_site));
                SetTypeFace.setTypeFace(this, iconEntrance, 0, 0);
            }
            if (order.getStatus().equals(Order.UN_PICKUP) || order.getStatus().equals(Order.UN_PICKUP_ENTRANCE)) {
                title.setText(getString(R.string.view_tv_un_pickup));
                commit.setText(getString(R.string.view_tv_confirm_pickup));
                mold = OrderEvent.MOLD_PICKUP;

            } else {
                title.setText(getString(R.string.view_tv_executing));
                commit.setText(getString(R.string.view_tv_confirm_delivery));
                mold = OrderEvent.MOLD_DELIVERY;
            }
            halfEvent.setOnClickListener(this);
            entrance.setOnClickListener(this);
        } else {
            mold = OrderEvent.MOLD_DELIVERY;
            SetTypeFace.setTypeFace(this, iconHalfEvent, getResources().getColor(R.color.green), 0);
            SetTypeFace.setTypeFace(this, iconEntrance, getResources().getColor(R.color.green), 0);
            SetTypeFace.setTypeFace(this, IconScan, 0, 0);
            title.setText(getString(R.string.view_tv_transfer));
            commit.setText(getString(R.string.view_tv_confirm_goods));
        }

        List<OrderEvent> list = orderEventBaseDao.find(null, "order_id=? and mold=? and status=?", new String[]{order.getOrderId(), mold, OrderEvent.STATUS_NEW + ""}, null, null, null, null);
        if (list.size() == 1) {
            OrderEvent orderEvent = list.get(0);
            String scanCode = orderEvent.getOrderCode();
            if (!StringTools.isEmp(scanCode)) {
                haveCode = true;
                haveCodeImg.setVisibility(View.VISIBLE);
                scanCodeTv.setText(getString(R.string.view_tv_continue_scan));
            } else {
                haveCode = false;
                haveCodeImg.setVisibility(View.INVISIBLE);
                scanCodeTv.setText(getString(R.string.view_tv_scan));
            }
        }

        if ((order.getStatus().equals(Order.UN_DELIVERY) || order.getStatus().equals(Order.UN_PICKUP)) && order.getOrderType().equals(Order.DRIVER_ORDER)) {
            haveEntrance.setVisibility(View.VISIBLE);
        }
    }

    public abstract void toShootActivity();

    public abstract void toUploadEventActivity(int type);

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.submit:
                boolean mustPickupEntrance = order.getStatus().equals(Order.UN_PICKUP_ENTRANCE) && order.getPickupEntranceForce().equals("true");
                boolean mustDeliveryEntrance = order.getStatus().equals(Order.UN_DELIVERY_ENTRANCE) && order.getDeliveryEntranceForce().equals("true");
                if (mustPickupEntrance || mustDeliveryEntrance) {
                    String msg = null;
                    if (mustPickupEntrance) {
                        msg = getString(R.string.prompt_must_pickup_enter);
                    } else if (mustDeliveryEntrance) {
                        msg = getString(R.string.prompt_must_delivery_enter);
                    }
                    DialogView.showChoiceDialog(this, DialogView.SINGLE_BTN, getString(R.string.prompt_dl_title_1), msg, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            if (msg.what == DialogView.ACCEPT) {
                                toUploadEventActivity(TO_ENTRANCE);
                            }
                        }
                    });
                    break;
                }
                toShootActivity();
                break;
            case R.id.head_right:
                intent = new Intent(getApplicationContext(), OrderTimeAxisActivity.class);
                intent.putExtra(Order.ORDER, order);
                startActivity(intent);
                break;
            case R.id.head_back:
                finish();
                break;
            case R.id.scan_code:
                if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    Toast.makeText(this, R.string.prompt_no_sdcard, Toast.LENGTH_LONG).show();
                    return;
                }
                if (!haveCode) {
                    intent = new Intent(getApplicationContext(), MipcaActivityCapture.class);
                    startActivityForResult(intent, MipcaActivityCapture.SCAN_CODE);
                } else {
                    intent = new Intent(getApplicationContext(), ScanCodeListActivity.class);
                    intent.putExtra(Order.ORDER, order);
                    startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
                }
                break;
            case R.id.entrance_event:
                toUploadEventActivity(TO_ENTRANCE);
                break;
            case R.id.half_event:
                toUploadEventActivity(TO_HALF_EVENT);
                break;
            default:
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MipcaActivityCapture.SCAN_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
                Pattern patt = Pattern.compile(regex);
                String text = bundle.getString("result");
                Matcher matcher = patt.matcher(bundle.getString("result"));
                boolean isMatch = matcher.matches();
                if (!isMatch) {
                    //获取扫码时间
                    SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
                    Date date = new Date(System.currentTimeMillis());
                    String time = format.format(date);
                    OrderCode orderCode = new OrderCode();
                    orderCode.setScanTime(time);
                    orderCode.setCode(text);
                    Intent intent = new Intent(getApplicationContext(), ScanCodeListActivity.class);
                    intent.putExtra(OrderCode.ORDER_CODE, orderCode);
                    intent.putExtra(Order.ORDER, order);
                    startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
                } else {
                    Toast.makeText(this, R.string.prompt_invalid_scan_info, Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == SCANNIN_GREQUEST_CODE) {
            changeUI();
        }
    }
}
