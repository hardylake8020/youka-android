package com.zzqs.app_kc.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.entity.OrderCode;
import com.zzqs.app_kc.widgets.swipelistview.SwipeListView;

import java.util.List;

/**
 * Created by lance on 15/5/20.
 */
public class OrderCodeAdapter extends BaseAdapter {
    private List<OrderCode> orderCodeList = null;
    private Context context;
    private SwipeListView mSwipeListView;

    public OrderCodeAdapter(List<OrderCode> orderCodeList, Context context, SwipeListView mSwipeListView) {
        this.orderCodeList = orderCodeList;
        this.context = context;
        this.mSwipeListView = mSwipeListView;
    }

    @Override
    public int getCount() {
        return orderCodeList.size();
    }

    @Override
    public Object getItem(int i) {
        return orderCodeList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        Viewholder viewholder;
        OrderCode orderCode = orderCodeList.get(i);
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_order_code, null);
            viewholder = new Viewholder();
            viewholder.codeNo = (TextView) view.findViewById(R.id.scan_code_No);
            viewholder.scanText = (TextView) view.findViewById(R.id.scan_text);
            viewholder.deleteCode = (Button) view.findViewById(R.id.id_remove);
            view.setTag(viewholder);
        } else {
            viewholder = (Viewholder) view.getTag();
        }
        viewholder.codeNo.setText(i + 1 + "");
        viewholder.scanText.setText(orderCode.getCode());
        viewholder.deleteCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                orderCodeList.remove(i);
//                notifyDataSetChanged();
//                /**
//                 * 关闭SwipeListView
//                 * 不关闭的话，刚删除位置的item存在问题
//                 * 在监听事件中onListChange中关闭，会出现问题
//                 */
//                mSwipeListView.closeOpenedItems();
                dialog(i);
            }
        });
        return view;
    }

    private class Viewholder {
        TextView codeNo, scanText;
        Button deleteCode;
    }

    private void dialog(final int i){
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.prompt_dl_title_1))
                .setMessage(context.getString(R.string.prompt_dl_confirm_delete))
                .setPositiveButton(context.getString(R.string.view_bt_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        orderCodeList.remove(i);
                        notifyDataSetChanged();
                        /**
                         * 关闭SwipeListView
                         * 不关闭的话，刚删除位置的item存在问题
                         * 在监听事件中onListChange中关闭，会出现问题
                         */
                        mSwipeListView.closeOpenedItems();
                    }
                })
                .setNegativeButton(context.getString(R.string.view_tv_cancel), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                }).setCancelable(true).show();
    }
}
