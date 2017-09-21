package com.android.zcomponent.common;


import android.widget.LinearLayout;

import com.android.zcomponent.http.api.model.MessageData;


public interface IDataEmpty
{


    /**
     * <p>
     * Description: 获取空布局View
     * <p>
     * @date 2015-3-18 
     * @author wei
     * @return
     */
    public LinearLayout getView();
    

    /**
     * <p>
     * Description: 设置布局隐藏、显示
     * <p>
     * @date 2015-3-18 
     * @author wei
     * @param visibility View.Gone  View.VISIBLE
     */
    public void setVisibility(int visibility);

    /**
     * <p>
     * Description: 设置布局隐藏、显示
     * <p>
     * @date 2015-3-18 
     * @author wei
     * @param visibility View.Gone  View.VISIBLE
     */
    public void dismiss();
    
    /**
     * <p>
     * Description: 移除view
     * <p>
     * @date 2015-3-18 
     * @author wei
     */
    public void removeAllViews();

    /**
     * <p>
     * Description: 显示数据请求等待布局
     * <p>
     * @date 2015-3-18 
     * @author wei
     */
    public void showViewWaiting();

    /**
     * <p>
     * Description: 数据为空时显示页面提示布局
     * <p>
     * 
     * @date 2013-8-21
     * @author WEI
     * @param isClickable
     *            是否接收点击事件
     * @param isSuportRefresh
     *            是否可以下拉刷新， 如果不是接收点击刷新
     * @param returnCode
     *            错误返回码
     * @param resEmptyId
     *            数据为空提示信息
     * @return
     */
    public void showViewDataEmpty(boolean isClickable, boolean isSuportRefresh,
                                  int returnCode, int resEmptyId);

    /**
     * <p>
     * Description: 数据为空时显示页面提示布局
     * <p>
     * 
     * @date 2013-8-21
     * @author WEI
     * @param isClickable
     *            是否接收点击事件
     * @param isSuportRefresh
     *            是否可以下拉刷新， 如果不是接收点击刷新
     * @param returnCode
     *            错误返回码
     * @param resEmpty
     *            数据为空提示信息
     * @return
     */
    public void showViewDataEmpty(boolean isClickable, boolean isSuportRefresh,
                                  int returnCode, String resEmpty);

    /**
     * <p>
     * Description: 数据为空时显示页面提示布局
     * <p>
     * 
     * @date 2013-8-21
     * @author WEI
     * @param isClickable
     *            是否接收点击事件
     * @param isSuportRefresh
     *            是否可以下拉刷新， 如果不是接收点击刷新
     * @param isNeedChange
     *            是否需要接受自定义事件
     * @param returnCode
     *            错误返回码
     * @param resEmpty
     *            数据为空提示信息
     * @param strChangeTitle
     *            自定义事件标题
     * @return
     */
    public void showViewDataEmpty(boolean isClickable, boolean isSuportRefresh, boolean isNeedChange,
                                  int returnCode, String resEmpty, String strChangeTitle);
    
    /**
     * <p>
     * Description: 数据为空时显示页面提示布局
     * <p>
     * 
     * @date 2013-8-21
     * @author WEI
     * @param isClickable
     *            是否接收点击事件
     * @param isSuportRefresh
     *            是否可以下拉刷新， 如果不是接收点击刷新
     * @param isNeedChange
     *            是否需要接受自定义事件
     * @param returnCode
     *            错误返回码
     * @param resEmpty
     *            数据为空提示信息
     * @param strChangeTitle
     *            自定义事件标题
     * @return
     */
    public void showViewDataEmpty(boolean isClickable, boolean isSuportRefresh, boolean isNeedChange,
                                  int returnCode, String resEmpty, int strChangeTitle);
    
    
    /**
     * <p>
     * Description: 数据为空时显示页面提示布局
     * <p>
     * 
     * @date 2013-8-21
     * @author WEI
     * @param isClickable
     *            是否接收点击事件
     * @param isSuportRefresh
     *            是否可以下拉刷新， 如果不是接收点击刷新
     * @param isNeedChange
     *            是否需要接受自定义事件
     * @param returnCode
     *            错误返回码
     * @param resEmpty
     *            数据为空提示信息
     * @param strChangeTitle
     *            自定义事件标题
     * @return
     */
    public void showViewDataEmpty(boolean isClickable, boolean isSuportRefresh,
                                  boolean isNeedChange, int returnCode, int resEmpty, int strChangeTitle);
    
    /**
     * <p>
     * Description: 数据为空时显示页面提示布局
     * <p>
     * 
     * @date 2013-8-21
     * @author WEI
     * @param isClickable
     *            是否接收点击事件
     * @param isSuportRefresh
     *            是否可以下拉刷新， 如果不是接收点击刷新
     * @param isNeedChange
     *            是否需要接受自定义事件
     * @param returnCode
     *            错误返回码
     * @param resEmpty
     *            数据为空提示信息
     * @param strChangeTitle
     *            自定义事件标题
     * @return
     */
    public void showViewDataEmpty(boolean isClickable, boolean isSuportRefresh,
                                  boolean isNeedChange, int returnCode, int resEmpty, String strChangeTitle);
    
    /**
     * <p>
     * Description: 数据为空时显示页面提示布局
     * <p>
     * 
     * @date 2013-8-21
     * @author WEI
     * @param isClickable
     *            是否接收点击事件
     * @param isSuportRefresh
     *            是否可以下拉刷新， 如果不是接收点击刷新
     * @param msg
     *            错误信息
     * @param resEmptyMsg
     *            数据为空提示信息
     * @return
     */
    public void showViewDataEmpty(boolean isClickable, boolean isSuportRefresh,
                                  MessageData msg, int resEmptyId);

    /**
     * <p>
     * Description: 数据为空时显示页面提示布局
     * <p>
     * 
     * @date 2013-8-21
     * @author WEI
     * @param isClickable
     *            是否接收点击事件
     * @param isSuportRefresh
     *            是否可以下拉刷新， 如果不是接收点击刷新
     * @param returnCode
     *            错误返回码
     * @param resEmptyMsg
     *            数据为空提示信息
     * @return
     */
    public void showViewDataEmpty(boolean isClickable, boolean isSuportRefresh,
                                  MessageData msg, String resEmpty);

}
