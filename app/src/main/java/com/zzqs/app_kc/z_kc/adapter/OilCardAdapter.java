package com.zzqs.app_kc.z_kc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.z_kc.entitiy.OilCard;

import java.util.List;

/**
 * Created by ray on 2016/12/25.
 * Class name : OilCardAdapter
 * Description :
 */
public class OilCardAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<OilCard> oilCards;
    private boolean needShowSelectImage;

    public OilCardAdapter(Context context, List<OilCard> oilCards, boolean needShowSelectImage) {
        this.context = context;
        this.oilCards = oilCards;
        this.needShowSelectImage = needShowSelectImage;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return oilCards != null ? oilCards.size() : 0;

    }

    @Override
    public Object getItem(int position) {
        return oilCards != null ? oilCards.get(position) : null;

    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.z_kc_item_kc_oil_card, null);

        }
        return null;
    }

    private class ViewHolder {
        TextView tvCardStatus, tvCardType, tvCardNumber;
        ImageView ivChoice;
    }
}
