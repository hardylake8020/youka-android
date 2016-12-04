package com.zzqs.app_kc.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.activities.UploadEvent.BaseUploadEventActivity;
import com.zzqs.app_kc.adapter.ActualDeliveryAdapter;
import com.zzqs.app_kc.app.ContentData;
import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.db.DaoManager;
import com.zzqs.app_kc.entity.EventFile;
import com.zzqs.app_kc.entity.Events;
import com.zzqs.app_kc.entity.Order;
import com.zzqs.app_kc.entity.OrderEvent;
import com.zzqs.app_kc.net.RestAPI;
import com.zzqs.app_kc.utils.ImageUtil;
import com.zzqs.app_kc.utils.StringTools;
import com.zzqs.app_kc.widgets.DialogView;
import com.zzqs.app_kc.widgets.SafeProgressDialog;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 描述：实际收货信息页面
 * by:ray
 * 2015-9-18 21:45
 */
public class ActualDeliveryActivity extends BaseActivity implements View.OnClickListener {
    public static final String EVENT_TYPE = "mold";
    TextView head_title, tv_money;
    Button btn_commit;
    ListView lv_goods;
    private ActualDeliveryAdapter actualAdapter;
    private ArrayList<Goods> list, operationList;
    private Order order;
    protected OrderEvent orderEvent;
    public static int ACTUAL = 105;
    private String mold;
    protected String photoName;
    protected File tmpFile;
    protected Uri uri;
    protected SafeProgressDialog pd;
    private int index;
    private ArrayList<EventFile> eventFiles = null;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Toast.makeText(ActualDeliveryActivity.this, getString(R.string.prompt_take_photo_success), Toast.LENGTH_SHORT).show();
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                if (!StringTools.isEmp(order.getActualGoodsId())) {//新运单
                    operationList.get(index).setHasDamage("true");
                    actualAdapter.setOperationList(operationList);
                }
            } else if (msg.what == 2) {
                Toast.makeText(ActualDeliveryActivity.this, getString(R.string.prompt_save_file_failed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_actual_delivery);
        initView();
        initData();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        pd.dismiss();
    }

    public void onEvent(Events.GoodsEvent event) {
        if (event.getType() == Events.TAkE_PHOTO) {//货损拍照
            takePhoto();
            index = event.getCurrent();
        } else if (event.getType() == Events.CUT_MONEY) {//货差数量增加
            moneyCount();
        }
    }

    private void initView() {
        pd = new SafeProgressDialog(this);
        head_title = (TextView) findViewById(R.id.head_title);
        btn_commit = (Button) findViewById(R.id.btn_commit);
        btn_commit.setOnClickListener(this);
        findViewById(R.id.head_back).setOnClickListener(this);
        lv_goods = (ListView) findViewById(R.id.lv_goods);
        tv_money = (TextView) findViewById(R.id.tv_money);
    }

    private void initData() {
        mold = getIntent().getStringExtra(EVENT_TYPE);
        String orderId = getIntent().getStringExtra(Order.ORDER_ID);
        if (!StringTools.isEmp(orderId)) {
            List<Order> orderList = DaoManager.getOrderDao(getApplicationContext()).find(null, "order_id=?", new String[]{orderId}, null, null, null, null);
            if (null != orderList && orderList.size() == 1) {
                order = orderList.get(0);
            }
        } else {
            finish();
        }
        eventFiles = new ArrayList<>();
        int eventId = getIntent().getIntExtra(OrderEvent.ORDER_EVENT_ID, 0);
        if (eventId != 0) {
            List<OrderEvent> eventList = DaoManager.getOrderEventDao(getApplicationContext()).find(null, "_id = ?", new String[]{eventId + ""}, null, null, null, null);
            if (null != eventList && eventList.size() == 1) {
                orderEvent = eventList.get(0);
            }
        } else {
            finish();
        }
        if (mold.equals(OrderEvent.MOLD_PICKUP)) {
            head_title.setText(getString(R.string.view_tv_actual_pickup));
        } else {
            head_title.setText(getString(R.string.view_tv_actual_delivery));
        }
        if (!StringTools.isEmp(order.getActualGoodsId())) {//新版本创建的运单
            list = new ArrayList<Goods>();
            operationList = new ArrayList<Goods>();
            String[] names = order.getActualGoodsName().split("/zzqs/");
            String[] units = order.getActualGoodsUnit().split("/zzqs/");
            String[] units2 = order.getActualGoodsUnit2().split("/zzqs/");
            String[] units3 = order.getActualGoodsUnit3().split("/zzqs/");
            String[] counts = order.getActualGoodsCount().split("/zzqs/");
            String[] countsNext = order.getActualGoodsCount2().split("/zzqs/");
            String[] countsLast = order.getActualGoodsCount3().split("/zzqs/");
            String[] ids = order.getActualGoodsId().split("/zzqs/");
            String[] price = order.getActualPrice().split("/zzqs/");
            for (int j = 0; j < names.length; j++) {
                Goods goods = new Goods();
                goods.setName(names[j]);
                goods.setUnit(units[j]);
                goods.setCount(counts[j]);
                goods.setGoodsID(ids[j]);
                goods.setPrice(price[j]);
                if (null != units2 && units2.length >= j + 1) {
                    goods.setCount2(countsNext[j]);
                    goods.setCount3(countsLast[j]);
                    goods.setUnit2(units2[j]);
                    goods.setUnit3(units3[j]);
                }
                list.add(goods);
            }

            String[] op_names = order.getOperationGoodsName().split("/zzqs/");
            String[] op_units = order.getOperationGoodsUnit().split("/zzqs/");
            String[] op_counts = order.getOperationGoodsCount().split("/zzqs/");
            String[] op_ids = order.getOperationId().split("/zzqs/");
            String[] op_lack = order.getOperationHasLack().split("/zzqs/");
            String[] op_damage = order.getOperationHasDamage().split("/zzqs/");
            String[] op_price = order.getOperationPrice().split("/zzqs/");
            for (int j = 0; j < op_names.length; j++) {
                Goods goods = new Goods();
                goods.setName(op_names[j]);
                goods.setUnit(op_units[j]);
                goods.setCount(op_counts[j]);
                goods.setGoodsID(op_ids[j]);
                goods.setHasDamage(op_damage[j]);
                goods.setHasLack(op_lack[j]);
                goods.setPrice(op_price[j]);
                operationList.add(goods);
            }

            if (order.getStatus().equals(Order.UN_PICKUP_ENTRANCE) || order.getStatus().equals(Order.UN_PICKUP)) {//提货运单不显示
                tv_money.setVisibility(View.GONE);
            } else {
                tv_money.setVisibility(View.VISIBLE);
                if (StringTools.isEmp(order.getActualPrice()) || StringTools.isEmp(order.getActualGoodsCount())) {
                    tv_money.setText(getString(R.string.view_tv_unknown_money));
                } else {
                    moneyCount();
                }
            }
            actualAdapter = new ActualDeliveryAdapter(this, list, operationList);
            lv_goods.setAdapter(actualAdapter);
        }
    }

    private void moneyCount() {
        double money = 0;
        double x = 0;
        for (int j = 0; j < list.size(); j++) {
            String count = list.get(j).getCount();
            double totalCount, lossCount, price;
            if (StringTools.isNumber(count)) {
                totalCount = Double.parseDouble(list.get(j).getCount());
                if (StringTools.isNumber(operationList.get(j).getCount())) {
                    if (operationList.get(j).getHasLack().equals("true")) {
                        lossCount = Double.parseDouble(operationList.get(j).getCount());
                    } else {
                        lossCount = 0;
                    }
                } else {
                    lossCount = 0;
                }
                if (StringTools.isNumber(list.get(j).getPrice())) {
                    price = Double.parseDouble(list.get(j).getPrice());
                } else {
                    price = 0;
                }
                x = (totalCount - lossCount) * price;
            }
            money += x;
        }
        String str;
        if (money == 0) {
            str = "0";
        } else {
            str = new DecimalFormat("#.00").format(money);
        }
        tv_money.setText(getString(R.string.view_tv_money) + str);
    }


    private void commitNewTypeOrder(final Intent intent) {//交货运单
        operationList = actualAdapter.getList();
        StringBuffer goodsId = new StringBuffer();
        StringBuffer goodsName = new StringBuffer();
        StringBuffer goodsUnit = new StringBuffer();
        StringBuffer goodsCount = new StringBuffer();
        StringBuffer goodsLack = new StringBuffer();
        StringBuffer goodsDamage = new StringBuffer();
        StringBuilder operationCount = new StringBuilder();
        boolean flag = false;
        for (int i = 0; i < operationList.size(); i++) {
            goodsId.append(operationList.get(i).getGoodsID());
            goodsName.append(operationList.get(i).getName());
            goodsUnit.append(operationList.get(i).getUnit());
            if (!operationList.get(i).getHasLack().equals("true") && !operationList.get(i).getHasDamage().equals("true")) {//既不是货损也不是或缺
                if (StringTools.isEmp(list.get(i).getCount())) {
                    goodsCount.append("0");
                } else {
                    goodsCount.append(list.get(i).getCount());
                }
                operationCount.append("0");
            } else {
                if (StringTools.isEmp(operationList.get(i).getCount())) {
                    goodsCount.append("0");
                    operationCount.append("0");
                } else {
                    goodsCount.append(operationList.get(i).getCount());
                    operationCount.append(operationList.get(i).getCount());
                }
            }
            goodsLack.append(operationList.get(i).getHasLack());
            goodsDamage.append(operationList.get(i).getHasDamage());
            if (i != operationList.size() - 1) {
                goodsId.append("/zzqs/");
                goodsName.append("/zzqs/");
                goodsUnit.append("/zzqs/");
                goodsCount.append("/zzqs/");
                goodsLack.append("/zzqs/");
                goodsDamage.append("/zzqs/");
                operationCount.append("/zzqs/");
            }
        }
        for (int i = 0; i < operationList.size(); i++) {
            String str;
            if (StringTools.isEmp(list.get(i).getCount())) {
                str = "0";
            } else {
                str = list.get(i).getCount();
            }

            if (!str.equals("0") && !str.equals("0.0") && Double.valueOf(operationList.get(i).getCount()) != 0) {
                if (!operationList.get(i).getCount().equals(list.get(i).getCount())) {
                    if (!operationList.get(i).getHasLack().equals("true") && !operationList.get(i).getHasDamage().equals("true")) {
                        flag = true;
                    }
                }
            }
        }

        if (flag) {
            Toast.makeText(this, getString(R.string.prompt_no_lack_reason), Toast.LENGTH_SHORT).show();
            return;
        }
        order.setOperationId(goodsId.toString());
        order.setOperationGoodsName(goodsName.toString());
        order.setOperationGoodsCount(operationCount.toString());
        order.setOperationGoodsUnit(goodsUnit.toString());
        order.setOperationHasDamage(goodsDamage.toString());
        order.setOperationHasLack(goodsLack.toString());

        List<JSONObject> goods1 = new ArrayList();
        if (!StringTools.isEmp(order.getOperationId())) {
            String[] names = order.getOperationGoodsName().split("/zzqs/");
            String[] units = order.getOperationGoodsUnit().split("/zzqs/");
            String[] counts1 = goodsCount.toString().split("/zzqs/");
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
                        object.put("count", counts2[i]);
                    } else {
                        if (counts1[i].equals("0")) {//如果数量是0，表示没有修改过，就用原本的数量，否则用修改后的数量
                            object.put("count", counts2[i]);
                        } else {
                            if (lack[i].equals("false") && damage[i].equals("true")) {//只有货损没有或缺
                                object.put("count", counts2[i]);
                            } else {
                                if (StringTools.isNumber(counts2[i])) {
                                    double count1 = Double.parseDouble(counts1[i]);
                                    double count2 = Double.parseDouble(counts2[i]);
                                    object.put("count", (count2 - count1));
                                } else {
                                    object.put("count", counts2[i]);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ZZQSApplication.getInstance().CrashToLogin();
                }
                goods1.add(object);
            }
        }
        pd.setMessage(getString(R.string.prompt_dl_submitting));
        pd.setCancelable(false);
        pd.show();
        RestAPI.getInstance(getApplicationContext()).uploadActualGoods(order.getOrderId(), goods1, new RestAPI.RestResponse() {
            @Override
            public void onSuccess(Object object) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                order.setCommitDeliveryConfigDetail("true");
                DaoManager.getOrderDao(getApplicationContext()).update(order);
                DaoManager.getEventFileDao(getApplicationContext()).inserts(eventFiles);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFailure(Object object) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                if (object.toString().equals("disconnected")) {
                    DialogView.showChoiceDialog(ZZQSApplication.getInstance().getCurrentContext(), DialogView.SINGLE_BTN, getString(R.string.prompt_dl_other_equipment_login_title), getString(R.string.prompt_dl_other_equipment_login_msg), new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            ZZQSApplication.getInstance().clearUser(ActualDeliveryActivity.this);
                            ZZQSApplication.getInstance().cleanAllActivity();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        }
                    });
                } else {
                    Toast.makeText(ActualDeliveryActivity.this, object.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void pickUpNewTypeOrder(Intent intent) {//提货运单
        operationList = actualAdapter.getList();
        StringBuffer goodsId = new StringBuffer();
        StringBuffer goodsName = new StringBuffer();
        StringBuffer goodsUnit = new StringBuffer();
        StringBuffer goodsCount = new StringBuffer();
        StringBuffer goodsLack = new StringBuffer();
        StringBuffer goodsDamage = new StringBuffer();
        boolean flag = false;
        for (int i = 0; i < operationList.size(); i++) {
            goodsId.append(operationList.get(i).getGoodsID());
            goodsName.append(operationList.get(i).getName());
            goodsUnit.append(operationList.get(i).getUnit());
            if (!operationList.get(i).getHasLack().equals("true") && !operationList.get(i).getHasDamage().equals("true")) {//既不是货损也不是或缺
                    goodsCount.append(0 + "");
            } else {
                if (StringTools.isEmp(operationList.get(i).getCount())) {
                    goodsCount.append("0");
                } else {
                    goodsCount.append(operationList.get(i).getCount());
                }
            }
            goodsLack.append(operationList.get(i).getHasLack());
            goodsDamage.append(operationList.get(i).getHasDamage());
            if (i != operationList.size() - 1) {
                goodsId.append("/zzqs/");
                goodsName.append("/zzqs/");
                goodsUnit.append("/zzqs/");
                goodsCount.append("/zzqs/");
                goodsLack.append("/zzqs/");
                goodsDamage.append("/zzqs/");
            }
        }

        for (int i = 0; i < operationList.size(); i++) {
            String str;
            if (StringTools.isEmp(list.get(i).getCount())) {
                str = "0";
            } else {
                str = list.get(i).getCount();
            }

            if (!str.equals("0") && !str.equals("0.0") && Double.valueOf(operationList.get(i).getCount()) != 0) {
                if (!operationList.get(i).getCount().equals(list.get(i).getCount())) {
                    if (!operationList.get(i).getHasLack().equals("true") && !operationList.get(i).getHasDamage().equals("true")) {
                        flag = true;
                    }
                }
            }
        }

        if (flag) {
            Toast.makeText(this, getString(R.string.prompt_no_lack_reason), Toast.LENGTH_SHORT).show();
            return;
        }
        order.setOperationId(goodsId.toString());
        order.setOperationGoodsName(goodsName.toString());
        order.setOperationGoodsCount(goodsCount.toString());
        order.setOperationGoodsUnit(goodsUnit.toString());
        order.setOperationHasDamage(goodsDamage.toString());
        order.setOperationHasLack(goodsLack.toString());
        order.setCommitPickupConfigDetail("true");
        DaoManager.getOrderDao(getApplicationContext()).update(order);
        DaoManager.getEventFileDao(getApplicationContext()).inserts(eventFiles);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.head_back:
                finish();
                break;
            case R.id.btn_commit:
                Intent intent = new Intent();
                if (!StringTools.isEmp(order.getActualGoodsId())) {
                    if (order.getStatus().equals(Order.UN_DELIVERY) || order.getStatus().equals(Order.UN_DELIVERY_ENTRANCE)) {
                        commitNewTypeOrder(intent);//交货的时候才提交
                    } else {
                        pickUpNewTypeOrder(intent);//提货的时候不提交
                    }
                }
                break;
            default:
                break;
        }
    }

    public static class Goods implements Parcelable {
        private String goodsName;
        private String goodsUnit;
        private String goodsCount;
        private String goods_id;
        private String hasDamage = "false";//货损
        private String hasLack = "false";//缺失
        private String price;
        private String goodsUnit2;
        private String goodsUnit3;
        private String goodsCount2;
        private String goodsCount3;


        public Goods() {

        }

        public String getName() {
            return goodsName;
        }

        public void setName(String name) {
            this.goodsName = name;
        }

        public String getUnit() {
            return goodsUnit;
        }

        public void setUnit(String unit) {
            this.goodsUnit = unit;
        }

        public String getUnit2() {
            return goodsUnit2;
        }

        public void setUnit2(String unit) {
            this.goodsUnit2 = unit;
        }

        public String getUnit3() {
            return goodsUnit3;
        }

        public void setUnit3(String unit) {
            this.goodsUnit3 = unit;
        }

        public String getCount() {
            return goodsCount;
        }

        public void setCount(String count) {
            this.goodsCount = count;
        }

        public String getCount2() {
            return goodsCount2;
        }

        public void setCount2(String count) {
            this.goodsCount2 = count;
        }

        public String getCount3() {
            return goodsCount3;
        }

        public void setCount3(String count) {
            this.goodsCount3 = count;
        }

        public String getGoodsID() {
            return goods_id;
        }

        public void setGoodsID(String id) {
            this.goods_id = id;
        }

        public String getHasDamage() {
            return hasDamage;
        }

        public void setHasDamage(String hasDamage) {
            this.hasDamage = hasDamage;
        }

        public String getHasLack() {
            return hasLack;
        }

        public void setHasLack(String hasLack) {
            this.hasLack = hasLack;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.goodsName);
            parcel.writeString(this.goodsUnit);
            parcel.writeString(this.goodsUnit2);
            parcel.writeString(this.goodsUnit3);
            parcel.writeString(this.goodsCount);
            parcel.writeString(this.goodsCount2);
            parcel.writeString(this.goodsCount3);
            parcel.writeString(this.goods_id);
            parcel.writeString(this.hasDamage);
            parcel.writeString(this.hasLack);
            parcel.writeString(this.price);

        }

        private Goods(Parcel in) {
            this.goodsName = in.readString();
            this.goodsUnit = in.readString();
            this.goodsUnit2 = in.readString();
            this.goodsUnit3 = in.readString();
            this.goodsCount = in.readString();
            this.goodsCount2 = in.readString();
            this.goodsCount3 = in.readString();
            this.goods_id = in.readString();
            this.hasDamage = in.readString();
            this.hasLack = in.readString();
            this.price = in.readString();
        }

        public static final Creator<Goods> CREATOR = new Creator<Goods>() {
            public Goods createFromParcel(Parcel source) {
                return new Goods(source);
            }

            public Goods[] newArray(int size) {
                return new Goods[size];
            }
        };

        @Override
        public String toString() {
            return "Goods{" +
                    "goodsName='" + goodsName + '\'' +
                    ", goodsUnit='" + goodsUnit + '\'' +
                    ", goodsCount='" + goodsCount + '\'' +
                    ", goods_id='" + goods_id + '\'' +
                    ", hasDamage='" + hasDamage + '\'' +
                    ", hasLack='" + hasLack + '\'' +
                    ", price='" + price + '\'' +
                    ", goodsUnit2='" + goodsUnit2 + '\'' +
                    ", goodsUnit3='" + goodsUnit3 + '\'' +
                    ", goodsCount2='" + goodsCount2 + '\'' +
                    ", goodsCount3='" + goodsCount3 + '\'' +
                    '}';
        }
    }

    public void takePhoto() {
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
        startActivityForResult(intent, BaseUploadEventActivity.SHOOT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == BaseUploadEventActivity.SHOOT_PHOTO) {
                try {
                    if (null != tmpFile && tmpFile.exists()) {
                        pd.setMessage(getString(R.string.prompt_dl_compressed_image));
                        pd.setCancelable(false);
                        pd.show();
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                Bitmap bitmap = ImageUtil.comp(tmpFile.getAbsolutePath(), 50, 600, 800);
                                int degree = ImageUtil.readPictureDegree(tmpFile.getAbsolutePath());
                                bitmap = ImageUtil.rotaingImageView(degree, bitmap);
                                boolean isSuccess = ImageUtil.saveCompressBitmap(bitmap, tmpFile.getAbsolutePath(), 50);
                                if (!isSuccess) {
                                    if (tmpFile != null && tmpFile.exists()) {
                                        tmpFile.delete();
                                    }
                                    handler.sendEmptyMessage(2);
                                    return;
                                }
                                EventFile eventFile = new EventFile();
                                eventFile.setOrderId(orderEvent.getOrderId());
                                eventFile.setEventId(orderEvent.get_id());
                                eventFile.setStatus(EventFile.STATUS_NEW);
                                eventFile.setFilePath(tmpFile.getAbsolutePath());
                                eventFile.setMold(EventFile.MOLD_DAMAGE_PHOTO);
                                eventFile.setConfigName(EventFile.DAMAGE);
                                eventFiles.add(eventFile);
                                handler.sendEmptyMessage(1);
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
                    Toast.makeText(this, getString(R.string.prompt_unexpected_error_take_photo), Toast.LENGTH_SHORT).show();
                    if (tmpFile != null && tmpFile.exists()) {
                        tmpFile.delete();
                    }
                    Log.d("压缩图片出错", e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("imageFilePath1", photoName);
        outState.putParcelableArrayList("operationList", operationList);
        outState.putParcelableArrayList("eventFiles", eventFiles);
        outState.putInt("index", index);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            photoName = savedInstanceState.getString("imageFilePath1");
            if (null != photoName) {
                tmpFile = new File(ContentData.BASE_PICS + "/" + photoName);
            }
            operationList = savedInstanceState.getParcelableArrayList("operationList");
            eventFiles = savedInstanceState.getParcelableArrayList("eventFiles");
            index = savedInstanceState.getInt("index");
        }
    }
}
