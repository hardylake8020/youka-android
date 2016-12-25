package com.zzqs.app_kc.z_kc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.utils.CommonFiled;
import com.zzqs.app_kc.z_kc.entitiy.TenderEvent;
import com.zzqs.app_kc.utils.StringTools;
import com.zzqs.app_kc.widgets.DialogView;
import com.zzqs.app_kc.z_kc.entitiy.TenderEventPhoto;
import com.zzqs.app_kc.z_kc.util.TimeUtil;

import java.util.Calendar;
import java.util.List;

/**
 * Created by lance on 15/4/11.
 */
public class TenderEventsAdapter extends BaseAdapter {
  private List<TenderEvent> mDataList;
  private Context context;

  public TenderEventsAdapter(Context context, List<TenderEvent> mDataList) {
    this.context = context;
    this.mDataList = mDataList;
  }

  @Override
  public int getCount() {
    return mDataList != null ? mDataList.size() : 0;
  }

  @Override
  public Object getItem(int i) {
    return mDataList.get(i);
  }

  @Override
  public long getItemId(int i) {
    return 0;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    final TenderEvent tenderEvent = mDataList.get(position);
    if (convertView == null) {
      holder = new ViewHolder();
      convertView = LayoutInflater.from(context).inflate(R.layout.item_order_time_axis, null);
      holder.flag_img = (ImageView) convertView.findViewById(R.id.flag_img);
      holder.flag_tv = (TextView) convertView.findViewById(R.id.flag_tv);
      holder.address = (TextView) convertView.findViewById(R.id.address);
      holder.remark = (TextView) convertView.findViewById(R.id.remark);
      holder.time1 = (TextView) convertView.findViewById(R.id.time1);
      holder.time2 = (TextView) convertView.findViewById(R.id.time2);
      holder.pic_gv = (GridView) convertView.findViewById(R.id.pic_gv);
      holder.remark_ll = (LinearLayout) convertView.findViewById(R.id.remark_ll);
      holder.photos_ll = (LinearLayout) convertView.findViewById(R.id.photos_ll);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }
    if (tenderEvent.getType().equals(TenderEvent.MOLD_PICKUP)) {
      holder.flag_img.setBackgroundResource(R.drawable.icon_pickup);
      holder.flag_tv.setText(R.string.view_tv_confirm_pickup);
    } else if (tenderEvent.getType().equals(TenderEvent.MOLD_HALFWAY)) {
      holder.flag_img.setBackgroundResource(R.drawable.icon_halfway);
      holder.flag_tv.setText(R.string.view_tv_halfway_event);
    } else if (tenderEvent.getType().equals(TenderEvent.MOLD_DELIVERY)) {
      holder.flag_img.setBackgroundResource(R.drawable.icon_delivery);
      holder.flag_tv.setText(R.string.view_tv_confirm_delivery);
    } else if (tenderEvent.getType().equals(TenderEvent.MOLD_PICKUP_ENTRANCE)) {
      holder.flag_img.setBackgroundResource(R.drawable.icon_halfway);
      holder.flag_tv.setText(R.string.view_tv_pickup_enter);
    } else if (tenderEvent.getType().equals(TenderEvent.MOLD_DELIVERY_ENTRANCE)) {
      holder.flag_img.setBackgroundResource(R.drawable.icon_halfway);
      holder.flag_tv.setText(R.string.view_tv_delivery_enter);
    } else if (tenderEvent.getType().equals(TenderEvent.MOLD_CONFIRM)) {
      holder.flag_img.setBackgroundResource(R.drawable.icon_halfway);
      holder.flag_tv.setText(R.string.prompt_order_confirm);
    }

    if (!StringTools.isEmp(tenderEvent.getAddress()))
      holder.address.setText(tenderEvent.getAddress() + "");
    if (!StringTools.isEmp(tenderEvent.getDescription()))
      holder.remark.setText(tenderEvent.getDescription() + "");
    if (!StringTools.isEmp(tenderEvent.getTime())) {
      Calendar calendar = TimeUtil.convertStringToCalendar(tenderEvent.getTime(), TimeUtil.SERVER_TIME_FORMAT);
      holder.time1.setText(calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH));
      holder.time2.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.MINUTE));
    }
    if (tenderEvent.getPhotos().size() > 0) {
      holder.photos_ll.setVisibility(View.VISIBLE);
      TenderEventFilePhotoAdapter adapter = new TenderEventFilePhotoAdapter(context, tenderEvent.getPhotos());
      holder.pic_gv.setAdapter(adapter);
      holder.pic_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
          TenderEventPhoto photo = tenderEvent.getPhotos().get(i);
          DialogView.showBigImageDialog(context, CommonFiled.QINIU_ZOOM + photo.getUrl(), null, photo.getName(), null);
        }
      });
    } else {
      holder.photos_ll.setVisibility(View.GONE);
    }
    return convertView;
  }

  private class ViewHolder {
    ImageView flag_img;
    TextView flag_tv, address, remark, time1, time2;
    LinearLayout photos_ll, remark_ll;
    GridView pic_gv;
  }
}
