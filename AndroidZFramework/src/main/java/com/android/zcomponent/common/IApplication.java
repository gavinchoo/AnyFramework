package com.android.zcomponent.common;


import java.util.List;

import android.app.Activity;
import android.view.View;


public interface IApplication
{
    public void onUnauthorized();

    public void onSystemMaintance(String message);
    
    public void onTitleBarMoreItemClick(View view, int position);
    
    public void onTitleBarCreate(View view, View morePop, boolean isShowMore);
    
    public void onTitleBarDestory(View view, View morePop, boolean isShowMore);
    
    public void onTitleBarResume();
    
    public void setTitleBarMoreShow(boolean isShow);
    
    public boolean isTitleBarMoreShow();

    public void reLogin();

    public void addActivity(Activity activity);

    public boolean closeAcitity(Class<?> activity);
    
    public void removeActivity(Activity activity);
    
    public Activity getActivity(Class<?> activity);
    
    public List<Activity> getActivitys();

    public Activity getCurActivity();
}
