package com.zzqs.app_kc.z_kc.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.utils.CommonTools;
import com.zzqs.app_kc.z_kc.adapter.TenderEventsAdapter;
import com.zzqs.app_kc.z_kc.entitiy.ErrorInfo;
import com.zzqs.app_kc.z_kc.entitiy.Tender;
import com.zzqs.app_kc.z_kc.entitiy.TenderEvent;
import com.zzqs.app_kc.z_kc.network.TenderApiImpl;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

/**
 * Created by lance on 15/4/11.
 */
public class TenderTimeAxisActivity extends BaseActivity {
  ListView lvEvents;
  TextView headMessage;
  private TenderEventsAdapter adapter;
  private String tenderId;
  private List<TenderEvent> tenderEventList;

  @Override
  public void initVariables() {
    tenderId = getIntent().getStringExtra(Tender.TENDER_ID);
    if (TextUtils.isEmpty(tenderId)) {
      finish();
    }
    tenderEventList = new ArrayList<>();
  }

  @Override
  public void initViews(Bundle savedInstanceState) {
    setContentView(R.layout.z_kc_act_tender_time_axis);
    headMessage = (TextView) findViewById(R.id.head_title);
    headMessage.setText(R.string.view_tv_check_time_axis);
    findViewById(R.id.head_back).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
      }
    });
    lvEvents = (ListView) findViewById(R.id.lvEvents);
    adapter = new TenderEventsAdapter(this, tenderEventList);
    lvEvents.setAdapter(adapter);
  }

  @Override
  public void loadData() {
    safePd.show();
    TenderApiImpl.getUserApiImpl().getTransportEvents(CommonTools.getToken(this), tenderId, new Subscriber<ErrorInfo>() {
      @Override
      public void onCompleted() {

      }

      @Override
      public void onError(Throwable e) {
        e.printStackTrace();
        safePd.dismiss();
      }

      @Override
      public void onNext(ErrorInfo errorInfo) {
        safePd.dismiss();
        if (errorInfo.getType().equals(ErrorInfo.SUCCESS)) {
          List<TenderEvent> list = (List<TenderEvent>) errorInfo.object;
          tenderEventList.addAll(list);
          adapter.notifyDataSetChanged();
        } else {
          Toast.makeText(mContext, errorInfo.getMessage(), Toast.LENGTH_SHORT).show();
        }
      }
    });
  }
}
