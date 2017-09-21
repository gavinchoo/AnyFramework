package com.android.zcomponent.views.swipebacklayout.lib.app;


import java.lang.reflect.Method;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import com.android.component.zframework.R;
import com.android.zcomponent.views.swipebacklayout.lib.SwipeBackLayout;


/**
 * @author xicheng
 */
public class SwipeBackDialogHelper
{

    private Dialog mDialog;

    private SwipeBackLayout mSwipeBackLayout;

    public SwipeBackDialogHelper(Dialog dialog)
    {
        mDialog = dialog;
    }

    @SuppressWarnings("deprecation")
    public void onActivityCreate()
    {
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        mDialog.getWindow().getDecorView().setBackgroundDrawable(null);
        mSwipeBackLayout = (SwipeBackLayout) LayoutInflater.from(mDialog.getContext())
                .inflate(R.layout.swipeback_layout, null);
        mSwipeBackLayout.addSwipeListener(new SwipeBackLayout.SwipeListener() {

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
        mSwipeBackLayout.attachToDialog(mDialog);
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
            Method method = Dialog.class.getDeclaredMethod("convertFromTranslucent",
                null);
            method.setAccessible(true);
            method.invoke(mDialog, null);
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
            Method method = Dialog.class.getDeclaredMethod("convertToTranslucent",
                translucentConversionListenerClazz);
            method.setAccessible(true);
            method.invoke(mDialog, new Object[]{null});
        }
        catch (Throwable t)
        {
        }
    }
}
