
package com.android.zcomponent.http.image;

import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageSize;

public interface IDisplayImage
{

	public void reloadAllImage();

	public void loadImage(String uri, DisplayImageOptions options);

	public void loadImage(String uri, ImageSize targetImageSize,
						  DisplayImageOptions options);

	public void displayImage(String uri, ImageView imageView,
							 DisplayImageOptions options);

	public void loadImage(String uri, DisplayImageOptions options,
						  LoadingState loadingState);

	public void loadImage(String uri, ImageSize targetImageSize,
						  DisplayImageOptions options, LoadingState loadingState);

	public void displayImage(String uri, ImageView imageView,
							 DisplayImageOptions options, LoadingState loadingState);
}
