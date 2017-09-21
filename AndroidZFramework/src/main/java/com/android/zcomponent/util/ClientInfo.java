/**
 * <p>
 * Copyright: Copyright (c) 2012
 * Company: ZTE
 * Description:网络信息类
 * </p>
 * @Title ClientNetworkInfo.java
 * @Package com.zte.iptvclient.android.common
 * @version 1.0
 * @author 0043200560
 * @date 2012-2-22
 */
package com.android.zcomponent.util;


import java.io.File;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.android.component.zframework.R;


/**
 * @ClassName:ClientNetworkInfo
 * @Description: 当前客户端网络信息类
 * @date: 2013-4-22
 * 
 */
public class ClientInfo
{

    /** 定义一个ConnectivityManager对象 */
    private ConnectivityManager m_connectivityManager = null;

    /** 当前activity的网络信息 */
    private NetworkInfo m_netInfoClientNetworkInfo = null;

    /** 当前activity */
    private Context m_ctxMainActivity = null;

    /**
     * 构造函数
     * 
     * @param ctx
     *            当前的activity
     */
    public ClientInfo(Context ctx)
    {
        m_ctxMainActivity = ctx;
        if (null != m_ctxMainActivity)
        {
            // 实例化mConnectivityManager对象
            m_connectivityManager = (ConnectivityManager) m_ctxMainActivity
                    .getSystemService(Activity.CONNECTIVITY_SERVICE);// 获取系统的连接服务
            if (null != m_connectivityManager)
            {
                // 实例化mActiveNetInfo对象，获取网络连接的信息
                m_netInfoClientNetworkInfo = m_connectivityManager.getActiveNetworkInfo();
            }
        }
    }

    /**
     * <p>
     * Description: 获取本地IP列表函数
     * <p>
     * 
     * @date 2013-4-22
     * @return 本地IP字符串，当没有可用的网络地址的时候返回null
     * @throws SocketException
     *             SocketException
     */
    private List<InetAddress> getLocalIPAddresses() throws SocketException
    {
        List<InetAddress> listInetAddrs = new ArrayList<InetAddress>();
        for (Enumeration<NetworkInterface> mEnumeration = NetworkInterface
                .getNetworkInterfaces(); mEnumeration.hasMoreElements();)
        {
            NetworkInterface intf = mEnumeration.nextElement();
            for (Enumeration<InetAddress> enumIPAddr = intf.getInetAddresses(); enumIPAddr
                    .hasMoreElements();)
            {
                InetAddress inetAddress = enumIPAddr.nextElement();
                // 如果不是回环地址
                if (!inetAddress.isLoopbackAddress())
                {
                    // 将此地址加入到地址列表中。
                    listInetAddrs.add(inetAddress);
                }
            }
        }
        return listInetAddrs;
    }

    /**
     * 获取本地IP地址，优先获取IPV4地址。如果没有IPV4地址，则返回IPV6地址。如果没有IPV6地址，则返回0.0.0.0
     * 
     * @return ip地址字符串，优先获取IPV4地址。如果没有IPV4地址，则返回IPV6地址。如果没有IPV6地址，则返回0.0.0.0
     * 
     * @throws SocketException
     *             SocketException
     */
    public String getLocalIPAddress() throws SocketException
    {
        // 获取IPV4地址并返回
        for (InetAddress inetAddress : getLocalIPAddresses())
        {
            if (inetAddress instanceof Inet4Address)
            {
                return inetAddress.getHostAddress().toString();
            }
        }
        // 获取IPV6地址并返回
        for (InetAddress inetAddress : getLocalIPAddresses())
        {
            if (inetAddress instanceof Inet6Address)
            {
                return inetAddress.getHostAddress().toString();
            }
        }
        // 返回默认地址
        return "0.0.0.0";
    }

