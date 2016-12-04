package com.zzqs.app_kc.receiver;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.RemoteViews;

import com.igexin.sdk.PushConsts;
import com.zzqs.app_kc.R;
import com.zzqs.app_kc.activities.CompaniesActivity;
import com.zzqs.app_kc.activities.DeliveryOrderActivity;
import com.zzqs.app_kc.activities.LoginActivity;
import com.zzqs.app_kc.activities.MainActivity;
import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.db.DaoManager;
import com.zzqs.app_kc.db.hibernate.dao.BaseDao;
import com.zzqs.app_kc.entity.Company;
import com.zzqs.app_kc.entity.Events;
import com.zzqs.app_kc.entity.Order;
import com.zzqs.app_kc.entity.User;
import com.zzqs.app_kc.net.RestAPI;
import com.zzqs.app_kc.utils.CommonFiled;
import com.zzqs.app_kc.utils.CommonTools;
import com.zzqs.app_kc.utils.StringTools;
import com.zzqs.app_kc.widgets.DialogView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by lance on 15-1-4.
 */

public class PushReceiver extends BroadcastReceiver {
    public static String cid;
    private int notification_id = 0;
    private User user;
    private BaseDao<Order> orderDao;

    @Override
    public void onReceive(final Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        user = ZZQSApplication.getInstance().getUser();
        orderDao = DaoManager.getOrderDao(context);
        if (null != user) {
            switch (bundle.getInt(PushConsts.CMD_ACTION)) {
                case PushConsts.GET_CLIENTID:
                    cid = bundle.getString("clientid");
                    if (!StringTools.isEmp(cid)) {
                        if (!user.getDevice_id().equals(cid)) {
                            RestAPI.getInstance(context).setDeviceId(cid, new RestAPI.RestResponse() {

                                @Override
                                public void onSuccess(Object object) {
                                    user.setDevice_id(cid);
                                    ZZQSApplication.getInstance().updateUser(user);
                                }

                                @Override
                                public void onFailure(Object object) {
                                }
                            });
                        }
                    }
                    break;
                case PushConsts.GET_MSG_DATA:
                    byte[] payload = bundle.getByteArray("payload");
                    String data = new String(payload);
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        String type = jsonObject.optString("type");
                        String username = jsonObject.optString("username");
                        if (user.getUsername().equals(username)) {
                            final Intent mIntent = new Intent();
                            final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            final Notification notification = new Notification(R.drawable.ic_launcher, context.getString(R.string.app_name), 1000);
                            notification.contentView = new RemoteViews(context.getPackageName(), R.layout.notification);
                            notification.contentView.setImageViewResource(R.id.image, R.drawable.ic_launcher);
                            notification.flags |= Notification.FLAG_AUTO_CANCEL;
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = new Date();
                            String time = format.format(date);
                            notification.contentView.setTextViewText(R.id.title, time);
                            if (type.equals("new_order") || type.equals("warehouse_order")) {
                                if (jsonObject.has("order")) {
                                    JSONObject orderObject = jsonObject.optJSONObject("order");
                                    Order order = RestAPI.orderParse(context,orderObject);
                                    order.setIsNew(Order.NEW);
                                    List<Order> list = orderDao.find(null, "order_id=?", new String[]{order.getOrderId() + ""}
                                            , null, null, null, null);
                                    boolean exist1;
                                    if (list == null || list.size() == 0) {
                                        exist1 = false;
                                        orderDao.insert(order);
                                        mIntent.setClass(context, MainActivity.class);
                                        mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                                        mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        String msg;
                                        Events.OrderEvent event;
                                        if (type.equals("new_order")) {
                                            msg = context.getString(R.string.prompt_new_driver_order);
                                            event = new Events.OrderEvent(order, Events.ADD_ORDER_TO_UN_PICKUP_ORDER_FRAGMENT);
                                        } else {
                                            msg = context.getString(R.string.prompt_new_warehouse_order);
                                            event = new Events.OrderEvent(order, Events.ADD_ORDER_TO_WAREHOUSE_ORDER_FRAGMENT);
                                        }
                                        EventBus.getDefault().post(event);
                                        notification.contentView.setTextViewText(R.id.content, msg);
                                        notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.new_order);
                                        notification.contentIntent = PendingIntent.getActivity(context, notification_id, mIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                                    } else {
                                        exist1 = true;
                                        order.set_id(list.get(0).get_id());
                                        orderDao.update(order);
                                    }
                                    if (!exist1) {
                                        notificationManager.notify(notification_id++, notification);
                                    }
                                }
                            } else if (type.equals("new_invite")) {
                                if (jsonObject.has("invite_driver")) {
                                    JSONObject jsonObject1 = jsonObject.optJSONObject("invite_driver");
                                    if (jsonObject1.has("company")) {
                                        JSONObject companyObject = jsonObject1.optJSONObject("company");
                                        Company company = new Company();
                                        company.setCompany_id(companyObject.optString("_id"));
                                        company.setCompany_name(companyObject.optString("name"));
                                        company.setAddress(companyObject.optString("address"));
                                        company.setType(companyObject.optString("type"));
                                        company.setUsername(username);
                                        BaseDao<Company> companyBaseDao = DaoManager.getCompanyDao(context);
                                        List<Company> companies = companyBaseDao.find(null, "company_id=?", new String[]{company.getCompany_id()}, null, null, null, null);
                                        boolean exist2;
                                        if (companies == null || companies.size() == 0) {
                                            exist2 = false;
                                            company.setStatus(Company.ACCEPT);
                                            companyBaseDao.insert(company);
                                            mIntent.setClass(context, CompaniesActivity.class).addCategory(Intent.CATEGORY_LAUNCHER).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.new_invite);
                                            notification.contentView.setTextViewText(R.id.content, context.getString(R.string.prompt_new_driver_order));
                                            notification.contentIntent = PendingIntent.getActivity(context, notification_id, mIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                                        } else {
                                            exist2 = true;
                                            company.set_id(companies.get(0).get_id());
                                            companyBaseDao.update(company);
                                        }
                                        if (!exist2) {
                                            Events.OtherEvent otherEvent = new Events.OtherEvent(company.getCompany_name(), Events.NEW_INVITE_COMPANY);
                                            EventBus.getDefault().post(otherEvent);
                                            CommonTools.setNewMessage(CommonFiled.COMPANY_MSG, true, context);
                                            final Activity nowContext = ZZQSApplication.getInstance().getCurrentContext();
                                            if (null != nowContext) {
                                                DialogView.showChoiceDialog(nowContext, DialogView.NORMAL, context.getString(R.string.prompt_dl_title_1), context.getString(R.string.prompt_new_company_invitation), new Handler() {
                                                    @Override
                                                    public void handleMessage(Message msg) {
                                                        super.handleMessage(msg);
                                                        if (msg.what == DialogView.ACCEPT) {
                                                            nowContext.startActivity(new Intent(context, CompaniesActivity.class));
                                                        }
                                                    }
                                                });
                                            }
                                            notificationManager.notify(notification_id++, notification);
                                        }
                                    }
                                }
                            } else if (type.equals("multi_order")) {
                                String orderType = jsonObject.optString("order_type");
                                if (orderType.equals(Order.DRIVER_ORDER)) {
                                    RestAPI.getInstance(context).getOrders(new String[]{Order.UN_PICKUP_ENTRANCE}, Order.DRIVER_ORDER, new RestAPI.RestResponse() {
                                        @Override
                                        public void onSuccess(Object object) {
                                            int newOrderNumber = 0;
                                            List<Order> orderList = (ArrayList<Order>) object;
                                            if (orderList.size() > 0) {
                                                for (Order order : orderList) {
                                                    List<Order> list = orderDao.find(null, "order_id=?", new String[]{order.getOrderId() + ""}
                                                            , null, null, null, null);
                                                    if (list.size() == 0) {
                                                        order.setIsNew(Order.NEW);
                                                        orderDao.insert(order);
                                                        ++newOrderNumber;
                                                    } else {
                                                        order.setIsNew(order.EXIST);
                                                        order.set_id(list.get(0).get_id());
                                                        orderDao.update(order);
                                                    }
                                                }
                                                if (newOrderNumber > 0) {
                                                    mIntent.setClass(context, MainActivity.class).addCategory(Intent.CATEGORY_LAUNCHER).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.new_order);
                                                    notification.contentView.setTextViewText(R.id.content, context.getString(R.string.prompt_new_orders));
                                                    notification.contentIntent = PendingIntent.getActivity(context, notification_id, mIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                                                    Events.OrderEvent event = new Events.OrderEvent(orderList.get(0), Events.BATCH_ORDER_TO_PICKUP_ORDER_FRAGMENT);
                                                    EventBus.getDefault().post(event);
                                                    notificationManager.notify(notification_id++, notification);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Object object) {

                                        }
                                    });
                                } else if (orderType.equals(Order.WAREHOUSE_ORDER)) {
                                    RestAPI.getInstance(context).getOrders(new String[]{Order.UN_DELIVERY}, Order.WAREHOUSE_ORDER, new RestAPI.RestResponse() {
                                        @Override
                                        public void onSuccess(Object object) {
                                            int newOrderNumber = 0;
                                            List<Order> orderList = (ArrayList<Order>) object;
                                            if (orderList.size() > 0) {
                                                for (Order order : orderList) {
                                                    List<Order> list = orderDao.find(null, "order_id=?", new String[]{order.getOrderId() + ""}
                                                            , null, null, null, null);
                                                    if (list.size() == 0) {
                                                        order.setIsNew(Order.NEW);
                                                        orderDao.insert(order);
                                                        ++newOrderNumber;
                                                    } else {
                                                        order.setIsNew(order.EXIST);
                                                        order.set_id(list.get(0).get_id());
                                                        orderDao.update(order);
                                                    }
                                                }
                                                if (newOrderNumber > 0) {
                                                    mIntent.setClass(context, MainActivity.class).addCategory(Intent.CATEGORY_LAUNCHER).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.new_order);
                                                    notification.contentView.setTextViewText(R.id.content, context.getString(R.string.prompt_new_orders));
                                                    notification.contentIntent = PendingIntent.getActivity(context, notification_id, mIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                                                    Events.OrderEvent event = new Events.OrderEvent(orderList.get(0), Events.BATCH_ORDER_TO_WAREHOUSE_ORDER_FRAGMENT);
                                                    EventBus.getDefault().post(event);
                                                    notificationManager.notify(notification_id++, notification);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Object object) {

                                        }
                                    });
                                }

                            } else if (type.equals("modify_order")) {
                                if (jsonObject.has("order")) {
                                    JSONObject orderObject = jsonObject.optJSONObject("order");
                                    Order order = RestAPI.orderParse(context,orderObject);
                                    List<Order> list = orderDao.find(null, "order_id=?", new String[]{order.getOrderId()}, null, null, null, null);
                                    if (list.size() == 1) {
                                        order.set_id(list.get(0).get_id());
                                        order.setIsNew(Order.UPDATE);
                                        orderDao.update(order);
                                        int eventType;
                                        if (order.getOrderType().equals(Order.DRIVER_ORDER)) {
                                            if (order.getStatus().equals(Order.UN_PICKUP) || order.getStatus().equals(Order.UN_PICKUP_ENTRANCE)) {
                                                eventType = Events.UPDATE_ORDER_IN_PICKUP_ORDER_FRAGMENT;
                                            } else {
                                                eventType = Events.UPDATE_ORDER_IN_EXECUTING_ORDER_FRAGMENT;
                                            }
                                        } else {
                                            eventType = Events.UPDATE_ORDER_IN_WAREHOUSE_ORDER_FRAGMENT;
                                        }
                                        Events.OrderEvent event = new Events.OrderEvent(order, eventType);
                                        EventBus.getDefault().post(event);
                                        mIntent.setClass(context, MainActivity.class).addCategory(Intent.CATEGORY_LAUNCHER).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.modify_order);
                                        notification.contentView.setTextViewText(R.id.content, context.getString(R.string.prompt_modify_order));
                                        notification.contentIntent = PendingIntent.getActivity(context, notification_id, mIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                                        notificationManager.notify(notification_id++, notification);
                                    } else {
                                        int eventType;
                                        if (order.getOrderType().equals(Order.DRIVER_ORDER)) {
                                            if (order.getStatus().equals(Order.UN_PICKUP) || order.getStatus().equals(Order.UN_PICKUP_ENTRANCE)) {
                                                eventType = Events.ADD_ORDER_TO_UN_PICKUP_ORDER_FRAGMENT;
                                            } else {
                                                eventType = Events.ADD_ORDER_TO_EXECUTING_ORDER_FRAGMENT;
                                            }
                                        } else {
                                            eventType = Events.ADD_ORDER_TO_WAREHOUSE_ORDER_FRAGMENT;
                                        }
                                        Events.OrderEvent event = new Events.OrderEvent(order, eventType);
                                        EventBus.getDefault().post(event);
                                        notificationManager.notify(notification_id++, notification);
                                        mIntent.setClass(context, MainActivity.class).addCategory(Intent.CATEGORY_LAUNCHER).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        notification.contentView.setTextViewText(R.id.content, context.getString(R.string.prompt_new_driver_order));
                                        notification.contentIntent = PendingIntent.getActivity(context, notification_id, mIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                                        notificationManager.notify(notification_id++, notification);

                                    }
                                }
                            } else if (type.equals("delete_order")) {
                                String orderId = jsonObject.optString("order_id");
                                List<Order> list = orderDao.find(null, "order_id=?", new String[]{orderId}, null, null, null, null);
                                if (list.size() > 0) {
                                    Order order = list.get(0);
                                    order.setStatus(Order.STATUS_INVALID);
                                    order.setIsNew(Order.EXIST);
                                    orderDao.update(order);
                                    int eventType = 0;
                                    if (order.getOrderType().equals(Order.DRIVER_ORDER)) {
                                        eventType = Events.UPDATE_ORDER_IN_PICKUP_ORDER_FRAGMENT;
                                    } else {
                                        eventType = Events.UPDATE_ORDER_IN_WAREHOUSE_ORDER_FRAGMENT;
                                    }
                                    if (eventType != 0) {
                                        Events.OrderEvent event = new Events.OrderEvent(order, eventType);
                                        EventBus.getDefault().post(event);
                                    }
                                    mIntent.setClass(context, DeliveryOrderActivity.class).addCategory(Intent.CATEGORY_LAUNCHER).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.delete_order);
                                    notification.contentView.setTextViewText(R.id.content, context.getString(R.string.prompt_delete_order));
                                    notification.contentIntent = PendingIntent.getActivity(context, notification_id, mIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                                }
                                notificationManager.notify(notification_id++, notification);
                            } else if (type.equals("account_disconnected")) {
                                if (null != ZZQSApplication.getInstance().getCurrentContext()) {
                                    final Activity nowContext = ZZQSApplication.getInstance().getCurrentContext();
                                    DialogView.showChoiceDialog(ZZQSApplication.getInstance().getCurrentContext(), DialogView.SINGLE_BTN, context.getString(R.string.prompt_dl_other_equipment_login_title), context.getResources().getString(R.string.prompt_dl_other_equipment_login_msg), new Handler() {
                                        @Override
                                        public void handleMessage(Message msg) {
                                            ZZQSApplication.getInstance().clearUser(context);
                                            ZZQSApplication.getInstance().cleanAllActivity();
                                            nowContext.startActivity(new Intent(nowContext, LoginActivity.class));
                                        }
                                    });
                                } else {
                                    ZZQSApplication.getInstance().clearUser(context);
                                    ZZQSApplication.getInstance().cleanAllActivity();
                                    mIntent.setClass(context, LoginActivity.class).addCategory(Intent.CATEGORY_LAUNCHER).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    notification.contentView.setTextViewText(R.id.content, context.getString(R.string.prompt_dl_other_equipment_login_msg));
                                    notification.contentIntent = PendingIntent.getActivity(context, notification_id, mIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                                    notificationManager.notify(notification_id++, notification);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ZZQSApplication.getInstance().CrashToLogin();
                    }
                    break;
                default:
                    break;
            }
        }
    }

}
