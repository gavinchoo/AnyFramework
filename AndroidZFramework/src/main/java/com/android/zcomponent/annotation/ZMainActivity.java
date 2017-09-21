
package com.android.zcomponent.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @ClassName:ZMainActivity
 * @Description: 主Activity 注解
 * @author: wei
 * @date: 2015-8-15
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ZMainActivity
{

	public static class Helper
	{

		public static boolean isMainActivity(Object instance)
		{
			if (null == instance)
			{
				return false;
			}
			if (instance.getClass().isAnnotationPresent(ZMainActivity.class))
			{
				return true;
			}

			return false;
		}
	}
}
