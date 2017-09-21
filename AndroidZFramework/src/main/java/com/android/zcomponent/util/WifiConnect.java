package com.android.zcomponent.util;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.component.zframework.R;

/**
 * <p>
 * Description: Wifi连接
 * </p>
 * 
 * @ClassName:WifiConnect
 * @author: WEI
 * @date: 2014-7-28
 * 
 */
public class WifiConnect
{
	private static final String TAG = "WifiConnect";

	private WifiManager mWifiManager;

	// 定义一个WifiInfo对象
	private WifiInfo mWifiInfo;

	// 扫描出的网络连接列表
	private List<ScanResult> mWifiList;

	// 网络连接列表
	private List<WifiConfiguration> mWifiConfigurations;

	private WifiLock mWifiLock;

	/** WIFI SSID */
	private String mstrSsid = "";

	/** WIFI 密码 */
	private String password = "";

	/** WIFI 扫描结果监听 */
	private WifiReceiver mWifiReceiver;

	/** wifi 连接提示框 */
	private Dialog mwifiDialog;

	/** wifi 连接提示标题 */
	private TextView mtvewContent;

	private Context mContext;

	/** wifi是否正在连接标记 */
	private boolean isWifiConnecting = false;

	// 定义几种加密方式，一种是WEP，一种是WPA，还有没有密码的情况
	public enum WifiCipherType
	{
		WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
	}

	// 构造函数
	public WifiConnect(Context context)
	{
		// 取得WifiManager对象
		mWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		// 取得WifiInfo对象
		mWifiInfo = mWifiManager.getConnectionInfo();
		mContext = context;
		createWifiLoadingDialog();
		openWifi();
		registerWifiReceiver();
		mWifiManager.startScan();
	}

