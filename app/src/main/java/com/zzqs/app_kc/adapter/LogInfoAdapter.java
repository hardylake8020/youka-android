package com.zzqs.app_kc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.entity.LogInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by lance on 15/4/17.
 */
public class LogInfoAdapter extends BaseAdapter {
    private ArrayList<LogInfo> logInfos = null;
    private Context context;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Date date;
    public LogInfoAdapter(ArrayList<LogInfo> logInfos, Context context) {
        this.logInfos = logInfos;
        this.context = context;
    }

    @Override
    public int getCount() {
        return logInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return logInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        LogInfo logInfo = logInfos.get(i);
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_log_info, null);
            viewHolder = new ViewHolder();
            viewHolder.type = (TextView) view.findViewById(R.id.type);
            viewHolder.time = (TextView) view.findViewById(R.id.time);
            viewHolder.content = (TextView) view.findViewById(R.id.content);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.type.setText(logInfo.getType());
        date = new Date(logInfo.getTime());
        viewHolder.time.setText(sdf.format(date));
        viewHolder.content.setText(logInfo.getContent());
        return view;
    }

    private class ViewHolder {
        TextView type, time, content;
    }
}
