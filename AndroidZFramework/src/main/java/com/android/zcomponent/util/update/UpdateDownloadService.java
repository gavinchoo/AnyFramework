
package com.android.zcomponent.util.update;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.android.component.zframework.R;
import com.android.zcomponent.util.ClientInfo;
import com.android.zcomponent.util.CommonUtil;
import com.android.zcomponent.util.LogEx;
import com.android.zcomponent.util.ShowMsg;
import com.android.zcomponent.util.StringUtil;

public class UpdateDownloadService extends Service
{

	public static final String TAG = "[软件更新服务]";

	public static final int DOWNLOAD_STOP = 100;

	public static final int DOWNLOAD_FAIL = 101;

	public static final int DOWNLOAD_UPDATE = 102;

	public final static String ACTION_CANCEL_UPDATE =
			"com.android.zcomponent.util.update.cancel";

	public final static String ACTION_CANCEL_PAUSE =
			"com.android.zcomponent.util.update.pause";

	public final static String ACTION_UPDATE_DOWNLOAD =
			"com.android.zcomponent.util.update";

	private Notification notification;

	private String mstrApkName = "";

	private String mstrApkFileUrl = "";

	/**
	 * 正在下载的标记
	 */
	public static boolean mFlagDownloading = false;

	public static NotificationManager mNotificationManager;

	private MyHandler myHandler;

	private RemoteViews views;

	public static int notificationId = 1234;

	private static Context mContext;

	private static Downloader downloader;

