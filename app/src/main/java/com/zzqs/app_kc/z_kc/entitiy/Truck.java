package com.zzqs.app_kc.z_kc.entitiy;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lance on 2016/12/4.
 */

public class Truck implements Parcelable {
  public static final String TRUCK_ID = "truckId";
  public static final String TRUCK = "truck";
  public static final String TRUCKS = "trucks";
  public static final String UN_USAGE = "unUsage";
  public static final String USAGE = "usage";
  @SerializedName("_id")
  private String truck_id;
  private String truck_number;
  private String truck_type;
  private List<Double> location;

  private String driver_number;
  private String truck_photo;
  private String driver_name;
  private String status;
  private String card_id;

  private boolean isSelect;

  public String getTruck_number() {
    return truck_number;
  }

  public void setTruck_number(String truck_number) {
    this.truck_number = truck_number;
  }

  public String getTruck_type() {
    return truck_type;
  }

  public void setTruck_type(String truck_type) {
    this.truck_type = truck_type;
  }

  public String getDriver_number() {
    return driver_number;
  }

  public void setDriver_number(String driver_number) {
    this.driver_number = driver_number;
  }

  public String getTruck_photo() {
    return truck_photo;
  }

  public void setTruck_photo(String truck_photo) {
    this.truck_photo = truck_photo;
  }

  public String getDriver_name() {
    return driver_name;
  }

  public void setDriver_name(String driver_name) {
    this.driver_name = driver_name;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }


  public List<Double> getLocation() {
    return location;
  }

  public void setLocation(List<Double> location) {
    this.location = location;
  }


  public boolean isSelect() {
    return isSelect;
  }

  public void setSelect(boolean select) {
    isSelect = select;
  }

  public String getTruck_id() {
    return truck_id;
  }

  public void setTruck_id(String truck_id) {
    this.truck_id = truck_id;
  }

  public String getCard_id() {
    return card_id;
  }

  public void setCard_id(String card_id) {
    this.card_id = card_id;
  }


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.truck_id);
    dest.writeString(this.truck_number);
    dest.writeString(this.truck_type);
    dest.writeList(this.location);
    dest.writeString(this.driver_number);
    dest.writeString(this.truck_photo);
    dest.writeString(this.driver_name);
    dest.writeString(this.status);
    dest.writeString(this.card_id);
    dest.writeByte(this.isSelect ? (byte) 1 : (byte) 0);
  }

  public Truck() {
  }

  protected Truck(Parcel in) {
    this.truck_id = in.readString();
    this.truck_number = in.readString();
    this.truck_type = in.readString();
    this.location = new ArrayList<Double>();
    in.readList(this.location, Double.class.getClassLoader());
    this.driver_number = in.readString();
    this.truck_photo = in.readString();
    this.driver_name = in.readString();
    this.status = in.readString();
    this.card_id = in.readString();
    this.isSelect = in.readByte() != 0;
  }

  public static final Creator<Truck> CREATOR = new Creator<Truck>() {
    @Override
    public Truck createFromParcel(Parcel source) {
      return new Truck(source);
    }

    @Override
    public Truck[] newArray(int size) {
      return new Truck[size];
    }
  };
}
