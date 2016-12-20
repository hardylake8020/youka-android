package com.zzqs.app_kc.z_kc.entitiy;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lance on 2016/12/12.
 */

public class Goods implements Parcelable{
  private String name;
  private double count;
  private String unit;
  private double count2;
  private String unit2;
  private double count3;
  private String unit3;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getCount() {
    return count;
  }

  public void setCount(double count) {
    this.count = count;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public double getCount2() {
    return count2;
  }

  public void setCount2(double count2) {
    this.count2 = count2;
  }

  public String getUnit2() {
    return unit2;
  }

  public void setUnit2(String unit2) {
    this.unit2 = unit2;
  }

  public double getCount3() {
    return count3;
  }

  public void setCount3(double count3) {
    this.count3 = count3;
  }

  public String getUnit3() {
    return unit3;
  }

  public void setUnit3(String unit3) {
    this.unit3 = unit3;
  }


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.name);
    dest.writeDouble(this.count);
    dest.writeString(this.unit);
    dest.writeDouble(this.count2);
    dest.writeString(this.unit2);
    dest.writeDouble(this.count3);
    dest.writeString(this.unit3);
  }

  public Goods() {
  }

  protected Goods(Parcel in) {
    this.name = in.readString();
    this.count = in.readDouble();
    this.unit = in.readString();
    this.count2 = in.readDouble();
    this.unit2 = in.readString();
    this.count3 = in.readDouble();
    this.unit3 = in.readString();
  }

  public static final Creator<Goods> CREATOR = new Creator<Goods>() {
    @Override
    public Goods createFromParcel(Parcel source) {
      return new Goods(source);
    }

    @Override
    public Goods[] newArray(int size) {
      return new Goods[size];
    }
  };
}
