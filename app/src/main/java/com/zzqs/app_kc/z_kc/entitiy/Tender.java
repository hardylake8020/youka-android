package com.zzqs.app_kc.z_kc.entitiy;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lance on 2016/12/3.
 */

public class Tender implements Parcelable {
  public static final String TENDER = "tender";
  public static final String TENDERS = "tenders";
  public static final String GRAB = "grab";
  public static final String COMPARE = "compare";


  @SerializedName("_id")
  private String tender_id;
  private String tender_number;
  private String order_number;
  private String refer_order_number;
  private String sender_company;//发标方
  private String sender_phone;
  private String sender_name;
  private String pay_approver;//付款审核人
  private String finance_officer;//财务负责人
  private String status;//unStarted,inProgress,completed,stop,obsolete,deleted;未开始，进行中，已截止， 已完成，已过时，已删除
  private String start_time;
  private String end_time;
  private String truck_type;
  private int truck_count;
  private List<Goods> mobile_goods = new ArrayList<>();
  private String remark;
  private String transport_type;
  private int distance;

  private String pickup_province;
  private String pickup_city;
  private String pickup_region;
  private String pickup_street;
  private String pickup_address;
  private List<Double> pickup_location = new ArrayList<>();
  private String pickup_start_time_format;
  private String pickup_end_time_format;
  private String pickup_name;
  private String pickup_mobile_phone;
  private String pickup_tel_phone;

  private String delivery_province;
  private String delivery_city;
  private String delivery_region;
  private String delivery_street;
  private String delivery_address;
  private List<Double> delivery_location = new ArrayList<>();
  private String delivery_start_time_format;
  private String delivery_end_time_format;
  private String delivery_name;
  private String delivery_mobile_phone;
  private String delivery_tel_phone;

  private String initiator_name;
  private String initiator_phone;


  private double payment_top_rate;
  private double payment_top_cash_rate;
  private double payment_top_card_rate;
  private double payment_tail_rate;
  private double payment_tail_cash_rate;
  private double payment_tail_card_rate;
  private double payment_last_rate;
  private double payment_last_cash_rate;
  private double payment_last_card_rate;

  private String tender_type;
  private double lowest_protect_price;
  private double highest_protect_price;
  private double lowest_grab_price;
  private double highest_grab_price;
  private double grab_increment_price;
  private double current_grab_price;

  private double driver_price;

  public String getTender_id() {
    return tender_id;
  }

  public void setTender_id(String tender_id) {
    this.tender_id = tender_id;
  }

  public String getTender_number() {
    return tender_number;
  }

  public void setTender_number(String tender_number) {
    this.tender_number = tender_number;
  }

  public String getOrder_number() {
    return order_number;
  }

  public void setOrder_number(String order_number) {
    this.order_number = order_number;
  }

  public String getRefer_order_number() {
    return refer_order_number;
  }

  public void setRefer_order_number(String refer_order_number) {
    this.refer_order_number = refer_order_number;
  }

  public String getSender_company() {
    return sender_company;
  }

  public void setSender_company(String sender_company) {
    this.sender_company = sender_company;
  }

  public String getSender_phone() {
    return sender_phone;
  }

  public void setSender_phone(String sender_phone) {
    this.sender_phone = sender_phone;
  }

  public String getSender_name() {
    return sender_name;
  }

  public void setSender_name(String sender_name) {
    this.sender_name = sender_name;
  }

  public String getPay_approver() {
    return pay_approver;
  }

  public void setPay_approver(String pay_approver) {
    this.pay_approver = pay_approver;
  }

  public String getFinance_officer() {
    return finance_officer;
  }