	private void createWifiLoadingDialog()
	{
		mwifiDialog = new Dialog(mContext, R.style.DialogCustomTheme);
		View v = LayoutInflater.from(mContext).inflate(
				R.layout.dialog_wifi_loading, null);
		v.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{}
		});
		mwifiDialog.setContentView(v);
		mtvewContent = (TextView) v
				.findViewById(R.id.wifi_dailog_title);
	}

	private void showDialog()
	{
		if (null != mContext && !((Activity) mContext).isFinishing())
		{
			if (!mwifiDialog.isShowing())
			{
				mwifiDialog.show();
			}
		}
	}

	public void dismissDialog()
	{
		if (null != mwifiDialog && mwifiDialog.isShowing())
		{
			mwifiDialog.cancel();
		}
		else
		{
			LogEx.w(TAG, "mwifiDialog null");
		}
	}

	private void setDialogTitle(String strMsg)
	{
		mtvewContent.setText(strMsg);
	}

	public boolean isConnecting()
	{
		return isWifiConnecting;
	}

	private boolean Connect(String SSID, String Password, WifiCipherType Type)
	{
		if (!this.openWifi())
		{
			return false;
		}
		WifiConfiguration wifiConfig = this
				.CreateWifiInfo(SSID, Password, Type);
		//
		if (wifiConfig == null)
		{
			return false;
		}
		WifiConfiguration tempConfig = this.IsExsits(SSID);
		if (tempConfig != null)
		{
			mWifiManager.removeNetwork(tempConfig.networkId);
		}
		int netID = mWifiManager.addNetwork(wifiConfig);
		boolean bRet = mWifiManager.enableNetwork(netID, true);
		return bRet;
	}

	// 查看以前是否也配置过这个网络
	private WifiConfiguration IsExsits(String SSID)
	{
		List<WifiConfiguration> existingConfigs = mWifiManager
				.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs)
		{
			if (existingConfig.SSID.equals("\"" + SSID + "\""))
			{
				return existingConfig;
			}
		}
		return null;
	}

	private WifiConfiguration CreateWifiInfo(String SSID, String Password,
			WifiCipherType Type)
	{
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";
		if (Type == WifiCipherType.WIFICIPHER_NOPASS)
		{
			config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (Type == WifiCipherType.WIFICIPHER_WEP)
		{
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (Type == WifiCipherType.WIFICIPHER_WPA)
		{
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;
		}
		else
		{
			return null;
		}
		return config;
	}

	// 打开wifi功能
	private boolean openWifi()
	{
		boolean bRet = true;
		if (!mWifiManager.isWifiEnabled())
		{
			bRet = mWifiManager.setWifiEnabled(true);
		}
		return bRet;
	}

	// 关闭wifi
	public void closeWifi()
	{
		if (!mWifiManager.isWifiEnabled())
		{
			mWifiManager.setWifiEnabled(false);
		}
	}

	// 检查当前wifi状态
	public int checkState()
	{
		return mWifiManager.getWifiState();
	}

	// 锁定wifiLock
	public void acquireWifiLock()
	{
		mWifiLock.acquire();
	}

	// 解锁wifiLock
	public void releaseWifiLock()
	{
		// 判断是否锁定
		if (mWifiLock.isHeld())
		{
			mWifiLock.acquire();
		}
	}

	// 创建一个wifiLock
	public void createWifiLock()
	{
		mWifiLock = mWifiManager.createWifiLock("test");
	}

	// 得到配置好的网络
	public List<WifiConfiguration> getConfiguration()
	{
		return mWifiConfigurations;
	}

	// 指定配置好的网络进行连接
	public void connetionConfiguration(int index)
	{
		if (index > mWifiConfigurations.size())
		{
			return;
		}
		// 连接配置好指定ID的网络
		mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId,
				true);
	}

	public void getConnectedWifi()
	{
		// 取得WifiInfo对象
		mWifiInfo = mWifiManager.getConnectionInfo();
		if (null != mWifiInfo)
		{
			Log.d(TAG, "mWifiInfo " + mWifiInfo.toString());
		}
		else
		{
			Log.w(TAG, "mWifiInfo empty");
		}
	}

	/**
	 * <p>
	 * Description: 提供一个外部接口，传入要连接的无线网
	 * <p>
	 * 
	 * @date 2013-7-30
	 * @author WEI
	 * @param SSID
	 * @param Password
	 * @return
	 */
	public boolean connect(String SSID, String Password)
	{
		this.mstrSsid = SSID;
		this.password = Password;
		if (!this.openWifi())
		{
			return false;
		}
		getConnectedWifi();
		if (null != mWifiInfo && null != mWifiInfo.getSSID())
		{
			String curSSID = mWifiInfo.getSSID();
			// 如果当前已经连接了WiFi，直接返回
			LogEx.d(TAG, "mWifiInfo.getSSID()  " + curSSID);
			if (null != curSSID && ("\"" + mstrSsid + "\"").equals(curSSID))
			{
				setDialogTitle("WiFi已连接");
				showDialog();
				mHandler.sendEmptyMessageDelayed(0, 500);
				return true;
			}
		}
		else
		{
		}
		if (!isWifiInArea(SSID) && isConnecting())
		{
			return true;
		}
		isWifiConnecting = true;
		showDialog();
		mWifiList = getWifiList();
		if (null != mWifiList && mWifiList.size() > 0)
		{
			connectWifi();
		}
		else
		{
			mWifiManager.startScan();
		}
		return true;
	}

	/**
	 * <p>
	 * Description: 判断wifi是否在可连接范围。
	 * <p>
	 * 
	 * @date 2013-10-9
	 * @author WEI
	 * @param ssid
	 * @return
	 */
	private boolean isWifiInArea(String ssid)
	{
		return false;
	}

	public void registerWifiReceiver()
	{
		mWifiReceiver = new WifiReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		mContext.registerReceiver(mWifiReceiver, filter);
	}

	public void unregisterWifiReceiver()
	{
		mContext.unregisterReceiver(mWifiReceiver);
	}

	private class WifiReceiver extends BroadcastReceiver
	{
		public void onReceive(Context c, Intent intent)
		{
			if (intent.getAction().equals(
					WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
			{
				// 得到配置好的网络连接
				mWifiConfigurations = mWifiManager.getConfiguredNetworks();
				getConnectedWifi();
				if (null != mWifiInfo && null != mWifiInfo.getSSID())
				{
					String curSSID = mWifiInfo.getSSID();
					if (null != curSSID
							&& ("\"" + mstrSsid + "\"").equals(curSSID))
					{
						return;
					}
				}
				// 得到扫描结果
				mWifiList = getWifiList();
				// 获取到数据后，停止监听
				if (null != mWifiList)
				{
					connectWifi();
				}
				isWifiConnecting = false;
				
				if (null != changeListener)
				{
					changeListener.onWifiStateChange();
				}
			}
			else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent
					.getAction()))
			{
				int wifiState = intent.getIntExtra(
						WifiManager.EXTRA_WIFI_STATE, 0);
				switch (wifiState)
				{
				case WifiManager.WIFI_STATE_ENABLED:
				{
					mWifiManager.startScan();
					break;
				}
				}
			}
			else if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION))
			{
				mWifiManager.startScan();
			}
		}
	}

	private void connectWifi()
	{
		if (StringUtil.isEmptyString(mstrSsid))
		{
			return;
		}
		boolean isSuccess = false;
		for (int i = 0; i < mWifiList.size(); i++)
		{
			ScanResult scanResult = mWifiList.get(i);
			if (scanResult.SSID.equals(mstrSsid))
			{
				if (scanResult.capabilities.contains("WPA"))
				{
					this.Connect(mstrSsid, password,
							WifiCipherType.WIFICIPHER_WPA);
				}
				else if (scanResult.capabilities.contains("WEP"))
				{
					this.Connect(mstrSsid, password,
							WifiCipherType.WIFICIPHER_WEP);
				}
				else
				{
					this.Connect(mstrSsid, password,
							WifiCipherType.WIFICIPHER_NOPASS);
				}
				isSuccess = true;
				setDialogTitle("WiFi连接成功");
			}
			if (!isSuccess)
			{
				setDialogTitle("WiFi连接失败");
			}
			Log.d("zjw", scanResult.toString() + "\n");
		}
		mHandler.sendEmptyMessageDelayed(0, 500);
	}

	Handler mHandler = new Handler(new Callback()
	{
		@Override
		public boolean handleMessage(Message msg)
		{
			dismissDialog();
			return false;
		}
	});

	// 得到网络列表
	public List<ScanResult> getWifiList()
	{
		return mWifiManager.getScanResults();
	}

	// 查看扫描结果
	public StringBuffer lookUpScan()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mWifiList.size(); i++)
		{
			sb.append("Index_" + new Integer(i + 1).toString() + ":");
			// 将ScanResult信息转换成一个字符串包
			// 其中把包括：BSSID、SSID、capabilities、frequency、level
			sb.append((mWifiList.get(i)).toString()).append("\n");
		}
		return sb;
	}

	public String getMacAddress()
	{
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
	}

	public String getBSSID()
	{
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
	}

	public int getIpAddress()
	{
		return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
	}

	// 得到连接的ID
	public int getNetWordId()
	{
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	// 得到wifiInfo的所有信息
	public String getWifiInfo()
	{
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
	}

	// 添加一个网络并连接
	public void addNetWork(WifiConfiguration configuration)
	{
		int wcgId = mWifiManager.addNetwork(configuration);
		mWifiManager.enableNetwork(wcgId, true);
	}

	// 断开指定ID的网络
	public void disConnectionWifi(int netId)
	{
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
	}

	private OnWifiStateChangeListener changeListener;

	public interface OnWifiStateChangeListener
	{
		public void onWifiStateChange();
	}

	public void setOnWifiStateChangeListener(
			OnWifiStateChangeListener changeListener)
	{
		this.changeListener = changeListener;
	}
}
