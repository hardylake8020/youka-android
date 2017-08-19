package com.zzqs.app_kc.z_kc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.entity.User;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;

import java.text.DecimalFormat;

/**
 * Created by lance on 2016/12/7.
 */

public class MyWalletActivity extends BaseActivity {
  TextView tvLeft, tvTitle, tvAccountBalance, tvBankCard, tvTopUp, tvWithdraw, tvCashDeposit, tvBill;
  private User user;

  @Override
  public void initVariables() {
    user = ZZQSApplication.getInstance().getUser();
    //test
    user.setAccountBalance(0.0);
  }

  @Override
  public void initViews(Bundle savedInstanceState) {
    setContentView(R.layout.z_kc_act_my_wallet);
    tvLeft = (TextView) findViewById(R.id.head_back);
    tvLeft.setText("");
    tvLeft.setOnClickListener(new MyOnClickListener() {
      @Override
      public void OnceOnClick(View view) {
        finish();
      }
    });
    tvTitle = (TextView) findViewById(R.id.head_title);
    tvTitle.setText(R.string.my_wallet);
    tvAccountBalance = (TextView) findViewById(R.id.tvAccountBalance);
    DecimalFormat df = new DecimalFormat("######0.00");
    tvAccountBalance.setText(df.format(user.getAccountBalance()));
    tvBankCard = (TextView) findViewById(R.id.tvBankCard);
    tvBankCard.setOnClickListener(new MyOnClickListener() {
      @Override
      public void OnceOnClick(View view) {

      }
    });
    tvTopUp = (TextView) findViewById(R.id.tvTopUp);
    tvTopUp.setOnClickListener(new MyOnClickListener() {//充值
      @Override
      public void OnceOnClick(View view) {

      }
    });
    tvWithdraw = (TextView) findViewById(R.id.tvWithdraw);
    tvWithdraw.setOnClickListener(new MyOnClickListener() {//提现
      @Override
      public void OnceOnClick(View view) {

      }
    });
    tvCashDeposit = (TextView) findViewById(R.id.tvCashDeposit);
    tvCashDeposit.setOnClickListener(new MyOnClickListener() {
      @Override
      public void OnceOnClick(View view) {
        startActivity(new Intent(mContext, CashDepositActivity.class));
      }
    });
    tvBill = (TextView) findViewById(R.id.tvBill);
    tvBill.setOnClickListener(new MyOnClickListener() {
      @Override
      public void OnceOnClick(View view) {

      }
    });
  }

  @Override
  public void loadData() {

  }
}
