
package com.android.zcomponent.http.image;

import android.app.Activity;

import com.android.component.zframework.R;
import com.android.zcomponent.util.MyLayoutAdapter;

public abstract class ImageUrlFormat
{

	private static float miScaleRatio = 1;

	private static int mBigHeight;

	private static int mMiddleHeight;

	private static int mSmallMiddleHeight;

	private static int mSmallHeight;

	private Activity mActivity;

	public void init(Activity activity)
	{
		mActivity = activity;
		mBigHeight =
				mActivity.getResources().getDimensionPixelSize(
						R.dimen.image_panel_height_big);
		mMiddleHeight =
				mActivity.getResources().getDimensionPixelSize(
						R.dimen.image_panel_height_middle);
		mSmallMiddleHeight =
				mActivity.getResources().getDimensionPixelSize(
						R.dimen.image_panel_height_small_middle);
		mSmallHeight =
				mActivity.getResources().getDimensionPixelSize(
						R.dimen.image_height_small);
	}

	private static float getScaleRatio()
	{
		int screenHeight =
				(int) MyLayoutAdapter.getInstance().getScreenHeight();
		float scaleRatio = 1;
		if (screenHeight > 480 && screenHeight <= 960)
		{
			scaleRatio = scaleRatio - 0.1f;
		}
		else if (screenHeight >= 960 && screenHeight < 1920)
		{
			scaleRatio = scaleRatio - 0.2f;
		}
		else if (screenHeight >= 1920)
		{
			scaleRatio = scaleRatio - 0.3f;
		}

		return scaleRatio;
	}

	public static int[] getImageLayoutParams(ImageScaleModel mode)
	{
		int width = 0;
		int height = 0;
		int screenHeight =
				(int) MyLayoutAdapter.getInstance().getScreenHeight();
		miScaleRatio = getScaleRatio();
		if (mode == ImageScaleModel.FULL_SCREEN)
		{
			width = (int) MyLayoutAdapter.getInstance().getScreenWidth();
			height = (int) MyLayoutAdapter.getInstance().getScreenHeight();
			width = (int) (width * miScaleRatio);
			height = (int) (height * miScaleRatio);
		}
		else if (mode == ImageScaleModel.BIG)
		{

			width = (int) MyLayoutAdapter.getInstance().getScreenWidth();

			if (mBigHeight > 0)
			{
				height = mBigHeight;
			}
			else
			{
				if (screenHeight == 480)
				{
					height =
							(int) (240 * MyLayoutAdapter.getInstance()
									.getDensityRatio());
				}
				else if (screenHeight == 1280 || screenHeight == 1920)
				{
					height =
							(int) (360 * MyLayoutAdapter.getInstance()
									.getDensityRatio());
				}
				else
				{
					height =
							(int) (300 * MyLayoutAdapter.getInstance()
									.getDensityRatio());
				}
			}
			width = (int) (width * miScaleRatio);
			height = (int) (height * miScaleRatio);
		}
		else if (mode == ImageScaleModel.MIDDLE)
		{
			width = (int) MyLayoutAdapter.getInstance().getScreenWidth();

			if (mMiddleHeight > 0)
			{
				height = mMiddleHeight;
			}
			else
			{
				if (screenHeight == 480)
				{
					height =
							(int) (220 * MyLayoutAdapter.getInstance()
									.getDensityRatio());
				}
				else if (screenHeight == 1280 || screenHeight == 1920)
				{
					height =
							(int) (300 * MyLayoutAdapter.getInstance()
									.getDensityRatio());
				}
				else
				{
					height =
							(int) (260 * MyLayoutAdapter.getInstance()
									.getDensityRatio());
				}
			}
			width = (int) (width * miScaleRatio);
			height = (int) (height * miScaleRatio);
		}
		else if (mode == ImageScaleModel.SMALL_MIDDLE)
		{
			width = (int) MyLayoutAdapter.getInstance().getScreenWidth();
			if (mSmallMiddleHeight > 0)
			{
				height = mSmallMiddleHeight;
			}
			else
			{
				if (screenHeight == 480)
				{
					height =
							(int) (160 * MyLayoutAdapter.getInstance()
									.getDensityRatio());
				}
				else if (screenHeight == 1280 || screenHeight == 1920)
				{
					height =
							(int) (220 * MyLayoutAdapter.getInstance()
									.getDensityRatio());
				}
				else
				{
					height =
							(int) (180 * MyLayoutAdapter.getInstance()
									.getDensityRatio());
				}
			}
			width = (int) (width * miScaleRatio);
			height = (int) (height * miScaleRatio);
		}
		else if (mode == ImageScaleModel.SMALL)
		{
			if (mSmallHeight > 0)
			{
				width = mSmallHeight;
				height = mSmallHeight;
			}
			else
			{
				width =
						(int) (80 * MyLayoutAdapter.getInstance()
								.getDensityRatio());
				height =
						(int) (80 * MyLayoutAdapter.getInstance()
								.getDensityRatio());
			}
		}

		return new int[] { width, height };
	}

	public String getScaleImageUrl(String resImageUrl, ImageScaleModel mode)
	{
		return getScaleImageUrl(resImageUrl, mode, 60);
	}

	public String getScaleImageUrl(String resImageUrl, ImageScaleModel mode,
			int compress)
	{
		int[] layoutParams = getImageLayoutParams(mode);
		return getScaleImageUrl(resImageUrl, layoutParams[0], layoutParams[1],
				compress);
	}

	public static String getImageFormat(String resImageUrl)
	{
		String format = "jpg";
		if (resImageUrl.contains(".png"))
		{
			format = "png";
		}

		return format;
	}

	public abstract String getScaleImageUrl(String resImageUrl, int width,
			int height, int compress);

	/** 是否是正式版本（调试版本设置为false，正式版本设置为true） */
	public static boolean IS_RELEASE_VERSION = true;
}
