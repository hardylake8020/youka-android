package com.zzqs.app_kc.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.zzqs.app_kc.db.hibernate.annotation.COLUMN;
import com.zzqs.app_kc.db.hibernate.annotation.ID;
import com.zzqs.app_kc.db.hibernate.annotation.TABLE;

/**
 * Created by lance on 15/3/26.
 */
@TABLE(name = "event_file")
public class EventFile implements Parcelable {

    public static final String DAMAGE = "货损";
    public static final String PICKUP_ENTER = "提货进场";
    public static final String PICKUP = "提货";
    public static final String DELIVERY_ENTER = "交货进场";
    public static final String DELIVERY = "交货";
    public static final String HALF_WAY = "中途事件";
    public static final String DEFAULT = "追加";

    @ID
    @COLUMN(name = "_id")
    private int _id;

    @COLUMN(name = "file_path")
    private String filePath;//sd卡路径

    @COLUMN(name = "big_file_path")
    private String bigFilePath;

    @COLUMN(name = "url")
    private String url;

    @COLUMN(name = "order_id")
    private String orderId;
    @COLUMN(name = "event_id")
    private int eventId;//本地数据库orderEvent的id
    @COLUMN(name = "mold")
    private int mold;// 1货物照片 2单据照片 3录音 4其他类型照片 5货损照片 6配置要求拍摄的照片 7普通照片

    public static final int MOLD_NORMAL_PHOTO = 7;
    public static final int MOLD_CONFIG_PHOTO = 6;
    public static final int MOLD_DAMAGE_PHOTO = 5;
    public static final int MOLD_OTHER_PHOTO = 4;
    public static final int MOLD_VOICE = 3;
    public static final int MOLD_CREDENTIAL_PHOTO = 2;
    public static final int MOLD_GOODS_PHOTO = 1;

    @COLUMN(name = "status")//99没有上传 100已经上传 101 本地图片找寻不到
    private int status;
    public final static int STATUS_NEW = 99;
    public final static int STATUS_COMMIT = 100;
    public final static int STATUS_UNFIND = 101;
    public final static String ZZQS_CONFIG_PHOTO = "zzqs_config_photo";
    @COLUMN(name = "config_name")
    private String configName;//配置中图片的名字

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    public int getMold() {
        return mold;
    }

    public void setMold(int mold) {
        this.mold = mold;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getBigFilePath() {
        return bigFilePath;
    }

    public void setBigFilePath(String bigFilePath) {
        this.bigFilePath = bigFilePath;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this._id);
        dest.writeString(this.filePath);
        dest.writeString(this.url);
        dest.writeString(this.bigFilePath);
        dest.writeString(this.orderId);
        dest.writeInt(this.eventId);
        dest.writeInt(this.mold);
        dest.writeInt(this.status);
        dest.writeString(this.configName);
    }

    public EventFile() {
    }

    protected EventFile(Parcel in) {
        this._id = in.readInt();
        this.filePath = in.readString();
        this.url = in.readString();
        this.bigFilePath = in.readString();
        this.orderId = in.readString();
        this.eventId = in.readInt();
        this.mold = in.readInt();
        this.status = in.readInt();
        this.configName = in.readString();
    }

    public static final Creator<EventFile> CREATOR = new Creator<EventFile>() {
        @Override
        public EventFile createFromParcel(Parcel source) {
            return new EventFile(source);
        }

        @Override
        public EventFile[] newArray(int size) {
            return new EventFile[size];
        }
    };

    @Override
    public String toString() {
        return "EventFile{" +
                "_id=" + _id +
                ", filePath='" + filePath + '\'' +
                ", bigFilePath='" + bigFilePath + '\'' +
                ", url='" + url + '\'' +
                ", orderId='" + orderId + '\'' +
                ", eventId=" + eventId +
                ", mold=" + mold +
                ", status=" + status +
                ", configName='" + configName + '\'' +
                '}';
    }
}

