package com.zzqs.app_kc.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.zzqs.app_kc.R;
import com.zzqs.app_kc.entity.EventFile;
import com.zzqs.app_kc.entity.OrderEvent;
import com.zzqs.app_kc.utils.SetTypeFace;
import com.zzqs.app_kc.utils.StringTools;

import java.io.File;
import java.util.List;

/**
 * Created by lance on 15/3/25.
 */
public class EventFilePhotoAdapter extends BaseAdapter {
    private List<EventFile> list = null;
    private LayoutInflater inflater;
    private Context context;
    private boolean isAddPhoto;
    private String mold;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public EventFilePhotoAdapter(Context context, List<EventFile> list, boolean isAddPhoto, String mold) {
        this.list = list;
        this.context = context;
        this.isAddPhoto = isAddPhoto;
        this.mold = mold;
        inflater = LayoutInflater.from(context);
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

    }

    @Override
    public int getCount() {
//         TODO Auto-generated method stub
        return list != null ? list.size() + 1 : 1;
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
            convertView = inflater.inflate(R.layout.item_gridview, null);
            holder = new ViewHolder();
            holder.img = (ImageView) convertView.findViewById(R.id.ivPhoto);
            holder.mold = (TextView) convertView.findViewById(R.id.mold);
            holder.tv_icon = (TextView) convertView.findViewById(R.id.tv_icon);
            holder.tv_shoot = (TextView) convertView.findViewById(R.id.tv_shoot);
            SetTypeFace.setTypeFace(context, holder.tv_icon, 0, 0);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position < list.size()) {
            EventFile eventFile = list.get(position);
            if (!StringTools.isEmp(eventFile.getFilePath()) && !eventFile.getFilePath().equals(EventFile.ZZQS_CONFIG_PHOTO)) {
                holder.tv_icon.setVisibility(View.GONE);
                holder.tv_shoot.setVisibility(View.GONE);
                holder.img.setVisibility(View.VISIBLE);
                holder.mold.setVisibility(View.VISIBLE);
                Uri uri = Uri.fromFile(new File(eventFile.getFilePath()));
                imageLoader.displayImage(uri.toString(), holder.img, options);
                if (!StringTools.isEmp(eventFile.getConfigName())) {
                    holder.mold.setText(eventFile.getConfigName());
                    switch (eventFile.getConfigName()) {
                        case "可选":
                        case EventFile.PICKUP_ENTER:
                        case EventFile.PICKUP:
                        case EventFile.DELIVERY_ENTER:
                        case EventFile.DELIVERY:
                        case EventFile.HALF_WAY:
                            holder.mold.setBackgroundColor(context.getResources().getColor(R.color.green));
                            break;
                        case EventFile.DAMAGE:
                            holder.mold.setBackgroundColor(context.getResources().getColor(R.color.red_live_4));
                            break;
                        default:
                            holder.mold.setBackgroundColor(context.getResources().getColor(R.color.yellow_config));
                            break;

                    }
                }
            } else {
                holder.img.setVisibility(View.INVISIBLE);
                holder.mold.setVisibility(View.INVISIBLE);
                holder.tv_icon.setVisibility(View.VISIBLE);
                holder.tv_shoot.setVisibility(View.VISIBLE);
                if (!StringTools.isEmp(eventFile.getConfigName())) {
                    holder.tv_shoot.setText(eventFile.getConfigName());
                }
            }
        } else {
            if (isAddPhoto) {
                holder.img.setVisibility(View.INVISIBLE);
                holder.mold.setVisibility(View.INVISIBLE);
                holder.tv_icon.setVisibility(View.VISIBLE);
                holder.tv_shoot.setVisibility(View.VISIBLE);
                String moldName;
                switch (mold) {
                    case OrderEvent.MOLD_PICKUP_ENTRANCE:
                        moldName = "提货进场(可选)";
                        break;
                    case OrderEvent.MOLD_PICKUP:
                        moldName = "提货(可选)";
                        break;
                    case OrderEvent.MOLD_DELIVERY_ENTRANCE:
                        moldName = "交货进场(可选)";
                        break;
                    case OrderEvent.MOLD_DELIVERY:
                        moldName = "交货(可选)";
                        break;
                    case OrderEvent.MOLD_HALFWAY:
                        moldName = "中途事件(可选)";
                        break;
                    default:
                        moldName = "可选";
                        break;
                }
                holder.tv_shoot.setText(moldName);
            } else {
                holder.img.setVisibility(View.GONE);
                holder.mold.setVisibility(View.GONE);
                holder.tv_icon.setVisibility(View.GONE);
                holder.tv_shoot.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    private class ViewHolder {
        ImageView img;
        TextView mold, tv_icon, tv_shoot;
    }
}
