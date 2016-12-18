package com.zzqs.app_kc.z_kc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zzqs.app_kc.R;
import com.zzqs.app_kc.widgets.CircleImageView;
import com.zzqs.app_kc.widgets.xlistView.XListView;
import com.zzqs.app_kc.z_kc.adapter.KCOrderAdapter;
import com.zzqs.app_kc.z_kc.entitiy.Goods;
import com.zzqs.app_kc.z_kc.entitiy.KCOrder;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lance on 2016/12/3.
 */

public class MainActivity extends BaseActivity implements XListView.IXListViewListener {
  CircleImageView cvUserPhoto;
  TextView tvUnDealOrderNum, tvUnDealWaybillNum;
  RelativeLayout rlFindGoods, rlMyCars, rlMyWallet, rlMyOilCard;
  LinearLayout llZCOrder, llDriverOrder;
  XListView lvOrders;
  KCOrderAdapter adapter;
  private List<KCOrder> kcOrderList;

  @Override
  public void initVariables() {
    kcOrderList = new ArrayList<>();
  }

  @Override
  public void initViews(Bundle savedInstanceState) {
    setContentView(R.layout.z_kc_act_main);
    cvUserPhoto = (CircleImageView) findViewById(R.id.cvUserPhoto);
    tvUnDealOrderNum = (TextView) findViewById(R.id.tvUnDealOrderNum);
    tvUnDealWaybillNum = (TextView) findViewById(R.id.tvUnDealWaybillNum);
    rlFindGoods = (RelativeLayout) findViewById(R.id.rlFindGoods);
    rlFindGoods.setOnClickListener(new MyOnClickListener() {
      @Override
      public void OnceOnClick(View view) {
        startActivity(new Intent(mContext, FindGoodsActivity.class));
      }
    });
    rlMyCars = (RelativeLayout) findViewById(R.id.rlMyCars);
    rlMyCars.setOnClickListener(new MyOnClickListener() {
      @Override
      public void OnceOnClick(View view) {
        startActivity(new Intent(mContext, MyCarsActivity.class));
      }
    });
    rlMyWallet = (RelativeLayout) findViewById(R.id.rlMyWallet);
    rlMyWallet.setOnClickListener(new MyOnClickListener() {
      @Override
      public void OnceOnClick(View view) {
        startActivity(new Intent(mContext, MyWalletActivity.class));
      }
    });
    rlMyOilCard = (RelativeLayout) findViewById(R.id.rlMyOilCard);
    lvOrders = (XListView) findViewById(R.id.lvOrders);
    lvOrders.setPullRefreshEnable(true);
    lvOrders.setPullLoadEnable(false);
    lvOrders.setXListViewListener(this);
    adapter = new KCOrderAdapter(this, kcOrderList);
    lvOrders.setAdapter(adapter);
    lvOrders.stopRefresh();
    lvOrders.stopLoadMore();
    lvOrders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position > 0) {
          Intent intent = new Intent(mContext, ZCOrderDetailActivity.class);
          KCOrder order = kcOrderList.get(position - 1);
          intent.putExtra(KCOrder.KC_ORDER, order);
          startActivity(intent);
        }
      }
    });

    llZCOrder = (LinearLayout) findViewById(R.id.llZCOrder);
    llZCOrder.setOnClickListener(new MyOnClickListener() {
      @Override
      public void OnceOnClick(View view) {

      }
    });
    llDriverOrder = (LinearLayout) findViewById(R.id.llDriverOrder);
    llDriverOrder.setOnClickListener(new MyOnClickListener() {
      @Override
      public void OnceOnClick(View view) {
        startActivity(new Intent(mContext, com.zzqs.app_kc.activities.MainActivity.class));
      }
    });
  }

  @Override
  public void loadData() {

  }

  @Override
  public void onRefresh() {
    getKCOrders();
    onLoad();
  }

  @Override
  public void onLoadMore() {
    getKCOrders();
    onLoad();
  }

  private void getKCOrders() {
    Gson gson = new Gson();

    KCOrder order1 = new KCOrder();
    order1.setTender_type(KCOrder.GRAB);
    order1.setPickup_city("上海");
    order1.setPickup_region("虹口区");
    order1.setPickup_address("上海市虹口区XX路XXX号");
    order1.setPickup_start_time_format("2016-12-2T16:00:00.000Z");
    order1.setPickup_end_time_format("2016-12-2T22:00:00.000Z");
    order1.setDelivery_city("南京");
    order1.setDelivery_region("中山区");
    order1.setDelivery_address("南京市中山区xxx路xx号");
    order1.setDelivery_start_time_format("2016-12-4T00:00:00.000Z");
    order1.setDelivery_end_time_format("2016-12-6T11:00:00.000Z");
    order1.setHighest_protect_price(2333);
    order1.setStart_time("2016-12-1T00:00:00.000Z");
    order1.setEnd_time("2016-12-5T17:00:00.000Z");
    order1.setTransport_type("快运");
    order1.setSender_company("上海圆通");
    order1.setInitiator_name("张三");
    order1.setInitiator_phone("18721850339");
    order1.setDistance(200);
    order1.setPayment_top_rate(50);
    order1.setPayment_top_cash_rate(40);
    order1.setPayment_top_card_rate(60);
    order1.setPayment_last_rate(25.5);
    order1.setPayment_last_cash_rate(100);
    order1.setPayment_tail_rate(24.5);
    order1.setPayment_tail_card_rate(100);
    order1.setTruck_type("金杯车");
    order1.setTruck_count(10);

    List<Goods> goodsList = new ArrayList<>();
    Goods goods1 = new Goods();
    goods1.setCount(1);
    goods1.setUnit("箱");
    goods1.setCount2(2);
    goods1.setUnit2("吨");
    goods1.setCount3(3);
    goods1.setUnit3("立方");
    goods1.setName("大象");
    goodsList.add(goods1);
    Goods goods2 = new Goods();
    goods2.setCount(10);
    goods2.setUnit("箱");
    goods2.setCount2(20);
    goods2.setUnit2("吨");
    goods2.setCount3(30);
    goods2.setUnit3("立方");
    goods2.setName("河马");
    goodsList.add(goods2);
    order1.setGoods(goodsList);
    kcOrderList.add(order1);
    KCOrder order2 = gson.fromJson(gson.toJson(order1), KCOrder.class);
    order2.setHighest_grab_price(50000);
    order2.setTender_type(KCOrder.COMPARE);
    order2.setCurrent_grab_price(12345);
    order2.setPayment_top_rate(50);
    order2.setPayment_top_cash_rate(50);
    order2.setPayment_top_card_rate(50);
    order2.setPayment_last_rate(50);
    order2.setPayment_last_card_rate(100);
    order2.setRemark("嘻嘻哈哈");
    kcOrderList.add(order2);
    adapter.notifyDataSetChanged();
  }

  /**
   * 停止刷新，
   */
  private void onLoad() {
    lvOrders.stopRefresh();
    lvOrders.stopLoadMore();
    lvOrders.setRefreshTime(getString(R.string.xilstview_refreshed));
    if (kcOrderList.size() >= 10) {
      lvOrders.setPullLoadEnable(true);
    }
  }
}
