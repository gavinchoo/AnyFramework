
package com.android.zcomponent.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @ClassName:ZSwipeBack
 * @Description: 是否需要网络提示注解
 * @author: wei
 * @date: 2015-8-15
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ZNetNotify
{

	public boolean value() default true;

	public static class Helper
	{

		public static boolean isEnable(Object instance)
		{
			if (null == instance)
			{
				return false;
			}

			if (instance.getClass().isAnnotationPresent(ZNetNotify.class))
			{
				ZNetNotify netNofity =
						instance.getClass().getAnnotation(ZNetNotify.class);
				if (null != netNofity)
				{
					return netNofity.value();
				}
			}
			return true;
		}
	}
}
