package com.zzqs.app_kc.db.hibernate.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.zzqs.app_kc.entity.LogInfo;

public class SQLHelper extends SQLiteOpenHelper {
    private Class<?>[] modelClasses;
    private Context context;

    public SQLHelper(Context context, String name, CursorFactory factory, int version, Class<?>[] modelClasses) {
        super(context, name, factory, version);
        this.modelClasses = modelClasses;
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //根据所有class创建所有表 当然具体创建步奏根据db 以及注解操作
        DBManager.createTableByClasses(db, modelClasses);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        switch (oldVersion) {
            case 18:
            case 19:
                updateDatabase18to20(db);
            case 20:
                updateDatabase20to21(db);
            case 21:
                updateDatabase21to22(db);
            case 22:
                updateDatabase22to23(db);
            case 23:
                updateDatabase23to24(db);
            case 24:
                updateDatabase24to25(db);
            case 25:
                updateDatabase25to26(db);
            case 26:
                updateDatabase26to27(db);
            case 27:
                updateDatabase27to28(db);
            case 28:
                updateDatabase28to29(db);
            case 29:
                updateDatabase29to30(db);
            case 30:
                updateDatabase30to31(db);
            case 31:
                updateDatabase31to32(db);
            case 32:
                updateDatabase32to33(db);
            case 33:
                updateDataBase33to34(db);
            case 34:
                updateDataBase34to35(db);
            case 35:
                updateDataBase35to36(db);
            case 36:
                updateDataBase36to37(db);
                break;
            default:
                if (oldVersion < 18) {
                    DBManager.dropTablesByClasses(db, modelClasses);
                    onCreate(db);
                }
                break;
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    private void updateDatabase18to20(SQLiteDatabase db) {
        db.execSQL("alter table zz_order ADD order_type string NOT NULL Default driver");
        db.execSQL("alter table order_event ADD order_code string");
    }

    private void updateDatabase20to21(SQLiteDatabase db) {
        db.execSQL("alter table zz_order ADD update_time string");
    }

    private void updateDatabase21to22(SQLiteDatabase db) {
        db.execSQL("alter table zz_order ADD receiver_name string");
    }

    private void updateDatabase22to23(SQLiteDatabase db) {
        db.execSQL("alter table zz_order ADD pickup_entrance_force string NOT NULL Default false");
        db.execSQL("alter table zz_order ADD pickup_photo_force string NOT NULL Default false");
        db.execSQL("alter table zz_order ADD delivery_entrance_force string NOT NULL Default false");
        db.execSQL("alter table zz_order ADD delivery_photo_force string NOT NULL Default true");
    }

    private void updateDatabase23to24(SQLiteDatabase db) {
        db.execSQL("alter table zz_order ADD road_order_name string");
    }

    private void updateDatabase24to25(SQLiteDatabase db) {
        db.execSQL("alter table zz_order ADD order_detail_id string");
    }

    private void updateDatabase25to26(SQLiteDatabase db) {
        db.execSQL("alter table zz_order ADD sender_name string");
    }

    private void updateDatabase26to27(SQLiteDatabase db) {
        db.execSQL("alter table zz_order ADD description string");
    }

    private void updateDatabase27to28(SQLiteDatabase db) {
        db.execSQL("alter table zz_order ADD actual_delivery_goodsName string");
        db.execSQL("alter table zz_order ADD actual_delivery_quantity string");
        db.execSQL("alter table zz_order ADD actual_delivery_volume string");
        db.execSQL("alter table zz_order ADD actual_delivery_weight string");
        Class<?>[] clazz = {LogInfo.class};
        DBManager.createTableByClasses(db, clazz);
    }

    private void updateDatabase28to29(SQLiteDatabase db) {
        db.execSQL("alter table user ADD rate int NOT NULL Default -1");
    }

    private void updateDatabase29to30(SQLiteDatabase db) {
        db.execSQL("alter table zz_order ADD actual_goods_id string");
        db.execSQL("alter table zz_order ADD actual_goodsName string");
        db.execSQL("alter table zz_order ADD actual_goodsUnit string");
        db.execSQL("alter table zz_order ADD actual_goodsCount string");
    }

    private void updateDatabase30to31(SQLiteDatabase db) {
        db.execSQL("alter table zz_order ADD operation_id string");
        db.execSQL("alter table zz_order ADD operation_goodsName string");
        db.execSQL("alter table zz_order ADD operation_goodsUnit string");
        db.execSQL("alter table zz_order ADD operation_goodsCount string");
    }

    private void updateDatabase31to32(SQLiteDatabase db) {
        db.execSQL("alter table zz_order ADD actual_hasLack string");
        db.execSQL("alter table zz_order ADD actual_hasDamage string");
        db.execSQL("alter table zz_order ADD operation_hasLack string");
        db.execSQL("alter table zz_order ADD operation_hasDamage string");
    }

    private void updateDatabase32to33(SQLiteDatabase db) {
        db.execSQL("alter table zz_order ADD actual_price string");
        db.execSQL("alter table zz_order ADD operation_price string");
    }

    private void updateDataBase33to34(SQLiteDatabase db) {
        db.execSQL("alter table zz_order ADD config_id string");
        db.execSQL("alter table zz_order ADD pickup_entrance_photos string");
        db.execSQL("alter table zz_order ADD pickup_take_photos string");
        db.execSQL("alter table zz_order ADD pickup_must_confirm_detail string");
        db.execSQL("alter table zz_order ADD delivery_entrance_photos string");
        db.execSQL("alter table zz_order ADD delivery_take_photos string");
        db.execSQL("alter table zz_order ADD delivery_must_confirm_detail string");
        db.execSQL("alter table event_file ADD config_name string");
        db.execSQL("alter table zz_order ADD commit_pickup_config_detail string Default false");
        db.execSQL("alter table zz_order ADD commit_delivery_config_detail string Default false");
    }

    private void updateDataBase34to35(SQLiteDatabase db) {
        db.execSQL("alter table user ADD local_photo string");
        db.execSQL("alter table user ADD local_id_card_photo string");
        db.execSQL("alter table user ADD local_driving_id_photo string");
        db.execSQL("alter table user ADD local_travel_id_photo string");
        db.execSQL("alter table user ADD local_trading_id_photo string");
        db.execSQL("alter table user ADD local_plate_photo string");
        db.execSQL("alter table zz_order ADD actual_goodsUnit2 string Default \'null\'");
        db.execSQL("alter table zz_order ADD actual_goodsUnit3 string Default \'null\'");
        db.execSQL("alter table zz_order ADD actual_goodsCount2 string Default \'null\'");
        db.execSQL("alter table zz_order ADD actual_goodsCount3 string Default \'null\'");
        db.execSQL("alter table order_event ADD is_damaged int Default 0");
    }

    private void updateDataBase35to36(SQLiteDatabase db) {
        db.execSQL("alter table zz_order ADD confirm_status string");
    }

    private void updateDataBase36to37(SQLiteDatabase db) {
        db.execSQL("alter table event_file ADD big_file_path string");
    }
}
