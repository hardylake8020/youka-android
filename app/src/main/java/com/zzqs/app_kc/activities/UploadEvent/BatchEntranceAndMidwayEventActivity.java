package com.zzqs.app_kc.activities.UploadEvent;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.zzqs.app_kc.R;
import com.zzqs.app_kc.entity.EventFile;
import com.zzqs.app_kc.entity.Events;
import com.zzqs.app_kc.entity.OrderEvent;
import com.zzqs.app_kc.utils.StringTools;

import java.util.ArrayList;
import java.util.Date;

import de.greenrobot.event.EventBus;

/**
 * Created by lance on 15/11/22.
 */
public class BatchEntranceAndMidwayEventActivity extends BaseUploadEventActivity {

    @Override
    public void initCallbackHandler() {
        callbackHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what != TO_FUNCTION_UPLOAD && pd.isShowing()) {
                    pd.dismiss();
                }
                switch (msg.what) {
                    case TO_FUNCTION_UPLOAD:
                        batchUpload(mold);
                        break;
                    case GET_PHOTO:
                        adapter.notifyDataSetChanged();
                        break;
                    case UPLOAD_SUCCESS:
                        Events.OrderEvent event = new Events.OrderEvent(order, Events.REFRESH_ORDER);
                        EventBus.getDefault().post(event);
                        finish();
                        break;
                    case SAVE_FILE_FAILED:
                        Toast.makeText(BatchEntranceAndMidwayEventActivity.this, getString(R.string.prompt_save_file_failed), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
    }

    @Override
    public void subclassView() {
        timeLine.setVisibility(View.GONE);
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
        new Thread() {
            @Override
            public void run() {
                super.run();
                orderEventDao.update(orderEvent);
                isHaveEventId();
                ArrayList<EventFile> insertFiles = new ArrayList();
                ArrayList<EventFile> updateFiles = new ArrayList<>();
                if (null != voiceFile && !StringTools.isEmp(voiceFile.getFilePath())) {
                    voiceFile.setOrderId(order.getOrderId());
                    voiceFile.setEventId(orderEvent.get_id());
                    if (voiceFile.get_id() > 0) {
                        updateFiles.add(voiceFile);
                    } else {
                        insertFiles.add(voiceFile);
                    }
                } else {
                    voiceFile = new EventFile();
                }
                for (OrderEvent event : orderEvents) {
                    if (!event.getOrderId().equals(order.getOrderId())) {
                        eventFileDao.execSql("delete from event_file where event_id=?", new String[]{event.get_id() + ""});
                        event.setEventId(orderEvent.getEventId());
                        event.setContent(orderEvent.getContent());
                        event.setRemark(orderEvent.getRemark());
                        event.setCreateTime(orderEvent.getCreateTime());
                        event.setAddress(locationAddress);
                        event.setLatitude(latitude);
                        event.setLongitude(longitude);
                        if (!StringTools.isEmp(voiceFile.getFilePath())) {
                            EventFile voiceFile1 = new EventFile();
                            voiceFile1.setMold(voiceFile.getMold());
                            voiceFile1.setOrderId(event.getOrderId());
                            voiceFile1.setEventId(event.get_id());
                            voiceFile1.setFilePath(voiceFile.getFilePath());
                            voiceFile1.setStatus(EventFile.STATUS_COMMIT);
                            insertFiles.add(voiceFile1);
                        }
                        for (EventFile eventFile : photoList) {
                            EventFile eventFile1 = new EventFile();
                            eventFile1.setFilePath(eventFile.getFilePath());
                            eventFile1.setConfigName(eventFile.getConfigName());
                            eventFile1.setMold(eventFile.getMold());
                            eventFile1.setOrderId(event.getOrderId());
                            eventFile1.setEventId(event.get_id());
                            eventFile1.setStatus(EventFile.STATUS_COMMIT);
                            insertFiles.add(eventFile1);
                        }
                    } else {
                        for (EventFile eventFile : photoList) {
                            if (eventFile.get_id() > 0) {
                                updateFiles.add(eventFile);
                            } else {
                                insertFiles.add(eventFile);
                            }
                        }
                    }
                }
                eventFileDao.inserts(insertFiles);
                eventFileDao.updates(updateFiles);
                Message msg = callbackHandler.obtainMessage();
                msg.what = TO_FUNCTION_UPLOAD;
                callbackHandler.sendMessage(msg);

            }
        }.start();
    }

    @Override
    public void back() {
        finish();
    }
}
