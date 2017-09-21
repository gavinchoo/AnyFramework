
package com.android.zcomponent.http.image;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.android.zcomponent.util.LogEx;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

public class ImageDisplayer implements IDisplayImage,
		ImageLoadingProgressListener, ImageLoadingListener
{

	private static final String TAG = ImageDisplayer.class.getSimpleName();

	private List<DisplayInfo> mDisplayInfos = new ArrayList<DisplayInfo>();

	@Override
	public void reloadAllImage()
	{
		LogEx.i(TAG, "DisplayInfos size = " + mDisplayInfos.size());

		for (DisplayInfo displayInfo : mDisplayInfos)
		{
			if (null != displayInfo.imageView)
			{
				displayImage(displayInfo.uri, displayInfo.imageView,
						displayInfo.options, displayInfo.loadingState);
			}
			else if (null != displayInfo.imageSize)
			{
				loadImage(displayInfo.uri, displayInfo.imageSize,
						displayInfo.options, displayInfo.loadingState);
			}
			else
			{
				loadImage(displayInfo.uri, displayInfo.options,
						displayInfo.loadingState);
			}
		}
	}

	@Override
	public void loadImage(String uri, DisplayImageOptions options,
			LoadingState loadingState)
	{
		saveDisplayInfo(uri, null, null, options, loadingState);
		ImageLoader.getInstance().loadImage(uri, options, this);
	}

	@Override
	public void loadImage(String uri, DisplayImageOptions options)
	{
		loadImage(uri, options, null);
	}

	@Override
	public void loadImage(String uri, ImageSize targetImageSize,
			DisplayImageOptions options, LoadingState loadingState)
	{
		saveDisplayInfo(uri, targetImageSize, null, options, loadingState);
		ImageLoader.getInstance().loadImage(uri, targetImageSize, options,
				this, this);
	}

	@Override
	public void loadImage(String uri, ImageSize imageSize,
			DisplayImageOptions options)
	{
		loadImage(uri, imageSize, options, null);
	}

	@Override
	public void displayImage(String uri, ImageView imageView,
			DisplayImageOptions options, LoadingState loadingState)
	{
		saveDisplayInfo(uri, null, imageView, options, loadingState);
		ImageLoader.getInstance().displayImage(uri, imageView, options, this,
				this);
	}

	@Override
	public void displayImage(String uri, ImageView imageView,
			DisplayImageOptions options)
	{
		displayImage(uri, imageView, options, null);
	}

	private void saveDisplayInfo(String uri, ImageSize imageSize,
			ImageView imageView, DisplayImageOptions options,
			LoadingState loadingState)
	{
		if (contains(uri))
		{
			return;
		}

		DisplayInfo displayInfo = new DisplayInfo();
		displayInfo.imageSize = imageSize;
		displayInfo.imageView = imageView;
		displayInfo.loadingState = loadingState;
		displayInfo.options = options;
		displayInfo.uri = uri;

		mDisplayInfos.add(displayInfo);
	}

	private boolean contains(String uri)
	{
		for (DisplayInfo displayInfo : mDisplayInfos)
		{
			if (uri.equals(displayInfo.uri))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public void onLoadingCancelled(String uri, View view)
	{
		for (DisplayInfo info : mDisplayInfos)
		{
			if (null != info.loadingState)
			{
				info.loadingState.onLoadingCancelled(uri, view);
			}
		}
	}

	@Override
	public void onLoadingComplete(String uri, View view, Bitmap bitmap)
	{
		for (DisplayInfo info : mDisplayInfos)
		{
			if (null != info.loadingState)
			{
				info.loadingState.onLoadingComplete(uri, view, bitmap);
			}
		}
	}

	@Override
	public void onLoadingFailed(String uri, View view, FailReason failReason)
	{
		for (DisplayInfo info : mDisplayInfos)
		{
			if (null != info.loadingState)
			{
				info.loadingState.onLoadingFailed(uri, view, failReason);
			}

		}
	}

	@Override
	public void onLoadingStarted(String uri, View view)
	{
		for (DisplayInfo info : mDisplayInfos)
		{
			if (null != info.loadingState)
			{
				info.loadingState.onLoadingStarted(uri, view);
			}
		}
	}

	@Override
	public void onProgressUpdate(String uri, View view, int current, int total)
	{
		for (DisplayInfo info : mDisplayInfos)
		{
			if (null != info.loadingState)
			{

			}
			info.loadingState.onProgressUpdate(uri, view, current, total);
		}
	}

	public class DisplayInfo
	{

		String uri;

		ImageSize imageSize;

		ImageView imageView;

		DisplayImageOptions options;

		LoadingState loadingState;
	}
}
