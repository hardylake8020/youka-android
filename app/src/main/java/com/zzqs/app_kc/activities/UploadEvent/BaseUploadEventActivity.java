package com.zzqs.app_kc.activities.UploadEvent;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.zzqs.app_kc.R;
import com.zzqs.app_kc.activities.ActualDeliveryActivity;
import com.zzqs.app_kc.activities.BaseActivity;
import com.zzqs.app_kc.activities.LoginActivity;
import com.zzqs.app_kc.activities.MipcaActivityCapture;
import com.zzqs.app_kc.activities.OrderTimeAxisActivity;
import com.zzqs.app_kc.adapter.EventFilePhotoAdapter;
import com.zzqs.app_kc.app.ContentData;
import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.db.DaoManager;
import com.zzqs.app_kc.db.hibernate.dao.BaseDao;
import com.zzqs.app_kc.entity.DriverTrace;
import com.zzqs.app_kc.entity.EventFile;
import com.zzqs.app_kc.entity.Events;
import com.zzqs.app_kc.entity.Order;
import com.zzqs.app_kc.entity.OrderEvent;
import com.zzqs.app_kc.net.Connectivities;
import com.zzqs.app_kc.net.RestAPI;
import com.zzqs.app_kc.service.LocationService;
import com.zzqs.app_kc.utils.FileUtil;
import com.zzqs.app_kc.utils.ImageUtil;
import com.zzqs.app_kc.utils.SoundMeter;
import com.zzqs.app_kc.utils.StringTools;
import com.zzqs.app_kc.widgets.DialogView;
import com.zzqs.app_kc.widgets.SafeProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

/**
 * Created by lance on 15/11/20.
 */
public abstract class BaseUploadEventActivity extends BaseActivity implements View.OnClickListener, OnGetGeoCoderResultListener {
    public static final String EVENT_TYPE = "mold";
    protected String mold;
    GridView gridView;
    Button submit, scanCommit;
    TextView headMsg, timeLine, tvDamage, hasDamage, tvActualInfo, headBack, tvCompression;
    RelativeLayout rlActualDelivery, rlDamage, rlCompression;

    protected BaseDao<EventFile> eventFileDao;
    protected BaseDao<Order> orderDao;
    protected Order order;
    protected OrderEvent orderEvent;
    protected List<Order> orders;
    protected List<OrderEvent> orderEvents;
    protected BaseDao<OrderEvent> orderEventDao;
    protected EventFilePhotoAdapter adapter;
    protected ArrayList<EventFile> photoList = null;
    protected EventFile voiceFile = null;
    protected String photoName;
    protected String voiceName;
    protected File tmpFile;
    protected Uri uri;
    protected SafeProgressDialog pd;

    public static final int SHOOT_PHOTO = 10;
    public static final int DAMAGE = 20;
    protected boolean isUpload;

    protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    protected DriverTrace lastTrace;
    protected GeoCoder mSearch = null;//搜索模块
    protected boolean isSearch = false;
    protected double latitude = 0.0;// 纬度
    protected double longitude = 0.0;// 精度
    protected String locationAddress;
    protected String uploadTime = null;

    protected boolean isDamaged = false;
    protected String isScanCode = "false";
    protected boolean isLast = false;
    protected int index;

    View rcChatPopup;
    Button playSound;
    ImageView ivPopUp, volume, img1, scImg1;
    EditText et;
    RelativeLayout btnBottom;
    TextView tvRcd;
    protected SoundMeter mSensor;
    protected boolean isVoice = false;
    protected boolean isShort = false;
    protected int voiceFlag = 1;
    protected MediaPlayer mediaPlayer;
    LinearLayout voiceRcdHintLoading, voiceCcdHintRcding, voiceRcdHintTooShort, delRe;

    private Handler mHandler = new Handler();
    private int state = STOP;
    private static final int STOP = 1;
    private static final int START = 2;
    private long startVoiceT, endVoiceT;
    private static final int POLL_INTERVAL = 300;

    protected static final int SAVE = 598;
    protected static final int GET_PHOTO = 599;
    protected static final int UPLOAD_SUCCESS = 600;
    protected static final int ORDER_NOT_EXIST = 601;
    protected static final int ORDER_STATUS_HAS_CHANGED = 602;
    protected static final int TO_FUNCTION_UPLOAD = 603;
    protected static final int SAVE_FILE_FAILED = 604;
    protected Handler callbackHandler = null;

