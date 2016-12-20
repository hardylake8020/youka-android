package com.zzqs.app_kc.z_kc.entitiy;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lance on 2016/12/3.
 */

public class Tender implements Parcelable {
  public static final String TENDER = "tender";
  public static final String TENDERS = "tenders";
  public static final String NORMAL_TENDER = "normal_tender";
  public static final String BIDDING_TENDER = "bidding_tender";

  @SerializedName("_id")
  private String tender_id;
  private String start_time;
  private String end_time;
  private String remark;
  private String truck_type;
  private int distance;

  private String pickup_province;
  private String pickup_city;
  private String pickup_region;
  private String pickup_street;
  private String pickup_address;
  private String pickup_start_time;
  private String pickup_end_time;


  private String end_city;
  private String end_district;
  private String end_address;
  private String delivery_start_time;
  private String delivery_end_time;

  private double total_quantity;
  private double total_volume;
  private double total_weight;
  private String quantity_unit;
  private String volume_unit;
  private String weight_unit;

  private String initiator;//发标方
  private String initiator_name;
  private String initiator_phone;

  private String tender_type;
  private double fixed_prince;

  private double my_prince;
  private double current_prince;
  private double max_prince;

  private double first_pay;
  private double last_pay;
  private double receipt_pay;
  private double first_pay_cash;
  private double first_pay_oil_card;
  private double last_pay_cash;
  private double last_pay_oil_card;
  private double receipt_pay_cash;
  private double receipt_pay_oil_card;

  public String getTender_id() {
    return tender_id;
  }

