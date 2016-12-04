package com.zzqs.app_kc.z_kc.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by lance on 16/6/17.
 */
public class TimeUtil {
  public static final String SERVER_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
  private static long lastClickTime;

  /**
   * 是否点击过快
   *
   * @author lance
   * @time 16/7/19 下午5:37
   */
  public synchronized static boolean isFastClick() {
    long time = System.currentTimeMillis();
    if (time - lastClickTime < 500) {
      return true;
    }
    lastClickTime = time;
    return false;
  }

  /**
   * 比较两个时间的前后,时间格式为yyyy-MM-dd hh:mm:ss
   *
   * @return DATE1在前返回1，DATE1在后返回-1，相等返回0
   * @author lance
   * @time 16/7/19 下午5:37
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

  /**
   * UTC时间转毫秒数
   *
   * @param time UTC时间
   * @author lance
   * @time 16/8/12 下午2:49
   */
  public static long UTCTimeToTimeMillis(String time) {
    SimpleDateFormat sdf = new SimpleDateFormat(SERVER_TIME_FORMAT);
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    try {
      Date date = sdf.parse(time);
      return date.getTime();
    } catch (ParseException e) {
      e.printStackTrace();
      return -1;
    }
  }

  /**
   * 0时区时间转本地时间
   *
   * @param strDate    服务器时间
   * @param fromFormat 原时间格式
   * @param toFormat   转化后的时间格式
   * @author lance
   * @time 16/9/7 上午10:58
   */
  public static String convertDateStringFormat(String strDate, String fromFormat, String toFormat) {
    try {
      DateFormat inputFormat = new SimpleDateFormat(fromFormat);
      inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
      DateFormat outputFormat = new SimpleDateFormat(toFormat);
      Date parsed = inputFormat.parse(strDate);
      String outputText = outputFormat.format(parsed);
      return outputText;
    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }
  }

  /**
   * 0时区字符串转换成calendar对象
   *
   * @author lance
   * @time 16/9/8 下午5:31
   */
  public static Calendar convertStringToCalendar(String strDate, String fromFormat) {
    try {
      DateFormat inputFormat = new SimpleDateFormat(fromFormat);
      inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
      Date date = inputFormat.parse(strDate);
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      return calendar;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }


  /**
   * 毫秒数转制定格式时间字符串
   *
   * @author lance
   * @time 2016/11/7 下午4:15
   */
  public static String TimeMillisToUTCTime(long timeMillis, String format) {
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    Date date = new Date(timeMillis);
    return sdf.format(date);
  }
}
