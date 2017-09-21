package com.android.zcomponent.util;


import java.lang.reflect.Method;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.component.zframework.R;
import com.android.zcomponent.constant.FrameworkR;


public class CustomDialog extends Dialog
{

    private static final int default_width = 320; // 默认宽度

    private static final int default_height = 480;// 默认高度

    private Activity mActivity;

    private TextView mtvewTitle;

    private TextView mtvewBack;

    private View mRootView;
    
    private boolean isAnimation;

    private int resAnimId;

    public CustomDialog(Context context, int layout, int style, boolean isSwipeDrawer)
    {
        this(context, default_width, default_height, layout, style, true, R.style.in_out,
            isSwipeDrawer);
    }

    public CustomDialog(Context context, int layout, boolean isSwipeDrawer)
    {
        this(context, default_width, default_height, layout, R.style.DialogCustomTheme,
            true, R.style.in_out, isSwipeDrawer);
    }

    public CustomDialog(Context context, int layout, boolean isAnimation,
            boolean isSwipeDrawer)
    {
        this(context, default_width, default_height, layout, R.style.DialogCustomTheme,
            isAnimation, R.style.in_out, isSwipeDrawer);
    }

    public CustomDialog(Context context, int layout, boolean isAnimation, int resAnimId,
            boolean isSwipeDrawer)
    {
        this(context, default_width, default_height, layout, R.style.DialogCustomTheme,
            isAnimation, resAnimId, isSwipeDrawer);
    }

    public CustomDialog(Context context, int width, int height, int layout, int style,
            boolean isAnimation, int resAnimId, boolean isSwipeDrawer)
    {
        super(context, style);
        this.mActivity = (Activity) context;
        this.isAnimation = isAnimation;
        this.resAnimId = resAnimId;
        mRootView = LayoutInflater.from(context).inflate(layout, null);
        setContentView(mRootView);
        initCustomTitleBar();
        setWindowAttributes();
    }

    /**
     * <p>
     * Description: 标题栏使用common_title_layout.xml， 初始化返回键和标题
     * <p>
     * @date 2015-2-5 
     * @author wei
     */
    private void initCustomTitleBar()
    {
        mtvewTitle = (TextView) findViewById(FrameworkR.id.common_title_tvew_txt);
        mtvewBack = (TextView) findViewById(FrameworkR.id.common_title_tvew_back);
        if (null != mtvewBack)
        {
            mtvewBack.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0)
                {
                    onTitleBarBackClick();
                }
            });
        }
    }

    private void setWindowAttributes()
    {
        Window window = getWindow();
        if (isAnimation)
        {
            window.setWindowAnimations(resAnimId);
        }
        WindowManager.LayoutParams params = window.getAttributes();
        // set width,height by density and gravity
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        params.width = dm.widthPixels;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.dimAmount = 0.3f;
        window.setAttributes(params);
    }

    public void setMaxWidth(int maxWidth)
    {
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        if (dm.widthPixels > maxWidth)
        {
            mRootView.getLayoutParams().width = maxWidth;
        }
    }
    
    
    /**
     * <p>
     * Description: 标题栏返回按钮点击回调，使用该方法需布局文件引入common_title_layout.xml文件
     * <p>
     * @date 2015-2-5 
     * @author wei
     * @param view
     */
    public void onTitleBarBackClick()
    {
        if (null != mtvewTitle)
        {
            ClientInfo.closeSoftInput(mtvewTitle, mActivity);
        }
        cancel();
    }

    /**
     * <p>
     * Description: 标题栏标题名称，使用该方法需布局文件引入common_title_layout.xml文件
     * <p>
     * @date 2015-2-5 
     * @author wei
     * @param titleId
     */
    public void setTitleBarText(int titleId)
    {
        setTitleBarText(mActivity.getString(titleId));
    }

    /**
     * <p>
     * Description: 标题栏标题名称，使用该方法需布局文件引入common_title_layout.xml文件
     * <p>
     * @date 2015-2-5 
     * @author wei
     * @param titleId
     */
    public void setTitleBarText(String title)
    {
        if (null != mtvewTitle)
        {
            mtvewTitle.setText(title);
        }
    }

    public boolean hasSmartBar()
    {
        try
        {
            // 新型号可用反射调用Build.hasSmartBar()
            Method method = Class.forName("android.os.Build").getMethod("hasSmartBar");
            return ((Boolean) method.invoke(null)).booleanValue();
        } catch (Exception e)
        {
        }
        // 反射不到Build.hasSmartBar()，则用Build.DEVICE判断
        if (Build.DEVICE.equals("mx2"))
        {
            return true;
        } else if (Build.DEVICE.equals("mx") || Build.DEVICE.equals("m9"))
        {
            return false;
        }
        return false;
    }

    @Override
    public void show()
    {
        setWindowAttributes();
        super.show();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        Log.d("CustomDialog", "onKeyDown");
        switch (event.getKeyCode())
        {
            case KeyEvent.KEYCODE_BACK : // 退出键
            {
                // 如果用户按下返回键，则交由BaseActivity处理。
                if (onKeyBack(event.getKeyCode(), event))
                {
                    return true;
                }
                break;
            }
            case KeyEvent.KEYCODE_HOME : // 返回主页按钮
            {
                break;
            }
            case KeyEvent.KEYCODE_SEARCH : // 返回搜索按钮
            {
                break;
            }
            case KeyEvent.KEYCODE_MENU : // 返回菜单按钮
            {
                break;
            }
            default : // 其他按键消息
            {
                break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public boolean onKeyBack(int iKeyCode, KeyEvent event)
    {
        return false;
    }

    public void setDimAmount(float dimAmount)
    {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        // set width,height by density and gravity
        params.gravity = Gravity.CENTER;
        params.dimAmount = dimAmount;
        window.setAttributes(params);
    }
}
