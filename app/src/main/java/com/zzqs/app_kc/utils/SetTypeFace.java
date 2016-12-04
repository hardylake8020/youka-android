package com.zzqs.app_kc.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Created by lance on 15/11/17.
 */
public class SetTypeFace {
    private static Typeface font;

    public static void setTypeFace(Context context, TextView textView, int color, float size) {
        if (font == null) {
            font = Typeface.createFromAsset(context.getAssets(), "fontawesome-webfont.ttf");
        }
        textView.setTypeface(font);
        if (color != 0) {
            textView.setTextColor(color);
        }
        if (size != 0) {
            textView.setTextSize(size);
        }
    }
}
