package com.zzqs.app_kc.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.zzqs.app_kc.db.hibernate.annotation.COLUMN;
import com.zzqs.app_kc.db.hibernate.annotation.ID;
import com.zzqs.app_kc.db.hibernate.annotation.TABLE;

/**
 * Created by lance on 15/3/21.
 */
@TABLE(name = "user")
public class User implements Parcelable {
    public static final String USER = "user";
    @ID
    @COLUMN(name = "_id")
    private int _id;
    @COLUMN(name = "username")
    private String username;//当前用户id
    @COLUMN(name = "nickname")
    private String nickname;//用户名字
    @COLUMN(name = "phone")
    private String phone;//当前用户手机号
    @COLUMN(name = "photo")
    private String photo;//头像
    @COLUMN(name = "local_photo")
    private String localPhoto;//头像文件的本地路径
    @COLUMN(name = "id_card_photo")
    private String idCardPhoto;//身份证图片
    @COLUMN(name = "local_id_card_photo")
    private String localIdCardPhoto;
    @COLUMN(name = "driving_id_photo")
    private String drivingIdPhoto;//驾驶证图片
    @COLUMN(name = "local_driving_id_photo")
    private String localDrivingIdPhoto;
    @COLUMN(name = "travel_id_photo")
    private String travelIdPhoto;//行驶证图片
    @COLUMN(name = "local_travel_id_photo")
    private String localTravelIdPhoto;
    @COLUMN(name = "trading_id_photo")
    private String tradingIdPhoto;//营运证图片
    @COLUMN(name = "local_trading_id_photo")
    private String localTradingIdPhoto;
    @COLUMN(name = "plate_photo")
    private String platePhotos;//车牌照片,多个车牌以;隔开
    @COLUMN(name = "local_plate_photo")
    private String localPlatePhotos;
    @COLUMN(name = "plate_number")
    private String plateNumbers;//车牌号，多个车牌以;隔开
    @COLUMN(name = "birthday")
    private String birthday;//生日
    @COLUMN(name = "driving_date")
    private String drivingDate;//驾照获取日期
    @COLUMN(name = "device_id")
    private String device_id;
    @COLUMN(name = "rate")
    private int rate;

    @COLUMN(name = "account_balance")
    private double accountBalance;//账户余额
    @COLUMN(name = "cash_deposit")
    private double cashDeposit;//保证金

    @COLUMN(name = "driver_id")
    private String driver_id;

    @COLUMN(name = "truck_type")//车型
    private String truck_type;//
    @COLUMN(name = "truck_number")//车牌
    private String truck_number;
    @COLUMN(name = "truck_photo")//车辆照片
    private String truck_photo;
    @COLUMN(name = "truck_list_photo")//
    private String truck_list_photo;
    @COLUMN(name = "bank_username")//开户名
    private String bank_username;
    @COLUMN(name = "bank_name")//开户行
    private String bank_name;
    @COLUMN(name = "bank_number_photo")//银行卡照片
    private String bank_number_photo;
    @COLUMN(name = "bank_number")//银行卡号
    private String bank_number;
    @COLUMN(name = "id_card_number")//身份证号
    private String id_card_number;
    @COLUMN(name = "car_plate_photo")//车牌照片
    private String car_plate_photo;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getLocalPhoto() {
        return localPhoto;
    }

    public void setLocalPhoto(String localPhoto) {
        this.localPhoto = localPhoto;
    }

    public String getIdCardPhoto() {
        return idCardPhoto;
    }

    public void setIdCardPhoto(String idCardPhoto) {
        this.idCardPhoto = idCardPhoto;
    }

    public String getLocalIdCardPhoto() {
        return localIdCardPhoto;
    }

    public void setLocalIdCardPhoto(String localIdCardPhoto) {
        this.localIdCardPhoto = localIdCardPhoto;
    }

    public String getDrivingIdPhoto() {
        return drivingIdPhoto;
    }

    public void setDrivingIdPhoto(String drivingIdPhoto) {
        this.drivingIdPhoto = drivingIdPhoto;
    }

    public String getLocalDrivingIdPhoto() {
        return localDrivingIdPhoto;
    }

    public void setLocalDrivingIdPhoto(String localdrivingIdPhoto) {
        this.localDrivingIdPhoto = localdrivingIdPhoto;
    }

    public String getTravelIdPhoto() {
        return travelIdPhoto;
    }

    public void setTravelIdPhoto(String travelIdPhoto) {
        this.travelIdPhoto = travelIdPhoto;
    }

    public String getLocalTravelIdPhoto() {
        return localTravelIdPhoto;
    }

    public void setLocalTravelIdPhoto(String localTravelIdPhoto) {
        this.localTravelIdPhoto = localTravelIdPhoto;
    }

    public String getTradingIdPhoto() {
        return tradingIdPhoto;
    }

    public void setTradingIdPhoto(String tradingIdPhoto) {
        this.tradingIdPhoto = tradingIdPhoto;
    }

    public String getLocalTradingIdPhoto() {
        return localTradingIdPhoto;
    }

