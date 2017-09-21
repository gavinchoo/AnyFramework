package com.android.zcomponent.common;


import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


public interface ITitleBar
{
    public View getView();
    
    public void onCreateView(View titlebar, View morepop);
    
    public void onDestory(View titlebar, View morepop);
    
    public void onResume();

    /**
     * <p>
     * Description: 设置标题栏标题名称，使用该方法需布局文件引入common_title_layout.xml文件
     * <p>
     * @date 2015-2-5 
     * @author wei
     * @param titleId
     */
    public void setTitleText(int titleId);
    
    /**
     * <p>
     * Description: 设置标题栏标题名称，使用该方法需布局文件引入common_title_layout.xml文件
     * <p>
     * @date 2015-2-5 
     * @author wei
     * @param title
     */
    public void setTitleText(String title);
    
    /**
     * <p>
     * Description: 设置标题栏标题字体颜色
     * <p>
     * @date 2015-3-18
     * @param color
     */
    public void setTitleColor(int color);

    /**
     * <p>
     * Description: 设置标题栏右侧按钮字体颜色
     * <p>
     * @date 2015-3-18
     * @param color
     */
    public void setTitleRightTextColor(int color);
    
    /**
     * <p>
     * Description: 显示标题栏右侧文本按钮。
     * <p>
     * @date 2015-3-18
     * @param rightTitle 按钮名称
     */
    public void showRightTextView(int rightTitle);

    /**
     * <p>
     * Description: 显示标题栏右侧文本按钮。
     * <p>
     * @date 2015-3-18
     * @param rightTitle 按钮名称
     */
    public void showRightTextView(String rightTitle);

    /**
     * <p>
     * Description: 显示标题栏右侧图片按钮。
     * <p>
     * @date 2015-3-18
     * @param imgRes 图片资源
     */
    public void showFirstRightImageButton(int imgRes);

    /**
     * <p>
     * Description: 显示标题栏右侧图片按钮。
     * <p>
     * @date 2015-3-18
     * @param imgRes 图片资源
     */
    public void showSecondRightImageButton(int imgRes);
    
    public void showRightMoreImageButton(int imgRes);
    
    /**
     * <p>
     * Description: 获取标题栏标题文本控件
     * <p>
     * @date 2015-3-18
     * @return  TextView
     */
    public TextView getTitleTextView();

    /**
     * <p>
     * Description: 获取标题栏右侧文本按钮控件
     * <p>
     * @date 2015-3-18
     * @return  TextView
     */
    public TextView getBackTextView();
    
    /**
     * <p>
     * Description: 获取标题栏右侧图片按钮控件
     * <p>
     * @date 2015-3-18
     * @return  ImageButton
     */
    public TextView getRightTextView();

    /**
     * <p>
     * Description: 获取标题栏左侧返回按钮控件
     * <p>
     * @date 2015-3-18
     * @return  TextView
     */
    public ImageButton getFirstRightImageButton();
    
    /**
     * <p>
     * Description: 获取标题栏左侧返回按钮控件
     * <p>
     * @date 2015-3-18
     * @return  TextView
     */
    public ImageButton getSecondRightImageButton();
    
    public ImageButton getRightMoreImageButton();
}