    protected boolean isNeedBigFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_shoot);
        mSensor = new SoundMeter();
        orderDao = DaoManager.getOrderDao(getApplicationContext());
        eventFileDao = DaoManager.getEventFileDao(getApplicationContext());
        orderEventDao = DaoManager.getOrderEventDao(getApplicationContext());
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
        mold = getIntent().getStringExtra(EVENT_TYPE);
        String orderId = getIntent().getStringExtra(Order.ORDER_ID);
        if (!StringTools.isEmp(orderId)) {
            List<Order> orderList = orderDao.find(null, "order_id=?", new String[]{orderId}, null, null, null, null);
            if (null != orderList && orderList.size() == 1) {
                order = orderList.get(0);
            }
        } else {
            finish();
        }
        ArrayList<String> orderIdList = getIntent().getStringArrayListExtra(Order.ORDER_IDS);
        if (null != orderIdList && orderIdList.size() > 0) {
            orders = new ArrayList<>();
            for (String id : orderIdList) {
                List<Order> orderList = orderDao.find(null, "order_id = ?", new String[]{id}, null, null, null, null);
                if (null != orderList && orderList.size() > 0) {
                    orders.add(orderList.get(0));
                }
            }
        }
        initView();
        initData();
        subclassView();
        initCallbackHandler();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pd.dismiss();
    }


    private void initView() {
        pd = new SafeProgressDialog(this);
        gridView = (GridView) findViewById(R.id.photos);
        submit = (Button) findViewById(R.id.submit);
        scanCommit = (Button) findViewById(R.id.btn_scancode_commit);
        scanCommit.setOnClickListener(this);
        headMsg = (TextView) findViewById(R.id.head_title);
        headMsg.setVisibility(View.VISIBLE);
        timeLine = (TextView) findViewById(R.id.head_right);
        headBack = (TextView) findViewById(R.id.head_back);
        rlDamage = (RelativeLayout) findViewById(R.id.rl_damage);
        tvDamage = (TextView) findViewById(R.id.tv_damage);
        hasDamage = (TextView) findViewById(R.id.has_damage);
        rlActualDelivery = (RelativeLayout) findViewById(R.id.rl_actualDelivery);
        tvActualInfo = (TextView) findViewById(R.id.tv_actualInfo);
        playSound = (Button) findViewById(R.id.play_sound);
        ivPopUp = (ImageView) findViewById(R.id.iv_popup);
        et = (EditText) findViewById(R.id.et_send_message);
        btnBottom = (RelativeLayout) findViewById(R.id.btn_bottom);
        tvRcd = (TextView) findViewById(R.id.btn_rcd);
        volume = (ImageView) findViewById(R.id.volume);
        img1 = (ImageView) findViewById(R.id.img1);
        scImg1 = (ImageView) findViewById(R.id.sc_img1);
        voiceCcdHintRcding = (LinearLayout) findViewById(R.id.voice_rcd_hint_rcding);
        voiceRcdHintLoading = (LinearLayout) findViewById(R.id.voice_rcd_hint_loading);
        voiceRcdHintTooShort = (LinearLayout) findViewById(R.id.voice_rcd_hint_tooshort);
        delRe = (LinearLayout) findViewById(R.id.del_re);
        rcChatPopup = findViewById(R.id.rcChat_popup);

        submit.setOnClickListener(this);
        headBack.setOnClickListener(this);
        timeLine.setOnClickListener(this);
        hasDamage.setOnClickListener(this);
        rlActualDelivery.setOnClickListener(this);

        tvRcd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });

        ivPopUp.setOnClickListener(this);
        playSound.setOnClickListener(this);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                //判断点击的是不是最后一个拍照按钮
                if (position == adapterView.getChildCount() - 1) {//是最后一个，只拍照
                    isLast = true;
                    shootPhoto(SHOOT_PHOTO);
                } else {//不是最后一个，判断是显示大图还是去拍照
                    isLast = false;
                    final EventFile eventFile = photoList.get(position);
                    if (StringTools.isEmp(eventFile.getFilePath()) || eventFile.getFilePath().equals(eventFile.ZZQS_CONFIG_PHOTO)) {
                        shootPhoto(SHOOT_PHOTO);
                        index = position;
                    } else {//显示大图
                        EventFile ef = photoList.get(position);
                        if (!new File(ef.getFilePath()).exists()) {
                            Toast.makeText(BaseUploadEventActivity.this, R.string.prompt_no_photo_in_sdcard, Toast.LENGTH_SHORT).show();
                            eventFileDao.delete(ef.get_id());
                            photoList.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                        DialogView.showBigImageDialog(BaseUploadEventActivity.this, photoList.get(position).getFilePath(), null, eventFile.getConfigName(), new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                if (msg.what == DialogView.DELETE) {
                                    EventFile eventFile = photoList.get(position);
                                    File deleteFile = new File(eventFile.getFilePath());
                                    if (deleteFile.exists()) {
                                        deleteFile.delete();
                                    }
                                    if (eventFile.getMold() != EventFile.MOLD_CONFIG_PHOTO) {
                                        eventFileDao.execSql("delete from event_file where file_path=?", new String[]{eventFile.getFilePath()});
                                        photoList.remove(position);
                                    } else {
                                        photoList.get(position).setFilePath(eventFile.ZZQS_CONFIG_PHOTO);
                                        eventFileDao.update(photoList.get(position));
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }
            }
        });
        if (orders != null) {
            for (Order o : orders) {
                if (o.getIsNew() == Order.NEW) {
                    o.setIsNew(Order.EXIST);
                }
            }
        } else {
            if (order.getIsNew() == Order.NEW) {
                order.setIsNew(Order.EXIST);
            }
        }

        rlCompression = (RelativeLayout) findViewById(R.id.rl_compression);
        tvCompression = (TextView) findViewById(R.id.tv_compression);
    }

    private void initData() {
        photoList = new ArrayList<EventFile>();
        adapter = new EventFilePhotoAdapter(this, photoList, true, mold);
        gridView.setAdapter(adapter);
        if (null != orders) {
            initBatchData();
        } else {
            initSingleData();
        }
        if (!StringTools.isEmp(voiceFile.getFilePath()) && new File(voiceFile.getFilePath()).exists()) {
            tvRcd.setVisibility(View.VISIBLE);
            btnBottom.setVisibility(View.GONE);
            playSound.setVisibility(View.VISIBLE);
            ivPopUp.setImageResource(R.drawable.chatting_setmode_voice_btn);
            isVoice = true;
        }
        changeDamage();
    }

    private void initSingleData() {
        List<OrderEvent> list = new ArrayList<>();
        list.addAll(orderEventDao.find(null, "order_id=? and mold=? and status=?", new String[]{order.getOrderId() + "", mold, OrderEvent.STATUS_NEW + ""}, null, null, null, null));
        if (list.size() == 1) {
            orderEvent = list.get(0);
            isDamaged = orderEvent.getIsDamaged() == 0 ? false : true;
            photoList.clear();
            photoList.addAll(eventFileDao.find(null, "event_id=? and mold<>?", new String[]{orderEvent.get_id() + "", EventFile.MOLD_VOICE + ""}, null, null, null, null));
            adapter.notifyDataSetChanged();
            if (eventFileDao.rawQuery("select * from event_file where event_id=? and mold=?", new String[]{orderEvent.get_id() + "", EventFile.MOLD_VOICE + ""}).size() > 0) {
                voiceFile = eventFileDao.rawQuery("select * from event_file where event_id=? and mold=?", new String[]{orderEvent.get_id() + "", EventFile.MOLD_VOICE + ""}).get(0);
            }
        } else {
            initConfig();
        }
        if (voiceFile == null) {
            voiceFile = new EventFile();
        }
    }


    private void initBatchData() {
        List<OrderEvent> list = orderEventDao.find(null, "order_id=? and mold=? and status=?", new String[]{order.getOrderId() + "", mold, OrderEvent.STATUS_NEW + ""}, null, null, null, null);
        if (list != null && list.size() == 1) {
            orderEvent = list.get(0);
        }
        if (null != orderEvent) {
            photoList.clear();
            photoList.addAll(eventFileDao.find(null, "event_id=? and mold<>?", new String[]{orderEvent.get_id() + "", EventFile.MOLD_VOICE + ""}, null, null, null, null));
            if (photoList.size() > 0) {
                Iterator<EventFile> eventFileIterator = photoList.iterator();
                while (eventFileIterator.hasNext()) {
                    EventFile eventFile = eventFileIterator.next();
                    if (!eventFile.getFilePath().equals(EventFile.ZZQS_CONFIG_PHOTO)) {
                        File file = new File(eventFile.getFilePath());
                        if (!file.exists()) {
                            eventFileIterator.remove();
                            eventFileDao.delete(eventFile.get_id());
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
            if (eventFileDao.rawQuery("select * from event_file where event_id=? and mold=?", new String[]{orderEvent.get_id() + "", EventFile.MOLD_VOICE + ""}).size() > 0) {
                voiceFile = eventFileDao.rawQuery("select * from event_file where event_id=? and mold=?", new String[]{orderEvent.get_id() + "", EventFile.MOLD_VOICE + ""}).get(0);
            }
        } else {
            initConfig();
        }
        orderEvents = new ArrayList<OrderEvent>();
        ArrayList<OrderEvent> events = new ArrayList<>();
        for (Order order1 : orders) {
            List<OrderEvent> batchOrderEvent = orderEventDao.find(null, "order_id=? and mold=? and status=?", new String[]{order1.getOrderId() + "", mold, OrderEvent.STATUS_NEW + ""}, null, null, null, null);
            OrderEvent event = new OrderEvent();
            if (batchOrderEvent.size() == 0) {
                event.setOrderId(order1.getOrderId());
                event.setMold(mold);
                event.setStatus(OrderEvent.STATUS_NEW);
                events.add(event);
            } else if (batchOrderEvent.size() == 1) {
                event = batchOrderEvent.get(0);
            }
            orderEvents.add(event);
        }
        orderEventDao.inserts(events);
        for (OrderEvent event : orderEvents) {
            event.set_id(orderEventDao.find(null, "order_id=? and mold=? and status=?", new String[]{event.getOrderId() + "", mold, OrderEvent.STATUS_NEW + ""}, null, null, null, null).get(0).get_id());
        }
        if (voiceFile == null) {
            voiceFile = new EventFile();
        }
    }

    private void changeDamage() {
        if (isDamaged) {
            hasDamage.setBackgroundResource(R.drawable.on);
            tvDamage.setTextColor(getResources().getColor(R.color.red));
            submit.setText(R.string.view_bt_has_damage);
        } else {
            hasDamage.setBackgroundResource(R.drawable.off);
            tvDamage.setTextColor(getResources().getColor(R.color.middle_black));
            submit.setText(R.string.view_bt_submit);
        }
        order.setDamaged(isDamaged ? "true" : "false");
    }


    //初始化配置内容
    public void initConfig() {
        if (mold != OrderEvent.MOLD_HALFWAY) {
            orderEvent = new OrderEvent();
            orderEvent.setOrderId(order.getOrderId());
            orderEvent.setMold(mold);
            orderEvent.setStatus(OrderEvent.STATUS_NEW);
            orderEventDao.insert(orderEvent);
            String selection = "order_id=? and mold=? and status=?";
            int eventId = orderEventDao.find(null, selection, new String[]{order.getOrderId() + "", mold, OrderEvent.STATUS_NEW + ""}, null, null, null, null).get(0).get_id();
            orderEvent.set_id(eventId);
            String[] names = new String[0];
            switch (mold) {
                case OrderEvent.MOLD_PICKUP_ENTRANCE:
                    if (order.getPickupEntrancePhotos() != null) {
                        names = order.getPickupEntrancePhotos().split("/zzqs/");
                    }
                    break;
                case OrderEvent.MOLD_PICKUP:
                    if (order.getPickupTakePhotos() != null) {
                        names = order.getPickupTakePhotos().split("/zzqs/");
                    }
                    break;
                case OrderEvent.MOLD_DELIVERY_ENTRANCE:
                    if (order.getDeliveryEntrancePhotos() != null) {
                        names = order.getDeliveryEntrancePhotos().split("/zzqs/");
                    }
                    break;
                case OrderEvent.MOLD_DELIVERY:
                    if (order.getDeliveryTakePhotos() != null) {
                        names = order.getDeliveryTakePhotos().split("/zzqs/");
                    }
                    break;
            }
            if (names.length > 0) {
                ArrayList<EventFile> eventFiles = new ArrayList();
                for (int i = 0; i < names.length; i++) {
                    EventFile eventFile = new EventFile();
                    eventFile.setConfigName(names[i]);
                    eventFile.setOrderId(order.getOrderId());
                    eventFile.setEventId(orderEvent.get_id());
                    eventFile.setMold(EventFile.MOLD_CONFIG_PHOTO);
                    eventFile.setFilePath(eventFile.ZZQS_CONFIG_PHOTO);
                    eventFile.setStatus(EventFile.STATUS_NEW);
                    eventFiles.add(eventFile);
                }
                eventFileDao.inserts(eventFiles);
                photoList.addAll(eventFileDao.find(null, "event_id=? and mold<>?", new String[]{orderEvent.get_id() + "", EventFile.MOLD_VOICE + ""}, null, null, null, null));
                adapter.notifyDataSetChanged();
            }
        }
    }

    public abstract void initCallbackHandler();

    public abstract void subclassView();


    public void shootPhoto(int type) {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Toast.makeText(this, R.string.prompt_no_sdcard, Toast.LENGTH_LONG).show();
            return;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = format.format(new Date());
        photoName = order.getOrderId() + mold + time + ".jpg";
        tmpFile = new File(ContentData.BASE_PICS + "/" + photoName);
        if (!tmpFile.exists()) {
            try {
                tmpFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        uri = Uri.fromFile(tmpFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, type);
    }


    public void submit() {
        if (mold.equals(OrderEvent.MOLD_HALFWAY)) {
            String msg = et.getText().toString().trim();
            String voiceFilePath = null;
            if (null != voiceFile) {
                voiceFilePath = voiceFile.getFilePath();
            }
            int photosSize = 0;
            if (null != photoList) {
                photosSize = photoList.size();
            }
            if (StringTools.isEmp(msg) && StringTools.isEmp(voiceFilePath) && photosSize == 0) {
                Toast.makeText(this, R.string.prompt_no_operations, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (lastTrace != null) {
            if ((lastTrace.getLatitude() == 0 || lastTrace.getLongitude() == 0) && (orderEvent.getLatitude() == 0 || orderEvent.getLongitude() == 0)) {
                Toast.makeText(this, R.string.prompt_unable_get_location_1, Toast.LENGTH_SHORT).show();
                submit.setEnabled(true);
                return;
            }
            saveData();
        } else {
            Toast.makeText(this, R.string.prompt_unable_get_location_2, Toast.LENGTH_SHORT).show();
        }
    }

    public abstract void saveData();

    public abstract void back();

    protected void SingleUpload(String isScancode) {
        pd.setMessage(getString(R.string.prompt_dl_submitting));
        pd.setCancelable(false);
        pd.show();
        List<JSONObject> goods = new ArrayList();
        if (!StringTools.isEmp(order.getOperationId())) {
            String[] names = order.getOperationGoodsName().split("/zzqs/");
            String[] units = order.getOperationGoodsUnit().split("/zzqs/");
            String[] counts1 = order.getOperationGoodsCount().split("/zzqs/");
            String[] counts2 = order.getActualGoodsCount().split("/zzqs/");
            String[] ids = order.getOperationId().split("/zzqs/");
            String[] damage = order.getOperationHasDamage().split("/zzqs/");
            String[] lack = order.getOperationHasLack().split("/zzqs/");
            for (int i = 0; i < ids.length; i++) {
                JSONObject object = new JSONObject();
                try {
                    object.put("name", names[i]);
                    object.put("_id", ids[i]);
                    object.put("unit", units[i]);
                    if (StringTools.isEmp(counts1[i])) {//如果创建运单时就没有数量，则返还原本的值
                        object.put("count", "0");
                    } else {
                        if (counts1[i].equals("0")) {//如果数量是0，表示没有修改过，就用原本的数量，否则用修改后的数量
                            object.put("count", counts2[i]);
                        } else {
                            if (lack[i].equals("false") && damage[i].equals("true")) {//只有货损没有或缺
                                object.put("count", counts2[i]);
                            } else {
                                if (StringTools.isNumber(counts2[i]) && StringTools.isNumber(counts1[i])) {
                                    double count = Double.valueOf(counts2[i]) - Double.valueOf(counts1[i]);
                                    object.put("count", count);
                                } else {
                                    object.put("count", counts2[2]);
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                goods.add(object);
            }
        }
        RestAPI.getInstance(getApplicationContext()).uploadEvent(
                order.getUpdateTime(),
                orderEvent,
                voiceFile.getFilePath(),
                photoList,
                isScancode,
                goods,
                new RestAPI.RestResponse() {
                    @Override
                    public void onSuccess(Object object) {
                        switch (mold) {
                            case OrderEvent.MOLD_PICKUP_ENTRANCE:
                                order.setStatus(Order.UN_PICKUP);
                                order.setPickupEntranceTime(orderEvent.getCreateTime());
                                break;
                            case OrderEvent.MOLD_PICKUP:
                                order.setStatus(Order.UN_DELIVERY_ENTRANCE);
                                order.setPickupTime(orderEvent.getCreateTime());
                                break;
                            case OrderEvent.MOLD_DELIVERY_ENTRANCE:
                                order.setStatus(Order.UN_DELIVERY);
                                order.setDeliveryEntranceTime(orderEvent.getCreateTime());
                                break;
                            case OrderEvent.MOLD_DELIVERY:
                                order.setStatus(Order.STATUS_COMMIT);
                                order.setDeliveryTime(orderEvent.getCreateTime());
                                break;
                        }
                        orderDao.update(order);
                        callbackHandler.sendEmptyMessage(UPLOAD_SUCCESS);
                    }

                    @Override
                    public void onFailure(Object object) {
                        submit.setEnabled(true);
                        if (pd.isShowing()) {
                            pd.dismiss();
                        }
                        if (object.toString().equals(Order.ORDER_NOT_EXIST)) {
                            order.setStatus(Order.STATUS_INVALID);
                            orderDao.update(order);
                            callbackHandler.sendEmptyMessage(ORDER_NOT_EXIST);
                        } else if (object.toString().equals(Order.ORDER_STATUS_HAS_CHANGED)) {
                            RestAPI.getInstance(getApplicationContext()).getOrder(order.getOrderId(), new RestAPI.RestResponse() {
                                @Override
                                public void onSuccess(Object object) {
                                    if (object instanceof Order) {
                                        Order newOrder = (Order) object;
                                        newOrder.set_id(order.get_id());
                                        orderDao.update(newOrder);
                                        callbackHandler.sendEmptyMessage(ORDER_STATUS_HAS_CHANGED);
                                    }
                                }

                                @Override
                                public void onFailure(Object object) {
                                    if (object.toString().equals(Order.ORDER_NOT_EXIST)) {
                                        order.setStatus(Order.STATUS_INVALID);
                                        orderDao.update(order);
                                        callbackHandler.sendEmptyMessage(ORDER_NOT_EXIST);
                                    } else if (object.toString().equals("disconnected")) {
                                        DialogView.showChoiceDialog(ZZQSApplication.getInstance().getCurrentContext(), DialogView.SINGLE_BTN, getString(R.string.prompt_dl_other_equipment_login_title), getString(R.string.prompt_dl_other_equipment_login_msg), new Handler() {
                                            @Override
                                            public void handleMessage(Message msg) {
                                                ZZQSApplication.getInstance().clearUser(BaseUploadEventActivity.this);
                                                ZZQSApplication.getInstance().cleanAllActivity();
                                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                            }
                                        });
                                    }
                                }
                            });
                        } else if (object.toString().equals("disconnected")) {
                            DialogView.showChoiceDialog(ZZQSApplication.getInstance().getCurrentContext(), DialogView.SINGLE_BTN, getString(R.string.prompt_dl_other_equipment_login_title), getString(R.string.prompt_dl_other_equipment_login_msg), new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    ZZQSApplication.getInstance().clearUser(BaseUploadEventActivity.this);
                                    ZZQSApplication.getInstance().cleanAllActivity();
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                }
                            });
                        } else {
                            Toast.makeText(BaseUploadEventActivity.this, object.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    protected void batchUpload(final String mold) {
        pd.setMessage(getString(R.string.prompt_dl_submitting));
        pd.setCancelable(false);
        pd.show();
        final List<String> orderIds = new ArrayList<String>();
        List<String> orderTimes = new ArrayList<String>();
        for (Order order1 : orders) {
            orderIds.add(order1.getOrderId());
            orderTimes.add(order1.getUpdateTime());
        }
        RestAPI.getInstance(getApplicationContext()).uploadEvents(
                orderTimes,
                orderIds,
                orderEvent,
                voiceFile.getFilePath(),
                photoList,
                new RestAPI.RestResponse() {
                    @Override
                    public void onSuccess(Object object) {
                        final HashMap<String, String> result = (HashMap<String, String>) object;
                        StringBuilder stringBuilder = new StringBuilder();
                        ArrayList<Order> updateOrderList = new ArrayList<>();
                        ArrayList<OrderEvent> insertEventList = new ArrayList<>();
                        ArrayList<OrderEvent> updateEventList = new ArrayList<>();
                        for (final Order order : orders) {
                            String back = result.get(order.getOrderId());
                            if (!back.equals("true")) {
                                if (back.equals(Order.ORDER_NOT_EXIST)) {
                                    stringBuilder.append("订单" + order.getSerialNo() + ",运单已被取消请与物流公司联系 \n");
                                    order.setStatus(Order.STATUS_INVALID);
                                    updateOrderList.add(order);
                                } else if (back.equals(Order.ORDER_STATUS_HAS_CHANGED)) {
                                    stringBuilder.append("订单" + order.getSerialNo() + "此运单状态与其他运单不同无法批量操作\n");
                                    RestAPI.getInstance(getApplicationContext()).getOrder(order.getOrderId(), new RestAPI.RestResponse() {
                                        @Override
                                        public void onSuccess(Object object) {
                                            if (object instanceof Order) {
                                                Order newOrder = (Order) object;
                                                newOrder.set_id(order.get_id());
                                                orderDao.update(newOrder);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Object object) {
                                        }
                                    });
                                }
                            } else {
                                if (mold.equals(OrderEvent.MOLD_PICKUP_ENTRANCE)) {
                                    order.setStatus(Order.UN_PICKUP);
                                    order.setPickupEntranceTime(orderEvent.getCreateTime());
                                } else if (mold.equals(OrderEvent.MOLD_PICKUP)) {
                                    order.setStatus(Order.UN_DELIVERY_ENTRANCE);
                                    order.setPickupTime(orderEvent.getCreateTime());
                                } else if (mold.equals(OrderEvent.MOLD_DELIVERY_ENTRANCE)) {
                                    order.setStatus(Order.UN_DELIVERY);
                                    order.setDeliveryEntranceTime(orderEvent.getCreateTime());
                                } else if (mold.equals(OrderEvent.MOLD_DELIVERY)) {
                                    order.setStatus(Order.STATUS_COMMIT);
                                    order.setDeliveryTime(orderEvent.getCreateTime());
                                }
                                updateOrderList.add(order);
                            }
                        }
                        order.setStatus(orders.get(0).getStatus());
                        for (OrderEvent event : orderEvents) {
                            if (result.get(event.getOrderId()).equals("true")) {
                                event.setStatus(OrderEvent.STATUS_COMMIT);
                            } else {
                                event.setStatus(OrderEvent.STATUS_NEW);
                            }
                            if (event.get_id() > 0) {
                                updateEventList.add(event);
                            } else {
                                insertEventList.add(event);
                            }
                        }

                        orderDao.updates(updateOrderList);
                        orderEventDao.updates(updateEventList);
                        orderEventDao.inserts(insertEventList);

                        if (stringBuilder.length() == 0) {
                            callbackHandler.sendEmptyMessage(UPLOAD_SUCCESS);
                            Toast.makeText(BaseUploadEventActivity.this, getString(R.string.prompt_submitted), Toast.LENGTH_SHORT).show();
                        } else {
                            DialogView.showChoiceDialog(BaseUploadEventActivity.this, DialogView.NORMAL, getString(R.string.prompt_submit_result), stringBuilder.toString(), new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    super.handleMessage(msg);
                                    callbackHandler.sendEmptyMessage(UPLOAD_SUCCESS);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Object object) {
                        submit.setEnabled(true);
                        if (pd.isShowing()) {
                            pd.dismiss();
                        }
                        if (object.toString().equals("disconnected")) {
                            DialogView.showChoiceDialog(ZZQSApplication.getInstance().getCurrentContext(), DialogView.SINGLE_BTN, getString(R.string.prompt_dl_other_equipment_login_title), getString(R.string.prompt_dl_other_equipment_login_msg), new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    ZZQSApplication.getInstance().clearUser(BaseUploadEventActivity.this);
                                    ZZQSApplication.getInstance().cleanAllActivity();
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                }
                            });
                        } else {
                            Toast.makeText(BaseUploadEventActivity.this, object.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    protected void isHaveEventId() {
        if (StringTools.isEmp(orderEvent.getEventId())) {
            String currentTime = String.valueOf(System.currentTimeMillis());
            currentTime = currentTime.substring(currentTime.length() - 4, currentTime.length());
            String eventId = ZZQSApplication.getInstance().getUser().getUsername() + orderEvent.getOrderId().substring(orderEvent.getOrderId().length() - 4, orderEvent.getOrderId().length()) + orderEvent.getMold() + currentTime;
            orderEvent.setEventId(eventId);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.has_damage:
                if (!isDamaged) {
                    isLast = true;
                    shootPhoto(DAMAGE);
                } else {
                    isDamaged = false;
                    changeDamage();
                }
                break;
            case R.id.btn_scancode_commit://扫码交货
                if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    Toast.makeText(this, R.string.prompt_no_sdcard, Toast.LENGTH_LONG).show();
                    return;
                }
                intent = new Intent(this, MipcaActivityCapture.class);
                startActivityForResult(intent, MipcaActivityCapture.SCAN_CODE);
                break;
            case R.id.rl_actualDelivery:
                intent = new Intent(this, ActualDeliveryActivity.class);
                String type;
                if (order.getStatus().equals(Order.UN_PICKUP_ENTRANCE) || order.getStatus().equals(Order.UN_PICKUP)) {
                    type = OrderEvent.MOLD_PICKUP;
                } else {
                    type = OrderEvent.MOLD_DELIVERY;
                }
                intent.putExtra(BaseUploadEventActivity.EVENT_TYPE, type);
                intent.putExtra(Order.ORDER_ID, order.getOrderId());
                intent.putExtra(OrderEvent.ORDER_EVENT_ID, orderEvent.get_id());
                startActivityForResult(intent, ActualDeliveryActivity.ACTUAL);
                break;
            case R.id.head_back:
                isUpload = false;
                back();
                break;
            case R.id.head_right:
                intent = new Intent(getApplicationContext(), OrderTimeAxisActivity.class);
                intent.putExtra(Order.ORDER, order);
                startActivity(intent);
                break;
            case R.id.submit:
                for (int j = 0; j < photoList.size(); j++) {
                    if (photoList.get(j).getFilePath().equals(EventFile.ZZQS_CONFIG_PHOTO)) {
                        Toast.makeText(BaseUploadEventActivity.this, getString(R.string.prompt_lack_photos), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (!StringTools.isEmp(order.getOperationGoodsName())) {
                    if (mold.equals(OrderEvent.MOLD_PICKUP)) {
                        if (!StringTools.isEmp(order.getPickupMustConfirmDetail()) && order.getPickupMustConfirmDetail().equals("true")) {
                            if (order.getCommitPickupConfigDetail().equals("false")) {
                                Toast.makeText(BaseUploadEventActivity.this, getString(R.string.prompt_must_pickup_actual), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                    if (mold.equals(OrderEvent.MOLD_DELIVERY)) {
                        System.out.println("1:" + order.getOperationGoodsName());
                        System.out.println("2:" + order.getDeliveryMustConfirmDetail());
                        System.out.println("3:" + order.getCommitDeliveryConfigDetail());
                        if (!StringTools.isEmp(order.getDeliveryMustConfirmDetail()) && order.getDeliveryMustConfirmDetail().equals("true")) {
                            if (order.getCommitDeliveryConfigDetail().equals("false")) {
                                Toast.makeText(BaseUploadEventActivity.this, getString(R.string.prompt_must_delivery_actual), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                }
                isUpload = true;
                lastTrace = LocationService.lastTrace;
                submit();
                break;
            case R.id.iv_popup:
                if (isVoice) {
                    tvRcd.setVisibility(View.GONE);
                    playSound.setVisibility(View.GONE);
                    btnBottom.setVisibility(View.VISIBLE);
                    isVoice = false;
                    ivPopUp.setImageResource(R.drawable.chatting_setmode_msg_btn);
                } else {
                    tvRcd.setVisibility(View.VISIBLE);
                    btnBottom.setVisibility(View.GONE);
                    if (!StringTools.isEmp(voiceName)) {
                        playSound.setVisibility(View.VISIBLE);
                    }
                    ivPopUp.setImageResource(R.drawable.chatting_setmode_voice_btn);
                    isVoice = true;
                }
                break;
            case R.id.play_sound:
                try {
                    if (mediaPlayer == null || state == STOP) {
                        // 创建MediaPlayer对象并设置Listener
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {
                                playSound.setBackgroundResource(R.drawable.btn_pause_sound);
                                playSound.setEnabled(false);
                                mediaPlayer.start();
                                state = START;
                            }
                        });
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                playSound.setBackgroundResource(R.drawable.btn_play_sound);
                                playSound.setEnabled(true);
                                state = STOP;
                            }
                        });
                    } else {
                        // 复用MediaPlayer对象
                        mediaPlayer.reset();
                    }
                    mediaPlayer.setDataSource(voiceFile.getFilePath());
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }

    }

    Handler photoHandler = new Handler() {//运单没填货物名时货损拍照返回改变按钮状态
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    isDamaged = !isDamaged;
                    changeDamage();
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SHOOT_PHOTO || requestCode == DAMAGE) {
                try {
                    if (null != tmpFile && tmpFile.exists()) {
                        pd.setMessage(getString(R.string.prompt_dl_compressed_image));
                        pd.setCancelable(false);
                        pd.show();
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                String bigFilePath = null;
                                if (isNeedBigFile) {
                                    bigFilePath = ContentData.BASE_BIG_PICS + "/" + photoName;
                                    FileUtil.copyFile(tmpFile.getAbsolutePath(), bigFilePath);
                                }
                                Bitmap bitmap = ImageUtil.comp(tmpFile.getAbsolutePath(), 50, 600, 800);
                                int degree = ImageUtil.readPictureDegree(tmpFile.getAbsolutePath());
                                bitmap = ImageUtil.rotaingImageView(degree, bitmap);
                                boolean saveSuccess = ImageUtil.saveCompressBitmap(bitmap, tmpFile.getAbsolutePath(), 50);
                                if (!saveSuccess) {
                                    if (tmpFile != null && tmpFile.exists()) {
                                        tmpFile.delete();
                                    }
                                    callbackHandler.sendEmptyMessage(SAVE_FILE_FAILED);
                                    return;
                                }
                                if (isLast) {//如果点击的是最后一个默认按钮，每次都新建一个eventFile
                                    EventFile eventFile = new EventFile();
                                    eventFile.setOrderId(orderEvent.getOrderId());
                                    eventFile.setEventId(orderEvent.get_id());
                                    eventFile.setStatus(EventFile.STATUS_NEW);
                                    eventFile.setFilePath(tmpFile.getAbsolutePath());

                                    if (isNeedBigFile) {
                                        eventFile.setBigFilePath(bigFilePath);
                                    }

                                    if (requestCode == DAMAGE) {
                                        eventFile.setMold(EventFile.MOLD_DAMAGE_PHOTO);
                                        eventFile.setConfigName(EventFile.DAMAGE);
                                        photoHandler.sendEmptyMessage(0);
                                    } else {
                                        eventFile.setMold(EventFile.MOLD_NORMAL_PHOTO);
                                        String name;
                                        switch (mold) {
                                            case OrderEvent.MOLD_PICKUP_ENTRANCE:
                                                name = EventFile.PICKUP_ENTER;
                                                break;
                                            case OrderEvent.MOLD_PICKUP:
                                                name = EventFile.PICKUP;
                                                break;
                                            case OrderEvent.MOLD_DELIVERY_ENTRANCE:
                                                name = EventFile.DELIVERY_ENTER;
                                                break;
                                            case OrderEvent.MOLD_DELIVERY:
                                                name = EventFile.DELIVERY;
                                                break;
                                            case OrderEvent.MOLD_HALFWAY:
                                                name = EventFile.HALF_WAY;
                                                break;
                                            default:
                                                name = EventFile.DEFAULT;
                                                break;

                                        }
                                        eventFile.setConfigName(name);
                                    }
                                    photoList.add(photoList.size(), eventFile);
                                    eventFile.set_id((int) eventFileDao.insert(eventFile));
                                    isLast = false;
                                } else {//不是最后一个，直接赋值给eventFile
                                    photoList.get(index).setFilePath(tmpFile.getAbsolutePath());
                                    EventFile eventFile = photoList.get(index);
                                    eventFile.setFilePath(tmpFile.getAbsolutePath());
                                    if (isNeedBigFile) {
                                        eventFile.setBigFilePath(bigFilePath);
                                    }
                                    eventFileDao.update(eventFile);
                                }
                                System.out.println("bigFilePath:" + bigFilePath);
                                callbackHandler.sendEmptyMessage(GET_PHOTO);
                            }
                        }.start();
                    } else {
                        if (pd.isShowing()) {
                            pd.dismiss();
                        }
                        Toast.makeText(this, getString(R.string.prompt_unexpected_error_take_photo), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                    Log.d("压缩图片出错", e.getMessage());
                    e.printStackTrace();
                    Toast.makeText(this, getString(R.string.prompt_unexpected_error_take_photo), Toast.LENGTH_SHORT).show();
                    if (tmpFile != null && tmpFile.exists()) {
                        tmpFile.delete();
                    }
                }
            } else if (requestCode == MipcaActivityCapture.SCAN_CODE) {
                Bundle bundle = data.getExtras();
                String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
                Pattern patt = Pattern.compile(regex);
                String text = bundle.getString("result");
                Matcher matcher = patt.matcher(bundle.getString("result"));
                boolean isMatch = matcher.matches();
                if (!isMatch) {
                    if (order.getOrderDetailId().equals(text)) {
                        if (!Connectivities.isConnected(getApplicationContext()) && !Connectivities.isWifiConnected(this)) {
                            DialogView.showChoiceDialog(this, DialogView.NORMAL, getString(R.string.prompt_dl_title_2), getString(R.string.prompt_dl_not_open_network), null);
                        } else {
                            isScanCode = "true";
                            isUpload = true;
                            lastTrace = LocationService.lastTrace;
                            submit.setEnabled(false);
                            submit();
                        }
                    } else {
                        Toast.makeText(this, getString(R.string.prompt_no_same_order), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.prompt_invalid_scan_info), Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == ActualDeliveryActivity.ACTUAL) {
                order = orderDao.find(null, "order_id = ?", new String[]{order.getOrderId()}, null, null, null, null).get(0);
                EventBus.getDefault().post(new Events.OrderEvent(order, Events.REFRESH_ORDER));
                if (!StringTools.isEmp(order.getActualGoodsId())) {//新类型运单
                    if (order.getOperationHasDamage().contains("true")) {
                        isDamaged = true;
                    }
                } else {
                    if (order.getOperationHasDamage().equals("true")) {//旧类型运单
                        isDamaged = true;
                    }
                }
                orderEvent.setIsDamaged(isDamaged ? 1 : 0);
                orderEventDao.update(orderEvent);
                initData();
                subclassView();
            }
        } else {
            if (tmpFile != null && tmpFile.exists()) {
                tmpFile.delete();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!Environment.getExternalStorageDirectory().exists()) {
            Toast.makeText(this, R.string.prompt_no_sdcard, Toast.LENGTH_LONG).show();
            return false;
        }

        if (isVoice) {
            int[] location = new int[2];
            tvRcd.getLocationInWindow(location); // 获取在当前窗口内的绝对坐标
            int btn_rc_Y = location[1];
            int btn_rc_X = location[0];
            int[] del_location = new int[2];
            delRe.getLocationInWindow(del_location);
            int del_Y = del_location[1];
            int del_x = del_location[0];
            if (event.getAction() == MotionEvent.ACTION_DOWN && voiceFlag == 1) {
                if (!Environment.getExternalStorageDirectory().exists()) {
                    Toast.makeText(this, R.string.prompt_no_sdcard, Toast.LENGTH_LONG).show();
                    return false;
                }
                if (event.getY() > btn_rc_Y && event.getX() > btn_rc_X) {//判断手势按下的位置是否是语音录制按钮的范围内
                    //删除上次录制的音频
                    if (null != voiceFile && !StringTools.isEmp(voiceFile.getFilePath())) {
                        File vf = new File(voiceFile.getFilePath());
                        if (vf.exists()) {
                            vf.delete();
                        }
                    } else {
                        voiceFile = new EventFile();
                    }
                    tvRcd.setBackgroundResource(R.drawable.voice_rcd_bg_pressed);
                    rcChatPopup.setVisibility(View.VISIBLE);
                    voiceRcdHintLoading.setVisibility(View.VISIBLE);
                    voiceCcdHintRcding.setVisibility(View.GONE);
                    voiceRcdHintTooShort.setVisibility(View.GONE);
                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            if (!isShort) {
                                voiceRcdHintLoading.setVisibility(View.GONE);
                                voiceCcdHintRcding.setVisibility(View.VISIBLE);
                            }
                        }
                    }, 300);
                    startVoiceT = System.currentTimeMillis();
                    voiceName = order.getOrderId() + startVoiceT + ".amr";
                    voiceFile.setFilePath(ContentData.BASE_SOUNDS + "/" + voiceName);
                    voiceFile.setMold(EventFile.MOLD_VOICE);
                    voiceFile.setStatus(EventFile.STATUS_NEW);
                    start(voiceName);
                    voiceFlag = 2;
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP && voiceFlag == 2) {//松开手势时执行录制完成
                tvRcd.setBackgroundResource(R.drawable.voice_rcd_bg_nor);
                if (event.getY() >= del_Y
                        && event.getY() <= del_Y + delRe.getHeight()
                        && event.getX() >= del_x
                        && event.getX() <= del_x + delRe.getWidth()) {
                    rcChatPopup.setVisibility(View.GONE);
                    img1.setVisibility(View.VISIBLE);
                    delRe.setVisibility(View.GONE);
                    stop();
                    voiceFlag = 1;
                    File vf = new File(voiceFile.getFilePath());
                    if (vf.exists()) {
                        vf.delete();
                        playSound.setVisibility(View.GONE);
                        voiceFile.setFilePath(null);
                    }
                } else {
                    voiceCcdHintRcding.setVisibility(View.GONE);
                    endVoiceT = System.currentTimeMillis();
                    voiceFlag = 1;
                    int time = (int) ((endVoiceT - startVoiceT) / 1000);
                    if (time < 1) {
                        isShort = true;
                        voiceRcdHintLoading.setVisibility(View.GONE);
                        voiceCcdHintRcding.setVisibility(View.GONE);
                        voiceRcdHintTooShort.setVisibility(View.VISIBLE);
                        File vf = new File(voiceFile.getFilePath());
                        if (vf.exists()) {
                            vf.delete();
                            playSound.setVisibility(View.GONE);
                            voiceFile.setFilePath(null);
                            stop();
                        }
                        mHandler.postDelayed(new Runnable() {
                            public void run() {
                                voiceRcdHintTooShort.setVisibility(View.GONE);
                                rcChatPopup.setVisibility(View.GONE);
                                isShort = false;
                            }
                        }, 500);
                        return false;
                    }
                    rcChatPopup.setVisibility(View.GONE);
                    playSound.setVisibility(View.VISIBLE);
                    stop();
                }
            }
            if (event.getY() < btn_rc_Y) {//手势按下的位置不在语音录制按钮的范围内
                Animation mLitteAnimation = AnimationUtils.loadAnimation(this, R.anim.cancel_rc);
                Animation mBigAnimation = AnimationUtils.loadAnimation(this, R.anim.cancel_rc2);
                img1.setVisibility(View.GONE);
                delRe.setVisibility(View.VISIBLE);
                delRe.setBackgroundResource(R.drawable.voice_rcd_cancel_bg);
                if (event.getY() >= del_Y
                        && event.getY() <= del_Y + delRe.getHeight()
                        && event.getX() >= del_x
                        && event.getX() <= del_x + delRe.getWidth()) {
                    delRe.setBackgroundResource(R.drawable.voice_rcd_cancel_bg_focused);
                    scImg1.startAnimation(mLitteAnimation);
                    scImg1.startAnimation(mBigAnimation);
                }
            } else {
                img1.setVisibility(View.VISIBLE);
                delRe.setVisibility(View.GONE);
                delRe.setBackgroundResource(0);
            }
        }
        return super.onTouchEvent(event);
    }

    private Runnable mPollTask = new Runnable() {
        public void run() {
            double amp = mSensor.getAmplitude();
            updateDisplay(amp);
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);

        }
    };
    private Runnable mSleepTask = new Runnable() {
        public void run() {
            stop();
        }
    };

    private void updateDisplay(double signalEMA) {

        switch ((int) signalEMA) {
            case 0:
            case 1:
                volume.setImageResource(R.drawable.amp1);
                break;
            case 2:
            case 3:
                volume.setImageResource(R.drawable.amp2);

                break;
            case 4:
            case 5:
                volume.setImageResource(R.drawable.amp3);
                break;
            case 6:
            case 7:
                volume.setImageResource(R.drawable.amp4);
                break;
            case 8:
            case 9:
                volume.setImageResource(R.drawable.amp5);
                break;
            case 10:
            case 11:
                volume.setImageResource(R.drawable.amp6);
                break;
            default:
                volume.setImageResource(R.drawable.amp7);
                break;
        }
    }

    private void start(String name) {
        mSensor.start(name);
        mHandler.postDelayed(mPollTask, POLL_INTERVAL);
    }

    private void stop() {
        mHandler.removeCallbacks(mSleepTask);
        mHandler.removeCallbacks(mPollTask);
        mSensor.stop();
        volume.setImageResource(R.drawable.amp1);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("imageFilePath", photoName);
        outState.putBoolean("isLast", isLast);
        outState.putInt("index", index);
        if (null != voiceFile && !StringTools.isEmp(voiceFile.getFilePath())) {
            outState.putParcelable("voiceFile", voiceFile);
        }
        outState.putBoolean("isDamaged", isDamaged);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            photoName = savedInstanceState.getString("imageFilePath");
            if (null != photoName) {
                tmpFile = new File(ContentData.BASE_PICS + "/" + photoName);
            }
            isLast = savedInstanceState.getBoolean("isLast");
            isDamaged = savedInstanceState.getBoolean("isDamaged");
            index = savedInstanceState.getInt("index");
            changeDamage();
            voiceName = savedInstanceState.getString("voiceName");
            voiceFile = savedInstanceState.getParcelable("voiceFile");
            if (null != voiceFile && !StringTools.isEmp(voiceFile.getFilePath())) {
                tvRcd.setVisibility(View.VISIBLE);
                btnBottom.setVisibility(View.GONE);
                if (!StringTools.isEmp(voiceName)) {
                    playSound.setVisibility(View.VISIBLE);
                }
                ivPopUp.setImageResource(R.drawable.chatting_setmode_voice_btn);
                isVoice = true;
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        if (reverseGeoCodeResult != null) {
            orderEvent.setAddress(reverseGeoCodeResult.getAddress());
        }
        saveData();
    }
}
