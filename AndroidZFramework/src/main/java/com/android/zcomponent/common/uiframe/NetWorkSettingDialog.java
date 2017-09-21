package com.android.zcomponent.common.uiframe;


import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.component.zframework.R;
import com.android.zcomponent.util.CustomDialog;
import com.android.zcomponent.util.MyLayoutAdapter;
import com.android.zcomponent.util.NetSateUtil;
import com.android.zcomponent.util.ShowMsg;
import com.android.zcomponent.views.UISwitchButton;


public class NetWorkSettingDialog extends CustomDialog
{

    private UISwitchButton switch_wifi, switch_gprs;

    ConnectivityManager mConnectivityManager;

    ImageView img_gprs, img_wifi;

    private LinearLayout mllayoutContent;

    Intent intent;

    Button but_close;

    private Activity mContext;

    public NetWorkSettingDialog(Context context)
    {
        super(context, R.layout.activity_network, true, false);
        mContext = (Activity) context;
        init();
    }

    public void init()
    {
        mllayoutContent = (LinearLayout) findViewById(R.id.network_llayout_content);
        switch_wifi = (UISwitchButton) findViewById(R.id.switch_wifi);
        switch_gprs = (UISwitchButton) findViewById(R.id.switch_liuliang);

        img_gprs = (ImageView) findViewById(R.id.img_gprs);
        img_wifi = (ImageView) findViewById(R.id.img_wifi);

        but_close = (Button) findViewById(R.id.but_close);

        setMaxWidth((int) (480 * MyLayoutAdapter.getInstance().getDensityRatio()));

        int type = NetSateUtil.getAPNType(mContext);
        if (2 == type)
        {
            img_gprs.setBackgroundResource(R.drawable.wuxianerlans);
            switch_gprs.setChecked(true);
        }
        else if (1 == type)
        {
            switch_wifi.setChecked(true);
            img_wifi.setBackgroundResource(R.drawable.wuxianlanse);
        }

        switch_wifi.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    toggleWiFi(true);
                    img_wifi.setBackgroundResource(R.drawable.wuxianlanse);
                    ShowMsg.showToast(mContext.getApplicationContext(), "正在打开WiFi网络...");
                }
                else
                {
                    toggleWiFi(false);
                    img_wifi.setBackgroundResource(R.drawable.wuxian1);
                    ShowMsg.showToast(mContext.getApplicationContext(), "正在关闭WiFi网络...");
                }
            }
        });

        switch_gprs.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    setMobileNetEnable();
                    img_gprs.setBackgroundResource(R.drawable.wuxianerlans);
                    ShowMsg.showToast(mContext.getApplicationContext(), "正在打开数据网络...");
                }
                else
                {
                    setMobileNetUnable();
                    ShowMsg.showToast(mContext.getApplicationContext(), "正在关闭数据网络...");
                    img_gprs.setBackgroundResource(R.drawable.wuxianer);
                }
            }
        });

        but_close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                int type = NetSateUtil.getAPNType(getContext());
                intent = new Intent();
                intent.putExtra("key", type + ""); // 设置要发送的数据
                mContext.setResult(Activity.RESULT_OK, intent);
                dismiss();
            }
        });

    }

    public void setMaxWidth(int maxWidth)
    {
        if (MyLayoutAdapter.getInstance().getScreenWidth() > maxWidth)
        {
            mllayoutContent.getLayoutParams().width = maxWidth;
        }
    }

    public void toggleWiFi(boolean status)
    {
        WifiManager wifiManager = (WifiManager) mContext
                .getSystemService(Context.WIFI_SERVICE);
        if (status == true && !wifiManager.isWifiEnabled())
        {
            wifiManager.setWifiEnabled(true);

        }
        else if (status == false && wifiManager.isWifiEnabled())
        {
            wifiManager.setWifiEnabled(false);
        }
    }

    public final void setMobileNetEnable()
    {

        mConnectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        Object[] arg = null;
        try
        {
            boolean isMobileDataEnable = invokeMethod("getMobileDataEnabled", arg);
            if (!isMobileDataEnable)
            {
                invokeBooleanArgMethod("setMobileDataEnabled", true);

            }
        }
        catch (Exception e)
        {

            e.printStackTrace();
        }

    }

    public final void setMobileNetUnable()
    {
        mConnectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        Object[] arg = null;
        try
        {
            boolean isMobileDataEnable = invokeMethod("getMobileDataEnabled", arg);
            if (isMobileDataEnable)
            {
                invokeBooleanArgMethod("setMobileDataEnabled", false);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean invokeMethod(String methodName, Object[] arg) throws Exception
    {

        ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        Class ownerClass = mConnectivityManager.getClass();

        Class[] argsClass = null;
        if (arg != null)
        {
            argsClass = new Class[1];
            argsClass[0] = arg.getClass();
        }

        Method method = ownerClass.getMethod(methodName, argsClass);

        Boolean isOpen = (Boolean) method.invoke(mConnectivityManager, arg);

        return isOpen;
    }

    public Object invokeBooleanArgMethod(String methodName, boolean value)
            throws Exception
    {

        ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        Class ownerClass = mConnectivityManager.getClass();

        Class[] argsClass = new Class[1];
        argsClass[0] = boolean.class;

        Method method = ownerClass.getMethod(methodName, argsClass);

        return method.invoke(mConnectivityManager, value);
    }
}
