package com.android.zcomponent.common.uiframe;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.zcomponent.constant.GlobalConst;
import com.android.zcomponent.util.LogEx;


/**
 * @ClassName:BroadcastReceiverActivity
 * @Description: activity接受广播消息类。此广播消息主要是心跳发送的广播
 * @author: 
 * @date: 2012-3-1
 * 
 */
public abstract class BroadcastReceiverActivity extends FragmentActivity
{

    private static final String TAG = "BroadcastReceiverActivity";

    /** 广播消息管理器实例 */
    private BroadcastReceiverMgr m_mgrBroadcast = null;

    /** 当前广播接收者的个数 */
    private static int m_iCurrBroadcastReceiverNum = 0;

    /** 当前未处理的心跳失败广播个数 */
    private static int m_iCurrHeartbeatErrorMsgNum = 0;

    private static boolean bIsConnectedSuccess = true;

    private static ConnectivityManager connManager;

    /**
     * 
     * 广播消息处理类
     * 
     * @ClassName:BroadcastReceiverMgr
     * @Description: 处理各种广播消息
     * @author: jamesqiao10065075
     * @date: 2012-3-9
     * 
     */
    public class BroadcastReceiverMgr extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context ctx, Intent intent)
        {
            Log.d(TAG, "receive broadcast, receiver is " + BroadcastReceiverActivity.this);
            if (null == ctx || null == intent)
            {
                Log.e(TAG, "ctx is null or intent is null!");
                return;
            }
            // 操作
            String strAction = intent.getAction();
            Log.d(TAG, "strAction=" + strAction);
            // 来电广播
            if (strAction.equals(GlobalConst.BROADCAST_ACTION_PHONE_STATE_CHANGED))
            {
                onPhoneStateChanged(ctx, intent);
            }
            // 心跳保活
            else if (strAction.equals(GlobalConst.BROADCAST_ACTION_HEARTBEAT))
            {
                onHeartbeat(ctx, intent);
            }
            // 注销操作
            else if (strAction.equals(GlobalConst.BROADCAST_ACTION_LOGOUT))
            {
                onLogout(ctx, intent);
            }
            else if (strAction.equals(Intent.ACTION_DATE_CHANGED))
            {
                onDateChanged();
            }
            // 网络状态有变化
            else if (strAction.equals(ConnectivityManager.CONNECTIVITY_ACTION))
            {
                bIsConnectedSuccess = onNetChanged();
                onNetChangedCallBack(bIsConnectedSuccess);
            }
        }

        /**
         * 
         * 电话状态变更的处理函数
         * <p>
         * Description: 一般是来电
         * <p>
         * 
         * @author jamesqiao10065075
         * @date 2012-3-9
         * @param context
         *            上下文
         * @param intent
         *            请求参数
         */
        private void onPhoneStateChanged(Context ctx, Intent intent)
        {
            if (null == ctx || null == intent)
            {
                Log.e(TAG, "ctx is null or intent is null!");
                return;
            }
            TelephonyManager mgrTelephony = (TelephonyManager) ctx
                    .getSystemService(Context.TELEPHONY_SERVICE);
            int iState = mgrTelephony.getCallState();
            // 根据来电状态进行相应处理
            switch (iState)
            {
                case TelephonyManager.CALL_STATE_RINGING : // 来电
                {
                    Log.i(TAG, "ringing.");
                    break;
                }
                case TelephonyManager.CALL_STATE_IDLE : // 挂断了
                {
                    Log.i(TAG, "idle.");
                    break;
                }
                case TelephonyManager.CALL_STATE_OFFHOOK : // 通话中
                {
                    Log.i(TAG, "offhook.");
                    break;
                }
                default : // 其他状态，不关心
                {
                    break;
                }
            }
        }

        /**
         * 
         * 注销请求的处理函数
         * <p>
         * Description: 可能包括注销和退出
         * <p>
         * 
         * @author Administrator
         * @date 2012-3-9
         * @param context
         * @param intent
         */
        private void onLogout(Context ctx, Intent intent)
        {
            if (null == ctx || null == intent)
            {
                Log.e(TAG, "ctx is null or intent is null!");
                return;
            }
            // 注销类型
            String strType = intent.getStringExtra(GlobalConst.BROADCAST_PARAM_FIELD);
            // 不管是注销还是退出，都要先执行注销操作
            if (strType.equals(GlobalConst.LOGOUT_BROADCAST_PARAM_VALUE_EXIT)
                || strType.equals(GlobalConst.LOGOUT_BROADCAST_PARAM_VALUE_LOGOUT))
            {
                // onLogoutBefore();
            }
            // 退出
            if (strType.equals(GlobalConst.LOGOUT_BROADCAST_PARAM_VALUE_EXIT))
            {
                // onExit();
            }
        }

        private void onDateChanged()
        {
            LogEx.e("zjw", "onDateChange onDateChange onDateChange");
        }
        
        /**
         * 
         * 心跳请求的处理函数
         * <p>
         * Description: 一般是心跳失败
         * <p>
         * 
         * @author 
         * @date 2012-3-9
         * @param ctx
         *            上下文
         * @param intent
         *            请求参数
         */
        public void onHeartbeat(Context ctx, Intent intent)
        {
            Log.d(TAG, "ctx=" + ctx + ";intent=" + intent);
        }

    }

    /**
     * <p>
     * Description: 网络状态变化处理
     * <p>
     * 
     * @date 2012-12-17
     * @author WEI
     * @param ctx
     * @param intent
     */
    public static boolean onNetChanged()
    {
        NetworkInfo[] info = null;

        if (null != connManager)
        {
            info = connManager.getAllNetworkInfo();
        }
        if (info != null)
        {
            for (int i = 0; i < info.length; i++)
            {
                if (info[i].getState() == NetworkInfo.State.CONNECTED)
                {
                    return true;
                }
            }
        }
        
        return false;
    }

    /**
     * <p>
     * Description: 判断网络是否已连接
     * <p>
     * @date 2013-9-24 
     * @author WEI
     * @return
     */
    public static boolean isNetConnected()
    {
        return onNetChanged();
    }

    @Override
    protected void onResume()
    {
        // 添加电话广播的监听
        m_mgrBroadcast = new BroadcastReceiverMgr();
        IntentFilter intentFilter = new IntentFilter();
        // 监听来电广播
        intentFilter.addAction(GlobalConst.BROADCAST_ACTION_PHONE_STATE_CHANGED);
        // 监听注销广播
        intentFilter.addAction(GlobalConst.BROADCAST_ACTION_LOGOUT);
        // 监听心跳保活广播
        intentFilter.addAction(GlobalConst.BROADCAST_ACTION_HEARTBEAT);
        // 监听网络状态变化
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
        //
        intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        registerReceiver(m_mgrBroadcast, intentFilter);
        m_iCurrBroadcastReceiverNum++;
        if (m_iCurrHeartbeatErrorMsgNum > 0)
        {
            BroadcastReceiverActivity.this.m_mgrBroadcast.onHeartbeat(
                BroadcastReceiverActivity.this, new Intent());
            m_iCurrHeartbeatErrorMsgNum = 0;
        }
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        // 取消注册广播监听
        unregisterReceiver(m_mgrBroadcast);
        m_iCurrBroadcastReceiverNum--;
        super.onPause();
    }

    /**
     * <p>
     * Description: 网络连接发生变化， 回调页面处理
     * <p>
     * @date 2013-9-24 
     * @author WEI
     * @param isConnected
     */
    public void onNetChangedCallBack(boolean isConnected)
    {
        LogEx.i(TAG, "网络连接状态   isConnected = " + isConnected);
    }

    /**
     * 获取当前可以接收广播的广播接收者数
     * <p>
     * Description: 获取当前可以接收广播的广播接收者数
     * <p>
     * 
     * @date 2012-3-17
     * @return 当前可以接收广播的广播接收者数
     */
    public static int getCurrBroadcastReceiverNum()
    {
        return m_iCurrBroadcastReceiverNum;
    }

    /**
     * 将当前未处理的消息数加1
     * <p>
     * Description: 将当前未处理的消息数加1
     * <p>
     * 
     * @date 2012-3-17
     */
    public static void addCurrHeartbeatErrorMsgNum()
    {
        if (m_iCurrHeartbeatErrorMsgNum <= 0)
        {
            m_iCurrHeartbeatErrorMsgNum = 1;
        }
        else
        {
            m_iCurrHeartbeatErrorMsgNum++;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // 获得网络连接服务
        connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    }

}
