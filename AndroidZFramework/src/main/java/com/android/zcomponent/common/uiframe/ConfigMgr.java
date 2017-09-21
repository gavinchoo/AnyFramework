
package com.android.zcomponent.common.uiframe;

/**
 * <p>
 * Copyright: Copyright (c) 2012
 * Company: ZTE
 * Description: 配置信息管理类的实现文件
 * </p>
 * @Title ConfigMgr.java
 * @Package com.zte.iptvclient.android.uiframe
 * @version 1.0
 * @author 0043200560
 * @date 2012-3-19
 */

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.text.TextUtils;

import com.android.zcomponent.communication.http.implement.HttpCommunicator;
import com.android.zcomponent.constant.GlobalConst;
import com.android.zcomponent.util.LogEx;
import com.android.zcomponent.util.LogEx.LogLevelType;
import com.android.zcomponent.util.StringUtil;

/**
 * 配置信息管理类
 * 
 * @ClassName:ConfigMgr
 * @Description:配置信息管理类
 * @author: 0043200560
 * @date: 2012-3-19
 * 
 */
public final class ConfigMgr
{

	private static final String TAG = "ConfigMgr";

	/** 成功 */
	private static final int SUCCESS = 0;

	/** 失败 */
	private static final int FAILURE = -1;

	/** 安装配置文件失败 */
	private static final int FAILURE_CHECK_CONFIG_FILE = -2;

	/** 显示版本号关键字 */
	public static final String KEY_SHOW_VERSION = "$showversion";

	/** 打开调试日志 */
	public static final String KEY_START_DEBUG_LOG = "$startdebug";

	/** 关闭调试日志 */
	public static final String KEY_STOP_DEBUG_LOG = "$stopdebug";

	private ConfigMgr()
	{

	}

	/**
	 * 初始化方法
	 * <p>
	 * Description: 应用初始化方法
	 * <p>
	 * 
	 * @date 2012-3-19
	 * @param ctx
	 *            当前activity, iTerminalType 当前终端类型
	 * @return 0表示成功，其他表示失败。-2表示安装配置文件失败， -3表示检查网络失败， -4表示图片缓存设置失败
	 */
	public static int initConfig(Context ctx)
	{
		// 初始化日志打印设置
		initLogConfig(ctx);

		if (SUCCESS != checkConfigFile(ctx))
		{
			LogEx.e(TAG, "check config file error!");
			return FAILURE_CHECK_CONFIG_FILE;
		}
		return SUCCESS;
	}

	/**
	 * 检查当前应用下的配置文件是否存在。如果不存在则从apk中copy过去
	 * <p>
	 * Description: 检查当前应用下的配置文件是否存在。如果不存在则从apk中copy过去
	 * <p>
	 * 
	 * @date 2012-3-14
	 * @return 0表示成功，其他表示失败
	 */
	private static int checkConfigFile(Context ctx)
	{
		// 检查INI文件
		File fileConfFile =
				new File(GlobalConst.STR_CONFIG_FILE
						+ GlobalConst.STR_TARGET_FILE_NAME_INI);

		if (fileConfFile.exists())
		{
			fileConfFile.delete();
		}
		if (FAILURE == copyAssetFileToDataDir(ctx, GlobalConst.STR_CONFIG_FILE,
				GlobalConst.STR_TARGET_FILE_NAME_INI))
		{
			return FAILURE;
		}

		return SUCCESS;
	}

	/**
	 * 这里写方法名
	 * <p>
	 * Description: 将Asset中的文件copy到目标地址
	 * <p>
	 * 
	 * @date 2012-3-16
	 * @param fileDstPath
	 *            文件目标地址
	 * @param fileSrcName
	 *            文件名称
	 * @return 0表示成功，其他表示失败
	 */
	private static int copyAssetFileToDataDir(Context ctx, String fileDstPath,
			String fileSrcName)
	{
		LogEx.d(TAG, "start copy config file. file name is " + fileSrcName);
		InputStream inputStreamConfig = null;
		OutputStream outputStreamConfig = null;
		try
		{
			// 打开assets中的文件。
			inputStreamConfig = ctx.getAssets().open(fileSrcName);

			// 在data/date/com.zte.iptvclient.android.iptvclient.activity/下面创建文件
			outputStreamConfig =
					new FileOutputStream(fileDstPath + fileSrcName);

			// copy文件过去
			byte[] buffer = new byte[1024];
			int length;
			while ((length = inputStreamConfig.read(buffer)) > 0)
			{
				outputStreamConfig.write(buffer, 0, length);
			}
			outputStreamConfig.flush();
			return SUCCESS;
		}
		catch (Exception e)
		{
			LogEx.e(TAG, "copy config file error. file name is " + fileSrcName);
		}
		finally
		{
			try
			{
				outputStreamConfig.close();
				inputStreamConfig.close();
			}
			catch (Exception e)
			{
				LogEx.e(TAG, "close config file error. file name is "
						+ fileSrcName);
			}
		}
		return FAILURE;
	}

	/**
	 * 
	 * 设置日志级别
	 * <p>
	 * Description: 设置日志级别
	 * <p>
	 * 
	 * @date 2012-6-11
	 * @author jamesqiao10065075
	 * @param typeNew
	 *            新日志级别，LogLevelType的枚举类型
	 */
	public static void setLogLevel(LogLevelType typeNew)
	{
		LogEx.i(TAG, "setLogLevel start");
		LogEx.setLogLevel(typeNew);

		if (typeNew == LogLevelType.TYPE_LOG_LEVEL_DEBUG)
		{
			HttpCommunicator.IS_DEBUG_LEVEL = true;
		}
		else
		{
			HttpCommunicator.IS_DEBUG_LEVEL = false;
		}
	}