    /**
     * <p>
     * Description: 获取本机mac地址，如果没有合适的地址。则返回“00:00:00:00:00:00”
     * <p>
     * 
     * @date 2013-4-22
     * @return 当前MAC地址
     */
    public String getLocalMacAddress()
    {
        WifiManager wifi = (WifiManager) m_ctxMainActivity
                .getSystemService(Context.WIFI_SERVICE);
        if (null != wifi)
        {
            WifiInfo info = wifi.getConnectionInfo();
            if (null != info)
            {
                String strMac = info.getMacAddress();
                if (strMac != null && strMac.length() > 0)
                {
                    return strMac;
                }
            }
        }
        // 返回默认mac地址
        return "00:00:00:00:00:00";
    }

    /**
     * <p>
     * Description: 检查网络是否可用
     * <p>
     * 
     * @date 2013-4-22
     * @return 如果为-1表示网络不可用；如果为其他整数，代表当前网络连接类型,请参考android.net.NetworkInfo中的网络类型。
     */
    public int checkNetworkInfo()
    {
        // 网络信息无效返回-1
        if (null == m_netInfoClientNetworkInfo
            || !m_netInfoClientNetworkInfo.isAvailable())
        {
            return -1;
        }
        // 网络信息有效，显示网络类型
        else
        {
            return m_netInfoClientNetworkInfo.getType();
        }
    }

    /**
     * 获取手机设备IMEI号
     * 
     * @param context
     * @return
     */
    public static String getIMEI(Context context)
    {
        String imei = "";
        try
        {
            if (imei.equals(""))
            {
                TelephonyManager tm = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                imei = tm.getDeviceId();
            }
        }
        catch (Exception e)
        {
            imei = "";
        }
        return imei;
    }

    /**
     * <p>
     * Description: 判断程序是否升级版本
     * <p>
     * 
     * @date 2013-11-18
     * @author WEI
     * @param context
     * @return
     */
    public static boolean isNewVersion(Context context, String keyName)
    {
        String lastVersionName = SettingsBase.getInstance().readStringByKey(keyName);
        String curVersionName = ClientInfo.getVersionName(context);
        if (curVersionName.equals(lastVersionName))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * <p>
     * Description: 获取应用程序版本号
     * <p>
     * 
     * @date 2013-7-6
     * @author WEI
     * @param context
     * @return -1 获取版本号失败
     */
    public static String getVersionName(Context context)
    {
        // 获取当前软件包信息
        PackageInfo pi = null;
        try
        {
            pi = context.getPackageManager().getPackageInfo(context.getPackageName(),
                PackageManager.GET_CONFIGURATIONS);
        }
        catch (NameNotFoundException e)
        {
            e.printStackTrace();
        }
        if (null != pi)
        {
            return pi.versionName;
        }
        else
        {
            return "";
        }
    }
    
    public static int getVersionCode(Context context)
    {
        // 获取当前软件包信息
        PackageInfo pi = null;
        try
        {
            pi = context.getPackageManager().getPackageInfo(context.getPackageName(),
                PackageManager.GET_CONFIGURATIONS);
        }
        catch (NameNotFoundException e)
        {
            e.printStackTrace();
        }
        if (null != pi)
        {
            return pi.versionCode;
        }
        else
        {
            return 0;
        }
    }

    /**
     * <p>
     * Description: Acitivty 右侧进入动画
     * <p>
     * 
     * @date 2013-11-23
     * @author WEI
     * @param context
     */
    public static void overridePendingTransitionInRight(Activity context)
    {
        context.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    /**
     * <p>
     * Description: Activity 左侧退出动画
     * <p>
     * 
     * @date 2013-11-23
     * @author WEI
     * @param context
     */
    public static void overridePendingTransitionOutLeft(Activity context)
    {
        context.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }
    
    /**
     * <p>
     * Description: Acitivty 底部进入动画
     * <p>
     * 
     * @date 2013-11-23
     * @author WEI
     * @param context
     */
    public static void overridePendingTransitionInBottom(Activity context)
    {
        context.overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }

    /**
     * <p>
     * Description: Activity 左侧退出动画
     * <p>
     * 
     * @date 2013-11-23
     * @author WEI
     * @param context
     */
    public static void overridePendingTransitionOutTop(Activity context)
    {
        context.overridePendingTransition(R.anim.in_from_top, R.anim.out_to_bottom);
    }

    /**
     * <p>
     * Description: 打开软键盘
     * <p>
     * 
     * @date 2013-11-23
     * @author WEI
     * @param view
     * @param context
     */
    public static void openSoftInput(View view, final Context context)
    {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run()
            {
                InputMethodManager imm = (InputMethodManager) context
                        .getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 500); // 在一秒后打开
    }

    /**
     * <p>
     * Description: 关闭软键盘
     * <p>
     * 
     * @date 2013-11-23
     * @author WEI
     * @param view
     * @param context
     */
    public static void closeSoftInput(View view, Context context)
    {
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0))
        {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
    }

    public static void installApk(Context context, String apkFileUri, String apkName)
    {
        File apkfile = null;
        if (FileUtil.hasSDCard())
        {
            apkfile = new File(apkFileUri + apkName);
        }
        else
        {
            File dir = context.getDir("apk", Context.MODE_PRIVATE
                | Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
            apkfile = new File(dir.getPath() + "/" + apkName);
        }
        if (null != apkfile && apkfile.exists())
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(apkfile),
                "application/vnd.android.package-archive");
            context.startActivity(intent);
        }
    }

