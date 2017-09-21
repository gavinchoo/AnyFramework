package com.android.zcomponent.views.slidingmenu.lib.app;


import android.graphics.Canvas;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Interpolator;

import com.android.zcomponent.common.uiframe.BaseActivity;
import com.android.zcomponent.views.slidingmenu.lib.SlidingMenu;
import com.android.zcomponent.views.slidingmenu.lib.SlidingMenu.CanvasTransformer;


public class SlidingFragmentActivity extends BaseActivity implements SlidingActivityBase
{

    /**
     * 分类菜单缩放动画
     */
    protected CanvasTransformer mTransformerScale = new CanvasTransformer() {

        @Override
        public void transformCanvas(Canvas canvas, float percentOpen)
        {
            canvas.scale(percentOpen, 1, 0, 0);
        }
    };

    /**
     * 分类菜单渐隐动画
     */
    protected CanvasTransformer mTransformerZoom = new CanvasTransformer() {

        @Override
        public void transformCanvas(Canvas canvas, float percentOpen)
        {
            float scale = (float) (percentOpen * 0.25 + 0.75);
            canvas.scale(scale, scale, canvas.getWidth() / 2, canvas.getHeight() / 2);
        }
    };

    /**
     * 分类菜单上滑动画
     */
    protected CanvasTransformer mTransformerSlideUp = new CanvasTransformer() {

        public void transformCanvas(Canvas canvas, float percentOpen)
        {
            canvas.translate(0,
                canvas.getHeight() * (1 - interp.getInterpolation(percentOpen)));
        }
    };

    protected static Interpolator interp = new Interpolator() {

        @Override
        public float getInterpolation(float t)
        {
            t -= 1.0f;
            return t * t * t + 1.0f;
        }
    };

    private SlidingActivityHelper mHelper;

    protected boolean isSlidingMenu = false;

    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (isSlidingMenu)
        {
            mHelper = new SlidingActivityHelper(this);
            mHelper.onCreate(savedInstanceState);
        }
    }

    protected void setSlidingMenu(boolean isSlidingMenu)
    {
        this.isSlidingMenu = isSlidingMenu;
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPostCreate(android.os.Bundle)
     */
    @Override
    public void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        if (isSlidingMenu)
        {
            mHelper.onPostCreate(savedInstanceState);
        }
    }

    /* (non-Javadoc)
     * @see android.app.Activity#findViewById(int)
     */
    @Override
    public View findViewById(int id)
    {
        View v = super.findViewById(id);

        if (isSlidingMenu)
        {
            if (v != null)
                return v;
            return mHelper.findViewById(id);
        } else
        {
            return v;
        }
    }

    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (isSlidingMenu)
        {
            mHelper.onSaveInstanceState(outState);
        }

    }

    /* (non-Javadoc)
     * @see android.app.Activity#setContentView(int)
     */
    @Override
    public void setContentView(int id)
    {
        super.setContentView(id);
        registerAboveContentView(getLayoutInflater().inflate(id, null), new LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    /* (non-Javadoc)
     * @see android.app.Activity#setContentView(android.view.View)
     */
    @Override
    public void setContentView(View v)
    {
        super.setContentView(v);
        registerAboveContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT));
    }

    /* (non-Javadoc)
     * @see android.app.Activity#setContentView(android.view.View, android.view.ViewGroup.LayoutParams)
     */
    @Override
    public void setContentView(View v, LayoutParams params)
    {
        super.setContentView(v, params);
        registerAboveContentView(v, params);
    }

    private void registerAboveContentView(View v, LayoutParams params)
    {
        if (isSlidingMenu)
        {
            mHelper.registerAboveContentView(v, params);
        }
    }

    /* (non-Javadoc)
     * @see com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityBase#setBehindContentView(int)
     */
    public void setBehindContentView(int id)
    {
        setBehindContentView(getLayoutInflater().inflate(id, null));
    }

    /* (non-Javadoc)
     * @see com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityBase#setBehindContentView(android.view.View)
     */
    public void setBehindContentView(View v)
    {
        setBehindContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT));
    }

    /* (non-Javadoc)
     * @see com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityBase#setBehindContentView(android.view.View, android.view.ViewGroup.LayoutParams)
     */
    public void setBehindContentView(View v, LayoutParams params)
    {
        if (isSlidingMenu)
        {
            mHelper.setBehindContentView(v, params);
        }
    }

    /* (non-Javadoc)
     * @see com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityBase#getSlidingMenu()
     */
    public SlidingMenu getSlidingMenu()
    {
        if (isSlidingMenu)
        {
            return mHelper.getSlidingMenu();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityBase#toggle()
     */
    public void toggle()
    {
        if (isSlidingMenu)
        {
            mHelper.toggle();
        }

    }

    /* (non-Javadoc)
     * @see com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityBase#showAbove()
     */
    public void showContent()
    {
        if (isSlidingMenu)
        {
            mHelper.showContent();
        }

    }

    /* (non-Javadoc)
     * @see com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityBase#showBehind()
     */
    public void showMenu()
    {
        if (isSlidingMenu)
        {
            mHelper.showMenu();
        }

    }

    /* (non-Javadoc)
     * @see com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityBase#showSecondaryMenu()
     */
    public void showSecondaryMenu()
    {
        if (isSlidingMenu)
        {
            mHelper.showSecondaryMenu();
        }

    }

    /* (non-Javadoc)
     * @see com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityBase#setSlidingActionBarEnabled(boolean)
     */
    public void setSlidingActionBarEnabled(boolean b)
    {
        if (isSlidingMenu)
        {
            mHelper.setSlidingActionBarEnabled(b);
        }

    }

    /* (non-Javadoc)
     * @see android.app.Activity#onKeyUp(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        if (isSlidingMenu)
        {
            boolean b = mHelper.onKeyUp(keyCode, event);
            if (b)
                return b;
        }
        return super.onKeyUp(keyCode, event);
    }

}
