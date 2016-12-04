package com.zzqs.app_kc.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.zzqs.app_kc.db.hibernate.annotation.COLUMN;
import com.zzqs.app_kc.db.hibernate.annotation.ID;
import com.zzqs.app_kc.db.hibernate.annotation.TABLE;

/**
 * Created by lance on 15/3/23.
 */
@TABLE(name = "company")
public class Company implements Parcelable {
    public static final String COMPANY = "company";
    @ID
    @COLUMN(name = "_id")
    private int _id;
    @COLUMN(name = "company_id")
    private String company_id;//公司id
    @COLUMN(name = "company_name")
    private String company_name;
    @COLUMN(name = "username")
    private String username;
    @COLUMN(name = "address")
    private String address;
    @COLUMN(name = "type")
    private String type;
    @COLUMN(name = "status")
    private int status;
    public static final int UN_ACCEPT = 1;
    public static final int ACCEPT = 2;

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this._id);
        dest.writeString(this.company_id);
        dest.writeString(this.company_name);
        dest.writeString(this.username);
        dest.writeString(this.address);
        dest.writeString(this.type);
        dest.writeInt(this.status);
    }

    public Company() {
    }

    private Company(Parcel in) {
        this._id = in.readInt();
        this.company_id = in.readString();
        this.company_name = in.readString();
        this.username = in.readString();
        this.address = in.readString();
        this.type = in.readString();
        this.status = in.readInt();
    }

    public static final Creator<Company> CREATOR = new Creator<Company>() {
        public Company createFromParcel(Parcel source) {
            return new Company(source);
        }

        public Company[] newArray(int size) {
            return new Company[size];
        }
    };

    @Override
    public String toString() {
        return "Company{" +
                "_id=" + _id +
                ", company_id='" + company_id + '\'' +
                ", company_name='" + company_name + '\'' +
                ", username='" + username + '\'' +
                ", address='" + address + '\'' +
                ", type='" + type + '\'' +
                ", status=" + status +
                '}';
    }
}
