
package com.android.zcomponent.util.update;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;

import com.android.zcomponent.util.FileUtil;
import com.android.zcomponent.util.LogEx;

public class Downloader
{

	private static final String TAG = Downloader.class.getSimpleName();

	private String urlstr;// 下载的地址

	private String localfilePath;// 保存路径

	private String localFileName;

	private Handler mHandler;// 消息处理器

	private Dao dao;// 工具类

	private DownloadInfo infos;// 存放下载信息类的集合

	private static final int INIT = 1;// 定义三种下载的状态：初始化状态，正在下载状态，暂停状态

	private static final int DOWNLOADING = 2;

	private static final int PAUSE = 3;

	private int state = INIT;

	private Context mContext;

	public Downloader(String urlstr, String localFilePath, String fileName,
			Context context, Handler mHandler)
	{
		this.urlstr = urlstr;
		this.localfilePath = localFilePath;
		this.localFileName = fileName;
		this.mContext = context;
		this.mHandler = mHandler;
		dao = new Dao(context);
	}

	/**
	 * 判断是否正在下载
	 */
	public boolean isdownloading()
	{
		return state == DOWNLOADING;
	}

	/**
	 * 得到downloader里的信息 首先进行判断是否是第一次下载，如果是第一次就要进行初始化，并将下载器的信息保存到数据库中
	 * 如果不是第一次下载，那就要从数据库中读出之前下载的信息（起始位置，结束为止，文件大小等），并将下载信息返回给下载器
	 */
	public void startDownload()
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				LoadInfo loadInfo = null;
				if (isFirst(urlstr))
				{
					if (init())
					{
						infos = new DownloadInfo(0, 0, 0, 0, urlstr);
						// 创建一个LoadInfo对象记载下载器的具体信息
						loadInfo = new LoadInfo(0, 0, urlstr);
					}
					else
					{
						LogEx.e(TAG, "文件下载获取文件大小失败");
					}
				}
				else
				{
					if (!FileUtil.hasSDCard())
					{
						File dir =
								mContext.getDir("apk", Context.MODE_PRIVATE
										| Context.MODE_WORLD_READABLE
										| Context.MODE_WORLD_WRITEABLE);
						localfilePath = dir.getPath() + "/";
					}
					// 得到数据库中已有的urlstr的下载器的具体信息
					List<DownloadInfo> all = dao.getInfos(urlstr);
					if (null != all && all.size() > 0)
					{
						infos = all.get(0);
						LogEx.i(TAG, "not isFirst size=" + all.size());
						int fileSize =
								infos.getEndPos() - infos.getStartPos() + 1;
						loadInfo =
								new LoadInfo(fileSize,
										infos.getCompeleteSize(), urlstr);
					}
				}

				int download_precent = 0;
				if (null != loadInfo && loadInfo.getComplete() > 0
						&& loadInfo.getFileSize() > 0)
				{
					download_precent =
							loadInfo.getComplete() * 100
									/ loadInfo.getFileSize();
				}