    public void setLocalTradingIdPhoto(String localTradingIdPhoto) {
        this.localTradingIdPhoto = localTradingIdPhoto;
    }

    public String getPlatePhotos() {
        return platePhotos;
    }

    public void setPlatePhotos(String platePhotos) {
        this.platePhotos = platePhotos;
    }

    public String getLocalPlatePhotos() {
        return localPlatePhotos;
    }

    public void setLocalPlatePhotos(String localPlatePhotos) {
        this.localPlatePhotos = localPlatePhotos;
    }

    public String getPlateNumbers() {
        return plateNumbers;
    }

    public void setPlateNumbers(String plateNumbers) {
        this.plateNumbers = plateNumbers;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getDrivingDate() {
        return drivingDate;
    }

    public void setDrivingDate(String drivingDate) {
        this.drivingDate = drivingDate;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public double getCashDeposit() {
        return cashDeposit;
    }

    public void setCashDeposit(double cashDeposit) {
        this.cashDeposit = cashDeposit;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
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

    public String getTruck_photo() {
        return truck_photo;
    }

    public void setTruck_photo(String truck_photo) {
        this.truck_photo = truck_photo;
    }

    public String getTruck_list_photo() {
        return truck_list_photo;
    }

    public void setTruck_list_photo(String truck_list_photo) {
        this.truck_list_photo = truck_list_photo;
    }

    public String getBank_username() {
        return bank_username;
    }

    public void setBank_username(String bank_username) {
        this.bank_username = bank_username;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getBank_number_photo() {
        return bank_number_photo;
    }

    public void setBank_number_photo(String bank_number_photo) {
        this.bank_number_photo = bank_number_photo;
    }

    public String getBank_number() {
        return bank_number;
    }

    public void setBank_number(String bank_number) {
        this.bank_number = bank_number;
    }

    public String getId_card_number() {
        return id_card_number;
    }

    public void setId_card_number(String id_card_number) {
        this.id_card_number = id_card_number;
    }

    public String getCar_plate_photo() {
        return car_plate_photo;
    }

    public void setCar_plate_photo(String car_plate_photo) {
        this.car_plate_photo = car_plate_photo;
    }

    public User() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this._id);
        dest.writeString(this.username);
        dest.writeString(this.nickname);
        dest.writeString(this.phone);
        dest.writeString(this.photo);
        dest.writeString(this.localPhoto);
        dest.writeString(this.idCardPhoto);
        dest.writeString(this.localIdCardPhoto);
        dest.writeString(this.drivingIdPhoto);
        dest.writeString(this.localDrivingIdPhoto);
        dest.writeString(this.travelIdPhoto);
        dest.writeString(this.localTravelIdPhoto);
        dest.writeString(this.tradingIdPhoto);
        dest.writeString(this.localTradingIdPhoto);
        dest.writeString(this.platePhotos);
        dest.writeString(this.localPlatePhotos);
        dest.writeString(this.plateNumbers);
        dest.writeString(this.birthday);
        dest.writeString(this.drivingDate);
        dest.writeString(this.device_id);
        dest.writeInt(this.rate);
        dest.writeDouble(this.accountBalance);
        dest.writeDouble(this.cashDeposit);
        dest.writeString(this.driver_id);
        dest.writeString(this.truck_type);
        dest.writeString(this.truck_number);
        dest.writeString(this.truck_photo);
        dest.writeString(this.truck_list_photo);
        dest.writeString(this.bank_username);
        dest.writeString(this.bank_name);
        dest.writeString(this.bank_number_photo);
        dest.writeString(this.bank_number);
        dest.writeString(this.id_card_number);
        dest.writeString(this.car_plate_photo);
    }

    protected User(Parcel in) {
        this._id = in.readInt();
        this.username = in.readString();
        this.nickname = in.readString();
        this.phone = in.readString();
        this.photo = in.readString();
        this.localPhoto = in.readString();
        this.idCardPhoto = in.readString();
        this.localIdCardPhoto = in.readString();
        this.drivingIdPhoto = in.readString();
        this.localDrivingIdPhoto = in.readString();
        this.travelIdPhoto = in.readString();
        this.localTravelIdPhoto = in.readString();
        this.tradingIdPhoto = in.readString();
        this.localTradingIdPhoto = in.readString();
        this.platePhotos = in.readString();
        this.localPlatePhotos = in.readString();
        this.plateNumbers = in.readString();
        this.birthday = in.readString();
        this.drivingDate = in.readString();
        this.device_id = in.readString();
        this.rate = in.readInt();
        this.accountBalance = in.readDouble();
        this.cashDeposit = in.readDouble();
        this.driver_id = in.readString();
        this.truck_type = in.readString();
        this.truck_number = in.readString();
        this.truck_photo = in.readString();
        this.truck_list_photo = in.readString();
        this.bank_username = in.readString();
        this.bank_name = in.readString();
        this.bank_number_photo = in.readString();
        this.bank_number = in.readString();
        this.id_card_number = in.readString();
        this.car_plate_photo = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}

