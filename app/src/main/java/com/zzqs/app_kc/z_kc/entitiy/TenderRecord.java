package com.zzqs.app_kc.z_kc.entitiy;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ray on 2017/2/12.
 * Class name : TenderRecord
 * Description :
 */
public class TenderRecord implements Parcelable {
    @SerializedName("_id")
    private String record_id;
    private double price;
    private String driver;
    private String tender;
    private double price_per_ton;

    public String getRecord_id() {
        return record_id;
    }

    public void setRecord_id(String record_id) {
        this.record_id = record_id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getTender() {
        return tender;
    }

    public void setTender(String tender) {
        this.tender = tender;
    }

    public double getPrice_per_ton() {
        return price_per_ton;
    }

    public void setPrice_per_ton(double price_per_ton) {
        this.price_per_ton = price_per_ton;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.record_id);
        dest.writeDouble(this.price);
        dest.writeString(this.driver);
        dest.writeString(this.tender);
        dest.writeDouble(this.price_per_ton);
    }

    public TenderRecord() {
    }

    protected TenderRecord(Parcel in) {
        this.record_id = in.readString();
        this.price = in.readDouble();
        this.driver = in.readString();
        this.tender = in.readString();
        this.price_per_ton = in.readDouble();
    }

    public static final Parcelable.Creator<TenderRecord> CREATOR = new Parcelable.Creator<TenderRecord>() {
        @Override
        public TenderRecord createFromParcel(Parcel source) {
            return new TenderRecord(source);
        }

        @Override
        public TenderRecord[] newArray(int size) {
            return new TenderRecord[size];
        }
    };
}
