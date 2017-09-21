/**
 * <p>
 * Copyright: Copyright (c) 2014
 * Company: 
 * Description: 这里写这个文件是干什么用的
 * </p>
 * @Title ComponentR.java
 * @Package com.android.zcomponent.constant
 * @version 1.0
 * @author WEI
 * @date 2014-7-29
 */
package com.android.zcomponent.constant;

import android.content.Context;

/**
 * <p>
 * Description: 某某某类, 这里用一句话描述这个类的作用
 * </p>
 * 
 * @ClassName:ComponentR
 * @author: WEI
 * @date: 2014-7-29
 * 
 */
public class FrameworkR
{
	private static Context mContext;

	public FrameworkR(Context context)
	{
		mContext = context;
		new layout();
		new id();
		new drawable();
		new string();
		new color();
		new anim();
	}

	public static int getIdentifierId(String name)
	{
		return mContext.getResources().getIdentifier(name, "id",
				mContext.getPackageName());
	}

	public static int getIdentifierLayout(String name)
	{
		return mContext.getResources().getIdentifier(name, "layout",
				mContext.getPackageName());
	}

	public static int getIdentifierDrawable(String name)
	{
		return mContext.getResources().getIdentifier(name, "drawable",
				mContext.getPackageName());
	}

	public static int getIdentifierString(String name)
	{
		return mContext.getResources().getIdentifier(name, "string",
				mContext.getPackageName());
	}

	public static int getIdentifierColor(String name)
	{
		return mContext.getResources().getIdentifier(name, "color",
				mContext.getPackageName());
	}

	public static int getIdentifierAnim(String name)
	{
		return mContext.getResources().getIdentifier(name, "anim",
				mContext.getPackageName());
	}

	public static int[] getIdentifierStyleable(String name)
	{
		return mContext.getResources().getIntArray(
				mContext.getResources().getIdentifier(name, "styleable",
						mContext.getPackageName()));
	}

	public static final class id
	{
		/**
		 * Messages IDs
		 */
		
        public static int common_title_tvew_txt;
        
        public static int common_title_tvew_back;
        
        public static int common_title_rlayout_parent;
        
        
		public id()
		{
			common_title_tvew_txt = getIdentifierId("common_title_tvew_txt");
			common_title_tvew_back = getIdentifierId("common_title_tvew_back");
			common_title_rlayout_parent = getIdentifierId("common_title_rlayout_parent");
		}
	}

	public static final class color
	{
		

		public color()
		{
		}
	}

	public static final class layout
	{
		public layout()
		{
		}
	}

	public static final class drawable
	{
		public drawable()
		{
		}
	}

	public static final class string
	{

		public string()
		{}
	}

	public static final class anim
	{
		public anim()
		{}
	}
}
