
package com.android.zcomponent.views.swipebacklayout.lib.app;

import java.lang.reflect.Method;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;

import com.android.component.zframework.R;
import com.android.zcomponent.views.swipebacklayout.lib.SwipeBackLayout;

/**
 * @author xicheng
 */
public class SwipeBackFragmentHelper
{

	private Fragment mFragment;

	private SwipeBackLayout mSwipeBackLayout;

	public SwipeBackFragmentHelper(Fragment fragment)
	{
		mFragment = fragment;
	}

	@SuppressWarnings("deprecation")
	public void onActivityCreate()
	{
		mFragment.getView().setBackgroundDrawable(new ColorDrawable(0));
		mSwipeBackLayout =
				(SwipeBackLayout) LayoutInflater.from(mFragment.getActivity()).inflate(
						R.layout.swipeback_layout, null);
		mSwipeBackLayout.addSwipeListener(new SwipeBackLayout.SwipeListener()
		{

			@Override
			public void onScrollStateChange(int state, float scrollPercent)
			{
				if (state == SwipeBackLayout.STATE_IDLE && scrollPercent == 0)
				{
					convertActivityFromTranslucent();
				}
			}

			@Override
			public void onEdgeTouch(int edgeFlag)
			{
				convertActivityToTranslucent();
			}

			@Override
			public void onScrollOverThreshold()
			{

			}

			@Override
			public void onScrollPercent(float scrollPercent)
			{

			}
		});
	}

	public void onPostCreate()
	{
		mSwipeBackLayout.attachToFragment(mFragment);
		convertActivityFromTranslucent();
	}

	public View findViewById(int id)
	{
		if (mSwipeBackLayout != null)
		{
			return mSwipeBackLayout.findViewById(id);
		}
		return null;
	}

	public SwipeBackLayout getSwipeBackLayout()
	{
		return mSwipeBackLayout;
	}

	/**
	 * Convert a translucent themed Activity
	 * {@link android.R.attr#windowIsTranslucent} to a fullscreen opaque
	 * Activity.
	 * <p>
	 * Call this whenever the background of a translucent Activity has changed
	 * to become opaque. Doing so will allow the {@link android.view.Surface} of
	 * the Activity behind to be released.
	 * <p>
	 * This call has no effect on non-translucent activities or on activities
	 * with the {@link android.R.attr#windowIsFloating} attribute.
	 */
	public void convertActivityFromTranslucent()
	{
		try
		{
			Method method = Dialog.class.getDeclaredMethod("convertFromTranslucent", null);
			method.setAccessible(true);
			method.invoke(mFragment, null);
		}
		catch (Throwable t)
		{
		}
	}

	/**
	 * Convert a translucent themed Activity
	 * {@link android.R.attr#windowIsTranslucent} back from opaque to
	 * translucent following a call to {@link #convertActivityFromTranslucent()}
	 * .
	 * <p>
	 * Calling this allows the Activity behind this one to be seen again. Once
	 * all such Activities have been redrawn
	 * <p>
	 * This call has no effect on non-translucent activities or on activities
	 * with the {@link android.R.attr#windowIsFloating} attribute.
	 */
	public void convertActivityToTranslucent()
	{
		try
		{
			Class<?>[] classes = Dialog.class.getDeclaredClasses();
			Class<?> translucentConversionListenerClazz = null;
			for (Class clazz : classes)
			{
				if (clazz.getSimpleName().contains("TranslucentConversionListener"))
				{
					translucentConversionListenerClazz = clazz;
				}
			}
			Method method =
					Dialog.class.getDeclaredMethod("convertToTranslucent",
							translucentConversionListenerClazz);
			method.setAccessible(true);
			method.invoke(mFragment, new Object[] { null });
		}
		catch (Throwable t)
		{
		}
	}
}