  public void setFinance_officer(String finance_officer) {
    this.finance_officer = finance_officer;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getStart_time() {
    return start_time;
  }

  public void setStart_time(String start_time) {
    this.start_time = start_time;
  }

  public String getEnd_time() {
    return end_time;
  }

  public void setEnd_time(String end_time) {
    this.end_time = end_time;
  }

  public String getTruck_type() {
    return truck_type;
  }

  public void setTruck_type(String truck_type) {
    this.truck_type = truck_type;
  }

  public int getTruck_count() {
    return truck_count;
  }

  public void setTruck_count(int truck_count) {
    this.truck_count = truck_count;
  }

  public List<Goods> getMobile_goods() {
    return mobile_goods;
  }

  public void setMobile_goods(List<Goods> mobile_goods) {
    this.mobile_goods = mobile_goods;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public String getTransport_type() {
    return transport_type;
  }

  public void setTransport_type(String transport_type) {
    this.transport_type = transport_type;
  }

  public int getDistance() {
    return distance;
  }

  public void setDistance(int distance) {
    this.distance = distance;
  }

  public String getPickup_province() {
    return pickup_province;
  }

  public void setPickup_province(String pickup_province) {
    this.pickup_province = pickup_province;
  }

  public String getPickup_city() {
    return pickup_city;
  }

  public void setPickup_city(String pickup_city) {
    this.pickup_city = pickup_city;
  }

  public String getPickup_region() {
    return pickup_region;
  }

  public void setPickup_region(String pickup_region) {
    this.pickup_region = pickup_region;
  }

  public String getPickup_street() {
    return pickup_street;
  }

  public void setPickup_street(String pickup_street) {
    this.pickup_street = pickup_street;
  }

  public String getPickup_address() {
    return pickup_address;
  }

  public void setPickup_address(String pickup_address) {
    this.pickup_address = pickup_address;
  }

  public List<Double> getPickup_location() {
    return pickup_location;
  }

  public void setPickup_location(List<Double> pickup_location) {
    this.pickup_location = pickup_location;
  }

  public String getPickup_start_time_format() {
    return pickup_start_time_format;
  }

  public void setPickup_start_time_format(String pickup_start_time_format) {
    this.pickup_start_time_format = pickup_start_time_format;
  }

  public String getPickup_end_time_format() {
    return pickup_end_time_format;
  }

  public void setPickup_end_time_format(String pickup_end_time_format) {
    this.pickup_end_time_format = pickup_end_time_format;
  }

  public String getPickup_name() {
    return pickup_name;
  }

  public void setPickup_name(String pickup_name) {
    this.pickup_name = pickup_name;
  }

  public String getPickup_mobile_phone() {
    return pickup_mobile_phone;
  }

  public void setPickup_mobile_phone(String pickup_mobile_phone) {
    this.pickup_mobile_phone = pickup_mobile_phone;
  }

  public String getPickup_tel_phone() {
    return pickup_tel_phone;
  }

  public void setPickup_tel_phone(String pickup_tel_phone) {
    this.pickup_tel_phone = pickup_tel_phone;
  }

  public String getDelivery_province() {
    return delivery_province;
  }

  public void setDelivery_province(String delivery_province) {
    this.delivery_province = delivery_province;
  }

  public String getDelivery_city() {
    return delivery_city;
  }

  public void setDelivery_city(String delivery_city) {
    this.delivery_city = delivery_city;
  }

  public String getDelivery_region() {
    return delivery_region;
  }

  public void setDelivery_region(String delivery_region) {
    this.delivery_region = delivery_region;
  }

  public String getDelivery_street() {
    return delivery_street;
  }

  public void setDelivery_street(String delivery_street) {
    this.delivery_street = delivery_street;
  }

  public String getDelivery_address() {
    return delivery_address;
  }

  public void setDelivery_address(String delivery_address) {
    this.delivery_address = delivery_address;
  }

  public List<Double> getDelivery_location() {
    return delivery_location;
  }

  public void setDelivery_location(List<Double> delivery_location) {
    this.delivery_location = delivery_location;
  }

  public String getDelivery_start_time_format() {
    return delivery_start_time_format;
  }

  public void setDelivery_start_time_format(String delivery_start_time_format) {
    this.delivery_start_time_format = delivery_start_time_format;
  }

  public String getDelivery_end_time_format() {
    return delivery_end_time_format;
  }

  public void setDelivery_end_time_format(String delivery_end_time_format) {
    this.delivery_end_time_format = delivery_end_time_format;
  }

  public String getDelivery_name() {
    return delivery_name;
  }

  public void setDelivery_name(String delivery_name) {
    this.delivery_name = delivery_name;
  }

  public String getDelivery_mobile_phone() {
    return delivery_mobile_phone;
  }

  public void setDelivery_mobile_phone(String delivery_mobile_phone) {
    this.delivery_mobile_phone = delivery_mobile_phone;
  }

  public String getDelivery_tel_phone() {
    return delivery_tel_phone;
  }

  public void setDelivery_tel_phone(String delivery_tel_phone) {
    this.delivery_tel_phone = delivery_tel_phone;
  }

  public String getInitiator_name() {
    return initiator_name;
  }

  public void setInitiator_name(String initiator_name) {
    this.initiator_name = initiator_name;
  }

  public String getInitiator_phone() {
    return initiator_phone;
  }

  public void setInitiator_phone(String initiator_phone) {
    this.initiator_phone = initiator_phone;
  }

  public double getPayment_top_rate() {
    return payment_top_rate;
  }

  public void setPayment_top_rate(double payment_top_rate) {
    this.payment_top_rate = payment_top_rate;
  }

  public double getPayment_top_cash_rate() {
    return payment_top_cash_rate;
  }

  public void setPayment_top_cash_rate(double payment_top_cash_rate) {
    this.payment_top_cash_rate = payment_top_cash_rate;
  }

  public double getPayment_top_card_rate() {
    return payment_top_card_rate;
  }

  public void setPayment_top_card_rate(double payment_top_card_rate) {
    this.payment_top_card_rate = payment_top_card_rate;
  }

  public double getPayment_tail_rate() {
    return payment_tail_rate;
  }

  public void setPayment_tail_rate(double payment_tail_rate) {
    this.payment_tail_rate = payment_tail_rate;
  }

  public double getPayment_tail_cash_rate() {
    return payment_tail_cash_rate;
  }

  public void setPayment_tail_cash_rate(double payment_tail_cash_rate) {
    this.payment_tail_cash_rate = payment_tail_cash_rate;
  }

  public double getPayment_tail_card_rate() {
    return payment_tail_card_rate;
  }

  public void setPayment_tail_card_rate(double payment_tail_card_rate) {
    this.payment_tail_card_rate = payment_tail_card_rate;
  }

  public double getPayment_last_rate() {
    return payment_last_rate;
  }

  public void setPayment_last_rate(double payment_last_rate) {
    this.payment_last_rate = payment_last_rate;
  }

  public double getPayment_last_cash_rate() {
    return payment_last_cash_rate;
  }

  public void setPayment_last_cash_rate(double payment_last_cash_rate) {
    this.payment_last_cash_rate = payment_last_cash_rate;
  }

  public double getPayment_last_card_rate() {
    return payment_last_card_rate;
  }

  public void setPayment_last_card_rate(double payment_last_card_rate) {
    this.payment_last_card_rate = payment_last_card_rate;
  }

  public String getTender_type() {
    return tender_type;
  }

  public void setTender_type(String tender_type) {
    this.tender_type = tender_type;
  }

  public double getLowest_protect_price() {
    return lowest_protect_price;
  }

  public void setLowest_protect_price(double lowest_protect_price) {
    this.lowest_protect_price = lowest_protect_price;
  }

  public double getHighest_protect_price() {
    return highest_protect_price;
  }

  public void setHighest_protect_price(double highest_protect_price) {
    this.highest_protect_price = highest_protect_price;
  }

  public double getLowest_grab_price() {
    return lowest_grab_price;
  }

  public void setLowest_grab_price(double lowest_grab_price) {
    this.lowest_grab_price = lowest_grab_price;
  }

  public double getHighest_grab_price() {
    return highest_grab_price;
  }

  public void setHighest_grab_price(double highest_grab_price) {
    this.highest_grab_price = highest_grab_price;
  }

  public double getGrab_increment_price() {
    return grab_increment_price;
  }

  public void setGrab_increment_price(double grab_increment_price) {
    this.grab_increment_price = grab_increment_price;
  }

  public double getCurrent_grab_price() {
    return current_grab_price;
  }

  public void setCurrent_grab_price(double current_grab_price) {
    this.current_grab_price = current_grab_price;
  }

  public double getDriver_price() {
    return driver_price;
  }

  public void setDriver_price(double driver_price) {
    this.driver_price = driver_price;
  }


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.tender_id);
    dest.writeString(this.tender_number);
    dest.writeString(this.order_number);
    dest.writeString(this.refer_order_number);
    dest.writeString(this.sender_company);
    dest.writeString(this.sender_phone);
    dest.writeString(this.sender_name);
    dest.writeString(this.pay_approver);
    dest.writeString(this.finance_officer);
    dest.writeString(this.status);
    dest.writeString(this.start_time);
    dest.writeString(this.end_time);
    dest.writeString(this.truck_type);
    dest.writeInt(this.truck_count);
    dest.writeTypedList(this.mobile_goods);
    dest.writeString(this.remark);
    dest.writeString(this.transport_type);
    dest.writeInt(this.distance);
    dest.writeString(this.pickup_province);
    dest.writeString(this.pickup_city);
    dest.writeString(this.pickup_region);
    dest.writeString(this.pickup_street);
    dest.writeString(this.pickup_address);
    dest.writeList(this.pickup_location);
    dest.writeString(this.pickup_start_time_format);
    dest.writeString(this.pickup_end_time_format);
    dest.writeString(this.pickup_name);
    dest.writeString(this.pickup_mobile_phone);
    dest.writeString(this.pickup_tel_phone);
    dest.writeString(this.delivery_province);
    dest.writeString(this.delivery_city);
    dest.writeString(this.delivery_region);
    dest.writeString(this.delivery_street);
    dest.writeString(this.delivery_address);
    dest.writeList(this.delivery_location);
    dest.writeString(this.delivery_start_time_format);
    dest.writeString(this.delivery_end_time_format);
    dest.writeString(this.delivery_name);
    dest.writeString(this.delivery_mobile_phone);
    dest.writeString(this.delivery_tel_phone);
    dest.writeString(this.initiator_name);
    dest.writeString(this.initiator_phone);
    dest.writeDouble(this.payment_top_rate);
    dest.writeDouble(this.payment_top_cash_rate);
    dest.writeDouble(this.payment_top_card_rate);
    dest.writeDouble(this.payment_tail_rate);
    dest.writeDouble(this.payment_tail_cash_rate);
    dest.writeDouble(this.payment_tail_card_rate);
    dest.writeDouble(this.payment_last_rate);
    dest.writeDouble(this.payment_last_cash_rate);
    dest.writeDouble(this.payment_last_card_rate);
    dest.writeString(this.tender_type);
    dest.writeDouble(this.lowest_protect_price);
    dest.writeDouble(this.highest_protect_price);
    dest.writeDouble(this.lowest_grab_price);
    dest.writeDouble(this.highest_grab_price);
    dest.writeDouble(this.grab_increment_price);
    dest.writeDouble(this.current_grab_price);
    dest.writeDouble(this.driver_price);
  }

