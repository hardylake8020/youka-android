package com.zzqs.app_kc.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.zzqs.app_kc.db.hibernate.annotation.COLUMN;
import com.zzqs.app_kc.db.hibernate.annotation.ID;
import com.zzqs.app_kc.db.hibernate.annotation.TABLE;

@TABLE(name = "zz_order")
public class Order implements Parcelable {
    public static final String ORDER_ID = "order_id";
    public static final String ORDER_IDS = "order_ids";
    public static final String ORDER = "order";
    public static final String ORDER_NOT_EXIST = "order_not_exist";
    public static final String ORDER_STATUS_HAS_CHANGED = "order_status_has_changed";
    public static final String DRIVER_ORDER = "driver";
    public static final String WAREHOUSE_ORDER = "warehouse";
    public static final String UN_PICKUP_ENTRANCE = "unPickupSigned";
    public static final String UN_PICKUP = "unPickuped";
    public static final String UN_DELIVERY_ENTRANCE = "unDeliverySigned";
    public static final String UN_DELIVERY = "unDeliveried";
    public static final String STATUS_COMMIT = "completed";
    public static final String STATUS_INVALID = "invaild";
    public static final int NEW = 100;
    public static final int UPDATE = 150;//是否被更新了
    public static final int EXIST = 200;
    @ID
    @COLUMN(name = "_id")
    private int _id;//编号

    @COLUMN(name = "order_id")
    private String orderId;

    @COLUMN(name = "driver_id")
    private String driverId;

    @COLUMN(name = "serial_no")
    private String serialNo;//编号

    @COLUMN(name = "ref_num")
    private String refNum;//参考单号

    @COLUMN(name = "goods_name")
    private String goodsName;//品名

    @COLUMN(name = "remark")
    private String remark;

    @COLUMN(name = "total_weight")
    private String totalWeight;//重量

    @COLUMN(name = "total_volume")
    private String totalVolume;//体积

    @COLUMN(name = "total_quantity")
    private String totalQuantity;//数量

    @COLUMN(name = "quantity_unit")
    private String quantityUnit;

    @COLUMN(name = "volume_unit")
    private String volumeUnit;

    @COLUMN(name = "weight_unit")
    private String weightUnit;

    @COLUMN(name = "pickup_time_start")
    private String pickupTimeStart;

    @COLUMN(name = "pickup_time_end")
    private String pickupTimeEnd;

    @COLUMN(name = "delivery_time_start")
    private String deliverTimeStart;

    @COLUMN(name = "delivery_time_end")
    private String deliverTimeEnd;

    @COLUMN(name = "from_address")
    private String fromAddress;//发起地址

    @COLUMN(name = "from_contact")
    private String fromContact;

    @COLUMN(name = "from_mobile_phone")
    private String fromMobilePhone;

    @COLUMN(name = "from_phone")
    private String fromPhone;

    @COLUMN(name = "from_workdays")
    private String fromWorkDays;

    @COLUMN(name = "to_address")
    private String toAddress;//目标地址

    @COLUMN(name = "to_contact")
    private String toContact;

    @COLUMN(name = "to_mobile_phone")
    private String toMobilePhone;

    @COLUMN(name = "to_phone")
    private String toPhone;

    @COLUMN(name = "to_workdays")
    private String toWorkDays;

    @COLUMN(name = "create_time")
    private String createTime;//发布时间

    @COLUMN(name = "pickup_time")
    private String pickupTime;
    @COLUMN(name = "pickup_entrance_time")
    private String pickupEntranceTime;//提货入场时间

    @COLUMN(name = "delivery_time")
    private String deliveryTime;
    @COLUMN(name = "delivery_entrance_time")
    private String deliveryEntranceTime;//交货入场时间

    @COLUMN(name = "receiver_name")
    private String receiverName;//收货方

    @COLUMN(name = "sender_name")
    private String senderName;//发货方

    @COLUMN(name = "description")
    private String description;//创建运单时的备注

