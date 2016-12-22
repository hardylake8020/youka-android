package com.zzqs.app_kc.z_kc.entitiy;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ray on 2016/12/22.
 * Class name : OilCard
 * Description :油卡对象
 */
public class OilCard implements Parcelable {
    public static final String UNETC = "unEtc";
    public static final String ETC = "etc";

    @SerializedName("_id")
    private String card_id;
    private String number;
    private String type;
    private String created;
    private String updated;
    private String owner;
    private String truck_number;

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTruck_number() {
        return truck_number;
    }

    public void setTruck_number(String truck_number) {
        this.truck_number = truck_number;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.card_id);
        dest.writeString(this.number);
        dest.writeString(this.type);
        dest.writeString(this.created);
        dest.writeString(this.updated);
        dest.writeString(this.owner);
        dest.writeString(this.truck_number);
    }

    public OilCard() {
    }

    protected OilCard(Parcel in) {
        this.card_id = in.readString();
        this.number = in.readString();
        this.type = in.readString();
        this.created = in.readString();
        this.updated = in.readString();
        this.owner = in.readString();
        this.truck_number = in.readString();
    }

    public static final Creator<OilCard> CREATOR = new Creator<OilCard>() {
        @Override
        public OilCard createFromParcel(Parcel source) {
            return new OilCard(source);
        }

        @Override
        public OilCard[] newArray(int size) {
            return new OilCard[size];
        }
    };

    @Override
    public String toString() {
        return "OilCard{" +
                "card_id='" + card_id + '\'' +
                ", number='" + number + '\'' +
                ", type='" + type + '\'' +
                ", created='" + created + '\'' +
                ", updated='" + updated + '\'' +
                ", owner='" + owner + '\'' +
                ", truck_number='" + truck_number + '\'' +
                '}';
    }
}
