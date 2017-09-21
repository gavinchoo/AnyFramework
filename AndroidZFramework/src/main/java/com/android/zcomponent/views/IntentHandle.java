
package com.android.zcomponent.views;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.android.zcomponent.common.IIntent;
import com.android.zcomponent.common.uiframe.BaseActivity;
import com.android.zcomponent.util.ClientInfo;

public class IntentHandle implements IIntent
{

	private BaseActivity mActivity;

	public IntentHandle(BaseActivity activity)
	{
		mActivity = activity;
	}

	public void intentToActivity(Bundle bundle, Class<?> cls,
			boolean inFromBottom)
	{
		Intent intent = new Intent();

		if (null != bundle)
		{
			intent.putExtras(bundle);
		}

		intent.putExtra("classname", mActivity.getComponentName()
				.getClassName());
		intent.putExtra("inFromBottom", inFromBottom);
		intent.setClass(mActivity, cls);

		if (inFromBottom)
		{
			startActivityBottom(intent);
		}
		else
		{
			mActivity.startActivity(intent);
		}
	}

	public void intentToActivity(Class<?> cls, boolean inFromBottom)
	{
		intentToActivity(null, cls, inFromBottom);
	}

	public void intentToActivity(Bundle bundle, Class<?> cls)
	{
		intentToActivity(bundle, cls, false);
	}

	public void intentToActivity(Class<?> cls)
	{
		intentToActivity(null, cls, false);
	}

	public void intentToActivityForResult(Bundle bundle, Class<?> cls)
	{
		Intent intent = new Intent();

		if (null != bundle)
		{
			intent.putExtras(bundle);
		}
		intent.setClass(mActivity, cls);
		mActivity.startActivityForResult(intent, 0);
	}

	public void intentToActivityForResult(Class<?> cls)
	{
		intentToActivity(null, cls);
	}

	public void startActivityBottom(Intent intent)
	{
		mActivity.startActivity(intent);
		if (null != mActivity.getParent())
		{
			ClientInfo.overridePendingTransitionInBottom(mActivity.getParent());
		}
		else
		{
			ClientInfo.overridePendingTransitionInBottom(mActivity);
		}
	}

	public boolean isIntentFrom(Class<?> activity)
	{
		if (null != mActivity.getIntent())
		{
			String intetnFrom =
					mActivity.getIntent().getStringExtra("classname");

			if (!TextUtils.isEmpty(intetnFrom)
					&& intetnFrom.equals(activity.getName()))
			{
				return true;
			}
		}

		return false;
	}
}