	/**
	 * 
	 * 获取日志级别
	 * <p>
	 * Description: 获取日志级别
	 * <p>
	 * 
	 * @date 2012-6-11
	 * @author
	 * @param typeNew
	 *            新日志级别，LogLevelType的枚举类型
	 */
	public static LogLevelType getLogLevel()
	{
		return LogEx.getLogLevel();
	}

	/**
	 * 设置日志信息
	 * <p>
	 * Description: 设置日志信息
	 * <p>
	 * 
	 * @date 2012-3-23
	 * @return
	 */
	private static void initLogConfig(Context ctx)
	{
		// LogEx.setLogLevel(LogLevelType.TYPE_LOG_LEVEL_DEBUG);
		InputStream inputStreamConfig = null;
		try
		{
			// 获取配置文件
			inputStreamConfig =
					ctx.getAssets().open(GlobalConst.STR_TARGET_FILE_NAME_INI);
			Properties properties = new Properties();
			properties.load(new BufferedInputStream(inputStreamConfig));

			// 读取日志是否将日志打印到文件配置
			String strdebugMode = properties.get("DebugMode").toString();

			// 如果不打印到文件，设置logex
			if (StringUtil.isEmptyString(strdebugMode)
					|| "0".equals(strdebugMode.trim()))
			{
				LogEx.setLogToFileFlag(false);
			}

			// 如果打印到文件，设置logex,并且设置日志文件路径
			else
			{
				LogEx.setLogToFileFlag(true);
				LogEx.setLogFilePath(getApplicationExceptionFilePath(ctx));

				// 根据对应位置设置日志文件大小，当为SD卡的时候设置为5M。当为机器内存的时候设置为1M。
				if (hasExternalStorage())
				{
					LogEx.setLogFileSize(5);
				}
				else
				{
					LogEx.setLogFileSize(1);
				}
			}
		}
		catch (Exception e)
		{
		}
	}

	/**
	 * 检查是否包含SD卡
	 * <p>
	 * Description: 检查是否包含SD卡
	 * <p>
	 * 
	 * @date 2012-3-23
	 * @return true表示包含。false表示没有SD卡
	 */
	private static boolean hasExternalStorage()
	{
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	/**
	 * 获取当前日志存放路径
	 * <p>
	 * Description: 获取当前日志存放路径
	 * <p>
	 * 
	 * @date 2012-3-23
	 * @param ctx
	 *            应用context
	 * @return 如果有SD卡，则返回SD卡的日志存放路径，如果没有，返回/data/data/引用下面的某个路径
	 */
	public static String getApplicationExceptionFilePath(Context ctx)
	{
		String strFilePath = null;

		// 如果有SK卡。返回一个SD卡日志存放路径
		if (hasExternalStorage())
		{
			String tempFilePath = null;
			try
			{
				ApplicationInfo appInfo =
						ctx.getPackageManager().getApplicationInfo(
								ctx.getPackageName(),
								PackageManager.GET_META_DATA);

				if (null != appInfo.metaData)
				{
					tempFilePath = appInfo.metaData.getString("log_file_path");
				}
			}
			catch (NameNotFoundException e)
			{
				e.printStackTrace();
			}

			if (TextUtils.isEmpty(tempFilePath))
			{
				strFilePath =
						Environment.getExternalStorageDirectory()
								.getAbsolutePath()
								+ GlobalConst.STR_SDCARD_EXCEPTION_FILE_PATH;
			}
			else
			{
				strFilePath =
						Environment.getExternalStorageDirectory()
								.getAbsolutePath() + tempFilePath;
			}
		}
		// 如果没有SK卡，返
		else
		{
			strFilePath =
					ctx.getApplicationContext().getFilesDir().getAbsolutePath()
							+ GlobalConst.STR_SYSTEM_EXCEPTION_FILE_PATH;
		}

		// 检查文件夹存不存在，如果不存在则创建
		File file = new File(strFilePath);
		if (!file.exists())
		{
			// 创建目录
			file.mkdirs();
		}

		return strFilePath;
	}

	/**
	 * 
	 * 处理命令行
	 * <p>
	 * Description: 处理用户输入的命令行
	 * <p>
	 * 
	 * @date 2012-5-24
	 * @author jamesqiao10065075
	 * @param ctxActivity
	 *            对话框上下文
	 * @param strCommand
	 *            命令行字符串
	 * @return 处理成功返回0，否则返回1。
	 */
	public static int processCommand(Context ctxActivity, String strCommand)
	{
		// 打开调试日志
		if (KEY_START_DEBUG_LOG.equalsIgnoreCase(strCommand))
		{
			LogEx.setLogLevel(LogLevelType.TYPE_LOG_LEVEL_DEBUG);
			LogEx.i(TAG, "Log level set to DEBUG.");
			return 0;
		}
		// 关闭调试日志
		else if (KEY_STOP_DEBUG_LOG.equalsIgnoreCase(strCommand))
		{
			LogEx.setLogLevel(LogLevelType.TYPE_LOG_LEVEL_ERROR);
			LogEx.i(TAG, "Log level set to ERROR.");
			return 0;
		}
		else
		{
			LogEx.w(TAG, "Unknown command:" + strCommand);
			return 1;
		}
	}
}
