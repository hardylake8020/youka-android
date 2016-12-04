package com.zzqs.app_kc.entity;


/**
 * Created by lance on 15/11/21.
 */
public class Events {
    public static final int ADD_ORDER_TO_UN_PICKUP_ORDER_FRAGMENT = 900;
    public static final int ADD_ORDER_TO_EXECUTING_ORDER_FRAGMENT = 901;
    public static final int ADD_ORDER_TO_WAREHOUSE_ORDER_FRAGMENT = 902;

    public static final int REMOVE_ORDER_TO_PICKUP_ORDER_FRAGMENT = 903;
    public static final int REMOVE_ORDER_TO_EXECUTING_ORDER_FRAGMENT = 904;
    public static final int REMOVE_ORDER_TO_WAREHOUSE_ORDER_FRAGMENT = 905;

    public static final int UPDATE_ORDER_IN_PICKUP_ORDER_FRAGMENT = 906;
    public static final int UPDATE_ORDER_IN_EXECUTING_ORDER_FRAGMENT = 907;
    public static final int UPDATE_ORDER_IN_WAREHOUSE_ORDER_FRAGMENT = 908;

    public static final int BATCH_ORDER_TO_PICKUP_ORDER_FRAGMENT = 909;
    public static final int BATCH_ORDER_TO_EXECUTING_ORDER_FRAGMENT = 910;
    public static final int BATCH_ORDER_TO_WAREHOUSE_ORDER_FRAGMENT = 911;

    public static final int REFRESH_ORDER = 912;

    public static final int NEW_INVITE_COMPANY = 913;

    public static final int INVITE_TIP = 914;

    public static final int TAkE_PHOTO = 915;

    public static final int FILE_EVENT = 916;

    public static final int ADD_MONEY = 917;

    public static final int CUT_MONEY = 917;

    public static class OrderEvent {
        private Order order;
        private int type;

        public OrderEvent(Order order, int type) {
            this.order = order;
            this.type = type;
        }

        public Order getOrder() {
            return order;
        }

        public int getType() {
            return type;
        }
    }

    public static class OtherEvent {
        private int type;
        private String msg;

        public OtherEvent(String msg, int type) {
            this.msg = msg;
            this.type = type;
        }

        public String getMsg() {
            return msg;
        }

        public int getType() {
            return type;
        }
    }

    public static class FileEvent {
        private EventFile eventFile;
        private int type;

        public FileEvent(EventFile eventFile, int type) {
            this.eventFile = eventFile;
            this.type = type;
        }

        public EventFile getEventFile() {
            return eventFile;
        }

        public int getType() {
            return type;
        }
    }

    public static class GoodsEvent {
        private int type;
        private int current;
        private int count;

        public GoodsEvent(int type) {
            this.type = type;
        }

        public GoodsEvent(int type, int current) {
            this.type = type;
            this.current = current;
        }


        public GoodsEvent(int type, int current, int count) {
            this.type = type;
            this.current = current;
            this.count = count;
        }

        public int getType() {
            return type;
        }

        public int getCurrent() {
            return current;
        }

        public int getCount() {
            return count;
        }
    }


}
