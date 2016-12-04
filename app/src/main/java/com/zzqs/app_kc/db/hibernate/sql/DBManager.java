package com.zzqs.app_kc.db.hibernate.sql;

import android.database.sqlite.SQLiteDatabase;

import com.zzqs.app_kc.db.hibernate.annotation.COLUMN;
import com.zzqs.app_kc.db.hibernate.annotation.ID;
import com.zzqs.app_kc.db.hibernate.annotation.TABLE;
import com.zzqs.app_kc.utils.StringTools;

import java.lang.reflect.Field;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author jyy
 * @2014年4月12日  下午2:47:46
 * TODO 数据库维护类
 *
 */
public class DBManager {
	private static final String TAG="Hibernate";
	public static<T>void createTableByClasses(SQLiteDatabase db,Class<?>[]clazzs){
		
		//遍历创建db table
		for(Class<?>clazz:clazzs){
			createTable(db,clazz);
			
		}
	}
	
	public static<T>void dropTablesByClasses(SQLiteDatabase db,Class<?>[]clazzs){
		for(Class<?> clazz:clazzs){
			dropTable(db,clazz);
		}
		
	}
	/**
	 * 创建表
	 * @param db
	 * @param clazz
	 */
	private static<T>void createTable(SQLiteDatabase db,Class clazz){
		String tableName="";
		if(clazz.isAnnotationPresent(TABLE.class)){//是注解table类
			TABLE t=(TABLE) clazz.getAnnotation(TABLE.class);
			tableName=t.name();
			if(!StringTools.isEmp(tableName)){//表存在才操作
				StringBuilder sb=new StringBuilder();
				sb.append("create table if not exists ").append(tableName).append(" (");
				List<Field>allFields= DBManager.joinFields(clazz.getDeclaredFields(), clazz.getSuperclass().getDeclaredFields());
				//遍历所有column列 用于拼接sql
				for(Field f:allFields){
					COLUMN column=f.getAnnotation(COLUMN.class);
					String columnType="";//创造当前的type
					if(column.type().equals("")){
						columnType=getColumnType(f.getType());
					}else{
						columnType=column.type();
					}
					sb.append(column.name()+" "+columnType);
					if(column.length()!=0){
						sb.append("(" + column.length() + ")");
					}
					if((f.isAnnotationPresent(ID.class))&&(f.getType()==Integer.class||f.getType()==Integer.TYPE)){
						sb.append(" primary key autoincrement");
					}else if(f.isAnnotationPresent(ID.class)){
						sb.append(" primary key");
					}
					sb.append(", ");
					
				}
				sb.delete(sb.length() - 2, sb.length() - 1);
				sb.append(")");//删除最后一个
				String sql=sb.toString();

				db.execSQL(sql);
			}
			
		}else{
			//不做操作 因为当前不是注解实体类
		}
		
	}
	
	
	/**
	 * 删除表
	 * @return
	 */
	private static void dropTable(SQLiteDatabase db,Class<?>clazz){
		String tableName="";
		if(clazz.isAnnotationPresent(TABLE.class)){
			TABLE table=clazz.getAnnotation(TABLE.class);
			tableName=table.name();
		}
		String sql=" Drop table if exists "+tableName;

		db.execSQL(sql);
		
	}

	
	private static String getColumnType(Class<?>fieldType){
		if(String.class==fieldType){
			return "TEXT";
		}else if((Integer.TYPE==fieldType)||(Integer.class==fieldType)){
			return "INTEGER";
		}else if((Long.TYPE==fieldType)||(Long.class==fieldType)){
			return "BIGINT";
		}else if((Float.TYPE==fieldType)||(Float.class==fieldType)){
			return "FLOAT";
		}else if((Short.TYPE==fieldType)||(Short.class==fieldType)){
			return "INT";
		}else if((Double.TYPE==fieldType)||(Double.class==fieldType)){
			return "Double";
		}else if(Blob.class==fieldType){
			return "BLOB";
		}
		return "TEXT";
		
	}
	
	/**
	 * 
	 * @param fields1 当前类所有字段
	 * @param fiedls2 当前类父类所有字段 
	 * @return 去掉非column字段 并且id放在首页
	 */
	public static List<Field>joinFields(Field[]fields1,Field[]fiedls2){
		Map<String,Field>map=new HashMap<String,Field>();
		for(Field f:fields1){
			//过滤到非column字段
			if(!f.isAnnotationPresent(COLUMN.class)){
				continue;
			}
			COLUMN column=f.getAnnotation(COLUMN.class);
			map.put(column.name(),f);
		}
		for(Field f1:fiedls2){
			//过滤到非column字段
			if(!f1.isAnnotationPresent(COLUMN.class)){
				continue;
			}
			COLUMN column=f1.getAnnotation(COLUMN.class);
			if(!map.containsKey(column.name())){
				map.put(column.name(), f1);
			}
		}
		List<Field>list=new ArrayList<Field>();
		for(String key:map.keySet()){
			Field ff=map.get(key);
			//把id放在第一个位置
			if(ff.isAnnotationPresent(ID.class)){
				list.add(0,ff);
			}else{
				list.add(ff);
			}
			
		}
		return list;
		
	}
}
