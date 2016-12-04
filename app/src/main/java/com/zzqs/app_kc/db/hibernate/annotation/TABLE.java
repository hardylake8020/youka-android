package com.zzqs.app_kc.db.hibernate.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target( { java.lang.annotation.ElementType.TYPE })
public @interface TABLE {
	/**
	 * 表明
	 */
	public abstract String name();
}
