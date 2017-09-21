
package com.android.zcomponent.views.swipebacklayout.lib.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.zcomponent.views.swipebacklayout.lib.SwipeBackLayout;

public class SwipeBackFragment extends Fragment implements SwipeBackActivityBase
{

	private static final String TAG = SwipeBackFragment.class.getSimpleName();

	private SwipeBackFragmentHelper mHelper;

	protected SwipeBackLayout mSwipeBackLayout;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		mHelper = new SwipeBackFragmentHelper(this);
		mHelper.onActivityCreate();
		mSwipeBackLayout = getSwipeBackLayout();
		mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
		mHelper.onPostCreate();
	}
	
	@Override
	public void onPause()
	{
//		ClientInfo.closeSoftInput(getView(), getActivity());
		super.onPause();
	}

	public View findViewById(int id)
	{
		if (mHelper != null)
		{
			return mHelper.findViewById(id);
		}
		else
		{
			return null;
		}
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
}
