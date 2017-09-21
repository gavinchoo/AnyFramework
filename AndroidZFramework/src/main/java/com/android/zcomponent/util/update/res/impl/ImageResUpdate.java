
package com.android.zcomponent.util.update.res.impl;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.view.View;

import com.android.zcomponent.constant.GlobalConst;
import com.android.zcomponent.util.BitmapUtil;
import com.android.zcomponent.util.ClientInfo;
import com.android.zcomponent.util.FileUtil;
import com.android.zcomponent.util.update.res.ResUpdate;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ImageResUpdate extends ResUpdate
{

	private Context mContext;

	private int versionCode;

	public ImageResUpdate()
	{
		init();
	}

	public ImageResUpdate(Context context, String timestamp)
	{
		mContext = context;
		mstrTimestamp = timestamp;
		init();
	}

	private void init()
	{
		if (null != mContext)
		{
			versionCode = ClientInfo.getVersionCode(mContext);
		}

		if (FileUtil.hasSDCard())
		{
			mstrFilePath =
					Environment.getExternalStorageDirectory() + "/"
							+ "aizachi/cache/images";
		}
		else
		{
			mstrFilePath = GlobalConst.STR_CONFIG_FILE + "images";
		}
		File fileDir = new File(mstrFilePath + "/");
		if (!fileDir.exists())
		{
			fileDir.mkdirs();
		}
	}

	@Override
	public boolean needUpdate()
	{
		String filePath = mstrFilePath + "/" + getFormatName();
		File file = new File(filePath);
		if (file.exists())
		{
			Bitmap bitmap = BitmapUtil.readBitmap(file, false);

			if (null != bitmap && bitmap.getWidth() > 0)
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		else
		{
			return true;
		}
	}

	@Override
	public void loadNewRes()
	{
		ImageLoader.getInstance().loadImage(mstrNewResUrl,
				new ImageLoadingListener()
				{

					@Override
					public void onLoadingStarted(String arg0, View arg1)
					{

					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2)
					{

					}

					@Override
					public void onLoadingComplete(String arg0, View arg1,
							Bitmap bitmap)
					{
						saveNewRes(bitmap);
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1)
					{

					}
				});
	}

	@Override
	public <T> void saveNewRes(T t)
	{
		deleteOldRes(mstrFileName);

		Bitmap bitmap = (Bitmap) t;

		CompressFormat format = null;
		if (mstrNewResUrl.contains(".png"))
		{
			format = CompressFormat.PNG;
		}
		else
		{
			format = CompressFormat.JPEG;
		}
		// 保存新图片到文件系统
		BitmapUtil.saveBitmap(mContext, bitmap, mstrFilePath, getFormatName(),
				format, false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T readNewRes(String fileName)
	{
		File newFile = searchFile(versionCode + fileName);
		Bitmap bitmap = BitmapUtil.readBitmap(newFile, false);
		if (null != bitmap)
		{
			return (T) bitmap;
		}
		return null;
	}

	@Override
	public void deleteOldRes(String fileName)
	{
		File oldFile = searchFile(fileName);
		if (null != oldFile)
		{
			oldFile.delete();
		}
	}

	public String getFormatName()
	{
		String format = "aoe";
		if (mstrNewResUrl.contains(".png"))
		{
			format = "aou";
		}

		return mstrTimestamp + versionCode + mstrFileName + "_" + format;
	}
}