  public Tender() {
  }

  protected Tender(Parcel in) {
    this.tender_id = in.readString();
    this.tender_number = in.readString();
    this.order_number = in.readString();
    this.refer_order_number = in.readString();
    this.sender_company = in.readString();
    this.sender_phone = in.readString();
    this.sender_name = in.readString();
    this.pay_approver = in.readString();
    this.finance_officer = in.readString();
    this.status = in.readString();
    this.start_time = in.readString();
    this.end_time = in.readString();
    this.truck_type = in.readString();
    this.truck_count = in.readInt();
    this.mobile_goods = in.createTypedArrayList(Goods.CREATOR);
    this.remark = in.readString();
    this.transport_type = in.readString();
    this.distance = in.readInt();
    this.pickup_province = in.readString();
    this.pickup_city = in.readString();
    this.pickup_region = in.readString();
    this.pickup_street = in.readString();
    this.pickup_address = in.readString();
    this.pickup_location = new ArrayList<Double>();
    in.readList(this.pickup_location, Double.class.getClassLoader());
    this.pickup_start_time_format = in.readString();
    this.pickup_end_time_format = in.readString();
    this.pickup_name = in.readString();
    this.pickup_mobile_phone = in.readString();
    this.pickup_tel_phone = in.readString();
    this.delivery_province = in.readString();
    this.delivery_city = in.readString();
    this.delivery_region = in.readString();
    this.delivery_street = in.readString();
    this.delivery_address = in.readString();
    this.delivery_location = new ArrayList<Double>();
    in.readList(this.delivery_location, Double.class.getClassLoader());
    this.delivery_start_time_format = in.readString();
    this.delivery_end_time_format = in.readString();
    this.delivery_name = in.readString();
    this.delivery_mobile_phone = in.readString();
    this.delivery_tel_phone = in.readString();
    this.initiator_name = in.readString();
    this.initiator_phone = in.readString();
    this.payment_top_rate = in.readDouble();
    this.payment_top_cash_rate = in.readDouble();
    this.payment_top_card_rate = in.readDouble();
    this.payment_tail_rate = in.readDouble();
    this.payment_tail_cash_rate = in.readDouble();
    this.payment_tail_card_rate = in.readDouble();
    this.payment_last_rate = in.readDouble();
    this.payment_last_cash_rate = in.readDouble();
    this.payment_last_card_rate = in.readDouble();
    this.tender_type = in.readString();
    this.lowest_protect_price = in.readDouble();
    this.highest_protect_price = in.readDouble();
    this.lowest_grab_price = in.readDouble();
    this.highest_grab_price = in.readDouble();
    this.grab_increment_price = in.readDouble();
    this.current_grab_price = in.readDouble();
    this.driver_price = in.readDouble();
  }