    private static String getAuthorityFromPermission(Context context, String permission)
    {
        if (TextUtils.isEmpty(permission))
        {
            return null;
        }
        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(
            PackageManager.GET_PROVIDERS);
        if (packs == null)
        {
            return null;
        }
        for (PackageInfo pack : packs)
        {
            ProviderInfo[] providers = pack.providers;
            if (providers != null)
            {
                for (ProviderInfo provider : providers)
                {
                    if (permission.equals(provider.readPermission)
                        || permission.equals(provider.writePermission))
                    {
                        return provider.authority;
                    }
                }
            }
        }
        return null;
    }

    public static boolean hasInstallShortcut(Context context)
    {
        // 获取当前应用名称
        String title = null;
        try
        {
            final PackageManager pm = context.getPackageManager();
            title = pm.getApplicationLabel(
                pm.getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA)).toString();
        }
        catch (Exception e)
        {
        }

        boolean hasInstall = false;

        final String uriStr;
        if (android.os.Build.VERSION.SDK_INT < 8)
        {
            uriStr = "content://com.android.launcher.settings/favorites?notify=true";
        }
        else
        {
            uriStr = "content://com.android.launcher2.settings/favorites?notify=true";
        }

        Uri CONTENT_URI = Uri.parse(uriStr);
        Cursor cursor = context.getContentResolver().query(CONTENT_URI, null, "title=?",
            new String[]{title}, null);
        if (cursor != null && cursor.getCount() > 0)
        {
            hasInstall = true;
        }
        return hasInstall;

    }

    public static void addShortcutToDesktop(Context cxt, int icon, Class<?> cls)
    {
        String title = null;
        try
        {
            final PackageManager pm = cxt.getPackageManager();
            title = pm
                    .getApplicationLabel(
                        pm.getApplicationInfo(cxt.getPackageName(),
                            PackageManager.GET_META_DATA)).toString();
        }
        catch (Exception e)
        {
        }

        // 创建快捷方式的Intent
        Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // 不允许重复创建
        shortcutIntent.putExtra("duplicate", false);
        // 需要现实的名称
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
        // 快捷图片
        Parcelable ico = Intent.ShortcutIconResource.fromContext(
            cxt.getApplicationContext(), icon);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, ico);
        Intent intent = new Intent(cxt, cls);
        // 下面两个属性是为了当应用程序卸载时桌面上的快捷方式会删除
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        // 点击快捷图片，运行的程序主入口
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        // 发送广播。OK
        cxt.sendBroadcast(shortcutIntent);

    }
}
