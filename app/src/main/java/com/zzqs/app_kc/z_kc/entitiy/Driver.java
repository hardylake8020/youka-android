package com.zzqs.app_kc.z_kc.entitiy;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ray on 2017/6/28.
 * Class name : Driver
 * Description :
 */
public class Driver implements Parcelable {
    public static final String DRIVERS = "drivers";
    public static final String DRIVER = "driver";
    public static final String OWN="own";
    public static final String SEARCHE="search";
    @SerializedName("_id")
    private String driver_id;
    private String username;
    private String id_card_photo;//身份证
    private String truck_type;
    private String truck_number;
    private String truck_list_photo;
    private String truck_photo;//车辆照片
    private String plate_photo;//
    private String travel_id_photo;//行驶证照片
    private String driving_id_photo;//驾驶证照片
    private String bank_number_photo;//银行卡照片
    private String id_card_number;//身份证号
    private String nickname;
    private String photo;
    private String type;

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId_card_photo() {
        return id_card_photo;
    }

    public void setId_card_photo(String id_card_photo) {
        this.id_card_photo = id_card_photo;
    }

    public String getTruck_type() {
        return truck_type;
    }

    public void setTruck_type(String truck_type) {
        this.truck_type = truck_type;
    }

    public String getTruck_number() {
        return truck_number;
    }

    public void setTruck_number(String truck_number) {
        this.truck_number = truck_number;
    }

    public String getTruck_list_photo() {
        return truck_list_photo;
    }

    public void setTruck_list_photo(String truck_list_photo) {
        this.truck_list_photo = truck_list_photo;
    }

    public String getTruck_photo() {
        return truck_photo;
    }

    public void setTruck_photo(String truck_photo) {
        this.truck_photo = truck_photo;
    }

    public String getPlate_photo() {
        return plate_photo;
    }

    public void setPlate_photo(String plate_photo) {
        this.plate_photo = plate_photo;
    }

    public String getTravel_id_photo() {
        return travel_id_photo;
    }

    public void setTravel_id_photo(String travel_id_photo) {
        this.travel_id_photo = travel_id_photo;
    }

    public String getDriving_id_photo() {
        return driving_id_photo;
    }

    public void setDriving_id_photo(String driving_id_photo) {
        this.driving_id_photo = driving_id_photo;
    }

    public String getBank_number_photo() {
        return bank_number_photo;
    }

    public void setBank_number_photo(String bank_number_photo) {
        this.bank_number_photo = bank_number_photo;
    }

    public String getId_card_number() {
        return id_card_number;
    }

    public void setId_card_number(String id_card_number) {
        this.id_card_number = id_card_number;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Driver() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.driver_id);
        dest.writeString(this.username);
        dest.writeString(this.id_card_photo);
        dest.writeString(this.truck_type);
        dest.writeString(this.truck_number);
        dest.writeString(this.truck_list_photo);
        dest.writeString(this.truck_photo);
        dest.writeString(this.plate_photo);
        dest.writeString(this.travel_id_photo);
        dest.writeString(this.driving_id_photo);
        dest.writeString(this.bank_number_photo);
        dest.writeString(this.id_card_number);
        dest.writeString(this.nickname);
        dest.writeString(this.photo);
        dest.writeString(this.type);
    }

    protected Driver(Parcel in) {
        this.driver_id = in.readString();
        this.username = in.readString();
        this.id_card_photo = in.readString();
        this.truck_type = in.readString();
        this.truck_number = in.readString();
        this.truck_list_photo = in.readString();
        this.truck_photo = in.readString();
        this.plate_photo = in.readString();
        this.travel_id_photo = in.readString();
        this.driving_id_photo = in.readString();
        this.bank_number_photo = in.readString();
        this.id_card_number = in.readString();
        this.nickname = in.readString();
        this.photo = in.readString();
        this.type = in.readString();
    }

    public static final Creator<Driver> CREATOR = new Creator<Driver>() {
        @Override
        public Driver createFromParcel(Parcel source) {
            return new Driver(source);
        }

        @Override
        public Driver[] newArray(int size) {
            return new Driver[size];
        }
    };

    @Override
    public String toString() {
        return "Driver{" +
                "driver_id='" + driver_id + '\'' +
                ", username='" + username + '\'' +
                ", id_card_photo='" + id_card_photo + '\'' +
                ", truck_type='" + truck_type + '\'' +
                ", truck_number='" + truck_number + '\'' +
                ", truck_list_photo='" + truck_list_photo + '\'' +
                ", truck_photo='" + truck_photo + '\'' +
                ", plate_photo='" + plate_photo + '\'' +
                ", travel_id_photo='" + travel_id_photo + '\'' +
                ", driving_id_photo='" + driving_id_photo + '\'' +
                ", bank_number_photo='" + bank_number_photo + '\'' +
                ", id_card_number='" + id_card_number + '\'' +
                ", nickname='" + nickname + '\'' +
                ", photo='" + photo + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
