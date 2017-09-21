
package com.android.zcomponent.util;

import android.os.Bundle;

import com.android.zcomponent.constant.GlobalConst;
import com.android.zcomponent.http.api.host.Endpoint;

public class InstanceState
{

	public static final String TAG = InstanceState.class.getName();

	public static void loadState(Bundle savedInstanceState)
	{
		loadCookieState(savedInstanceState);
		loadLayoutAdapterState(savedInstanceState);
	}

	public static void saveState(Bundle outState)
	{
		saveCookieState(outState);
		saveLayoutAdapterState(outState);
	}

	public static void loadLayoutAdapterState(Bundle savedInstanceState)
	{
		if (null != savedInstanceState
				&& savedInstanceState.getDouble("width") > 0)
		{
			MyLayoutAdapter.getInstance().setRatio(
					savedInstanceState.getDouble("ratio"));
			MyLayoutAdapter.getInstance().setPortraitSize(
					savedInstanceState.getDouble("width"),
					savedInstanceState.getDouble("height"));
		}
	}

	public static void saveLayoutAdapterState(Bundle outState)
	{
		if (null != outState)
		{
			outState.putDouble("ratio", MyLayoutAdapter.getInstance()
					.getDensityRatio());
			outState.putDouble("width", MyLayoutAdapter.getInstance()
					.getScreenWidth());
			outState.putDouble("height", MyLayoutAdapter.getInstance()
					.getScreenHeight());
		}
	}

	public static void loadCookieState(Bundle savedInstanceState)
	{
		if (null != savedInstanceState
				&& !StringUtil.isEmptyString(savedInstanceState
						.getString("cookie")))
		{
			Endpoint.Cookie = savedInstanceState.getString("cookie");

		}

		if (StringUtil.isEmptyString(Endpoint.Cookie)
				&& !StringUtil.isEmptyString(SettingsBase.getInstance()
						.readStringByKey(GlobalConst.STR_CONFIG_COOKIE)))
		{
			Endpoint.Cookie =
					SettingsBase.getInstance().readStringByKey(
							GlobalConst.STR_CONFIG_COOKIE);
		}
	}

	public static void saveCookieState(Bundle outState)
	{
		if (null != outState)
		{
			outState.putString("cookie", Endpoint.Cookie);
			SettingsBase.getInstance().writeStringByKey(
					GlobalConst.STR_CONFIG_COOKIE, Endpoint.Cookie);
		}
	}
}
