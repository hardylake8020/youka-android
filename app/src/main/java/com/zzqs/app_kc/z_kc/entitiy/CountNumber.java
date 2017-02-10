package com.zzqs.app_kc.z_kc.entitiy;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ray on 2017/1/14.
 * Class name : CountNumber
 * Description :
 */
public class CountNumber implements Parcelable {
    private int tender_count;
    private int order_count;

    public int getTender_count() {
        return tender_count;
    }

    public void setTender_count(int tender_count) {
        this.tender_count = tender_count;
    }

    public int getOrder_count() {
        return order_count;
    }

    public void setOrder_count(int order_count) {
        this.order_count = order_count;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.tender_count);
        dest.writeInt(this.order_count);
    }

    public CountNumber() {
    }

    protected CountNumber(Parcel in) {
        this.tender_count = in.readInt();
        this.order_count = in.readInt();
    }

    public static final Parcelable.Creator<CountNumber> CREATOR = new Parcelable.Creator<CountNumber>() {
        @Override
        public CountNumber createFromParcel(Parcel source) {
            return new CountNumber(source);
        }

        @Override
        public CountNumber[] newArray(int size) {
            return new CountNumber[size];
        }
    };

    @Override
    public String toString() {
        return "CountNumber{" +
                "tender_count=" + tender_count +
                ", order_count=" + order_count +
                '}';
    }
}
