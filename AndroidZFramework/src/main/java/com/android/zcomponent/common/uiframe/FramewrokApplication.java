
package com.android.zcomponent.common.uiframe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.View;

import com.android.zcomponent.common.IApplication;
import com.android.zcomponent.constant.FrameworkR;
import com.android.zcomponent.constant.GlobalConst;
import com.android.zcomponent.jpush.JPushUtil;
import com.android.zcomponent.util.LogEx;
import com.android.zcomponent.util.SettingsBase;
import com.android.zcomponent.util.SharedPreferencesUtil;
import com.android.zcomponent.views.SystemManitance;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class FramewrokApplication extends Application implements IApplication
{

	public static final String TAG = "FramewrokApplication";

	private static List<Activity> mlistActivity = new ArrayList<Activity>();

	public static String PackageName = "";
	
	private boolean isTitleBarMoreShow = false;
	
	protected boolean isManitanceShow = false;

	private static FramewrokApplication instance;

	protected SystemManitance mSystemManitance;

	public static FramewrokApplication getInstance()
	{
		return instance;
	}

	@Override
	public void onCreate()
	{
		LogEx.d(TAG, "BaseApplication onCreate");
		new FrameworkR(this);
		PackageName = getAppPackageName();
		SettingsBase.getInstance().setAssetManager(getAssets());
		SettingsBase.getInstance().addStringByKey(
				GlobalConst.STR_CONFIG_COOKIE, "");
		new SharedPreferencesUtil(getApplicationContext());
		// 获取异常信息捕获类实例
		CrashHandler crashHandler =
				CrashHandler
						.getInstance(ConfigMgr
								.getApplicationExceptionFilePath(getApplicationContext()));
		// 初始化
		crashHandler.init(getApplicationContext());
		super.onCreate();
		instance = this;
	}

	public static void initImageLoader(Context context)
	{
		ImageLoaderConfiguration config =
				new ImageLoaderConfiguration.Builder(context)
						.threadPriority(Thread.NORM_PRIORITY - 2)
						.denyCacheImageMultipleSizesInMemory()
						.discCacheFileNameGenerator(new Md5FileNameGenerator())
						.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		ImageLoader.getInstance().init(config);
	}
	
	public static void initJpush(Context context)
	{
		JPushUtil.init(context);
	}

	public String getAppPackageName()
	{
		PackageInfo info = null;
		try
		{
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
			if (null != info)
			{
				LogEx.d("packagename", info.packageName);
				return info.packageName;
			}
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return "";
	}

	public void clearCookie()
	{
		SettingsBase.getInstance().writeStringByKey(
				GlobalConst.STR_CONFIG_COOKIE, "");
	}

	public String[] getMsgCodeClass()
	{
		return null;
	}
	
	@Override
	public void addActivity(Activity activity)
	{
		mlistActivity.add(activity);
	}

	@Override
	public void removeActivity(Activity activity)
	{
		mlistActivity.remove(activity);
	}

	@Override
	public List<Activity> getActivitys()
	{
		return mlistActivity;
	}

	@Override
	public Activity getCurActivity()
	{
		return mlistActivity.get(mlistActivity.size() - 1);
	}

	@Override
	public void onUnauthorized()
	{

	}

	@Override
	public void onSystemMaintance(String message)
	{
		if (isManitanceShow)
		{
			return;
		}
		final Activity activity = getCurActivity();
		mSystemManitance = new SystemManitance(activity);
		mSystemManitance.showWebView("http://www.aizachi.com/maintenance.html");
	}

	public boolean isManitanceShow()
	{
		return isManitanceShow;
	}

	public void setManitanceShow(boolean isManitanceShow)
	{
		this.isManitanceShow = isManitanceShow;
	}

	@Override
	public void reLogin()
	{

	}

	@Override
	public boolean closeAcitity(Class<?> activity)
	{
		for (int i = 0; i < getActivitys().size(); i++)
		{
			if (getActivitys().get(i).getClass().equals(activity))
			{
				getActivitys().get(i).finish();
				return true;
			}
		}
		return false;
	}

	@Override
	public Activity getActivity(Class<?> activity)
	{
		for (int i = 0; i < getActivitys().size(); i++)
		{
			if (getActivitys().get(i).getClass().equals(activity))
			{
				return getActivitys().get(i);
			}
		}
		return null;
	}

	/**
	 * <p>
	 * Description: 退出，关闭所有Activity
	 * <p>
	 * 
	 * @date 2013-10-10
	 * @author WEI
	 */
	public static void exit()
	{
		for (int i = 0; i < mlistActivity.size(); i++)
		{
			mlistActivity.get(i).finish();
		}
	}

	/**
	 * 账号登录、注销监听
	 * 
	 * @ClassName:OnLoginSuccessListener
	 * @Description: 从登录页面登录成功后，可以通过该接口方法回调的之前页面
	 * @author: WEI
	 * @date: 2013-8-23
	 * 
	 */
	public interface OnLoginListener
	{

		/**
		 * <p>
		 * Description: 登录成功回调接口
		 * <p>
		 * 
		 * @date 2013-8-23
		 * @author WEI
		 */
		public void onLoginSuccess();

		/**
		 * <p>
		 * Description: 登录取消回调接口
		 * <p>
		 * 
		 * @date 2013-8-23
		 * @author WEI
		 */
		public void onLoginCancel();

		/**
		 * <p>
		 * Description: 登录注销回调接口
		 * <p>
		 * 
		 * @date 2013-8-23
		 * @author WEI
		 */
		public void onLoginOut();
	}

	private List<OnLoginListener> onLoginSuccessListeners =
			new ArrayList<OnLoginListener>();

	public void registerLoginSuccessListener(
			OnLoginListener onLoginSuccessListener)
	{
		if (!onLoginSuccessListeners.contains(onLoginSuccessListener))
		{
			this.onLoginSuccessListeners.add(onLoginSuccessListener);
		}
		LogEx.d(TAG,
				"onLoginSuccessListeners size "
						+ onLoginSuccessListeners.size());
	}

	public void unregisterLoginSuccessListener(
			OnLoginListener onLoginSuccessListener)
	{
		this.onLoginSuccessListeners.remove(onLoginSuccessListener);
		LogEx.d(TAG,
				"onLoginSuccessListeners size "
						+ onLoginSuccessListeners.size());
	}

	/**
	 * <p>
	 * Description: 判断是已经登录成功
	 * <p>
	 * 
	 * @date 2013-8-23
	 * @author WEI
	 * @return
	 */
	public static boolean isLogin()
	{
		if ("true".equals(SharedPreferencesUtil.get("isLogin")))
		{
			return true;
		}
		return false;
	}

	public void setLogin(boolean isLogin)
	{
		SharedPreferencesUtil.put("isLogin", String.valueOf(isLogin));
		if (isLogin)
		{
			for (int i = 0; i < onLoginSuccessListeners.size(); i++)
			{
				if (null != onLoginSuccessListeners.get(i))
				{
					onLoginSuccessListeners.get(i).onLoginSuccess();
				}
			}
		}
		else
		{
			for (int i = 0; i < onLoginSuccessListeners.size(); i++)
			{
				if (null != onLoginSuccessListeners.get(i))
				{
					onLoginSuccessListeners.get(i).onLoginOut();
				}
			}
		}
	}

	/**
	 * <p>
	 * Description: 判断是否使用微博账号登录
	 * <p>
	 * 
	 * @date 2013-10-10
	 * @author WEI
	 * @return
	 */
	public static boolean isWeiboLogin()
	{
		if ("true".equals(SharedPreferencesUtil.get("mIsWeiboLogin")))
		{
			return true;
		}
		return false;
	}

	public static void setWeiboLogin(boolean isweibologin)
	{
		SharedPreferencesUtil
				.put("mIsWeiboLogin", String.valueOf(isweibologin));
	}

	public void setLoginCancel()
	{
		for (int i = 0; i < onLoginSuccessListeners.size(); i++)
		{
			if (null != onLoginSuccessListeners.get(i))
			{
				onLoginSuccessListeners.get(i).onLoginCancel();
			}
		}
	}

	/**
	 * @ClassName:ActivityResultListener
	 * @Description: Activity回调监听
	 * @author: WEI
	 * @date: 2014-2-19
	 * 
	 */
	public interface OnActivityResultListener
	{

		public void onActivityResultCallBack(int resultCode, Intent intent);
	}

	private List<OnActivityResultListener> activityResultListeners =
			new ArrayList<OnActivityResultListener>();

	public void registerActivityResultListener(
			OnActivityResultListener activityResultListener)
	{
		if (!this.activityResultListeners.contains(activityResultListener))
		{
			this.activityResultListeners.add(activityResultListener);
		}
		LogEx.d(TAG,
				"activityResultListener size " + activityResultListeners.size());
	}

	public void unregisterActivityResultListener(
			OnActivityResultListener activityResultListener)
	{
		this.activityResultListeners.remove(activityResultListener);
		LogEx.d(TAG,
				"activityResultListener size " + activityResultListeners.size());
	}

	public void setActivityResult(int resultCode, Intent intent)
	{
		LogEx.d(TAG, "setActivityResult");
		int size = activityResultListeners.size();
		for (int i = 0; i < size; i++)
		{
			if (null != activityResultListeners.get(i))
			{
				activityResultListeners.get(i).onActivityResultCallBack(
						resultCode, intent);
			}
			else
			{
				LogEx.w(TAG, "activityResultListener null ");
			}
		}
	}

	@Override
	public void onTitleBarMoreItemClick(View view, int position)
	{
		
	}
	
	@Override
	public void onTitleBarCreate(View view, View morePop, boolean isShowMore)
	{
		
	}

	@Override
	public void onTitleBarDestory(View view, View morePop, boolean isShowMore)
	{
		
	}
	
	@Override
	public void setTitleBarMoreShow(boolean isShow)
	{
		isTitleBarMoreShow = isShow;
	}

	@Override
	public boolean isTitleBarMoreShow()
	{
		return isTitleBarMoreShow;
	}

	@Override
	public void onTitleBarResume()
	{
		
	}

}