	private static boolean isNotificationCanceled;

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		mContext = this;
		myHandler = new MyHandler(Looper.myLooper(), this);
		registerReceiver();
		intiNotification();
		if (null != intent)
		{
			String name = intent.getStringExtra("apk_name");
			String fileUrl = intent.getStringExtra("apk_file_uri");
			String uri = intent.getStringExtra("url");

			LogEx.d(TAG, "mstrApkName = " + mstrApkName);
			LogEx.d(TAG, "mstrApkFileUrl = " + mstrApkFileUrl);

			if (!StringUtil.isEmptyString(name))
			{
				mstrApkName = name;
			}
			if (!StringUtil.isEmptyString(fileUrl))
			{
				mstrApkFileUrl = fileUrl;
			}

			// 启动线程开始执行下载任务
			downFile(uri);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	public static NotificationManager initNotificationManager()
	{
		if (mNotificationManager == null)
		{
			mNotificationManager =
					(NotificationManager) mContext
							.getSystemService(Context.NOTIFICATION_SERVICE);
		}
		return mNotificationManager;
	}

	private void intiNotification()
	{
		// 设置任务栏中下载进程显示的views
		views =
				new RemoteViews(getPackageName(),
						R.layout.update_notification_layout);
		if (Build.VERSION.SDK_INT <= 9)
		{
			views.setViewVisibility(R.id.update_pause, View.GONE);
		}
		else
		{
			views.setViewVisibility(R.id.update_pause, View.VISIBLE);
			Intent btIntent = new Intent().setAction(ACTION_CANCEL_PAUSE);
			PendingIntent btPendingIntent =
					PendingIntent.getBroadcast(this, 1, btIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);
			views.setOnClickPendingIntent(R.id.update_pause, btPendingIntent);
		}
		// 初始化 状态栏通知
		Builder mBuilder = new Builder(this);
		mBuilder.setContent(views)
				.setContentIntent(
						PendingIntent.getBroadcast(this, 2, new Intent(),
								Notification.FLAG_ONGOING_EVENT))
				.setWhen(System.currentTimeMillis())
				// 通知产生的时间，会在通知信息里显示
				.setTicker(getString(R.string.app_name) + "更新")
				.setPriority(Notification.PRIORITY_DEFAULT)
				// 设置该通知优先级
				.setOngoing(true)
				.setSmallIcon(android.R.drawable.stat_sys_download);
		notification = mBuilder.build();
		// 点击 状态栏通知 取消下载
		Intent cintent = new Intent();
		cintent.setAction(ACTION_CANCEL_UPDATE);
		PendingIntent cancelIntent =
				PendingIntent.getBroadcast(this, 3, cintent,
						PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(this, "", "", cancelIntent);
		notification.flags |= Notification.FLAG_NO_CLEAR;
		notification.deleteIntent = cancelIntent;
		notification.defaults = Notification.DEFAULT_LIGHTS;
		// 将下载任务添加到任务栏中
		addNotification(notification);
	}

	public static void cancelNotification()
	{
		LogEx.i(TAG, "cancelNotification");
		initNotificationManager();
		isNotificationCanceled = true;
		mNotificationManager.cancel(notificationId);
	}

	public static void addNotification(Notification notification)
	{
		initNotificationManager();
		isNotificationCanceled = false;
		mNotificationManager.notify(notificationId, notification);
	}

	public static boolean isNotificationCanceled()
	{
		return isNotificationCanceled;
	}

	@Override
	public void onDestroy()
	{
		LogEx.i(TAG, "UpdateDownloadService onDestroy");
		stopDownloaod();
		super.onDestroy();
	}

	// 下载更新文件
	private void downFile(final String url)
	{
		Log.d(TAG, "开始下载更新文件..");
		mFlagDownloading = true;
		downloader =
				new Downloader(url, mstrApkFileUrl, mstrApkName, mContext,
						myHandler);
		if (downloader.isdownloading())
			return;
		downloader.startDownload();
	}

	public void stopDownloaod()
	{
		Message msg = new Message();
		msg.what = DOWNLOAD_STOP;
		stopDownload(msg);
	}

	private void stopDownload(Message msg)
	{
		Log.d(TAG, "发送异常或手动取消下载");
		mFlagDownloading = false;
		if (null != downloader)
		{
			downloader.pause();
		}
		if (null != AppUpdate.getProgressHandler())
		{
			AppUpdate.getProgressHandler().sendEmptyMessage(msg.what);
		}
		cancelNotification();
		unRegisterReceiver();
		// 停止掉当前的服务
		stopSelf();
	}
	
	public static void pauseDownloader()
	{
		mFlagDownloading = false;
		if (null != downloader)
		{
			downloader.pause();
		}
	}

	/* 事件处理类 */
	class MyHandler extends Handler
	{

		private Context context;

		public MyHandler(Looper looper, Context c)
		{
			super(looper);
			this.context = c;
		}

		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			if (msg != null)
			{
				switch (msg.what)
				{
				case 0:
				{
					ShowMsg.showToast(context, msg.obj.toString());
					break;
				}
				case DOWNLOAD_UPDATE:
				{
					int download_precent = msg.arg1;
					LogEx.i(TAG, "download_precent = " + download_precent);
					// 更新状态栏上的下载进度信息
					if (!isNotificationCanceled())
					{
						refreshNoficationView(download_precent);	
					}
					if (null != AppUpdate.getProgressHandler())
					{
						Message message = new Message();
						message.arg1 = msg.arg1;
						AppUpdate.getProgressHandler().sendMessage(message);
					}

					if (download_precent == 100)
					{
						// 下载完成后清除所有下载信息，执行安装提示
						Log.d(TAG, "开始安装");
						String url = (String) msg.obj;
						download_precent = 0;
						downloader.delete(url);
						downloader.reset();
						mFlagDownloading = false;
						ClientInfo.installApk(UpdateDownloadService.this,
								mstrApkFileUrl, mstrApkName);
						cancelNotification();
						unRegisterReceiver();
						// 停止掉当前的服务
						stopSelf();
					}
					break;
				}
				case DOWNLOAD_FAIL:
				case DOWNLOAD_STOP:
				{
					ShowMsg.showToast(context, msg.obj.toString());
					stopDownload(msg);
					break;
				}
				}
			}
		}
	}

	private void refreshNoficationView(int downloadPrecent)
	{
		LogEx.i(TAG, "refreshNoficationView");
		views.setTextViewText(R.id.tvProcess, "已下载" + downloadPrecent + "%");
		views.setProgressBar(R.id.pbDownload, 100, downloadPrecent, false);
		notification.contentView = views;
		addNotification(notification);
	}

	private void registerReceiver()
	{
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_CANCEL_UPDATE);
		filter.addAction(ACTION_CANCEL_PAUSE);
		registerReceiver(MyCancelBroadcast, filter);
	}

	private void unRegisterReceiver()
	{
		try
		{
			Log.d(TAG, "注销下载进度广播接收器");
			unregisterReceiver(MyCancelBroadcast);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public BroadcastReceiver MyCancelBroadcast = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context arg0, Intent intent)
		{
			LogEx.d(TAG, "intent.getAction() = " + intent.getAction());
			if (intent.getAction().equals(ACTION_CANCEL_UPDATE))
			{
				Log.d(TAG, "手动点击取消下载");
				if (null != myHandler)
				{
					Message message =
							myHandler.obtainMessage(DOWNLOAD_STOP, "下载已取消！");
					myHandler.sendMessage(message);
				}
			}
			else if (intent.getAction().equals(ACTION_CANCEL_PAUSE))
			{
				if (!CommonUtil.isLeastSingleClick())
				{
					return;
				}
				if (downloader.isdownloading())
				{
					views.setTextViewText(R.id.update_pause, "开始");
					downloader.pause();
				}
				else
				{
					views.setTextViewText(R.id.update_pause, "暂停");
					downloader.startDownload();
				}
				notification.contentView = views;
				addNotification(notification);
			}
		}
	};
}