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
  }

  public User() {
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

