package com.zzqs.app_kc.z_kc.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
        ViewHolder holder;
        OilCard oilCard = oilCards.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.z_kc_item_kc_oil_card, null);
            holder = new ViewHolder();
            holder.tvCardStatus = (TextView) convertView.findViewById(R.id.tvCardStatus);
            holder.tvCardType = (TextView) convertView.findViewById(R.id.tvCardType);
            holder.tvCardNumber = (TextView) convertView.findViewById(R.id.tvCardNumber);
            holder.ivChoice = (ImageView) convertView.findViewById(R.id.ivChoice);
            holder.rlOilCardItem = (RelativeLayout) convertView.findViewById(R.id.rlOilCardItem);
            if (needShowSelectImage) {
                holder.ivChoice.setVisibility(View.VISIBLE);
            } else {
                holder.ivChoice.setVisibility(View.GONE);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvCardNumber.setText(oilCard.getNumber());
        holder.tvCardType.setText(oilCard.getType());

        if (needShowSelectImage) {
            if (oilCard.isSelect()) {
                holder.ivChoice.setBackgroundResource(R.drawable.z_kc_ic_select);
            } else {
                holder.ivChoice.setBackgroundResource(R.drawable.z_kc_ic_un_select);
            }
        }

        if (oilCard.getType().equals(OilCard.ETC)) {
            holder.setCardType(context.getString(R.string.etc_card));
//            if (TextUtils.isEmpty(oilCard.getTruck_number())) {
//                holder.rlOilCardItem.setBackgroundResource(R.drawable.radius_5dp_col_white_border_red);
//                holder.setTextColor(context.getResources().getColor(R.color.z_kc_red));
//                holder.tvCardStatus.setText(context.getString(R.string.un_use));
//            } else {
//                holder.rlOilCardItem.setBackgroundResource(R.drawable.radius_5dp_col_red);
//                holder.setTextColor(context.getResources().getColor(R.color.base_gray));
//                holder.tvCardStatus.setText(oilCard.getTruck_number());
//            }
            if (!needShowSelectImage) {
                if (TextUtils.isEmpty(oilCard.getTruck_number())) {
                    holder.rlOilCardItem.setBackgroundResource(R.drawable.radius_5dp_col_white_border_red);
                    holder.setTextColor(context.getResources().getColor(R.color.z_kc_red));
                    holder.tvCardStatus.setText(context.getString(R.string.un_use));
                } else {
                    holder.rlOilCardItem.setBackgroundResource(R.drawable.radius_5dp_col_red);
                    holder.setTextColor(context.getResources().getColor(R.color.base_gray));
                    holder.tvCardStatus.setText(oilCard.getTruck_number());
                }
            } else {
                if (TextUtils.isEmpty(oilCard.getTruck_number())) {
                    holder.tvCardStatus.setText(context.getString(R.string.un_use));
                } else {
                    holder.tvCardStatus.setText(oilCard.getTruck_number());
                }
                holder.rlOilCardItem.setBackgroundResource(R.drawable.radius_5dp_col_white_border_red);
                holder.setTextColor(context.getResources().getColor(R.color.z_kc_red));
            }
        } else {
            holder.setCardType(context.getString(R.string.oil_card));
//            if (TextUtils.isEmpty(oilCard.getTruck_number())) {
//                holder.rlOilCardItem.setBackgroundResource(R.drawable.radius_5dp_col_white_border_blue);
//                holder.setTextColor(context.getResources().getColor(R.color.primary_colors));
//                holder.tvCardStatus.setText(context.getString(R.string.un_use));
//            } else {
//                holder.rlOilCardItem.setBackgroundResource(R.drawable.radius_5dp_col_blue);
//                holder.setTextColor(context.getResources().getColor(R.color.base_gray));
//                holder.tvCardStatus.setText(oilCard.getTruck_number());
//            }
            if (!needShowSelectImage) {
                if (TextUtils.isEmpty(oilCard.getTruck_number())) {
                    holder.rlOilCardItem.setBackgroundResource(R.drawable.radius_5dp_col_white_border_blue);
                    holder.setTextColor(context.getResources().getColor(R.color.primary_colors));
                    holder.tvCardStatus.setText(context.getString(R.string.un_use));
                } else {
                    holder.rlOilCardItem.setBackgroundResource(R.drawable.radius_5dp_col_blue);
                    holder.setTextColor(context.getResources().getColor(R.color.base_gray));
                    holder.tvCardStatus.setText(oilCard.getTruck_number());
                }
            } else {
                if (TextUtils.isEmpty(oilCard.getTruck_number())) {
                    holder.tvCardStatus.setText(context.getString(R.string.un_use));
                } else {
                    holder.tvCardStatus.setText(oilCard.getTruck_number());
                }
                holder.rlOilCardItem.setBackgroundResource(R.drawable.radius_5dp_col_white_border_blue);
                holder.setTextColor(context.getResources().getColor(R.color.primary_colors));
            }
        }
        return convertView;
    }

    private class ViewHolder {
        TextView tvCardStatus, tvCardType, tvCardNumber;
        ImageView ivChoice;
        RelativeLayout rlOilCardItem;

        public void setTextColor(int color) {
            this.tvCardStatus.setTextColor(color);
            this.tvCardType.setTextColor(color);
            this.tvCardNumber.setTextColor(color);
        }

        public void setCardType(String type) {
            this.tvCardType.setText(type);
        }
    }
}
