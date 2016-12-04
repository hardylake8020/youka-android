package com.zzqs.app_kc.z_kc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zzqs.app_kc.R;
import com.zzqs.app_kc.widgets.xlistView.XListView;
import com.zzqs.app_kc.z_kc.adapter.KCOrderAdapter;
import com.zzqs.app_kc.z_kc.entitiy.KCOrder;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lance on 2016/12/4.
 */

public class FindGoodsActivity extends BaseActivity implements XListView.IXListViewListener {
  TextView tvLeft, tvTitle, tvRight, tvSelectAll, tvSelectBidding, tvSelectGrab;
  EditText etStart, etEnd;
  XListView lvOrders;
  KCOrderAdapter adapter;
  private List<KCOrder> kcOrderList;
  private int selectType;
  private static final int ALL = 1;
  private static final int BIDDING = 2;
  private static final int GRAB = 3;

  @Override
  public void initVariables() {
    kcOrderList = new ArrayList<>();
  }

  @Override
  public void initViews(Bundle savedInstanceState) {
    setContentView(R.layout.z_kc_act_find_goods);
    tvLeft = (TextView) findViewById(R.id.head_back);
    tvLeft.setText("");
    tvLeft.setOnClickListener(new MyOnClickListener() {
      @Override
      public void OnceOnClick(View view) {
        finish();
      }
    });
    tvTitle = (TextView) findViewById(R.id.head_title);
    tvTitle.setText(R.string.goods_list);
    tvRight = (TextView) findViewById(R.id.head_right);
    tvRight.setText(R.string.find_good_by_map);
    tvRight.setVisibility(View.VISIBLE);
    tvSelectAll = (TextView) findViewById(R.id.tvSelectAll);
    tvSelectAll.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setSelectType(ALL);
      }
    });
    tvSelectAll.setTextColor(ContextCompat.getColor(this, R.color.red));
    tvSelectBidding = (TextView) findViewById(R.id.tvSelectBidding);
    tvSelectBidding.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setSelectType(BIDDING);
      }
    });
    tvSelectGrab = (TextView) findViewById(R.id.tvSelectGrab);
    tvSelectGrab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setSelectType(GRAB);
      }
    });
    etStart = (EditText) findViewById(R.id.etStart);
    etEnd = (EditText) findViewById(R.id.etEnd);
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
    kcOrderList.clear();
    Gson gson = new Gson();

    KCOrder order1 = new KCOrder();
    order1.setType(KCOrder.NORMAL_ORDER);
    order1.setStart_city("上海");
    order1.setStart_district("虹口区");
    order1.setStart_address("上海市虹口区XX路XXX号");
    order1.setPickup_start_time("2016-12-2T16:00:00.000Z");
    order1.setPickup_end_time("2016-12-2T22:00:00.000Z");
    order1.setEnd_city("南京");
    order1.setEnd_district("中山区");
    order1.setEnd_address("南京市中山区xxx路xx号");
    order1.setDelivery_start_time("2016-12-4T00:00:00.000Z");
    order1.setDelivery_end_time("2016-12-6T11:00:00.000Z");
    order1.setFixed_prince(2333);
    order1.setCreate_time("2016-12-1T00:00:00.000Z");
    order1.setOver_time("2016-12-5T17:00:00.000Z");
    order1.setTransport_type("快运");
    order1.setInitiator("上海圆通");
    order1.setInitiator_name("张三");
    order1.setInitiator_phone("18721850339");
    order1.setTotal_quantity(123);
    order1.setQuantity_unit("箱");
    order1.setTotal_volume(2);
    order1.setVolume_unit("吨");
    order1.setTotal_weight(5);
    order1.setWeight_unit("立方");
    order1.setDistance(200);
    order1.setFirst_pay(50);
    order1.setFirst_pay_cash(40);
    order1.setFirst_pay_oil_card(60);
    order1.setLast_pay(25.5);
    order1.setLast_pay_cash(100);
    order1.setReceipt_pay(24.5);
    order1.setReceipt_pay_oil_card(100);
    order1.setRequirement_car_type("金杯车");
    order1.setRequirement_car_number(10);
    KCOrder order2 = gson.fromJson(gson.toJson(order1), KCOrder.class);
    order2.setMax_prince(50000);
    order2.setType(KCOrder.BIDDING_ORDER);
    order2.setCurrent_prince(12345);
    order2.setTotal_quantity(0);
    order2.setFirst_pay(50);
    order2.setFirst_pay_cash(50);
    order2.setFirst_pay_oil_card(50);
    order2.setLast_pay(50);
    order2.setLast_pay_oil_card(100);
    order2.setRemark("嘻嘻哈哈");

    if (selectType == ALL) {
      kcOrderList.add(order1);
      kcOrderList.add(order1);
      kcOrderList.add(order2);
      kcOrderList.add(order2);
    } else if (selectType == BIDDING) {
      kcOrderList.add(order2);
      kcOrderList.add(order2);
    } else {
      kcOrderList.add(order1);
      kcOrderList.add(order1);
    }
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

  private void setSelectType(int type) {
    if (selectType == type) {
      return;
    }
    tvSelectAll.setTextColor(ContextCompat.getColor(this, R.color.click_gray));
    tvSelectBidding.setTextColor(ContextCompat.getColor(this, R.color.click_gray));
    tvSelectGrab.setTextColor(ContextCompat.getColor(this, R.color.click_gray));
    switch (type) {
      case ALL:
        tvSelectAll.setTextColor(ContextCompat.getColor(this, R.color.red));
        break;
      case BIDDING:
        tvSelectBidding.setTextColor(ContextCompat.getColor(this, R.color.red));
        break;
      case GRAB:
        tvSelectGrab.setTextColor(ContextCompat.getColor(this, R.color.red));
        break;
    }
    selectType = type;

    getKCOrders();
  }
}
