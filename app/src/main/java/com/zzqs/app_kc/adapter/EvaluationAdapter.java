package com.zzqs.app_kc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.entity.Evaluation;
import com.zzqs.app_kc.utils.StringTools;

import java.util.List;

/**
 * Created by lance on 15/11/2.
 */
public class EvaluationAdapter extends BaseAdapter {
    private List<Evaluation> evaluationList = null;
    private Context context;

    public EvaluationAdapter(List<Evaluation> evaluationList, Context context) {
        this.evaluationList = evaluationList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return evaluationList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        Evaluation evaluation = evaluationList.get(i);
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_comment, null);
            holder = new ViewHolder();
            holder.serialNo = (TextView) view.findViewById(R.id.serial_No);
            holder.companyName = (TextView) view.findViewById(R.id.company_name);
            holder.time1 = (TextView) view.findViewById(R.id.time1);
            holder.time2 = (TextView) view.findViewById(R.id.time2);
            holder.content = (TextView) view.findViewById(R.id.content);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (!StringTools.isEmp(evaluation.getSerialNo()))
            holder.serialNo.setText(evaluation.getSerialNo());
        if (!StringTools.isEmp(evaluation.getContent()))
            holder.content.setText(evaluation.getContent());
        if (!StringTools.isEmp(evaluation.getCompanyName()))
            holder.companyName.setText(evaluation.getCompanyName());
        if (!StringTools.isEmp(evaluation.getUpdateTime())) {
            String time1 = evaluation.getUpdateTime().split(" ")[0];
            String time2 = evaluation.getUpdateTime().split(" ")[1];
            holder.time1.setText(time1);
            holder.time2.setText(time2);
        }

        return view;
    }

    private class ViewHolder {
        TextView serialNo, companyName, content, time1, time2;
    }
}
