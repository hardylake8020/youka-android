package com.zzqs.app_kc.z_kc.util;

import java.math.BigDecimal;

/**
 * Created by lance on 16/4/9.
 */
public class NumberUtil {
    /**
    * double to string
    * @author lance
    * @time 16/7/19 下午5:32
    */
    public static String doubleTrans(double num) {
        if (Math.round(num) - num == 0) {
            return String.valueOf((long) num);
        }
        return String.valueOf(num);
    }

    public static Float ranFloat(float max, float min) {
        BigDecimal db = new BigDecimal(Math.random() * (max - min) + min);
        return Float.valueOf(db.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
    }
}
