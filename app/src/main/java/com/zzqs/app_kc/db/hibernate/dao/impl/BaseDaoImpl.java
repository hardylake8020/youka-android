package com.zzqs.app_kc.db.hibernate.dao.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.zzqs.app_kc.db.DBHelper;
import com.zzqs.app_kc.db.hibernate.annotation.COLUMN;
import com.zzqs.app_kc.db.hibernate.annotation.ID;
import com.zzqs.app_kc.db.hibernate.annotation.TABLE;
import com.zzqs.app_kc.db.hibernate.dao.BaseDao;
import com.zzqs.app_kc.db.hibernate.sql.DBManager;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BaseDaoImpl<T> implements BaseDao<T> {
    private final static String TAG = "AHibernate";
    private SQLiteOpenHelper dbHelper;
    private String tableName;
    private String idColumn;
    private Class<T> clazz;
    private List<Field> allFields;
    private SQLiteDatabase db;

    public BaseDaoImpl(SQLiteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
        Type type = getClass().getGenericSuperclass();

        //modifide by lx
        if (!ParameterizedType.class.isAssignableFrom(type.getClass())) {
            type = super.getClass().getGenericSuperclass();
            if (!ParameterizedType.class.isAssignableFrom(type.getClass())) {
                Type[] types = getClass().getGenericInterfaces();
                if (types.length > 0 && ParameterizedType.class.isAssignableFrom(types[0].getClass())) {
                    type = types[0];
                } else {
                    type = null;
                }
            }
        }

        if (type == null)
            return;
        this.clazz = ((Class<T>) ((ParameterizedType) type).getActualTypeArguments()[0]);
        if (this.clazz.isAnnotationPresent(TABLE.class)) {
            TABLE table = (TABLE) this.clazz.getAnnotation(TABLE.class);
            this.tableName = table.name();
        }

        // 加载所有字段
        this.allFields = DBManager.joinFields(this.clazz.getDeclaredFields(),
                this.clazz.getSuperclass().getDeclaredFields());

        // 找到主键
        for (Field field : this.allFields) {
            if (field.isAnnotationPresent(ID.class)) {
                COLUMN column = (COLUMN) field.getAnnotation(COLUMN.class);
                this.idColumn = column.name();
                break;
            }
        }

        Log.d(TAG, "clazz:" + this.clazz + " tableName:" + this.tableName
                + " idColumn:" + this.idColumn);
    }

    public SQLiteOpenHelper getDbHelper() {
        return dbHelper;
    }

    public synchronized T get(int id) {
        String selection = this.idColumn + " = ?";
        String[] selectionArgs = {Integer.toString(id)};
        Log.d(TAG, "[get]: select * from " + this.tableName + " where "
                + this.idColumn + " = '" + id + "'");
        List<T> list = find(null, selection, selectionArgs, null, null, null,
                null);
        if ((list != null) && (list.size() > 0)) {
            return (T) list.get(0);
        }
        return null;
    }

    public synchronized List<T> rawQuery(String sql, String[] selectionArgs) {
        Log.d(TAG, "[rawQuery]: " + sql);
        List<T> list = new ArrayList<T>();
        Cursor cursor = null;
        synchronized (DBHelper.dbLock) {
            try {
                db = this.dbHelper.getReadableDatabase();
                cursor = db.rawQuery(sql, selectionArgs);
                getListFromCursor(list, cursor);
            } catch (Exception e) {
                Log.e(this.TAG, "[rawQuery] from DB Exception.");
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                if (db != null) {
                    db.close();
                }
            }
        }
        return list;
    }

    public synchronized boolean isExist(String sql, String[] selectionArgs) {
        Log.d(TAG, "[isExist]: " + sql);
        Cursor cursor = null;
        synchronized (DBHelper.dbLock) {
            try {
                db = this.dbHelper.getReadableDatabase();
                cursor = db.rawQuery(sql, selectionArgs);
                if (cursor.getCount() > 0) {
                    return true;
                }
            } catch (Exception e) {
                Log.e(this.TAG, "[isExist] from DB Exception.");
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                if (db != null) {
                    db.close();
                }
            }
        }
        return false;
    }

    public synchronized List<T> find() {
        return find(null, null, null, null, null, null, null);
    }

    public synchronized List<T> find(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        List<T> list = new ArrayList<T>();
        Cursor cursor = null;
        synchronized (DBHelper.dbLock) {
            try {
                db = this.dbHelper.getReadableDatabase();
                cursor = db.query(this.tableName, columns, selection,
                        selectionArgs, groupBy, having, orderBy, limit);

                getListFromCursor(list, cursor);
            } catch (Exception e) {
                Log.e(this.TAG, "[find] from DB Exception");
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                if (db != null) {
                    db.close();
                }
            }
        }
        return list;
    }

    private synchronized void getListFromCursor(List<T> list, Cursor cursor)
            throws IllegalAccessException, InstantiationException {
        while (cursor.moveToNext()) {
            T entity = this.clazz.newInstance();

            for (Field field : this.allFields) {
                COLUMN column = null;
                if (field.isAnnotationPresent(COLUMN.class)) {
                    column = (COLUMN) field.getAnnotation(COLUMN.class);

                    field.setAccessible(true);
                    Class<?> fieldType = field.getType();

                    int c = cursor.getColumnIndex(column.name());
                    if (c < 0) {
                        continue; // 如果不存则循环下个属性值
                    } else if ((Integer.TYPE == fieldType)
                            || (Integer.class == fieldType)) {
                        field.set(entity, cursor.getInt(c));
                    } else if (String.class == fieldType) {
                        field.set(entity, cursor.getString(c));
                    } else if ((Long.TYPE == fieldType)
                            || (Long.class == fieldType)) {
                        field.set(entity, Long.valueOf(cursor.getLong(c)));
                    } else if ((Float.TYPE == fieldType)
                            || (Float.class == fieldType)) {
                        field.set(entity, Float.valueOf(cursor.getFloat(c)));
                    } else if ((Short.TYPE == fieldType)
                            || (Short.class == fieldType)) {
                        field.set(entity, Short.valueOf(cursor.getShort(c)));
                    } else if ((Double.TYPE == fieldType)
                            || (Double.class == fieldType)) {
                        field.set(entity, Double.valueOf(cursor.getDouble(c)));
                    } else if (Blob.class == fieldType) {
                        field.set(entity, cursor.getBlob(c));
                    } else if (Character.TYPE == fieldType) {
                        String fieldValue = cursor.getString(c);

                        if ((fieldValue != null) && (fieldValue.length() > 0)) {
                            field.set(entity, Character.valueOf(fieldValue
                                    .charAt(0)));
                        }
                    }
                }
            }

            list.add((T) entity);
        }
    }

    public synchronized long insert(T entity) {
        synchronized (DBHelper.dbLock) {
            try {
                db = this.dbHelper.getWritableDatabase();
                ContentValues cv = new ContentValues();
                setContentValues(entity, cv, "create");
                long row = db.insert(this.tableName, null, cv);
                return row;
            } catch (Exception e) {
                Log.d(this.TAG, "[insert] into DB Exception.");
                e.printStackTrace();
            } finally {
                if (db != null) {
                    db.close();
                }
            }
        }
        return 0L;
    }

    public synchronized long inserts(ArrayList<T> entities) {
        synchronized (DBHelper.dbLock) {
            db = this.dbHelper.getWritableDatabase();
            db.beginTransaction();
            long rows = 0;
            try {
                for (T entity : entities) {
                    ContentValues cv = new ContentValues();
                    setContentValues(entity, cv, "create");
                    rows = db.insert(this.tableName, null, cv);
                }
                db.setTransactionSuccessful();
                return rows;
            } catch (Exception e) {
                Log.d(this.TAG, "[inserts] into DB Exception.");
                e.printStackTrace();
            } finally {
                db.endTransaction();
                if (db != null) {
                    db.close();
                }
            }
        }
        return 0L;
    }

    public synchronized void delete(int id) {
        synchronized (DBHelper.dbLock) {
            db = this.dbHelper.getWritableDatabase();
            String where = this.idColumn + " = ?";
            String[] whereValue = {Integer.toString(id)};
            db.delete(this.tableName, where, whereValue);
            if (db != null) {
                db.close();
            }
        }
    }

    public synchronized void delete(Integer... ids) {
        if (ids.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ids.length; i++) {
                sb.append('?').append(',');
            }
            sb.deleteCharAt(sb.length() - 1);
            synchronized (DBHelper.dbLock) {
                db = this.dbHelper.getWritableDatabase();
                String sql = "delete from " + this.tableName + " where "
                        + this.idColumn + " in (" + sb + ")";
                db.execSQL(sql, (Object[]) ids);
                if (db != null) {
                    db.close();
                }
            }
        }
    }

    public synchronized void update(T entity) {
        synchronized (DBHelper.dbLock) {
            try {
                db = this.dbHelper.getWritableDatabase();
                ContentValues cv = new ContentValues();
                setContentValues(entity, cv, "update");
                String where = this.idColumn + " = ?";
                int id = Integer.parseInt(cv.get(this.idColumn).toString());
                cv.remove(this.idColumn);
                String[] whereValue = {Integer.toString(id)};
                db.update(this.tableName, cv, where, whereValue);
            } catch (Exception e) {
                Log.d(this.TAG, "[update] DB Exception.");
                e.printStackTrace();
            } finally {
                if (db != null) {
                    db.close();
                }
            }
        }
    }

    public synchronized void updates(ArrayList<T> entities) {
        synchronized (DBHelper.dbLock) {
            db = this.dbHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                for (T entity : entities) {
                    ContentValues cv = new ContentValues();
                    setContentValues(entity, cv, "update");
                    String where = this.idColumn + " = ?";
                    int id = Integer.parseInt(cv.get(this.idColumn).toString());
                    cv.remove(this.idColumn);
                    String[] whereValue = {Integer.toString(id)};
                    db.update(this.tableName, cv, where, whereValue);
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                Log.d(this.TAG, "[inserts] into DB Exception.");
                e.printStackTrace();
            } finally {
                db.endTransaction();
                if (db != null) {
                    db.close();
                }
            }
        }
    }

    private synchronized void setContentValues(T entity, ContentValues cv, String type)
            throws IllegalAccessException {

        for (Field field : this.allFields) {
            if (!field.isAnnotationPresent(COLUMN.class)) {
                continue;
            }
            COLUMN column = (COLUMN) field.getAnnotation(COLUMN.class);

            field.setAccessible(true);
            Object fieldValue = field.get(entity);
            if (fieldValue == null)
                continue;
            if (("create".equals(type))
                    && (field.isAnnotationPresent(ID.class))) {
                continue;
            }
            cv.put(column.name(), fieldValue.toString());
        }
    }

    /**
     * 将查询的结果保存为名值对map.
     *
     * @param sql           查询sql
     * @param selectionArgs 参数值
     * @return 返回的Map中的key全部是小写形式.
     */
    public synchronized List<Map<String, String>> query2MapList(String sql, String[] selectionArgs) {
        Cursor cursor = null;
        List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
        synchronized (DBHelper.dbLock) {
            try {
                db = this.dbHelper.getReadableDatabase();
                cursor = db.rawQuery(sql, selectionArgs);
                while (cursor.moveToNext()) {
                    Map<String, String> map = new HashMap<String, String>();
                    for (String columnName : cursor.getColumnNames()) {
                        map.put(columnName.toLowerCase(), cursor.getString(cursor
                                .getColumnIndex(columnName)));
                    }
                    retList.add(map);
                }
            } catch (Exception e) {
                Log.e(TAG, "[query2MapList] from DB exception");
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                if (db != null) {
                    db.close();
                }
            }
        }
        return retList;
    }

    /**
     * 封装执行sql代码.
     *
     * @param sql
     * @param selectionArgs
     */
    public synchronized void execSql(String sql, Object[] selectionArgs) {
        synchronized (DBHelper.dbLock) {
            try {
                db = this.dbHelper.getWritableDatabase();
                if (db != null) {
                    if (selectionArgs == null) {
                        db.execSQL(sql);
                    } else {
                        db.execSQL(sql, selectionArgs);
                    }
                } else {
                    return;
                }
            } catch (Exception e) {
                Log.e(TAG, "[execSql] DB exception.");
                e.printStackTrace();
            } finally {
                if (db != null) {
                    db.close();
                }
            }
        }
    }
}
