package com.zzqs.app_kc.z_kc.entitiy;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ray on 2017/2/11.
 * Class name : WinnerDriver
 * Description :
 */
public class WinnerDriver implements Parcelable {
    @SerializedName("_id")
    private String driver_id;
    private String username;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.driver_id);
        dest.writeString(this.username);
    }

    public WinnerDriver() {
    }

    protected WinnerDriver(Parcel in) {
        this.driver_id = in.readString();
        this.username = in.readString();
    }

    public static final Parcelable.Creator<WinnerDriver> CREATOR = new Parcelable.Creator<WinnerDriver>() {
        @Override
        public WinnerDriver createFromParcel(Parcel source) {
            return new WinnerDriver(source);
        }

        @Override
        public WinnerDriver[] newArray(int size) {
            return new WinnerDriver[size];
        }
    };

    @Override
    public String toString() {
        return "WinnerDriver{" +
                "driver_id='" + driver_id + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