  public static final Creator<Tender> CREATOR = new Creator<Tender>() {
    @Override
    public Tender createFromParcel(Parcel source) {
      return new Tender(source);
    }

    @Override
    public Tender[] newArray(int size) {
      return new Tender[size];
    }
  };

  @Override
  public String toString() {
    return "Tender{" +
            "tender_id='" + tender_id + '\'' +
            ", tender_number='" + tender_number + '\'' +
            ", order_number='" + order_number + '\'' +
            ", refer_order_number='" + refer_order_number + '\'' +
            ", sender_company='" + sender_company + '\'' +
            ", sender_phone='" + sender_phone + '\'' +
            ", sender_name='" + sender_name + '\'' +
            ", pay_approver='" + pay_approver + '\'' +
            ", finance_officer='" + finance_officer + '\'' +
            ", status='" + status + '\'' +
            ", start_time='" + start_time + '\'' +
            ", end_time='" + end_time + '\'' +
            ", truck_type='" + truck_type + '\'' +
            ", truck_count=" + truck_count +
            ", mobile_goods=" + mobile_goods +
            ", remark='" + remark + '\'' +
            ", transport_type='" + transport_type + '\'' +
            ", distance=" + distance +
            ", pickup_province='" + pickup_province + '\'' +
            ", pickup_city='" + pickup_city + '\'' +
            ", pickup_region='" + pickup_region + '\'' +
            ", pickup_street='" + pickup_street + '\'' +
            ", pickup_address='" + pickup_address + '\'' +
            ", pickup_location=" + pickup_location +
            ", pickup_start_time_format='" + pickup_start_time_format + '\'' +
            ", pickup_end_time_format='" + pickup_end_time_format + '\'' +
            ", pickup_name='" + pickup_name + '\'' +
            ", pickup_mobile_phone='" + pickup_mobile_phone + '\'' +
            ", pickup_tel_phone='" + pickup_tel_phone + '\'' +
            ", delivery_province='" + delivery_province + '\'' +
            ", delivery_city='" + delivery_city + '\'' +
            ", delivery_region='" + delivery_region + '\'' +
            ", delivery_street='" + delivery_street + '\'' +
            ", delivery_address='" + delivery_address + '\'' +
            ", delivery_location=" + delivery_location +
            ", delivery_start_time_format='" + delivery_start_time_format + '\'' +
            ", delivery_end_time_format='" + delivery_end_time_format + '\'' +
            ", delivery_name='" + delivery_name + '\'' +
            ", delivery_mobile_phone='" + delivery_mobile_phone + '\'' +
            ", delivery_tel_phone='" + delivery_tel_phone + '\'' +
            ", initiator_name='" + initiator_name + '\'' +
            ", initiator_phone='" + initiator_phone + '\'' +
            ", payment_top_rate=" + payment_top_rate +
            ", payment_top_cash_rate=" + payment_top_cash_rate +
            ", payment_top_card_rate=" + payment_top_card_rate +
            ", payment_tail_rate=" + payment_tail_rate +
            ", payment_tail_cash_rate=" + payment_tail_cash_rate +
            ", payment_tail_card_rate=" + payment_tail_card_rate +
            ", payment_last_rate=" + payment_last_rate +
            ", payment_last_cash_rate=" + payment_last_cash_rate +
            ", payment_last_card_rate=" + payment_last_card_rate +
            ", tender_type='" + tender_type + '\'' +
            ", lowest_protect_price=" + lowest_protect_price +
            ", highest_protect_price=" + highest_protect_price +
            ", lowest_grab_price=" + lowest_grab_price +
            ", highest_grab_price=" + highest_grab_price +
            ", grab_increment_price=" + grab_increment_price +
            ", current_grab_price=" + current_grab_price +
            ", driver_price=" + driver_price +
            '}';
  }
}
