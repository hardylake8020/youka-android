package com.zzqs.app_kc.adapter;

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
import com.zzqs.app_kc.db.DaoManager;
import com.zzqs.app_kc.db.hibernate.dao.BaseDao;
import com.zzqs.app_kc.entity.EventFile;
import com.zzqs.app_kc.entity.OrderEvent;
import com.zzqs.app_kc.utils.StringTools;
import com.zzqs.app_kc.widgets.DialogView;

import java.util.List;

/**
 * Created by lance on 15/4/11.
 */
public class OrderEventsAdapter extends BaseAdapter {
    private List<OrderEvent> mDataList;
    private Context context;
    private BaseDao<EventFile> eventfileDao;

    public OrderEventsAdapter(Context context, List<OrderEvent> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
        eventfileDao = DaoManager.getEventFileDao(context);
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
        OrderEvent orderEvent = mDataList.get(position);
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
        if (orderEvent.getMold().equals(OrderEvent.MOLD_PICKUP)) {
            holder.flag_img.setBackgroundResource(R.drawable.icon_pickup);
            holder.flag_tv.setText(R.string.view_tv_confirm_pickup);
        } else if (orderEvent.getMold().equals(OrderEvent.MOLD_HALFWAY)) {
            holder.flag_img.setBackgroundResource(R.drawable.icon_halfway);
            holder.flag_tv.setText(R.string.view_tv_halfway_event);
        } else if (orderEvent.getMold().equals(OrderEvent.MOLD_DELIVERY)) {
            holder.flag_img.setBackgroundResource(R.drawable.icon_delivery);
            holder.flag_tv.setText(R.string.view_tv_confirm_delivery);
        } else if (orderEvent.getMold().equals(OrderEvent.MOLD_PICKUP_ENTRANCE)) {
            holder.flag_img.setBackgroundResource(R.drawable.icon_halfway);
            holder.flag_tv.setText(R.string.view_tv_pickup_enter);
        } else if (orderEvent.getMold().equals(OrderEvent.MOLD_DELIVERY_ENTRANCE)) {
            holder.flag_img.setBackgroundResource(R.drawable.icon_halfway);
            holder.flag_tv.setText(R.string.view_tv_delivery_enter);
        } else if (orderEvent.getMold().equals(OrderEvent.MOLD_CONFIRM)) {
            holder.flag_img.setBackgroundResource(R.drawable.icon_halfway);
            holder.flag_tv.setText(R.string.prompt_order_confirm);
        }

        if (!StringTools.isEmp(orderEvent.getAddress()))
            holder.address.setText(orderEvent.getAddress() + "");
        if (!StringTools.isEmp(orderEvent.getRemark()))
            holder.remark.setText(orderEvent.getRemark() + "");
        if (!StringTools.isEmp(orderEvent.getCreateTime()))

        {
            String[] time = orderEvent.getCreateTime().split(" ");
            holder.time1.setText(time[0]);
            holder.time2.setText(time[1]);
        }
        final List<EventFile> pics = eventfileDao.find(null, "event_id=? and mold<>?", new String[]{orderEvent.get_id() + "", EventFile.MOLD_VOICE + ""}, null, null, null, null);
        if (pics.size() > 0) {
            holder.photos_ll.setVisibility(View.VISIBLE);
            EventFilePhotoAdapter adapter = new EventFilePhotoAdapter(context, pics, false, orderEvent.getMold());
            holder.pic_gv.setAdapter(adapter);
            holder.pic_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    DialogView.showBigImageDialog(context, pics.get(i).getFilePath(), null, pics.get(i).getConfigName(), null);
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
