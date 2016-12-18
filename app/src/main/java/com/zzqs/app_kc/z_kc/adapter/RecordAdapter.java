package com.zzqs.app_kc.z_kc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.z_kc.entitiy.Record;
import com.zzqs.app_kc.z_kc.util.NumberUtil;
import com.zzqs.app_kc.z_kc.util.TimeUtil;

import java.util.List;

/**
 * Created by lance on 2016/12/7.
 */

public class RecordAdapter extends BaseAdapter {
  private Context context;
  private LayoutInflater inflater;
  private List<Record> recordList;

  public RecordAdapter(Context context, List<Record> recordList) {
    this.context = context;
    inflater = LayoutInflater.from(context);
    this.recordList = recordList;
  }

  @Override
  public int getCount() {
    return recordList != null ? recordList.size() : 0;
  }

  @Override
  public Object getItem(int position) {
    return recordList != null ? recordList.get(position) : null;
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }

  @Override
  public View getView(int position, View view, ViewGroup parent) {
    Record record = recordList.get(position);
    ViewHolder holder;
    if (view == null) {
      view = inflater.inflate(R.layout.z_kc_item_record, null);
      holder = new ViewHolder();
      holder.ivType = (ImageView) view.findViewById(R.id.ivType);
      holder.ivSplitLine = (ImageView) view.findViewById(R.id.ivSplitLine);
      holder.tvInfo1 = (TextView) view.findViewById(R.id.tvInfo1);
      holder.tvInfo2 = (TextView) view.findViewById(R.id.tvInfo2);
      holder.tvTime = (TextView) view.findViewById(R.id.tvTime);
      view.setTag(holder);
    } else {
      holder = (ViewHolder) view.getTag();
    }
    holder.tvTime.setText(TimeUtil.convertDateStringFormat(record.getTime(), TimeUtil.SERVER_TIME_FORMAT, "yyyy-MM-dd"));
    if (record.getType().equals(Record.DEPOSIT)) {
      holder.ivType.setBackgroundResource(R.drawable.round_blue);
      holder.tvInfo1.setText(context.getString(R.string.payment_cash_deposit, NumberUtil.doubleTrans(record.getMoney())));
      holder.tvInfo2.setText(R.string.open_offer_permissions);
    } else if (record.getType().equals(Record.DRAW)) {
      holder.ivType.setBackgroundResource(R.drawable.round_green);
      holder.tvInfo1.setText(context.getString(R.string.extract_cash_deposit, NumberUtil.doubleTrans(record.getMoney())));
      holder.tvInfo2.setText(R.string.close_offer_permissions);
    }
    if (position + 1 < recordList.size()) {
      holder.ivSplitLine.setVisibility(View.VISIBLE);
    } else {
      holder.ivSplitLine.setVisibility(View.GONE);
    }
    return view;
  }

  private class ViewHolder {
    ImageView ivType, ivSplitLine;
    TextView tvInfo1, tvInfo2, tvTime;
  }
}
