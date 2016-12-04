package com.zzqs.app_kc.utils;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringTools {
    /**
     * 字符串是否为空 true为空 false为非空
     *
     * @param s
     * @return
     */
    public static boolean isEmp(String s) {
        if (s == null)
            return true;
        if (s.equals(""))
            return true;
        if (s.equals("null"))
            return true;
        if (s.equals("[]"))
            return true;
        return false;
    }

    /**
     * 判断输入是否是手机号码
     *
     * @param phoneNumber
     * @return
     */
    public static boolean isMobileNumber(String phoneNumber) {
        if (phoneNumber == null)
            return false;

        Pattern p = Pattern
                .compile("^(1)\\d{10}$");

        Matcher m = p.matcher(phoneNumber.trim());

        return m.matches();
    }

    /**
     * 判断是否是正数或小数
     */
    public static boolean isNumber(String string) {
        if (string == null)
            return false;

        Pattern p = Pattern
                .compile("^[0-9]+([.]{1}[0-9]+){0,1}$");
        Matcher m = p.matcher(string.trim());

        return m.matches();
    }

    /**
     * 比较两个时间的前后,时间格式为yyyy-MM-dd hh:mm:ss
     *
     * @return DATE1在前返回1，DATE1在后返回-1，相等返回0
     */
    public static int compareDate(String DATE1, String DATE2) {


        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return 0;
        }
    }

    public static String outputDate(String startTime, String endTime) {
        if (!isEmp(startTime) && isEmp(endTime)) {
            String st = startTime.substring(5, 16);
            st = st.replace("-", "月");
            st = st.replace(" ", "日 ");
            return st;
        } else if (isEmp(startTime) && !isEmp(endTime)) {
            String et = startTime.substring(5, 16);
            et = et.replace("-", "月");
            et = et.replace(" ", "日 ");
            return et;
        } else if (!isEmp(startTime) && !isEmp(endTime)) {
            startTime = startTime.substring(5, 16);
            endTime = endTime.substring(5, 16);
            startTime = startTime.replace("-", "月").replace(" ", "日 ");
            endTime = endTime.replace("-", "月").replace(" ", "日 ");
            if (startTime.substring(0, 6).equals(endTime.substring(0, 6))) {
                if (startTime.substring(7).equals(endTime.substring(7))) {
                    return startTime;
                } else {
                    return startTime.substring(0, 6) + startTime.substring(7) + "~" + endTime.substring(7);
                }
            } else {
                return startTime + "~" + endTime;
            }
        } else {
            return null;
        }
    }

    /**
     * MD5生成
     */
    private String MD5(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuilder stringBuilder = new StringBuilder("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    stringBuilder.append("0");
                stringBuilder.append(Integer.toHexString(i));
            }
            result = stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    public static boolean isCarnumberNO(String carnumber) {
              /*
          车牌号格式：汉字 + A-Z + 5位A-Z或0-9
         （只包括了普通车牌号，教练车和部分部队车等车牌号不包括在内）
          */
        String carnumRegex = "[\u4e00-\u9fa5]{1}[A-Z_a-z]{1}[A-Z_a-z_0-9]{5}";
        if (TextUtils.isEmpty(carnumber)) {
            return false;
        } else {
            return carnumber.matches(carnumRegex);
        }
    }
}
