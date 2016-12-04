package com.zzqs.app_kc.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.zzqs.app_kc.db.hibernate.annotation.COLUMN;
import com.zzqs.app_kc.db.hibernate.annotation.ID;
import com.zzqs.app_kc.db.hibernate.annotation.TABLE;

/**
 * Created by lx on 12/10/14.
 */
@TABLE(name = "driver_trace")
public class DriverTrace implements Parcelable {

    @ID
    @COLUMN(name = "_id")
    private int _id;

    @COLUMN(name = "username")
    private String username;

    @COLUMN(name = "time")
    private String time;

    @COLUMN(name = "latitude")
    private double latitude;

    @COLUMN(name = "longitude")
    private double longitude;

    @COLUMN(name = "status")
    private int status;

    @COLUMN(name = "address")
    private String address;

    @COLUMN(name = "type")
    private String type;

    public final static String GPS = "gps";
    public final static String BASE_STATION = "base_station";
    public final static int STATUS_NEW = 0;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public int get_id() {
        return _id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this._id);
        dest.writeString(this.username);
        dest.writeString(this.time);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeInt(this.status);
        dest.writeString(this.address);
        dest.writeString(this.type);
    }

    public DriverTrace() {
    }

    private DriverTrace(Parcel in) {
        this._id = in.readInt();
        this.username = in.readString();
        this.time = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.status = in.readInt();
        this.address = in.readString();
        this.type = in.readString();
    }

    public static final Creator<DriverTrace> CREATOR = new Creator<DriverTrace>() {
        public DriverTrace createFromParcel(Parcel source) {
            return new DriverTrace(source);
        }

        public DriverTrace[] newArray(int size) {
            return new DriverTrace[size];
        }
    };

    @Override
    public String toString() {
        return "DriverTrace{" +
                "_id=" + _id +
                ", username='" + username + '\'' +
                ", time='" + time + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", status=" + status +
                ", address='" + address + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}