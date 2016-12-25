package com.zzqs.app_kc.z_kc.entitiy;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lance on 2016/12/26.
 */

public class TenderEvent implements Parcelable {
  public static final String TRANSPORT_EVENTS = "transport_events";

  public static final String MOLD_PICKUP_ENTRANCE = "pickupSign";
  public static final String MOLD_PICKUP = "pickup";
  public static final String MOLD_DELIVERY_ENTRANCE = "deliverySign";
  public static final String MOLD_DELIVERY = "delivery";
  public static final String MOLD_HALFWAY = "halfway";
  public static final String MOLD_CONFIRM = "confirm";

  private String event_id;
  private String order;
  private String driver_name;
  private String driver_phone;
  private String type;
  private String address;
  private boolean transport_plate_difference;
  private boolean driver_plate_difference;
  private List<Double> recognize_plates = new ArrayList<>();
  private boolean is_wechat;
  private boolean delivery_by_qrcode;
  private String voice_file;
  private List<TenderEventPhoto> photos = new ArrayList<>();
  private List<Double> location = new ArrayList<>();
  private boolean address_difference;
  private boolean delivery_deferred;
  private boolean pickup_deferred;
  private boolean delivery_missing_packages;
  private boolean pickup_missing_packages;
  private boolean damaged;
  private String description;
  private String time;//utc时间
  private List<String> driver_plate_numbers = new ArrayList<>();

  public String getEvent_id() {
    return event_id;
  }

  public void setEvent_id(String event_id) {
    this.event_id = event_id;
  }

  public String getOrder() {
    return order;
  }

  public void setOrder(String order) {
    this.order = order;
  }

  public String getDriver_name() {
    return driver_name;
  }

  public void setDriver_name(String driver_name) {
    this.driver_name = driver_name;
  }

  public String getDriver_phone() {
    return driver_phone;
  }

