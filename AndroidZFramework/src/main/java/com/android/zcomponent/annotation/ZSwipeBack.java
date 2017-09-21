
package com.android.zcomponent.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @ClassName:ZSwipeBack
 * @Description: 是否支持滑动返回注解
 * @author: wei
 * @date: 2015-8-15
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ZSwipeBack
{

	public boolean Enable() default true;

	public static class Helper
	{

		public static boolean isEnable(Object instance)
		{
			if (instance.getClass().isAnnotationPresent(ZSwipeBack.class))
			{
				ZSwipeBack swipeBack =
						instance.getClass().getAnnotation(ZSwipeBack.class);
				return swipeBack.Enable();
			}

			return true;
		}
	}
}