    @COLUMN(name = "status")
    private String status;// 待提货入场 待提货 待交货入场 待交货 运单完成
    @COLUMN(name = "is_new")
    private int isNew;//是否查看过
    @COLUMN(name = "damaged")
    private String damaged;//是否有货损
    @COLUMN(name = "order_type")
    private String orderType;//运单类型
    @COLUMN(name = "update_time")
    private String updateTime;//运单更新时间
    @COLUMN(name = "road_order_name")
    private String roadOrderName;//路单名
    @COLUMN(name = "order_detail_id")
    private String orderDetailId;//二维码检测依据ID

    /**
     * 实际交货
     */
    @COLUMN(name = "actual_delivery_goodsName")
    private String actualDeliveryGoodsName;
    @COLUMN(name = "actual_delivery_quantity")
    private String actualDeliveryQuantity;
    @COLUMN(name = "actual_delivery_volume")
    private String actualDeliveryVolume;
    @COLUMN(name = "actual_delivery_weight")
    private String actualDeliveryWeight;

    /**
     * 多货物，用于比较的属性
     */
    @COLUMN(name = "actual_goods_id")
    private String actualGoodsId;
    @COLUMN(name = "actual_goodsName")
    private String actualGoodsName;
    @COLUMN(name = "actual_goodsUnit")
    private String actualGoodsUnit;
    @COLUMN(name = "actual_goodsCount")
    private String actualGoodsCount;
    @COLUMN(name = "actual_hasLack")
    private String actualHasLack;
    @COLUMN(name = "actual_hasDamage")
    private String actualHasDamage;
    @COLUMN(name = "actual_price")
    private String actualPrice;
    @COLUMN(name = "actual_goodsCount2")
    private String actualGoodsCount2;
    @COLUMN(name = "actual_goodsCount3")
    private String actualGoodsCount3;
    @COLUMN(name = "actual_goodsUnit2")
    private String actualGoodsUnit2;
    @COLUMN(name = "actual_goodsUnit3")
    private String actualGoodsUnit3;
    /**
     * 多货物，用于操作的属性
     */

    @COLUMN(name = "operation_id")
    private String operationId;
    @COLUMN(name = "operation_goodsName")
    private String operationGoodsName;
    @COLUMN(name = "operation_goodsUnit")
    private String operationGoodsUnit;
    @COLUMN(name = "operation_goodsCount")
    private String operationGoodsCount;
    @COLUMN(name = "operation_hasLack")
    private String operationHasLack;
    @COLUMN(name = "operation_hasDamage")
    private String operationHasDamage;
    @COLUMN(name = "operation_price")
    private String operationPrice;
    /**
     * 运单强制性要求参数配置
     */
    @COLUMN(name = "config_id")
    private String configId;//配置的ID
    @COLUMN(name = "pickup_entrance_force")
    private String pickupEntranceForce;//是否强制进行提货进场操作
    @COLUMN(name = "pickup_entrance_photos")
    private String pickupEntrancePhotos;//强制要求提货进场时拍摄的图片
    @COLUMN(name = "pickup_photo_force")
    private String pickupPhotoForce;//是否强制要求提货拍照
    @COLUMN(name = "pickup_take_photos")
    private String pickupTakePhotos;//强制提货所需要拍摄的照片
    @COLUMN(name = "pickup_must_confirm_detail")
    private String pickupMustConfirmDetail;//是否强制要求提货填写实收
    @COLUMN(name = "commit_pickup_config_detail")
    private String commitPickupConfigDetail;//是否进行了提货实收

    @COLUMN(name = "delivery_entrance_force")
    private String deliveryEntranceForce;//是否强制进行交货进场操作
    @COLUMN(name = "delivery_entrance_photos")
    private String deliveryEntrancePhotos;//强制要求交货进场时拍摄的图片
    @COLUMN(name = "delivery_photo_force")
    private String deliveryPhotoForce;//是否强制要求交货拍照
    @COLUMN(name = "delivery_take_photos")
    private String deliveryTakePhotos;//强制交货所需要拍摄的照片
    @COLUMN(name = "delivery_must_confirm_detail")
    private String deliveryMustConfirmDetail;//是否强制要求交货填写实收
    @COLUMN(name = "commit_delivery_config_detail")
    private String commitDeliveryConfigDetail;//是否进行了交货实收

