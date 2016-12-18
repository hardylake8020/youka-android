package com.zzqs.app_kc.z_kc.entitiy;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lance on 2016/12/4.
 */

public class Car implements Parcelable{
  public static final String CAR = "car";
  public static final String UN_USAGE = "unUsage";
  public static final String USAGE = "usage";
  private String truck_number;
  private String truck_type;
  private List<Double> location;

  private String driver_phone;
  private String car_photo;
  private String driver_name;
  private String status;
  private String oil_card;


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

  public String getDriver_phone() {
    return driver_phone;
  }

  public void setDriver_phone(String driver_phone) {
    this.driver_phone = driver_phone;
  }

  public String getCar_photo() {
    return car_photo;
  }

  public void setCar_photo(String car_photo) {
    this.car_photo = car_photo;
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

  public String getOil_card() {
    return oil_card;
  }

  public void setOil_card(String oil_card) {
    this.oil_card = oil_card;
  }

  public List<Double> getLocation() {
    return location;
  }

  public void setLocation(List<Double> location) {
    this.location = location;
  }


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.truck_number);
    dest.writeString(this.truck_type);
    dest.writeString(this.driver_phone);
    dest.writeString(this.car_photo);
    dest.writeString(this.driver_name);
    dest.writeString(this.status);
    dest.writeString(this.oil_card);
    dest.writeList(this.location);
  }

  public Car() {
  }

  protected Car(Parcel in) {
    this.truck_number = in.readString();
    this.truck_type = in.readString();
    this.driver_phone = in.readString();
    this.car_photo = in.readString();
    this.driver_name = in.readString();
    this.status = in.readString();
    this.oil_card = in.readString();
    this.location = new ArrayList<Double>();
    in.readList(this.location, Double.class.getClassLoader());
  }

  public static final Creator<Car> CREATOR = new Creator<Car>() {
    @Override
    public Car createFromParcel(Parcel source) {
      return new Car(source);
    }

    @Override
    public Car[] newArray(int size) {
      return new Car[size];
    }
  };
}
