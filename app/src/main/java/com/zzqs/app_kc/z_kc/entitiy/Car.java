package com.zzqs.app_kc.z_kc.entitiy;

import java.util.List;

/**
 * Created by lance on 2016/12/4.
 */

public class Car {
  private String plate_number;
  private String car_type;
  private String driver_phone;
  private String car_photo;
  private String driver_name;
  private String status;
  private String oil_card;
  private List<Double> location;
  public static final String UN_TRANSPORT = "un_transport";
  public static final String TRANSPORTING = "transporting";


  public String getPlate_number() {
    return plate_number;
  }

  public void setPlate_number(String plate_number) {
    this.plate_number = plate_number;
  }

  public String getCar_type() {
    return car_type;
  }

  public void setCar_type(String car_type) {
    this.car_type = car_type;
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
}
