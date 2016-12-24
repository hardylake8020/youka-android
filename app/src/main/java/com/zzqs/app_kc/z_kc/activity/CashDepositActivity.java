package com.zzqs.app_kc.z_kc.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.entity.User;
import com.zzqs.app_kc.widgets.xlistView.XListView;
import com.zzqs.app_kc.z_kc.adapter.RecordAdapter;
import com.zzqs.app_kc.z_kc.entitiy.Record;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lance on 2016/12/7.
 */

public class CashDepositActivity extends BaseActivity implements XListView.IXListViewListener {
  TextView tvLeft, tvTitle, tvCashDepositStatus, tvOperation;
  ImageView ivCashDepositStatus;
  RelativeLayout rlHead;
  XListView lvRecords;
  RecordAdapter adapter;
  private User user;
  List<Record> recordList;

  @Override
  public void initVariables() {
    user = ZZQSApplication.getInstance().getUser();
    user.setCashDeposit(200);
    recordList = new ArrayList<>();
  }

  @Override
  public void initViews(Bundle savedInstanceState) {
    setContentView(R.layout.z_kc_act_cash_deposit);
    tvLeft = (TextView) findViewById(R.id.head_back);
    tvLeft.setText("");
    tvLeft.setOnClickListener(new MyOnClickListener() {
      @Override
      public void OnceOnClick(View view) {
        finish();
      }
    });
    tvTitle = (TextView) findViewById(R.id.head_title);
    tvTitle.setText(R.string.cash_deposit);
    tvCashDepositStatus = (TextView) findViewById(R.id.tvCashDepositStatus);
    tvOperation = (TextView) findViewById(R.id.tvOperation);
    tvOperation.setOnClickListener(new MyOnClickListener() {
      @Override
      public void OnceOnClick(View view) {

      }
    });
    rlHead = (RelativeLayout) findViewById(R.id.rlDefaultHeadParent);
    rlHead.setBackgroundResource(R.color.green);
    ivCashDepositStatus = (ImageView) findViewById(R.id.ivCashDepositStatus);
    lvRecords = (XListView) findViewById(R.id.lvRecords);
    lvRecords.setPullRefreshEnable(true);
    lvRecords.setPullLoadEnable(false);
    lvRecords.setXListViewListener(this);
    adapter = new RecordAdapter(this, recordList);
    lvRecords.setAdapter(adapter);
    lvRecords.stopRefresh();
    lvRecords.stopLoadMore();
    if (user.getCashDeposit() > 0) {
      tvCashDepositStatus.setText(R.string.payed);
      ivCashDepositStatus.setBackgroundResource(R.drawable.ic_tick_blue);
      tvOperation.setText(getString(R.string.extract_cash_deposit, user.getCashDeposit() + ""));
    } else {
      tvCashDepositStatus.setText(R.string.un_pay);
      ivCashDepositStatus.setBackgroundResource(R.drawable.delete);
      tvOperation.setText(getString(R.string.payment_cash_deposit, "200.00"));
    }
  }

  @Override
  public void loadData() {
    testData(true);
  }

  private void testData(boolean isClear) {
    if (isClear) {
      recordList.clear();
    }
    Record record1 = new Record();
    record1.setMoney(200);
    record1.setTime("2016-12-6T11:00:00.000Z");
    record1.setType(Record.DEPOSIT);
    recordList.add(record1);
    Record record2 = new Record();
    record2.setMoney(200);
    record2.setTime("2016-12-8T11:00:00.000Z");
    record2.setType(Record.DRAW);
    recordList.add(record2);
    Record record3 = new Record();
    record3.setMoney(200);
    record3.setTime("2016-12-11T11:00:00.000Z");
    record3.setType(Record.DEPOSIT);
    recordList.add(record3);
    Record record4 = new Record();
    record4.setMoney(200);
    record4.setTime("2016-12-16T11:00:00.000Z");
    record4.setType(Record.DRAW);
    recordList.add(record4);
    adapter.notifyDataSetChanged();
  }

  @Override
  public void onRefresh() {
    testData(true);
    onLoad();
  }

  @Override
  public void onLoadMore() {
    testData(false);
    onLoad();
  }

  private void onLoad() {
    lvRecords.stopRefresh();
    lvRecords.stopLoadMore();
    lvRecords.setRefreshTime(getString(R.string.xilstview_refreshed));
    if (recordList.size() >= 10) {
      lvRecords.setPullLoadEnable(true);
    } else {
      lvRecords.setPullLoadEnable(false);
    }
  }
}
