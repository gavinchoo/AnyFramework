
package com.android.zcomponent.util.update.res;

import java.io.File;

import com.android.zcomponent.util.LogEx;

public abstract class ResUpdate implements IResUpdate
{

	private static final String TAG = ResUpdate.class.getSimpleName();
	
	protected String mstrFileName;

	protected String mstrFilePath;

	protected String mstrNewResUrl;

	protected String mstrTimestamp;

	@Override
	public void updateFile(String newResUrl, String fileName)
	{
		mstrFileName = fileName;
		mstrNewResUrl = newResUrl;

		if (needUpdate())
		{
			LogEx.i(TAG, "Load New Image Res");
			loadNewRes();
		}
	}

	@Override
	public File searchFile(String keyword)
	{
		File[] files = new File(mstrFilePath + "/").listFiles();
		for (File file : files)
		{
			if (file.getName().indexOf(keyword) >= 0)
			{
				return file;
			}
		}
		return null;
	}
	
	@Override
	public void delete(String fileName)
	{
		File[] files = new File(mstrFilePath + "/").listFiles();
		for (File file : files)
		{
			if (file.getName().indexOf(fileName) >= 0)
			{
				file.delete();
			}
		}
	}
	
	@Override
	public void deleteAll()
	{
		File[] files = new File(mstrFilePath + "/").listFiles();
		for (File file : files)
		{
			file.delete();
		}
	}

	public abstract boolean needUpdate();

	public abstract void loadNewRes();
	
	public abstract void deleteOldRes(String fileName);

	public abstract <T> void saveNewRes(T t);

	public abstract <T> T readNewRes(String fileName);
}