  public void setDriver_phone(String driver_phone) {
    this.driver_phone = driver_phone;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public boolean isTransport_plate_difference() {
    return transport_plate_difference;
  }

  public void setTransport_plate_difference(boolean transport_plate_difference) {
    this.transport_plate_difference = transport_plate_difference;
  }

  public boolean isDriver_plate_difference() {
    return driver_plate_difference;
  }

  public void setDriver_plate_difference(boolean driver_plate_difference) {
    this.driver_plate_difference = driver_plate_difference;
  }

  public List<Double> getRecognize_plates() {
    return recognize_plates;
  }

  public void setRecognize_plates(List<Double> recognize_plates) {
    this.recognize_plates = recognize_plates;
  }

  public boolean is_wechat() {
    return is_wechat;
  }

  public void setIs_wechat(boolean is_wechat) {
    this.is_wechat = is_wechat;
  }

  public boolean isDelivery_by_qrcode() {
    return delivery_by_qrcode;
  }

  public void setDelivery_by_qrcode(boolean delivery_by_qrcode) {
    this.delivery_by_qrcode = delivery_by_qrcode;
  }

  public String getVoice_file() {
    return voice_file;
  }

  public void setVoice_file(String voice_file) {
    this.voice_file = voice_file;
  }

  public List<TenderEventPhoto> getPhotos() {
    return photos;
  }

  public void setPhotos(List<TenderEventPhoto> photos) {
    this.photos = photos;
  }

  public List<Double> getLocation() {
    return location;
  }

  public void setLocation(List<Double> location) {
    this.location = location;
  }

  public boolean isAddress_difference() {
    return address_difference;
  }

  public void setAddress_difference(boolean address_difference) {
    this.address_difference = address_difference;
  }

  public boolean isDelivery_deferred() {
    return delivery_deferred;
  }

  public void setDelivery_deferred(boolean delivery_deferred) {
    this.delivery_deferred = delivery_deferred;
  }

  public boolean isPickup_deferred() {
    return pickup_deferred;
  }

  public void setPickup_deferred(boolean pickup_deferred) {
    this.pickup_deferred = pickup_deferred;
  }

  public boolean isDelivery_missing_packages() {
    return delivery_missing_packages;
  }

  public void setDelivery_missing_packages(boolean delivery_missing_packages) {
    this.delivery_missing_packages = delivery_missing_packages;
  }

  public boolean isPickup_missing_packages() {
    return pickup_missing_packages;
  }

  public void setPickup_missing_packages(boolean pickup_missing_packages) {
    this.pickup_missing_packages = pickup_missing_packages;
  }

  public boolean isDamaged() {
    return damaged;
  }

  public void setDamaged(boolean damaged) {
    this.damaged = damaged;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public List<String> getDriver_plate_numbers() {
    return driver_plate_numbers;
  }

  public void setDriver_plate_numbers(List<String> driver_plate_numbers) {
    this.driver_plate_numbers = driver_plate_numbers;
  }


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.event_id);
    dest.writeString(this.order);
    dest.writeString(this.driver_name);
    dest.writeString(this.driver_phone);
    dest.writeString(this.type);
    dest.writeString(this.address);
    dest.writeByte(this.transport_plate_difference ? (byte) 1 : (byte) 0);
    dest.writeByte(this.driver_plate_difference ? (byte) 1 : (byte) 0);
    dest.writeList(this.recognize_plates);
    dest.writeByte(this.is_wechat ? (byte) 1 : (byte) 0);
    dest.writeByte(this.delivery_by_qrcode ? (byte) 1 : (byte) 0);
    dest.writeString(this.voice_file);
    dest.writeList(this.photos);
    dest.writeList(this.location);
    dest.writeByte(this.address_difference ? (byte) 1 : (byte) 0);
    dest.writeByte(this.delivery_deferred ? (byte) 1 : (byte) 0);
    dest.writeByte(this.pickup_deferred ? (byte) 1 : (byte) 0);
    dest.writeByte(this.delivery_missing_packages ? (byte) 1 : (byte) 0);
    dest.writeByte(this.pickup_missing_packages ? (byte) 1 : (byte) 0);
    dest.writeByte(this.damaged ? (byte) 1 : (byte) 0);
    dest.writeString(this.description);
    dest.writeString(this.time);
    dest.writeStringList(this.driver_plate_numbers);
  }

  public TenderEvent() {
  }

  protected TenderEvent(Parcel in) {
    this.event_id = in.readString();
    this.order = in.readString();
    this.driver_name = in.readString();
    this.driver_phone = in.readString();
    this.type = in.readString();
    this.address = in.readString();
    this.transport_plate_difference = in.readByte() != 0;
    this.driver_plate_difference = in.readByte() != 0;
    this.recognize_plates = new ArrayList<Double>();
    in.readList(this.recognize_plates, Double.class.getClassLoader());
    this.is_wechat = in.readByte() != 0;
    this.delivery_by_qrcode = in.readByte() != 0;
    this.voice_file = in.readString();
    this.photos = new ArrayList<TenderEventPhoto>();
    in.readList(this.photos, TenderEventPhoto.class.getClassLoader());
    this.location = new ArrayList<Double>();
    in.readList(this.location, Double.class.getClassLoader());
    this.address_difference = in.readByte() != 0;
    this.delivery_deferred = in.readByte() != 0;
    this.pickup_deferred = in.readByte() != 0;
    this.delivery_missing_packages = in.readByte() != 0;
    this.pickup_missing_packages = in.readByte() != 0;
    this.damaged = in.readByte() != 0;
    this.description = in.readString();
    this.time = in.readString();
    this.driver_plate_numbers = in.createStringArrayList();
  }

  public static final Parcelable.Creator<TenderEvent> CREATOR = new Parcelable.Creator<TenderEvent>() {
    @Override
    public TenderEvent createFromParcel(Parcel source) {
      return new TenderEvent(source);
    }

    @Override
    public TenderEvent[] newArray(int size) {
      return new TenderEvent[size];
    }
  };
}