				Message message = new Message();
				message.what = UpdateDownloadService.DOWNLOAD_UPDATE;
				message.arg1 = download_precent;
				mHandler.sendMessage(message);
				downLoadHandler.sendEmptyMessage(0);
			}
		}).start();
	}

	Handler downLoadHandler = new Handler(new Callback()
	{

		@Override
		public boolean handleMessage(Message arg0)
		{
			download();
			return false;
		}
	});

	/**
      */
	private boolean init()
	{
		try
		{
			File file = null;
			if (FileUtil.hasSDCard())
			{
				File pathfile = new File(localfilePath);
				if (!pathfile.exists() && !pathfile.isDirectory())
				{
					pathfile.mkdirs();
				}
				file = new File(localfilePath + localFileName);
				if (file.exists())
				{
					file.delete();
				}
				file.createNewFile();
			}
			else
			{
				File dir =
						mContext.getDir("apk", Context.MODE_PRIVATE
								| Context.MODE_WORLD_READABLE
								| Context.MODE_WORLD_WRITEABLE);
				localfilePath = dir.getPath() + "/";
				file = new File(localfilePath + localFileName);
				if (!file.exists())
				{
					file.createNewFile();
				}
				String[] command =
						{ "chmod", "777", localfilePath + localFileName };
				ProcessBuilder builder = new ProcessBuilder(command);
				try
				{
					builder.start();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			// 本地访问文件
			RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
			accessFile.close();
		}
		catch (Exception e)
		{
			downloadFail();
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 判断是否是第一次 下载
	 */
	private boolean isFirst(String urlstr)
	{
		return dao.isHasInfors(urlstr);
	}

	/**
	 * 114 * 利用线程开始下载数据 115
	 */
	public void download()
	{
		LogEx.i(TAG, "开始下载文件");
		if (infos != null)
		{
			if (state == DOWNLOADING)
				return;
			state = DOWNLOADING;

			new MyThread(infos.getThreadId(), infos.getStartPos(),
					infos.getCompeleteSize(), infos.getUrl()).start();
		}
		else
		{
			LogEx.e(TAG, "文件下载信息为空了");
		}
	}

	public void downloadFail()
	{
		Message message = Message.obtain();
		message.what = UpdateDownloadService.DOWNLOAD_FAIL;
		message.obj = "下载文件失败！";
		mHandler.sendMessage(message);
		if (null != dao)
		{
			dao.delete(urlstr);
		}
	}

	public class MyThread extends Thread
	{

		private int threadId;

		private int startPos;

		private int compeleteSize;

		private String urlstr;

		public MyThread(int threadId, int startPos, int compeleteSize,
				String urlstr)
		{
			this.threadId = threadId;
			this.startPos = startPos;
			this.compeleteSize = compeleteSize;
			this.urlstr = urlstr;
		}

		@Override
		public void run()
		{
			LogEx.i(TAG, "线程开始下载文件");
			RandomAccessFile randomAccessFile = null;
			InputStream is = null;
			try
			{
				randomAccessFile =
						new RandomAccessFile(localfilePath + localFileName,
								"rwd");
				// 如果程序下载过更新文件，被删除，重新开始下载
				if (randomAccessFile.length() == 0 && compeleteSize != 0)
				{
					compeleteSize = 0;
				}
				randomAccessFile.seek(startPos + compeleteSize);
				
				String range = "bytes=" + (startPos + compeleteSize) + "-";
				DefaultHttpClient httpClient = new DefaultHttpClient();
				// 请求超时
				httpClient.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
				// 读取超时
				httpClient.getParams().setParameter(
						CoreConnectionPNames.SO_TIMEOUT, 30000);
				HttpGet httpGet = new HttpGet(urlstr);
				httpGet.addHeader("Range", range);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				long fileSize = 0;
				if (null != httpResponse)
				{
					HttpEntity httpEntity = httpResponse.getEntity();
					if (null != httpEntity)
					{
						fileSize =
								httpEntity.getContentLength() + compeleteSize;
						is = httpEntity.getContent();
					}
				}

				// 第一次下载保存记录到数据库
				if (isFirst(urlstr))
				{
					infos =
							new DownloadInfo(0, 0, (int) fileSize,
									compeleteSize, urlstr);
					// 保存infos中的数据到数据库
					dao.saveInfos(infos);
				}

				if (null == is || fileSize == 0)
				{
					downloadFail();
					stopDownload(randomAccessFile, is);
					return;
				}

				byte[] buffer = new byte[4096];
				int length = -1;
				int percent = 0;
				LogEx.i(TAG, "开始读取文件流, 文件大小 " + fileSize);
				while ((length = is.read(buffer)) != -1)
				{
					randomAccessFile.write(buffer, 0, length);
					compeleteSize += length;
					dao.updataInfos(threadId, compeleteSize, urlstr);
					int tempPercent = (int) (compeleteSize * 100 / fileSize);
					if (tempPercent - percent >= 1)
					{
						percent = tempPercent;
						// 更新数据库中的下载信息
						// 用消息将下载信息传给进度条，对进度条进行更新
						Message message = Message.obtain();
						message.what = UpdateDownloadService.DOWNLOAD_UPDATE;
						message.obj = urlstr;
						message.arg1 = percent;
						mHandler.sendMessage(message);
					}
					if (state == PAUSE)
					{
						LogEx.e(TAG, "PAUSE");
						break;
					}
				}
				LogEx.i(TAG, "文件下载完成");
			}
			catch (Exception e)
			{
				downloadFail();
				e.printStackTrace();
			}
			finally
			{
				stopDownload(randomAccessFile, is);
			}
		}
	}

	private void stopDownload(RandomAccessFile randomAccessFile, InputStream is)
	{
		try
		{
			if (null != is)
			{
				is.close();
			}
			if (null != randomAccessFile)
			{
				randomAccessFile.close();
			}

			if (null != dao)
			{
				dao.closeDb();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// 删除数据库中urlstr对应的下载器信息
	public void delete(String urlstr)
	{
		dao.delete(urlstr);
	}

	// 设置暂停
	public void pause()
	{
		state = PAUSE;
	}

	// 重置下载状态
	public void reset()
	{
		state = INIT;
	}
}
