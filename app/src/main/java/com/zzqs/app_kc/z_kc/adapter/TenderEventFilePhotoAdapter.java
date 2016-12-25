package com.zzqs.app_kc.z_kc.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.zzqs.app_kc.R;
import com.zzqs.app_kc.entity.EventFile;
import com.zzqs.app_kc.utils.CommonFiled;
import com.zzqs.app_kc.z_kc.entitiy.TenderEventPhoto;

import java.util.List;

/**
 * Created by lance on 15/3/25.
 */
public class TenderEventFilePhotoAdapter extends BaseAdapter {
  private List<TenderEventPhoto> list = null;
  private LayoutInflater inflater;
  private Context context;

  public TenderEventFilePhotoAdapter(Context context, List<TenderEventPhoto> list) {
    this.list = list;
    this.context = context;
    inflater = LayoutInflater.from(context);

  }

  @Override
  public int getCount() {
//         TODO Auto-generated method stub
    return list != null ? list.size() : 0;
  }

  @Override
  public Object getItem(int arg0) {
    // TODO Auto-generated method stub
    return arg0;
  }

  @Override
  public long getItemId(int arg0) {
    // TODO Auto-generated method stub
    return arg0;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    // TODO Auto-generated method stub
    ViewHolder holder;
    if (convertView == null) {
      convertView = inflater.inflate(R.layout.z_kc_item_tender_event_photo, null);
      holder = new ViewHolder();
      holder.sdPhoto = (SimpleDraweeView) convertView.findViewById(R.id.sdPhoto);
      holder.tvType = (TextView) convertView.findViewById(R.id.tvType);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }
    if (position < list.size()) {
      TenderEventPhoto photo = list.get(position);
      if (!TextUtils.isEmpty(photo.getUrl())) {
        Uri uri = Uri.parse(CommonFiled.QINIU_ZOOM + photo.getUrl());
        holder.sdPhoto.setImageURI(uri);
      }

      holder.tvType.setText(photo.getName());
      switch (photo.getName()) {
        case "可选":
        case EventFile.PICKUP_ENTER:
        case EventFile.PICKUP:
        case EventFile.DELIVERY_ENTER:
        case EventFile.DELIVERY:
        case EventFile.HALF_WAY:
          holder.tvType.setBackgroundColor(context.getResources().getColor(R.color.green));
          break;
        case EventFile.DAMAGE:
          holder.tvType.setBackgroundColor(context.getResources().getColor(R.color.red_live_4));
          break;
        default:
          holder.tvType.setBackgroundColor(context.getResources().getColor(R.color.yellow_config));
          break;
      }
    }
    return convertView;
  }

  private class ViewHolder {
    TextView tvType;
    SimpleDraweeView sdPhoto;
  }
}
