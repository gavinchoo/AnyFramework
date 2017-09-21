/**  
 * 文件名：AppUpdateHelper.java  
 *  
 * 版本信息：  
 * 日期：2013-7-25  
 * Copyright 足下 Corporation 2013   
 * 版权所有  
 *  
 */

package com.android.zcomponent.util.update;

import java.io.File;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.component.zframework.R;
import com.android.zcomponent.common.uiframe.FramewrokApplication;
import com.android.zcomponent.util.ClientInfo;
import com.android.zcomponent.util.CustomDialog;
import com.android.zcomponent.util.FileUtil;
import com.android.zcomponent.util.ShowMsg;
import com.android.zcomponent.util.StringUtil;

/**
 * 
 * 项目名称：OrderDishEmployee 类名称：AppUpdateHelper 类描述： 应用程序更新助手 创建人：zhouwu
 * 创建时间：2013-7-25 下午2:40:51 修改人：zhouwu 修改时间：2013-7-25 下午2:40:51 修改备注：
 * 
 * @version
 * 
 */
public class AppUpdate
{

	private static Context mContext;

	private CustomDialog noticeDialog;

	private static String saveFilePath = "/sdcard/aizachi/";

	private static String saveFileName = "Aizachi.apk";

	private static boolean isForceUpdate = false;

	/**
	 * 更新地址
	 */
	public String mUrl;

	/**
	 * 更新内容
	 */
	public String mUpdateContent;

	/**
	 * 更新版本号
	 */
	public String mUpdateVersionName;

	public int mUpdateVersionCode;

	private static CustomDialog mProgressDialog;

	private static ProgressBar mProgressBar;

	private static TextView mtvewProgress;

	private static TextView mtvewInstall;

	private static TextView mtvewProgressTitle;

	private static int mProgressMessage;

