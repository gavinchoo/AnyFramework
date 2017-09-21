package com.android.zcomponent.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * 
 * <p>
 * Description: 页面尺寸变化监听类, 继承RelativeLayout，  当页面尺寸变化时， 如软键盘弹出 ，通过该类可以监听到 
 * </p>
 * @ClassName:KeyboardListenRelativeLayout  
 * @author: WEI
 * @date: 2014-3-31
 *
 */
public class KeyboardListenRelativeLayout extends RelativeLayout
{

    public KeyboardListenRelativeLayout(Context context)
    {
        super(context);
    }

    public KeyboardListenRelativeLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public KeyboardListenRelativeLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    private onKeyboardStateChangedListener mListener;

    /**
     * 
     * 页面尺寸变化监听接口
     * <p>
     * Description: 当页面尺寸变化时， 如软键盘弹出 ，通过改接口进行回调 
     * </p>
     * @ClassName:onKeyboardStateChangedListener  
     * @author: WEI
     * @date: 2013-9-23
     *
     */
    public interface onKeyboardStateChangedListener
    {
        /**
         * <p>
         * Description: 尺寸变化回调方法
         * <p>
         * @date 2013-9-23 
         * @author WEI
         * @param w        Current width of this view. 
         * @param h        Current height of this view. 
         * @param oldw     Old width of this view. 
         * @param oldh     Old height of this view.  
         */
        void onKeyboardStateChanged(boolean isKeyboardShow);

    }

    /**
     * <p>
     * Description: 设置页面尺寸变化监听
     * <p>
     * @date 2013-9-23 
     * @author WEI
     * @param l
     */
    public void setOnKeyboardStateChangedListener(onKeyboardStateChangedListener l)
    {
        mListener = l;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mListener != null)
        {
            if (h < oldh)
            {
                mListener.onKeyboardStateChanged(true);
            }
            else
            {
                mListener.onKeyboardStateChanged(false);
            }
        }
    }
}
