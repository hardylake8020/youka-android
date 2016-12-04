package com.zzqs.app_kc.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.zzqs.app_kc.db.hibernate.annotation.COLUMN;
import com.zzqs.app_kc.db.hibernate.annotation.ID;
import com.zzqs.app_kc.db.hibernate.annotation.TABLE;

@TABLE(name = "order_event")
public class OrderEvent implements Parcelable {
    public static final String ORDER_EVENT_ID = "order_event_id";
    @ID
    @COLUMN(name = "_id")
    private int _id;
    @COLUMN(name = "driver_id")
    private String driverId;
    @COLUMN(name = "order_id")
    private String orderId;//服务器运单ID
    @COLUMN(name = "content")
    private String content;//内容（货物名，缺失或损坏数）
    @COLUMN(name = "remark")
    private String remark;//备注
    @COLUMN(name = "address")
    private String address;//gps地址
    @COLUMN(name = "longitude")
    private double longitude;//经度
    @COLUMN(name = "latitude")
    private double latitude;//纬度
    @COLUMN(name = "event_id")
    private String eventId;//自定义id，作为事件的唯一标识符
    @COLUMN(name = "mold")
    private String mold;//类型 0 提货入场 1提货  2 交货入场  3交货 4中途事件 5运单确认
    public static final String MOLD_PICKUP_ENTRANCE = "pickupSign";
    public static final String MOLD_PICKUP = "pickup";
    public static final String MOLD_DELIVERY_ENTRANCE = "deliverySign";
    public static final String MOLD_DELIVERY = "delivery";
    public static final String MOLD_HALFWAY = "halfway";
    public static final String MOLD_CONFIRM = "confirm";
    @COLUMN(name = "status")
    private int status;//是否已上传 0未提交  1已经提交
    public static final int STATUS_NEW = 0;
    public static final int STATUS_COMMIT = 1;
    @COLUMN(name = "damaged")
    private boolean damaged;//是否有货损，弃用
    @COLUMN(name = "create_time")
    private String createTime;// create time(UTC): "yyyy-MM-dd HH:mm:ss"

    @COLUMN(name = "order_code")
    private String orderCode;//条码

    @COLUMN(name = "is_damaged")
    private int isDamaged;//是否有货损

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
//
//    public boolean isDamaged() {
//        return damaged;
//    }
//
//    public void setDamaged(boolean damaged) {
//        this.damaged = damaged;
//    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getMold() {
        return mold;
    }

    public void setMold(String mold) {
        this.mold = mold;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public int getIsDamaged() {
        return isDamaged;
    }

    public void setIsDamaged(int isDamaged) {
        this.isDamaged = isDamaged;
    }

    public OrderEvent() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this._id);
        dest.writeString(this.driverId);
        dest.writeString(this.orderId);
        dest.writeString(this.content);
        dest.writeString(this.remark);
        dest.writeString(this.address);
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
        dest.writeString(this.eventId);
        dest.writeString(this.mold);
        dest.writeInt(this.status);
        dest.writeByte(damaged ? (byte) 1 : (byte) 0);
        dest.writeString(this.createTime);
        dest.writeString(this.orderCode);
        dest.writeInt(this.isDamaged);
    }

    protected OrderEvent(Parcel in) {
        this._id = in.readInt();
        this.driverId = in.readString();
        this.orderId = in.readString();
        this.content = in.readString();
        this.remark = in.readString();
        this.address = in.readString();
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
        this.eventId = in.readString();
        this.mold = in.readString();
        this.status = in.readInt();
        this.damaged = in.readByte() != 0;
        this.createTime = in.readString();
        this.orderCode = in.readString();
        this.isDamaged = in.readInt();
    }

    public static final Creator<OrderEvent> CREATOR = new Creator<OrderEvent>() {
        public OrderEvent createFromParcel(Parcel source) {
            return new OrderEvent(source);
        }

        public OrderEvent[] newArray(int size) {
            return new OrderEvent[size];
        }
    };

    @Override
    public String toString() {
        return "OrderEvent{" +
                "_id=" + _id +
                ", driverId='" + driverId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", content='" + content + '\'' +
                ", remark='" + remark + '\'' +
                ", address='" + address + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", eventId='" + eventId + '\'' +
                ", mold='" + mold + '\'' +
                ", status=" + status +
                ", createTime='" + createTime + '\'' +
                ", orderCode='" + orderCode + '\'' +
                ", isDamaged=" + isDamaged +
                '}';
    }
}
