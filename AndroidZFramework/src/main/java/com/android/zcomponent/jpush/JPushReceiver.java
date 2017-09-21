
package com.android.zcomponent.jpush;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

import com.android.zcomponent.activity.JpushMsgShowActivity;
import com.android.zcomponent.common.uiframe.FramewrokApplication;
import com.android.zcomponent.util.LogEx;
import com.android.zcomponent.util.StringUtil;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class JPushReceiver extends BroadcastReceiver
{

	public static final int CODE_JPUSH_MESSAGE = 1001001;
	
	public static final int CODE_JPUSH_NOTIFICATION = 1001002;
	
	private static final String TAG = "JPush";

	public static final String APP_PACKAGENAME = "APP_PACKAGENAME";

	public static final String LAUNCHER_ACTIVITY = "LAUNCHER_ACTIVITY";

	@Override
	public void onReceive(Context context, Intent intent)
	{
		Bundle bundle = intent.getExtras();
		LogEx.d(TAG, "[MyReceiver] onReceive - " + intent.getAction()
				+ ", extras: " + printBundle(bundle));

		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction()))
		{
			String regId =
					bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
			// send the Registration Id to your server...

		}
		else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
				.getAction()))
		{
			Log.d(TAG,
					"[MyReceiver] 接收到推送下来的自定义消息: "
							+ bundle.getString(JPushInterface.EXTRA_MESSAGE));
			if (null != FramewrokApplication.getInstance())
			{
				(FramewrokApplication.getInstance()).setActivityResult(
						CODE_JPUSH_MESSAGE, intent);
			}
		}
		else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
				.getAction()))
		{
			Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
			int notifactionId =
					bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
			if (null != FramewrokApplication.getInstance())
			{
				(FramewrokApplication.getInstance()).setActivityResult(
						CODE_JPUSH_NOTIFICATION, intent);
			}
		}
		else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
				.getAction()))
		{
			Log.d(TAG, "[MyReceiver] 用户点击打开了通知");

			JPushInterface.reportNotificationOpened(context,
					bundle.getString(JPushInterface.EXTRA_MSG_ID));

			startMainActivity(context);
			
			// 打开自定义的Activity
			if (null != FramewrokApplication.getInstance()
					&& null != FramewrokApplication.getInstance().getActivity(
							JpushMsgShowActivity.class))
			{
				Activity activity =
						FramewrokApplication.getInstance().getActivity(
								JpushMsgShowActivity.class);
				if (null != activity)
				{
					((JpushMsgShowActivity) activity).showMsg(bundle);
				}
			}
			else
			{
				Intent i = new Intent(context, JpushMsgShowActivity.class);
				i.putExtras(bundle);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
			}
		}
		else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent
				.getAction()))
		{
			Log.d(TAG,
					"[MyReceiver] 用户收到到RICH PUSH CALLBACK: "
							+ bundle.getString(JPushInterface.EXTRA_EXTRA));
			// 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
			// 打开一个网页等..

		}
		else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent
				.getAction()))
		{
			boolean connected =
					intent.getBooleanExtra(
							JPushInterface.EXTRA_CONNECTION_CHANGE, false);
			Log.e(TAG, "[MyReceiver]" + intent.getAction()
					+ " connected state change to " + connected);
		}
		else
		{
			Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
		}
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle)
	{
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet())
		{
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID))
			{
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}
			else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE))
			{
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			}
			else
			{
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
	
	private void startMainActivity(Context context)
	{
		try
		{
			ComponentName cn =
					new ComponentName(context, JPushReceiver.class);
			ActivityInfo info =
					context.getPackageManager().getReceiverInfo(cn,
							PackageManager.GET_META_DATA);
			if (null != info)
			{
				String packageName = null;
				String launcherActivity = null;
				if (null != info.metaData)
				{
					if (info.metaData.containsKey(APP_PACKAGENAME))
					{
						packageName =
								info.metaData.getString(APP_PACKAGENAME);
					}
					if (info.metaData.containsKey(LAUNCHER_ACTIVITY))
					{
						launcherActivity =
								info.metaData.getString(LAUNCHER_ACTIVITY);
					}
				}

				if (!isActivityRun(context, packageName))
				{
					// 启动首页
					Intent startHome = new Intent("android.intent.action.MAIN");  
					startHome.addCategory("android.intent.category.LAUNCHER");
					ComponentName componentName =
							new ComponentName(packageName, launcherActivity);
					startHome.setComponent(componentName);
					startHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(startHome);
				}
			}
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	private static boolean isActivityRun(Context context, String packageName)
	{
		if (null == context)
		{
			return true;
		}

		if (StringUtil.isEmptyString(packageName))
		{
			return true;
		}

		ActivityManager am =
				(ActivityManager) context
						.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(100);
		boolean isAppRunning = false;
		for (RunningTaskInfo info : list)
		{
			if (info.topActivity.getPackageName().equals(packageName)
					|| info.baseActivity.getPackageName().equals(packageName))
			{
				isAppRunning = true;
				break;
			}
		}

		return isAppRunning;
	}
}