  public void setTender_id(String tender_id) {
    this.tender_id = tender_id;
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

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public String getTruck_type() {
    return truck_type;
  }

  public void setTruck_type(String truck_type) {
    this.truck_type = truck_type;
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

  public String getPickup_start_time() {
    return pickup_start_time;
  }

  public void setPickup_start_time(String pickup_start_time) {
    this.pickup_start_time = pickup_start_time;
  }

  public String getPickup_end_time() {
    return pickup_end_time;
  }

  public void setPickup_end_time(String pickup_end_time) {
    this.pickup_end_time = pickup_end_time;
  }

  public String getEnd_city() {
    return end_city;
  }

  public void setEnd_city(String end_city) {
    this.end_city = end_city;
  }

  public String getEnd_district() {
    return end_district;
  }

  public void setEnd_district(String end_district) {
    this.end_district = end_district;
  }

  public String getEnd_address() {
    return end_address;
  }

  public void setEnd_address(String end_address) {
    this.end_address = end_address;
  }

  public String getDelivery_start_time() {
    return delivery_start_time;
  }

  public void setDelivery_start_time(String delivery_start_time) {
    this.delivery_start_time = delivery_start_time;
  }

  public String getDelivery_end_time() {
    return delivery_end_time;
  }

  public void setDelivery_end_time(String delivery_end_time) {
    this.delivery_end_time = delivery_end_time;
  }

  public double getTotal_quantity() {
    return total_quantity;
  }

  public void setTotal_quantity(double total_quantity) {
    this.total_quantity = total_quantity;
  }

  public double getTotal_volume() {
    return total_volume;
  }

  public void setTotal_volume(double total_volume) {
    this.total_volume = total_volume;
  }

  public double getTotal_weight() {
    return total_weight;
  }

  public void setTotal_weight(double total_weight) {
    this.total_weight = total_weight;
  }

  public String getQuantity_unit() {
    return quantity_unit;
  }

  public void setQuantity_unit(String quantity_unit) {
    this.quantity_unit = quantity_unit;
  }

  public String getVolume_unit() {
    return volume_unit;
  }

  public void setVolume_unit(String volume_unit) {
    this.volume_unit = volume_unit;
  }

  public String getWeight_unit() {
    return weight_unit;
  }

  public void setWeight_unit(String weight_unit) {
    this.weight_unit = weight_unit;
  }

  public String getInitiator() {
    return initiator;
  }

  public void setInitiator(String initiator) {
    this.initiator = initiator;
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

  public String getTender_type() {
    return tender_type;
  }

  public void setTender_type(String tender_type) {
    this.tender_type = tender_type;
  }

  public double getFixed_prince() {
    return fixed_prince;
  }

  public void setFixed_prince(double fixed_prince) {
    this.fixed_prince = fixed_prince;
  }

  public double getMy_prince() {
    return my_prince;
  }

  public void setMy_prince(double my_prince) {
    this.my_prince = my_prince;
  }

  public double getCurrent_prince() {
    return current_prince;
  }

  public void setCurrent_prince(double current_prince) {
    this.current_prince = current_prince;
  }

  public double getMax_prince() {
    return max_prince;
  }

  public void setMax_prince(double max_prince) {
    this.max_prince = max_prince;
  }

  public int getDistance() {
    return distance;
  }

  public void setDistance(int distance) {
    this.distance = distance;
  }

  public double getFirst_pay() {
    return first_pay;
  }

  public void setFirst_pay(double first_pay) {
    this.first_pay = first_pay;
  }

  public double getLast_pay() {
    return last_pay;
  }

  public void setLast_pay(double last_pay) {
    this.last_pay = last_pay;
  }

  public double getReceipt_pay() {
    return receipt_pay;
  }

  public void setReceipt_pay(double receipt_pay) {
    this.receipt_pay = receipt_pay;
  }

  public double getFirst_pay_cash() {
    return first_pay_cash;
  }

  public void setFirst_pay_cash(double first_pay_cash) {
    this.first_pay_cash = first_pay_cash;
  }

  public double getFirst_pay_oil_card() {
    return first_pay_oil_card;
  }

  public void setFirst_pay_oil_card(double first_pay_oil_card) {
    this.first_pay_oil_card = first_pay_oil_card;
  }

  public double getLast_pay_cash() {
    return last_pay_cash;
  }

  public void setLast_pay_cash(double last_pay_cash) {
    this.last_pay_cash = last_pay_cash;
  }

  public double getLast_pay_oil_card() {
    return last_pay_oil_card;
  }

  public void setLast_pay_oil_card(double last_pay_oil_card) {
    this.last_pay_oil_card = last_pay_oil_card;
  }

  public double getReceipt_pay_cash() {
    return receipt_pay_cash;
  }

  public void setReceipt_pay_cash(double receipt_pay_cash) {
    this.receipt_pay_cash = receipt_pay_cash;
  }

  public double getReceipt_pay_oil_card() {
    return receipt_pay_oil_card;
  }

  public void setReceipt_pay_oil_card(double receipt_pay_oil_card) {
    this.receipt_pay_oil_card = receipt_pay_oil_card;
  }


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.tender_id);
    dest.writeString(this.start_time);
    dest.writeString(this.end_time);
    dest.writeString(this.remark);
    dest.writeString(this.truck_type);
    dest.writeInt(this.distance);
    dest.writeString(this.pickup_city);
    dest.writeString(this.pickup_region);
    dest.writeString(this.pickup_street);
    dest.writeString(this.pickup_start_time);
    dest.writeString(this.pickup_end_time);
    dest.writeString(this.end_city);
    dest.writeString(this.end_district);
    dest.writeString(this.end_address);
    dest.writeString(this.delivery_start_time);
    dest.writeString(this.delivery_end_time);
    dest.writeDouble(this.total_quantity);
    dest.writeDouble(this.total_volume);
    dest.writeDouble(this.total_weight);
    dest.writeString(this.quantity_unit);
    dest.writeString(this.volume_unit);
    dest.writeString(this.weight_unit);
    dest.writeString(this.initiator);
    dest.writeString(this.initiator_name);
    dest.writeString(this.initiator_phone);
    dest.writeString(this.tender_type);
    dest.writeDouble(this.fixed_prince);
    dest.writeDouble(this.my_prince);
    dest.writeDouble(this.current_prince);
    dest.writeDouble(this.max_prince);
    dest.writeDouble(this.first_pay);
    dest.writeDouble(this.last_pay);
    dest.writeDouble(this.receipt_pay);
    dest.writeDouble(this.first_pay_cash);
    dest.writeDouble(this.first_pay_oil_card);
    dest.writeDouble(this.last_pay_cash);
    dest.writeDouble(this.last_pay_oil_card);
    dest.writeDouble(this.receipt_pay_cash);
    dest.writeDouble(this.receipt_pay_oil_card);
  }

  public Tender() {
  }

  protected Tender(Parcel in) {
    this.tender_id = in.readString();
    this.start_time = in.readString();
    this.end_time = in.readString();
    this.remark = in.readString();
    this.truck_type = in.readString();
    this.distance = in.readInt();
    this.pickup_city = in.readString();
    this.pickup_region = in.readString();
    this.pickup_street = in.readString();
    this.pickup_start_time = in.readString();
    this.pickup_end_time = in.readString();
    this.end_city = in.readString();
    this.end_district = in.readString();
    this.end_address = in.readString();
    this.delivery_start_time = in.readString();
    this.delivery_end_time = in.readString();
    this.total_quantity = in.readDouble();
    this.total_volume = in.readDouble();
    this.total_weight = in.readDouble();
    this.quantity_unit = in.readString();
    this.volume_unit = in.readString();
    this.weight_unit = in.readString();
    this.initiator = in.readString();
    this.initiator_name = in.readString();
    this.initiator_phone = in.readString();
    this.tender_type = in.readString();
    this.fixed_prince = in.readDouble();
    this.my_prince = in.readDouble();
    this.current_prince = in.readDouble();
    this.max_prince = in.readDouble();
    this.first_pay = in.readDouble();
    this.last_pay = in.readDouble();
    this.receipt_pay = in.readDouble();
    this.first_pay_cash = in.readDouble();
    this.first_pay_oil_card = in.readDouble();
    this.last_pay_cash = in.readDouble();
    this.last_pay_oil_card = in.readDouble();
    this.receipt_pay_cash = in.readDouble();
    this.receipt_pay_oil_card = in.readDouble();
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
            ", start_time='" + start_time + '\'' +
            ", end_time='" + end_time + '\'' +
            ", remark='" + remark + '\'' +
            ", truck_type='" + truck_type + '\'' +
            ", distance=" + distance +
            ", pickup_city='" + pickup_city + '\'' +
            ", pickup_region='" + pickup_region + '\'' +
            ", pickup_street='" + pickup_street + '\'' +
            ", pickup_start_time='" + pickup_start_time + '\'' +
            ", pickup_end_time='" + pickup_end_time + '\'' +
            ", end_city='" + end_city + '\'' +
            ", end_district='" + end_district + '\'' +
            ", end_address='" + end_address + '\'' +
            ", delivery_start_time='" + delivery_start_time + '\'' +
            ", delivery_end_time='" + delivery_end_time + '\'' +
            ", total_quantity=" + total_quantity +
            ", total_volume=" + total_volume +
            ", total_weight=" + total_weight +
            ", quantity_unit='" + quantity_unit + '\'' +
            ", volume_unit='" + volume_unit + '\'' +
            ", weight_unit='" + weight_unit + '\'' +
            ", initiator='" + initiator + '\'' +
            ", initiator_name='" + initiator_name + '\'' +
            ", initiator_phone='" + initiator_phone + '\'' +
            ", tender_type='" + tender_type + '\'' +
            ", fixed_prince=" + fixed_prince +
            ", my_prince=" + my_prince +
            ", current_prince=" + current_prince +
            ", max_prince=" + max_prince +
            ", first_pay=" + first_pay +
            ", last_pay=" + last_pay +
            ", receipt_pay=" + receipt_pay +
            ", first_pay_cash=" + first_pay_cash +
            ", first_pay_oil_card=" + first_pay_oil_card +
            ", last_pay_cash=" + last_pay_cash +
            ", last_pay_oil_card=" + last_pay_oil_card +
            ", receipt_pay_cash=" + receipt_pay_cash +
            ", receipt_pay_oil_card=" + receipt_pay_oil_card +
            '}';
  }
}
