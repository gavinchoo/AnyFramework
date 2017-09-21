
package com.android.zcomponent.views.swipebacklayout.lib.app;

import android.os.Bundle;
import android.view.View;

import com.android.zcomponent.annotation.ZSwipeBack;
import com.android.zcomponent.views.slidingmenu.lib.app.SlidingFragmentActivity;
import com.android.zcomponent.views.swipebacklayout.lib.SwipeBackLayout;
import com.android.zcomponent.views.swipebacklayout.lib.SwipeBackLayout.SwipeListener;

public class SwipeBackActivity extends SlidingFragmentActivity implements
		SwipeBackActivityBase, SwipeListener
{

	private SwipeBackActivityHelper mHelper;

	protected SwipeBackLayout mSwipeBackLayout;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mHelper = new SwipeBackActivityHelper(this);
		mHelper.onActivityCreate();
		mSwipeBackLayout = getSwipeBackLayout();
		mSwipeBackLayout.addSwipeListener(this);
		mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

		setSwipeBackEnable(ZSwipeBack.Helper.isEnable(this));
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		mHelper.onPostCreate();
	}

	@Override
	public View findViewById(int id)
	{
		View v = super.findViewById(id);
		if (v == null && mHelper != null)
			return mHelper.findViewById(id);
		return v;
	}

	@Override
	public SwipeBackLayout getSwipeBackLayout()
	{
		return mHelper.getSwipeBackLayout();
	}

	@Override
	public void setSwipeBackEnable(boolean enable)
	{
		getSwipeBackLayout().setEnableGesture(enable);
	}

	@Override
	public void scrollToFinishActivity()
	{
		getSwipeBackLayout().scrollToFinishActivity();
	}

	@Override
	public void onScrollStateChange(int state, float scrollPercent)
	{
		if (state == SwipeBackLayout.STATE_IDLE && scrollPercent == 0)
		{
			mHelper.convertActivityFromTranslucent();

			if (null != getHeartNotify())
			{
				getHeartNotify().setStop(false);
				getHeartNotify().show(getTitleBar().getView());
			}
		}
		else if (state == SwipeBackLayout.STATE_DRAGGING)
		{
			if (null != getHeartNotify())
			{
				getHeartNotify().setStop(true);
				getHeartNotify().dismiss();
			}
		}
	}

	@Override
	public void onEdgeTouch(int edgeFlag)
	{
		mHelper.convertActivityToTranslucent();
	}

	@Override
	public void onScrollOverThreshold()
	{
	}

	@Override
	public void onScrollPercent(float scrollPercent)
	{
	}
}