    @COLUMN(name = "confirm_status")
    private String confirmStatus;//是否进行了新运单确认,confirm_status un_confirmed

    public static final String CONFIRMED = "confirmed";
    public static final String UN_CONFIRMED = "un_confirmed";

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getRefNum() {
        return refNum;
    }

    public void setRefNum(String refNum) {
        this.refNum = refNum;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(String totalWeight) {
        this.totalWeight = totalWeight;
    }

    public String getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(String totalVolume) {
        this.totalVolume = totalVolume;
    }

    public String getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(String totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getQuantityUnit() {
        return quantityUnit;
    }

    public void setQuantityUnit(String quantityUnit) {
        this.quantityUnit = quantityUnit;
    }

    public String getVolumeUnit() {
        return volumeUnit;
    }

    public void setVolumeUnit(String volumeUnit) {
        this.volumeUnit = volumeUnit;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }

    public String getPickupTimeStart() {
        return pickupTimeStart;
    }

    public void setPickupTimeStart(String pickupTimeStart) {
        this.pickupTimeStart = pickupTimeStart;
    }

    public String getPickupTimeEnd() {
        return pickupTimeEnd;
    }

    public void setPickupTimeEnd(String pickupTimeEnd) {
        this.pickupTimeEnd = pickupTimeEnd;
    }

    public String getDeliverTimeStart() {
        return deliverTimeStart;
    }

    public void setDeliverTimeStart(String deliverTimeStart) {
        this.deliverTimeStart = deliverTimeStart;
    }

    public String getDeliverTimeEnd() {
        return deliverTimeEnd;
    }

    public void setDeliverTimeEnd(String deliverTimeEnd) {
        this.deliverTimeEnd = deliverTimeEnd;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getFromContact() {
        return fromContact;
    }

    public void setFromContact(String fromContact) {
        this.fromContact = fromContact;
    }

    public String getFromMobilePhone() {
        return fromMobilePhone;
    }

    public void setFromMobilePhone(String fromMobilePhone) {
        this.fromMobilePhone = fromMobilePhone;
    }

    public String getFromPhone() {
        return fromPhone;
    }

    public void setFromPhone(String fromPhone) {
        this.fromPhone = fromPhone;
    }

    public String getFromWorkDays() {
        return fromWorkDays;
    }

    public void setFromWorkDays(String fromWorkDays) {
        this.fromWorkDays = fromWorkDays;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getToContact() {
        return toContact;
    }

    public void setToContact(String toContact) {
        this.toContact = toContact;
    }

    public String getToMobilePhone() {
        return toMobilePhone;
    }

    public void setToMobilePhone(String toMobilePhone) {
        this.toMobilePhone = toMobilePhone;
    }

    public String getToPhone() {
        return toPhone;
    }

    public void setToPhone(String toPhone) {
        this.toPhone = toPhone;
    }

    public String getToWorkDays() {
        return toWorkDays;
    }

    public void setToWorkDays(String toWorkDays) {
        this.toWorkDays = toWorkDays;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public String getPickupEntranceTime() {
        return pickupEntranceTime;
    }

    public void setPickupEntranceTime(String pickupEntranceTime) {
        this.pickupEntranceTime = pickupEntranceTime;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getDeliveryEntranceTime() {
        return deliveryEntranceTime;
    }

    public void setDeliveryEntranceTime(String deliveryEntranceTime) {
        this.deliveryEntranceTime = deliveryEntranceTime;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getIsNew() {
        return isNew;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }

    public String getDamaged() {
        return damaged;
    }

    public void setDamaged(String damaged) {
        this.damaged = damaged;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getRoadOrderName() {
        return roadOrderName;
    }

    public void setRoadOrderName(String roadOrderName) {
        this.roadOrderName = roadOrderName;
    }

    public String getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(String orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

//    public String getActualDeliveryGoodsName() {
//        return actualDeliveryGoodsName;
//    }
//
//    public void setActualDeliveryGoodsName(String actualDeliveryGoodsName) {
//        this.actualDeliveryGoodsName = actualDeliveryGoodsName;
//    }

//    public String getActualDeliveryQuantity() {
//        return actualDeliveryQuantity;
//    }
//
//    public void setActualDeliveryQuantity(String actualDeliveryQuantity) {
//        this.actualDeliveryQuantity = actualDeliveryQuantity;
//    }

//    public String getActualDeliveryVolume() {
//        return actualDeliveryVolume;
//    }
//
//    public void setActualDeliveryVolume(String actualDeliveryVolume) {
//        this.actualDeliveryVolume = actualDeliveryVolume;
//    }

//    public String getActualDeliveryWeight() {
//        return actualDeliveryWeight;
//    }
//
//    public void setActualDeliveryWeight(String actualDeliveryWeight) {
//        this.actualDeliveryWeight = actualDeliveryWeight;
//    }

    public String getActualGoodsId() {
        return actualGoodsId;
    }

    public void setActualGoodsId(String actualGoodsId) {
        this.actualGoodsId = actualGoodsId;
    }

    public String getActualGoodsName() {
        return actualGoodsName;
    }

    public void setActualGoodsName(String actualGoodsName) {
        this.actualGoodsName = actualGoodsName;
    }

    public String getActualGoodsUnit() {
        return actualGoodsUnit;
    }

    public void setActualGoodsUnit(String actualGoodsUnit) {
        this.actualGoodsUnit = actualGoodsUnit;
    }

    public String getActualGoodsCount() {
        return actualGoodsCount;
    }

    public void setActualGoodsCount(String actualGoodsCount) {
        this.actualGoodsCount = actualGoodsCount;
    }

    public String getActualHasLack() {
        return actualHasLack;
    }

    public void setActualHasLack(String actualHasLack) {
        this.actualHasLack = actualHasLack;
    }

    public String getActualHasDamage() {
        return actualHasDamage;
    }

    public void setActualHasDamage(String actualHasDamage) {
        this.actualHasDamage = actualHasDamage;
    }

    public String getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(String actualPrice) {
        this.actualPrice = actualPrice;
    }

    public String getActualGoodsCount2() {
        return actualGoodsCount2;
    }

    public void setActualGoodsCount2(String actualGoodsCount2) {
        this.actualGoodsCount2 = actualGoodsCount2;
    }

    public String getActualGoodsCount3() {
        return actualGoodsCount3;
    }

    public void setActualGoodsCount3(String actualGoodsCount3) {
        this.actualGoodsCount3 = actualGoodsCount3;
    }

    public String getActualGoodsUnit2() {
        return actualGoodsUnit2;
    }

    public void setActualGoodsUnit2(String actualGoodsUnit2) {
        this.actualGoodsUnit2 = actualGoodsUnit2;
    }

    public String getActualGoodsUnit3() {
        return actualGoodsUnit3;
    }

    public void setActualGoodsUnit3(String actualGoodsUnit3) {
        this.actualGoodsUnit3 = actualGoodsUnit3;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public String getOperationGoodsName() {
        return operationGoodsName;
    }

    public void setOperationGoodsName(String operationGoodsName) {
        this.operationGoodsName = operationGoodsName;
    }

    public String getOperationGoodsUnit() {
        return operationGoodsUnit;
    }

    public void setOperationGoodsUnit(String operationGoodsUnit) {
        this.operationGoodsUnit = operationGoodsUnit;
    }

    public String getOperationGoodsCount() {
        return operationGoodsCount;
    }

    public void setOperationGoodsCount(String operationGoodsCount) {
        this.operationGoodsCount = operationGoodsCount;
    }

    public String getOperationHasLack() {
        return operationHasLack;
    }

    public void setOperationHasLack(String operationHasLack) {
        this.operationHasLack = operationHasLack;
    }

    public String getOperationHasDamage() {
        return operationHasDamage;
    }

    public void setOperationHasDamage(String operationHasDamage) {
        this.operationHasDamage = operationHasDamage;
    }

    public String getOperationPrice() {
        return operationPrice;
    }

    public void setOperationPrice(String operationPrice) {
        this.operationPrice = operationPrice;
    }

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public String getPickupEntranceForce() {
        return pickupEntranceForce;
    }

    public void setPickupEntranceForce(String pickupEntranceForce) {
        this.pickupEntranceForce = pickupEntranceForce;
    }

    public String getPickupEntrancePhotos() {
        return pickupEntrancePhotos;
    }

    public void setPickupEntrancePhotos(String pickupEntrancePhotos) {
        this.pickupEntrancePhotos = pickupEntrancePhotos;
    }

    public String getPickupPhotoForce() {
        return pickupPhotoForce;
    }

    public void setPickupPhotoForce(String pickupPhotoForce) {
        this.pickupPhotoForce = pickupPhotoForce;
    }

    public String getPickupTakePhotos() {
        return pickupTakePhotos;
    }

    public void setPickupTakePhotos(String pickupTakePhotos) {
        this.pickupTakePhotos = pickupTakePhotos;
    }

    public String getPickupMustConfirmDetail() {
        return pickupMustConfirmDetail;
    }

    public void setPickupMustConfirmDetail(String pickupMustConfirmDetail) {
        this.pickupMustConfirmDetail = pickupMustConfirmDetail;
    }

    public String getCommitPickupConfigDetail() {
        return commitPickupConfigDetail;
    }

    public void setCommitPickupConfigDetail(String commitPickupConfigDetail) {
        this.commitPickupConfigDetail = commitPickupConfigDetail;
    }

    public String getDeliveryEntranceForce() {
        return deliveryEntranceForce;
    }

    public void setDeliveryEntranceForce(String deliveryEntranceForce) {
        this.deliveryEntranceForce = deliveryEntranceForce;
    }

    public String getDeliveryEntrancePhotos() {
        return deliveryEntrancePhotos;
    }

    public void setDeliveryEntrancePhotos(String deliveryEntrancePhotos) {
        this.deliveryEntrancePhotos = deliveryEntrancePhotos;
    }

    public String getDeliveryPhotoForce() {
        return deliveryPhotoForce;
    }

    public void setDeliveryPhotoForce(String deliveryPhotoForce) {
        this.deliveryPhotoForce = deliveryPhotoForce;
    }

    public String getDeliveryTakePhotos() {
        return deliveryTakePhotos;
    }

    public void setDeliveryTakePhotos(String deliveryTakePhotos) {
        this.deliveryTakePhotos = deliveryTakePhotos;
    }

    public String getDeliveryMustConfirmDetail() {
        return deliveryMustConfirmDetail;
    }

    public void setDeliveryMustConfirmDetail(String deliveryMustConfirmDetail) {
        this.deliveryMustConfirmDetail = deliveryMustConfirmDetail;
    }

    public String getCommitDeliveryConfigDetail() {
        return commitDeliveryConfigDetail;
    }

    public void setCommitDeliveryConfigDetail(String commitDeliveryConfigDetail) {
        this.commitDeliveryConfigDetail = commitDeliveryConfigDetail;
    }

    public String getConfirmStatus() {
        return confirmStatus;
    }

    public void setConfirmStatus(String confirmStatus) {
        this.confirmStatus = confirmStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this._id);
        dest.writeString(this.orderId);
        dest.writeString(this.driverId);
        dest.writeString(this.serialNo);
        dest.writeString(this.refNum);
        dest.writeString(this.goodsName);
        dest.writeString(this.remark);
        dest.writeString(this.totalWeight);
        dest.writeString(this.totalVolume);
        dest.writeString(this.totalQuantity);
        dest.writeString(this.quantityUnit);
        dest.writeString(this.volumeUnit);
        dest.writeString(this.weightUnit);
        dest.writeString(this.pickupTimeStart);
        dest.writeString(this.pickupTimeEnd);
        dest.writeString(this.deliverTimeStart);
        dest.writeString(this.deliverTimeEnd);
        dest.writeString(this.fromAddress);
        dest.writeString(this.fromContact);
        dest.writeString(this.fromMobilePhone);
        dest.writeString(this.fromPhone);
        dest.writeString(this.fromWorkDays);
        dest.writeString(this.toAddress);
        dest.writeString(this.toContact);
        dest.writeString(this.toMobilePhone);
        dest.writeString(this.toPhone);
        dest.writeString(this.toWorkDays);
        dest.writeString(this.createTime);
        dest.writeString(this.pickupTime);
        dest.writeString(this.pickupEntranceTime);
        dest.writeString(this.deliveryTime);
        dest.writeString(this.deliveryEntranceTime);
        dest.writeString(this.receiverName);
        dest.writeString(this.senderName);
        dest.writeString(this.description);
        dest.writeString(this.status);
        dest.writeInt(this.isNew);
        dest.writeString(this.damaged);
        dest.writeString(this.orderType);
        dest.writeString(this.updateTime);
        dest.writeString(this.roadOrderName);
        dest.writeString(this.orderDetailId);
        dest.writeString(this.actualDeliveryGoodsName);
        dest.writeString(this.actualDeliveryQuantity);
        dest.writeString(this.actualDeliveryVolume);
        dest.writeString(this.actualDeliveryWeight);
        dest.writeString(this.actualGoodsId);
        dest.writeString(this.actualGoodsName);
        dest.writeString(this.actualGoodsUnit);
        dest.writeString(this.actualGoodsCount);
        dest.writeString(this.actualHasLack);
        dest.writeString(this.actualHasDamage);
        dest.writeString(this.actualPrice);
        dest.writeString(this.actualGoodsCount2);
        dest.writeString(this.actualGoodsCount3);
        dest.writeString(this.actualGoodsUnit2);
        dest.writeString(this.actualGoodsUnit3);
        dest.writeString(this.operationId);
        dest.writeString(this.operationGoodsName);
        dest.writeString(this.operationGoodsUnit);
        dest.writeString(this.operationGoodsCount);
        dest.writeString(this.operationHasLack);
        dest.writeString(this.operationHasDamage);
        dest.writeString(this.operationPrice);
        dest.writeString(this.configId);
        dest.writeString(this.pickupEntranceForce);
        dest.writeString(this.pickupEntrancePhotos);
        dest.writeString(this.pickupPhotoForce);
        dest.writeString(this.pickupTakePhotos);
        dest.writeString(this.pickupMustConfirmDetail);
        dest.writeString(this.commitPickupConfigDetail);
        dest.writeString(this.deliveryEntranceForce);
        dest.writeString(this.deliveryEntrancePhotos);
        dest.writeString(this.deliveryPhotoForce);
        dest.writeString(this.deliveryTakePhotos);
        dest.writeString(this.deliveryMustConfirmDetail);
        dest.writeString(this.commitDeliveryConfigDetail);
        dest.writeString(this.confirmStatus);
    }

    public Order() {
    }

    protected Order(Parcel in) {
        this._id = in.readInt();
        this.orderId = in.readString();
        this.driverId = in.readString();
        this.serialNo = in.readString();
        this.refNum = in.readString();
        this.goodsName = in.readString();
        this.remark = in.readString();
        this.totalWeight = in.readString();
        this.totalVolume = in.readString();
        this.totalQuantity = in.readString();
        this.quantityUnit = in.readString();
        this.volumeUnit = in.readString();
        this.weightUnit = in.readString();
        this.pickupTimeStart = in.readString();
        this.pickupTimeEnd = in.readString();
        this.deliverTimeStart = in.readString();
        this.deliverTimeEnd = in.readString();
        this.fromAddress = in.readString();
        this.fromContact = in.readString();
        this.fromMobilePhone = in.readString();
        this.fromPhone = in.readString();
        this.fromWorkDays = in.readString();
        this.toAddress = in.readString();
        this.toContact = in.readString();
        this.toMobilePhone = in.readString();
        this.toPhone = in.readString();
        this.toWorkDays = in.readString();
        this.createTime = in.readString();
        this.pickupTime = in.readString();
        this.pickupEntranceTime = in.readString();
        this.deliveryTime = in.readString();
        this.deliveryEntranceTime = in.readString();
        this.receiverName = in.readString();
        this.senderName = in.readString();
        this.description = in.readString();
        this.status = in.readString();
        this.isNew = in.readInt();
        this.damaged = in.readString();
        this.orderType = in.readString();
        this.updateTime = in.readString();
        this.roadOrderName = in.readString();
        this.orderDetailId = in.readString();
        this.actualDeliveryGoodsName = in.readString();
        this.actualDeliveryQuantity = in.readString();
        this.actualDeliveryVolume = in.readString();
        this.actualDeliveryWeight = in.readString();
        this.actualGoodsId = in.readString();
        this.actualGoodsName = in.readString();
        this.actualGoodsUnit = in.readString();
        this.actualGoodsCount = in.readString();
        this.actualHasLack = in.readString();
        this.actualHasDamage = in.readString();
        this.actualPrice = in.readString();
        this.actualGoodsCount2 = in.readString();
        this.actualGoodsCount3 = in.readString();
        this.actualGoodsUnit2 = in.readString();
        this.actualGoodsUnit3 = in.readString();
        this.operationId = in.readString();
        this.operationGoodsName = in.readString();
        this.operationGoodsUnit = in.readString();
        this.operationGoodsCount = in.readString();
        this.operationHasLack = in.readString();
        this.operationHasDamage = in.readString();
        this.operationPrice = in.readString();
        this.configId = in.readString();
        this.pickupEntranceForce = in.readString();
        this.pickupEntrancePhotos = in.readString();
        this.pickupPhotoForce = in.readString();
        this.pickupTakePhotos = in.readString();
        this.pickupMustConfirmDetail = in.readString();
        this.commitPickupConfigDetail = in.readString();
        this.deliveryEntranceForce = in.readString();
        this.deliveryEntrancePhotos = in.readString();
        this.deliveryPhotoForce = in.readString();
        this.deliveryTakePhotos = in.readString();
        this.deliveryMustConfirmDetail = in.readString();
        this.commitDeliveryConfigDetail = in.readString();
        this.confirmStatus = in.readString();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        public Order createFromParcel(Parcel source) {
            return new Order(source);
        }

        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    @Override
    public String toString() {
        return "Order{" +
                "_id=" + _id +
                ", orderId='" + orderId + '\'' +
                ", driverId='" + driverId + '\'' +
                ", serialNo='" + serialNo + '\'' +
                ", refNum='" + refNum + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", remark='" + remark + '\'' +
                ", totalWeight='" + totalWeight + '\'' +
                ", totalVolume='" + totalVolume + '\'' +
                ", totalQuantity='" + totalQuantity + '\'' +
                ", quantityUnit='" + quantityUnit + '\'' +
                ", volumeUnit='" + volumeUnit + '\'' +
                ", weightUnit='" + weightUnit + '\'' +
                ", pickupTimeStart='" + pickupTimeStart + '\'' +
                ", pickupTimeEnd='" + pickupTimeEnd + '\'' +
                ", deliverTimeStart='" + deliverTimeStart + '\'' +
                ", deliverTimeEnd='" + deliverTimeEnd + '\'' +
                ", fromAddress='" + fromAddress + '\'' +
                ", fromContact='" + fromContact + '\'' +
                ", fromMobilePhone='" + fromMobilePhone + '\'' +
                ", fromPhone='" + fromPhone + '\'' +
                ", fromWorkDays='" + fromWorkDays + '\'' +
                ", toAddress='" + toAddress + '\'' +
                ", toContact='" + toContact + '\'' +
                ", toMobilePhone='" + toMobilePhone + '\'' +
                ", toPhone='" + toPhone + '\'' +
                ", toWorkDays='" + toWorkDays + '\'' +
                ", createTime='" + createTime + '\'' +
                ", pickupTime='" + pickupTime + '\'' +
                ", pickupEntranceTime='" + pickupEntranceTime + '\'' +
                ", deliveryTime='" + deliveryTime + '\'' +
                ", deliveryEntranceTime='" + deliveryEntranceTime + '\'' +
                ", receiverName='" + receiverName + '\'' +
                ", senderName='" + senderName + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", isNew=" + isNew +
                ", damaged='" + damaged + '\'' +
                ", orderType='" + orderType + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", roadOrderName='" + roadOrderName + '\'' +
                ", orderDetailId='" + orderDetailId + '\'' +
                ", actualDeliveryGoodsName='" + actualDeliveryGoodsName + '\'' +
                ", actualDeliveryQuantity='" + actualDeliveryQuantity + '\'' +
                ", actualDeliveryVolume='" + actualDeliveryVolume + '\'' +
                ", actualDeliveryWeight='" + actualDeliveryWeight + '\'' +
                ", actualGoodsId='" + actualGoodsId + '\'' +
                ", actualGoodsName='" + actualGoodsName + '\'' +
                ", actualGoodsUnit='" + actualGoodsUnit + '\'' +
                ", actualGoodsCount='" + actualGoodsCount + '\'' +
                ", actualHasLack='" + actualHasLack + '\'' +
                ", actualHasDamage='" + actualHasDamage + '\'' +
                ", actualPrice='" + actualPrice + '\'' +
                ", actualGoodsCount2='" + actualGoodsCount2 + '\'' +
                ", actualGoodsCount3='" + actualGoodsCount3 + '\'' +
                ", actualGoodsUnit2='" + actualGoodsUnit2 + '\'' +
                ", actualGoodsUnit3='" + actualGoodsUnit3 + '\'' +
                ", operationId='" + operationId + '\'' +
                ", operationGoodsName='" + operationGoodsName + '\'' +
                ", operationGoodsUnit='" + operationGoodsUnit + '\'' +
                ", operationGoodsCount='" + operationGoodsCount + '\'' +
                ", operationHasLack='" + operationHasLack + '\'' +
                ", operationHasDamage='" + operationHasDamage + '\'' +
                ", operationPrice='" + operationPrice + '\'' +
                ", configId='" + configId + '\'' +
                ", pickupEntranceForce='" + pickupEntranceForce + '\'' +
                ", pickupEntrancePhotos='" + pickupEntrancePhotos + '\'' +
                ", pickupPhotoForce='" + pickupPhotoForce + '\'' +
                ", pickupTakePhotos='" + pickupTakePhotos + '\'' +
                ", pickupMustConfirmDetail='" + pickupMustConfirmDetail + '\'' +
                ", commitPickupConfigDetail='" + commitPickupConfigDetail + '\'' +
                ", deliveryEntranceForce='" + deliveryEntranceForce + '\'' +
                ", deliveryEntrancePhotos='" + deliveryEntrancePhotos + '\'' +
                ", deliveryPhotoForce='" + deliveryPhotoForce + '\'' +
                ", deliveryTakePhotos='" + deliveryTakePhotos + '\'' +
                ", deliveryMustConfirmDetail='" + deliveryMustConfirmDetail + '\'' +
                ", commitDeliveryConfigDetail='" + commitDeliveryConfigDetail + '\'' +
                ", confirmStatus='" + confirmStatus + '\'' +
                '}';
    }
}