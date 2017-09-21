package com.android.zcomponent.heartbeat;

import android.view.View;


public interface IHeartNotify
{
    public boolean isNeedShow();
    
    public boolean isShowing();
    
    public boolean isStopped();
    
    public void setState(boolean isNeedShow);
    
    public void setStop(boolean isStop);
    
    public void show(View view);
    
    public void dismiss();
}
