package com.zzqs.app_kc.z_kc.entitiy;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lance on 2016/12/3.
 */

public class KCOrder implements Parcelable {
  public static final String KC_ORDER = "kCOrder";
  public static final String NORMAL_ORDER = "normal_order";
  public static final String BIDDING_ORDER = "biddingOrder";

  @SerializedName("_id")
  private String order_id;
  private String create_time;
  private String over_time;
  private String remark;
  private String requirement_car_type;
  private int requirement_car_number;
  private String transport_type;
  private int distance;

  private String start_city;
  private String start_district;
  private String start_address;
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

  private String type;
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

  public String getOrder_id() {
    return order_id;
  }

  public void setOrder_id(String order_id) {
    this.order_id = order_id;
  }

  public String getCreate_time() {
    return create_time;
  }

  public void setCreate_time(String create_time) {
    this.create_time = create_time;
  }

  public String getOver_time() {
    return over_time;
  }

  public void setOver_time(String over_time) {
    this.over_time = over_time;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public String getRequirement_car_type() {
    return requirement_car_type;
  }

  public void setRequirement_car_type(String requirement_car_type) {
    this.requirement_car_type = requirement_car_type;
  }

  public int getRequirement_car_number() {
    return requirement_car_number;
  }

  public void setRequirement_car_number(int requirement_car_number) {
    this.requirement_car_number = requirement_car_number;
  }

  public String getStart_city() {
    return start_city;
  }

  public void setStart_city(String start_city) {
    this.start_city = start_city;
  }

  public String getStart_district() {
    return start_district;
  }

  public void setStart_district(String start_district) {
    this.start_district = start_district;
  }

  public String getStart_address() {
    return start_address;
  }

  public void setStart_address(String start_address) {
    this.start_address = start_address;
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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
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
    dest.writeString(this.order_id);
    dest.writeString(this.create_time);
    dest.writeString(this.over_time);
    dest.writeString(this.remark);
    dest.writeString(this.requirement_car_type);
    dest.writeInt(this.requirement_car_number);
    dest.writeString(this.transport_type);
    dest.writeInt(this.distance);
    dest.writeString(this.start_city);
    dest.writeString(this.start_district);
    dest.writeString(this.start_address);
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
    dest.writeString(this.type);
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

  public KCOrder() {
  }

  protected KCOrder(Parcel in) {
    this.order_id = in.readString();
    this.create_time = in.readString();
    this.over_time = in.readString();
    this.remark = in.readString();
    this.requirement_car_type = in.readString();
    this.requirement_car_number = in.readInt();
    this.transport_type = in.readString();
    this.distance = in.readInt();
    this.start_city = in.readString();
    this.start_district = in.readString();
    this.start_address = in.readString();
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
    this.type = in.readString();
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

  public static final Creator<KCOrder> CREATOR = new Creator<KCOrder>() {
    @Override
    public KCOrder createFromParcel(Parcel source) {
      return new KCOrder(source);
    }

    @Override
    public KCOrder[] newArray(int size) {
      return new KCOrder[size];
    }
  };
}
