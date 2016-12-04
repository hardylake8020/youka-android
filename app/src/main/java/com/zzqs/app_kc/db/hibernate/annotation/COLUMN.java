package com.zzqs.app_kc.db.hibernate.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.FIELD})
public @interface COLUMN {
	/**
	 * 列属性
	 */
	public abstract String name();
	public abstract String type()default "";
	public abstract int length()default 0;
	public abstract boolean auto()default true;
}
