package com.zzqs.app_kc.z_kc.entitiy;

/**
 * Created by lance on 2016/12/7.
 */

public class Record {
  public static final String DEPOSIT = "deposit";
  public static final String DRAW = "draw";
  private String type;
  private double money;
  private String time;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public double getMoney() {
    return money;
  }

  public void setMoney(double money) {
    this.money = money;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }
}