	public AppUpdate(Context context)
	{
		this.mContext = context;
		try
		{
			ApplicationInfo appInfo =
					mContext.getPackageManager().getApplicationInfo(
							mContext.getPackageName(),
							PackageManager.GET_META_DATA);
			String name = appInfo.metaData.getString("apk_name");
			String fileUrl = appInfo.metaData.getString("apk_file_uri");
			if (!StringUtil.isEmptyString(fileUrl))
			{
				saveFilePath =
						Environment.getExternalStorageDirectory() + "/"
								+ fileUrl;
			}
			if (!StringUtil.isEmptyString(name))
			{
				saveFileName = name;
			}
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * <p>
	 * Description: 版本更新
	 * <p>
	 * 
	 * @date 2015-3-23
	 * @author wei
	 * @param url
	 *            新版本下载地址
	 * @param updateVersionCode
	 *            新版本号
	 * @param updateVersionName
	 *            新版本名称
	 * @param updateContent
	 *            新版本描述
	 * @return
	 */
	public boolean checkUpdateInfo(String url, int updateVersionCode,
			String updateVersionName, String updateContent, int mask)
	{
		this.mUrl = url;
		this.mUpdateContent = updateContent;
		this.mUpdateVersionCode = updateVersionCode;
		this.mUpdateVersionName = updateVersionName;
		
		if (isNewVersion(updateVersionCode))
		{
			showNoticeDialog(mask);
			return false;
		}
		return true;
	}

	public static boolean isNewVersion(int updateVersionCode)
	{
		try
		{
			// 获取当前软件包信息
			PackageInfo pi =
					mContext.getPackageManager().getPackageInfo(
							mContext.getPackageName(),
							PackageManager.GET_CONFIGURATIONS);
			// 当前软件版本号
			int curversionCode = pi.versionCode;
			if (updateVersionCode > curversionCode)
			{
				return true;
			}
			return false;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return false;
	}

	private void showNoticeDialog(final int mask)
	{
		noticeDialog =
				new CustomDialog(mContext, R.layout.update_dialog, false, false);
		Button download =
				(Button) noticeDialog
						.findViewById(R.id.update_dialog_start_download);
		Button cancel =
				(Button) noticeDialog
						.findViewById(R.id.update_dialog_update_later);

		if (mask == 1)
		{
			isForceUpdate = true;
			LinearLayout cancelParent =
					(LinearLayout) noticeDialog
							.findViewById(R.id.update_dialog_update_later_parent);
			cancelParent.setVisibility(View.GONE);
			download.setText("立即更新");
			download.setBackgroundResource(R.drawable.common_dialog_single_btn_bg_selector);
		}
		TextView version =
				(TextView) noticeDialog
						.findViewById(R.id.update_dialog_version);
		TextView content =
				(TextView) noticeDialog
						.findViewById(R.id.updete_dialog_content);
		version.setText(mUpdateVersionName);
		content.setText(mUpdateContent);
		noticeDialog.setOnCancelListener(new OnCancelListener()
		{

			@Override
			public void onCancel(DialogInterface arg0)
			{
				if (null != mOnDismissListener)
				{
					mOnDismissListener.onVisibiltyChange(false);
				}
				if (isForceUpdate)
				{
					FramewrokApplication.exit();
				}
			}
		});
		download.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				noticeDialog.dismiss();
				if (isForceUpdate)
				{
					showProgressDialog();
				}
				startUpdate();
			}
		});
		cancel.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				noticeDialog.dismiss();
			}
		});
		noticeDialog.setOnDismissListener(new OnDismissListener()
		{

			@Override
			public void onDismiss(DialogInterface arg0)
			{
				if (null != mOnDismissListener)
				{
					mOnDismissListener.onVisibiltyChange(false);
				}
			}
		});
		if (null != mOnDismissListener)
		{
			mOnDismissListener.onVisibiltyChange(true);
		}
		noticeDialog.show();
	}

	private OnNoticeDialogVisibiltyChangeListener mOnDismissListener;

	public void setNoticeDialogVisibiltyChangeListener(
			OnNoticeDialogVisibiltyChangeListener dismissListener)
	{
		mOnDismissListener = dismissListener;
	}

	public interface OnNoticeDialogVisibiltyChangeListener
	{

		public void onVisibiltyChange(boolean isVisiable);
	}

	private void showProgressDialog()
	{
		mProgressDialog =
				new CustomDialog(mContext, R.layout.update_progress_dialog,
						false, false);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mtvewProgressTitle =
				(TextView) mProgressDialog.findViewById(R.id.title);
		mtvewProgress =
				(TextView) mProgressDialog
						.findViewById(R.id.update_progress_text);
		mProgressBar =
				(ProgressBar) mProgressDialog
						.findViewById(R.id.update_progress);
		mtvewInstall =
				(TextView) mProgressDialog
						.findViewById(R.id.update_progress_install);
		mtvewInstall.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				if (mProgressMessage == UpdateDownloadService.DOWNLOAD_FAIL
						|| mProgressMessage == UpdateDownloadService.DOWNLOAD_STOP)
				{
					showProgressDownload();
					startUpdateService();
				}
				else
				{
					ClientInfo.installApk(mContext, saveFilePath, saveFileName);
				}
			}
		});
		mProgressDialog.setOnKeyListener(new OnKeyListener()
		{

			@Override
			public boolean onKey(DialogInterface arg0, int arg1, KeyEvent event)
			{
				if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
				{
					mProgressDialog.dismiss();
					if (isForceUpdate)
					{
						FramewrokApplication.exit();
					}
				}
				return false;
			}
		});
		mProgressDialog.setOnCancelListener(new OnCancelListener()
		{

			@Override
			public void onCancel(DialogInterface arg0)
			{
				if (isForceUpdate)
				{
					FramewrokApplication.exit();
				}
			}
		});
		mProgressDialog.show();
	}

	public static Handler mProgressHandler = new Handler(new Callback()
	{

		@Override
		public boolean handleMessage(Message msg)
		{
			if (isForceUpdate)
			{
				mProgressMessage = msg.what;
				if (msg.what == UpdateDownloadService.DOWNLOAD_FAIL)
				{
					showProgressReload();
				}
				else if (msg.what == UpdateDownloadService.DOWNLOAD_STOP)
				{
					showProgressStop();
				}
				else
				{
					mtvewProgress.setText(msg.arg1 + "%");
					mProgressBar.setProgress(msg.arg1);
					if (msg.arg1 == 100)
					{
						showProgressInstall();
					}
				}
			}
			return false;
		}
	});

	private static void showProgressInstall()
	{
		if (null != mProgressDialog)
		{
			mtvewProgressTitle.setText("下载完成，请安装");
			mtvewProgress.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.GONE);
			mtvewInstall.setVisibility(View.VISIBLE);
			mtvewInstall.setText("立即安装");
		}
	}

	private static void showProgressDownload()
	{
		if (null != mProgressDialog)
		{
			mtvewProgressTitle.setText("正在下载");
			mtvewProgress.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.VISIBLE);
			mtvewInstall.setVisibility(View.GONE);
		}
	}

	private static void showProgressReload()
	{
		if (null != mProgressDialog)
		{
			mtvewProgressTitle.setText("下载失败，请重试");
			mtvewProgress.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.GONE);
			mtvewInstall.setVisibility(View.VISIBLE);
			mtvewInstall.setText("重试");
		}
	}

	private static void showProgressStop()
	{
		if (null != mProgressDialog)
		{
			mtvewProgressTitle.setText("下载已暂停");
			mtvewProgress.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.GONE);
			mtvewInstall.setVisibility(View.VISIBLE);
			mtvewInstall.setText("继续下载");
		}
	}

	public static Handler getProgressHandler()
	{
		return mProgressHandler;
	}

	/**
	 * 通知栏后台下载
	 */
	private void startUpdate()
	{
		if (!FileUtil.hasSDCard())
		{
			File dir =
					mContext.getDir("apk", Context.MODE_PRIVATE
							| Context.MODE_WORLD_READABLE
							| Context.MODE_WORLD_WRITEABLE);
			saveFilePath = dir.getPath() + "/";
		}

		File apkFile = new File(saveFilePath + saveFileName);
		PackageInfo info = null;
		if (apkFile.exists())
		{
			// 查找本地是否已经下载了新的apk
			PackageManager pm = mContext.getPackageManager();
			info =
					pm.getPackageArchiveInfo(saveFilePath + saveFileName,
							PackageManager.GET_ACTIVITIES);
		}

		if (info != null && mUpdateVersionCode == info.versionCode)
		{
			ShowMsg.showToast(mContext, "应用已下载成功，请直接安装！");
			ClientInfo.installApk(mContext, saveFilePath, saveFileName);
			if (isForceUpdate)
			{
				showProgressInstall();
			}
		}
		else
		{
			startUpdateService();
		}
	}

	public void startUpdateService()
	{
		if (null == mContext)
		{
			return;
		}
		Intent in = new Intent();
		in.setAction(UpdateDownloadService.ACTION_UPDATE_DOWNLOAD);
		in.putExtra("url", mUrl);
		in.putExtra("version", mUpdateVersionName);
		in.putExtra("content", mUpdateContent);
		in.putExtra("apk_name", saveFileName);
		in.putExtra("apk_file_uri", saveFilePath);
		in.setPackage(mContext.getPackageName());
		mContext.startService(in);
	}

	public static void stopUpdateService()
	{
		if (null == mContext)
		{
			return;
		}
		Intent in = new Intent();
		in.setAction(UpdateDownloadService.ACTION_UPDATE_DOWNLOAD);
		mContext.stopService(in);
	}

	public static void cancel()
	{
		try
		{
			UpdateDownloadService.pauseDownloader();
			UpdateDownloadService.cancelNotification();
			stopUpdateService();
		}
		catch (Exception e)
		{
		}
	}
}
