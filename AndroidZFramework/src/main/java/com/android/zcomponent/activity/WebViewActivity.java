
package com.android.zcomponent.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.android.component.zframework.R;
import com.android.zcomponent.annotation.ZSwipeBack;
import com.android.zcomponent.fragment.WebViewFragment;
import com.android.zcomponent.util.StringUtil;
import com.android.zcomponent.views.swipebacklayout.lib.app.SwipeBackActivity;

@ZSwipeBack(Enable = false)
public class WebViewActivity extends SwipeBackActivity
{

	public static final String WEB_URL = "url";

	public static final String WEB_HTML = "html";

	public static final String WEB_TITLE = "title";

	public static final String WEB_NO_TITLE = "no_title";

	private static final String TAG = WebViewActivity.class.getSimpleName();

	private String mUrl;

	private String mTitle;

	private String mHtml;

	private boolean isHasTitle;
	
	private WebViewFragment mWebViewFragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);
		init();
	}

	private void init()
	{
		Intent intent = getIntent();
		mUrl = intent.getStringExtra(WEB_URL);
		mTitle = intent.getStringExtra(WEB_TITLE);
		mHtml = intent.getStringExtra(WEB_HTML);
		isHasTitle =
				intent.getBooleanExtra(WebViewActivity.WEB_NO_TITLE, true);
		if (isHasTitle)
		{
			getTitleBar().getView().setVisibility(View.VISIBLE);
		}
		else
		{
			getTitleBar().getView().setVisibility(View.GONE);
		}
		if (!StringUtil.isEmptyString(mTitle))
		{
			getTitleBar().setTitleText(mTitle);
		}
		
		mWebViewFragment = new WebViewFragment()
		{
			
			@Override
			public String getUrl()
			{
				return mUrl;
			}

			@Override
			public String getTitle()
			{
				return mTitle;
			}

			@Override
			public String getHtml()
			{
				return mHtml;
			}
		};
		
		addFragment(R.id.flayout_webview, mWebViewFragment);
	}

	@Override
	public void onTitleBarBackClick(View view)
	{
		if (mWebViewFragment.canGoBack())
		{
			mWebViewFragment.goBack();
		}
		else
		{
			finish();
		}
	}

	@Override
	public boolean onKeyBack(int iKeyCode, KeyEvent event)
	{
		if (event.getAction() == KeyEvent.ACTION_UP)
		{
			if (mWebViewFragment.canGoBack())
			{
				mWebViewFragment.goBack();
				return true;
			}
		}
		return super.onKeyBack(iKeyCode, event);
	}

	/**
	 * <p>
	 * Description: 打开网页
	 * <p>
	 * 
	 * @date 2015-9-6
	 * @author WEI
	 * @param context
	 * @param url
	 *            网页地址
	 * @param title
	 *            网页标题，当值传空时，显示网页设置的标题
	 */
	public static void openUrl(Context context, String url, String title)
	{
		Intent intent = new Intent(context, WebViewActivity.class);
		intent.putExtra(WebViewActivity.WEB_URL, url);
		intent.putExtra(WebViewActivity.WEB_TITLE, title);
		context.startActivity(intent);
	}

	public static void openUrl(Context context, String url,
			boolean hasTitle, String title)
	{
		Intent intent = new Intent(context, WebViewActivity.class);
		intent.putExtra(WebViewActivity.WEB_URL, url);
		intent.putExtra(WebViewActivity.WEB_TITLE, title);
		intent.putExtra(WebViewActivity.WEB_NO_TITLE, hasTitle);
		context.startActivity(intent);
	}
	
	public static void openHtmlData(Context context, String url, String title)
	{
		Intent intent = new Intent(context, WebViewActivity.class);
		intent.putExtra(WebViewActivity.WEB_HTML, url);
		intent.putExtra(WebViewActivity.WEB_TITLE, title);
		context.startActivity(intent);
	}
}
