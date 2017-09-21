package com.android.zcomponent.views.swipebacklayout.lib.app;


import com.android.zcomponent.views.swipebacklayout.lib.SwipeBackLayout;


/**
 * @author xicheng
 */
public interface SwipeBackDialogBase
{

    /**
     * @return the SwipeBackLayout associated with this activity.
     */
    public abstract SwipeBackLayout getSwipeBackLayout();

    public abstract void setSwipeBackEnable(boolean enable);

    /**
     * Scroll out contentView and finish the activity
     */
    public abstract void scrollToFinishActivity();

}
