package com.zzqs.app_kc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.entity.Order;

/**
 * Created by lance on 15/4/29.
 */
public class OrderCompleteActivity extends BaseActivity implements View.OnClickListener {
  private Order order;
  Button waybillDetails, backToList;
  TextView serialNo, back;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.act_complete_order);
    init();
  }


  private void init() {
    order = getIntent().getParcelableExtra(Order.ORDER);
    back = (TextView) findViewById(R.id.head_back);
    back.setOnClickListener(this);
    waybillDetails = (Button) findViewById(R.id.waybill_details);
    waybillDetails.setOnClickListener(this);
    backToList = (Button) findViewById(R.id.back_to_list);
    backToList.setOnClickListener(this);
    serialNo = (TextView) findViewById(R.id.serialNo);
    serialNo.setText(order.getSerialNo());
  }


  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.head_back:
      case R.id.back_to_list:
        startActivity(new Intent(this, MainActivity.class));
        break;
      case R.id.waybill_details:
        Intent intent = new Intent(getApplicationContext(), OrderDetailsActivity.class);
        intent.putExtra(Order.ORDER, order);
        startActivity(intent);
        break;
    }
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      startActivity(new Intent(this, MainActivity.class));
      return true;
    } else {
      return super.onKeyDown(keyCode, event);
    }
  }
}