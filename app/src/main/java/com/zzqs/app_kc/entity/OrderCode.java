package com.zzqs.app_kc.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lance on 15/5/20.
 */
public class OrderCode implements Parcelable {
    public static final String ORDER_CODE = "orderCode";
    private String code;
    private String scanTime;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getScanTime() {
        return scanTime;
    }

    public void setScanTime(String scanTime) {
        this.scanTime = scanTime;
    }

    @Override
    public String toString() {
        return "OrderCode{" +
                "code='" + code + '\'' +
                ", scanTime='" + scanTime + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.code);
        dest.writeString(this.scanTime);
    }

    public OrderCode() {
    }

    private OrderCode(Parcel in) {
        this.code = in.readString();
        this.scanTime = in.readString();
    }

    public static final Creator<OrderCode> CREATOR = new Creator<OrderCode>() {
        public OrderCode createFromParcel(Parcel source) {
            return new OrderCode(source);
        }

        public OrderCode[] newArray(int size) {
            return new OrderCode[size];
        }
    };
}
