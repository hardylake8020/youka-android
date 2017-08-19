package com.zzqs.app_kc.z_kc.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.zzqs.app_kc.R;

import java.util.List;

/**
 * Created by ray on 2017/3/19.
 * Class name : PhotoAdapter
 * Description :
 */
public class PhotoAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ImageLoader imageLoader;
    private List<String> photos;
    private DisplayImageOptions options;

    public PhotoAdapter(Context context, List<String> photos) {
        this.photos = photos;
        this.context = context;
        inflater = LayoutInflater.from(context);
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

    }

    @Override
    public int getCount() {
        return photos != null ? photos.size() + 1 : 1;

    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.z_kc_item_photo, null);
            holder = new ViewHolder();
            holder.ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView ivPhoto;
    }
}
