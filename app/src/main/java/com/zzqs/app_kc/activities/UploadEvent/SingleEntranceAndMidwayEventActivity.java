package com.zzqs.app_kc.activities.UploadEvent;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.zzqs.app_kc.R;
import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.entity.EventFile;
import com.zzqs.app_kc.entity.Events;
import com.zzqs.app_kc.entity.OrderEvent;
import com.zzqs.app_kc.utils.StringTools;

import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by lance on 15/11/20.
 */
public class SingleEntranceAndMidwayEventActivity extends BaseUploadEventActivity {
    @Override
    public void initCallbackHandler() {
        callbackHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                switch (msg.what) {
                    case GET_PHOTO:
                        adapter.notifyDataSetChanged();
                        break;
                    case UPLOAD_SUCCESS:
                        orderEvent.setStatus(OrderEvent.STATUS_COMMIT);
                        if (orderEvent.get_id() > 0) {
                            orderEventDao.update(orderEvent);
                        } else {
                            orderEventDao.insert(orderEvent);
                        }
                        Events.OrderEvent event = new Events.OrderEvent(order, Events.REFRESH_ORDER);
                        EventBus.getDefault().post(event);
                        Toast.makeText(SingleEntranceAndMidwayEventActivity.this, getString(R.string.prompt_submitted), Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case ORDER_NOT_EXIST:
                        Toast.makeText(SingleEntranceAndMidwayEventActivity.this, getString(R.string.prompt_cancelled_order), Toast.LENGTH_SHORT).show();
                        ZZQSApplication.getInstance().finishUnMainActivity();
                        break;
                    case ORDER_STATUS_HAS_CHANGED:
                        Toast.makeText(SingleEntranceAndMidwayEventActivity.this, getString(R.string.prompt_operating_order), Toast.LENGTH_SHORT).show();
                        ZZQSApplication.getInstance().finishUnMainActivity();
                        break;
                    case SAVE_FILE_FAILED:
                        Toast.makeText(SingleEntranceAndMidwayEventActivity.this, getString(R.string.prompt_save_file_failed), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
    }

    @Override
    public void subclassView() {
        timeLine.setVisibility(View.VISIBLE);
        rlActualDelivery.setVisibility(View.GONE);
        rlDamage.setVisibility(View.GONE);
        if (mold.equals(OrderEvent.MOLD_HALFWAY)) {
            headMsg.setText(getString(R.string.view_tv_halfway_event));
        } else if (mold.equals(OrderEvent.MOLD_PICKUP_ENTRANCE)) {
            headMsg.setText(getString(R.string.view_tv_pickup_enter));
        } else {
            headMsg.setText(getString(R.string.view_tv_delivery_enter));
        }
    }

    @Override
    public void saveData() {
        String message = et.getText().toString().trim();
        if (!StringTools.isEmp(message)) {
            orderEvent.setRemark(message);
        }
        if (orderEvent.getLatitude() != 0 && orderEvent.getLongitude() != 0) {
            latitude = orderEvent.getLatitude();
            longitude = orderEvent.getLongitude();
            if (!StringTools.isEmp(orderEvent.getCreateTime())) {
                uploadTime = orderEvent.getCreateTime();
            }
            if (!StringTools.isEmp(orderEvent.getAddress())) {
                locationAddress = orderEvent.getAddress();
            } else {
                if (!isSearch) {
                    isSearch = !isSearch;
                    LatLng latLng = new LatLng(orderEvent.getLatitude(), orderEvent.getLongitude());
                    mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
                    return;
                }

            }
        } else {
            if (lastTrace != null) {
                latitude = lastTrace.getLatitude();
                longitude = lastTrace.getLongitude();
                uploadTime = lastTrace.getTime();
                if (!StringTools.isEmp(lastTrace.getAddress())) {
                    locationAddress = lastTrace.getAddress();
                } else {
                    if (!isSearch) {
                        isSearch = !isSearch;
                        LatLng latLng = new LatLng(orderEvent.getLatitude(), orderEvent.getLongitude());
                        mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
                        return;
                    }
                }
            }
        }
        if (!StringTools.isEmp(uploadTime) && !uploadTime.equals("1970-01-01 00:00:00")) {
            orderEvent.setCreateTime(uploadTime);
        } else {
            orderEvent.setCreateTime(sdf.format(new Date()));
        }
        orderEvent.setAddress(locationAddress);
        orderEvent.setLatitude(latitude);
        orderEvent.setLongitude(longitude);

        if (orderEvent.get_id() > 0) {
            orderEventDao.update(orderEvent);
        } else {
            orderEvent.setStatus(OrderEvent.STATUS_NEW);
            orderEvent.setOrderId(order.getOrderId());
            orderEvent.setMold(mold);
            orderEventDao.insert(orderEvent);
            List<OrderEvent> orderEvents = orderEventDao.rawQuery("select * from order_event where order_id=? and mold=? and status=?", new String[]{orderEvent.getOrderId() + "", orderEvent.getMold(), OrderEvent.STATUS_NEW + ""});
            if (orderEvents.size() > 0) {
                orderEvent.set_id(orderEvents.get(orderEvents.size() - 1).get_id());
            }
        }

        if (null != voiceFile && !StringTools.isEmp(voiceFile.getFilePath())) {
            voiceFile.setOrderId(order.getOrderId());
            voiceFile.setEventId(orderEvent.get_id());
            if (voiceFile.get_id() > 0) {
                eventFileDao.update(voiceFile);
            } else {
                eventFileDao.insert(voiceFile);
            }
        } else {
            voiceFile = new EventFile();
        }

        for (EventFile eventFile : photoList) {
            if (eventFile.get_id() > 0) {
                eventFileDao.update(eventFile);
            } else {
                eventFile.setEventId(orderEvent.get_id());
                eventFileDao.insert(eventFile);
            }
        }

        isHaveEventId();

        SingleUpload("false");
    }

    @Override
    public void back() {
        finish();
    }
}
