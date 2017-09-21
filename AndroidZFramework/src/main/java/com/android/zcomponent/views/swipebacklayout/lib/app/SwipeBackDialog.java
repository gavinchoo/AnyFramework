package com.android.zcomponent.views.swipebacklayout.lib.app;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.android.zcomponent.util.CustomDialog;
import com.android.zcomponent.views.swipebacklayout.lib.SwipeBackLayout;


public class SwipeBackDialog extends CustomDialog implements SwipeBackDialogBase
{

	private List<View> mAllViews;

    private static final String TAG = SwipeBackDialog.class.getSimpleName();

    private SwipeBackDialogHelper mHelper;

    protected SwipeBackLayout mSwipeBackLayout;
    
    public SwipeBackDialog(Context context, int layout, boolean isAnimation,
			boolean isSwipeDrawer)
	{
		super(context, layout, isAnimation, isSwipeDrawer);
		init();
		setSwipeBackEnable(isSwipeDrawer);
	}

	public SwipeBackDialog(Context context, int layout, boolean isAnimation,
			int resAnimId, boolean isSwipeDrawer)
	{
		super(context, layout, isAnimation, resAnimId, isSwipeDrawer);
		init();
		setSwipeBackEnable(isSwipeDrawer);
	}

	public SwipeBackDialog(Context context, int layout, boolean isSwipeDrawer)
	{
		super(context, layout, isSwipeDrawer);
		init();
		setSwipeBackEnable(isSwipeDrawer);
	}

	public SwipeBackDialog(Context context, int layout, int style,
			boolean isSwipeDrawer)
	{
		super(context, layout, style, isSwipeDrawer);
		init();
		setSwipeBackEnable(isSwipeDrawer);
	}

	public SwipeBackDialog(Context context, int width, int height, int layout,
			int style, boolean isAnimation, int resAnimId, boolean isSwipeDrawer)
	{
		super(context, width, height, layout, style, isAnimation, resAnimId,
				isSwipeDrawer);
		init();
		setSwipeBackEnable(isSwipeDrawer);
	}
    
    private void init()
    {
        mHelper = new SwipeBackDialogHelper(this);
        mHelper.onActivityCreate();
        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        mHelper.onPostCreate();
        
        mAllViews = getAllChildViews(mSwipeBackLayout);
    }

    private List<View> getAllChildViews(View view)
    {

        List<View> allchildren = new ArrayList<View>();

        if (view instanceof ViewGroup)
        {

            ViewGroup vp = (ViewGroup) view;

            for (int i = 0; i < vp.getChildCount(); i++)
            {

                View viewchild = vp.getChildAt(i);

                allchildren.add(viewchild);

                allchildren.addAll(getAllChildViews(viewchild));
            }
        }
        else
        {
            allchildren.add(view);
        }

        return allchildren;
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
}
