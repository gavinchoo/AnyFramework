
package com.android.zcomponent.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @ClassName:ZTitleMore
 * @Description: 是否标题栏显示更多
 * @author: wei
 * @date: 2015-8-15
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ZTitleMore
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

			if (instance.getClass().isAnnotationPresent(ZTitleMore.class))
			{
				ZTitleMore netNofity =
						instance.getClass().getAnnotation(ZTitleMore.class);
				if (null != netNofity)
				{
					return netNofity.value();
				}
			}
			return true;
		}
	}
}
