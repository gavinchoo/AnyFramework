
package com.android.zcomponent.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.android.component.zframework.R;
import com.android.zcomponent.common.uiframe.BaseActivity;
import com.android.zcomponent.common.uiframe.fragment.BaseFragment;
import com.android.zcomponent.http.constant.ErrorCode;
import com.android.zcomponent.util.LogEx;
import com.android.zcomponent.util.NetSateUtil;
import com.android.zcomponent.util.StringUtil;

public abstract class WebViewFragment extends BaseFragment
{

	private static final String TAG = WebViewFragment.class.getSimpleName();

	private WebView mWebView;

	private AudioManager mAudioMgr;

	private ProgressBar mProgressBar;

	private boolean isHasTitle;

	private View rootView;

	private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener =
			null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		rootView = inflater.inflate(R.layout.fragment_web_view, null);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		bindWidget();
		init();
	}

	private void bindWidget()
	{
		mWebView = (WebView) rootView.findViewById(R.id.kill_money_webview);
		mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
	}

	private void init()
	{
		initAudioManage();
		getDataEmptyView().setBackgroundResource(R.drawable.transparent);
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setDefaultTextEncodingName("gbk");
		webSettings.setLoadWithOverviewMode(true);// 3 设置Webview默认为全屏（满屏）显示
		webSettings.setUseWideViewPort(true);
		webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		webSettings.setDomStorageEnabled(true);
		webSettings.setDatabaseEnabled(true);
		webSettings.setPluginState(PluginState.ON);
		mWebView.setWebViewClient(new MyWebViewClient());
		mWebView.setVerticalScrollbarOverlay(true);

		mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		mWebView.setWebChromeClient(new WebChromeClient()
		{

			public void onProgressChanged(WebView view, int newProgress)
			{
				// activity的进度是0 to 10000 (both inclusive),所以要*100
				LogEx.e(TAG, "newProgress = " + newProgress);
				mProgressBar.setProgress(newProgress);
				if (newProgress == 100)
				{
					mProgressBar.setVisibility(View.GONE);
					if (BaseActivity.isNetConnected())
					{
						mWebView.setVisibility(View.VISIBLE);
						getDataEmptyView().setVisibility(View.GONE);
					}
				}
			}

			@Override
			public void onReceivedTitle(WebView view, String title)
			{
				super.onReceivedTitle(view, title);
				if (StringUtil.isEmptyString(getTitle()))
				{
					if (!StringUtil.isEmptyString(title))
					{
						((BaseActivity) getActivity()).getTitleBar().setTitleText(
								title);
					}
				}
				else
				{
					((BaseActivity) getActivity()).getTitleBar().setTitleText(
							getTitle());
				}
			}
		});
		
		loadUrl();
	}
	
	public void loadUrl()
	{
		if (!TextUtils.isEmpty(getUrl()))
		{
			mWebView.loadUrl(getUrl());
			getDataEmptyView().showViewWaiting();
		}
		else if (!TextUtils.isEmpty(getHtml()))
		{
			mWebView.loadData(getHtml(), "text/html; charset=utf-8", "utf-8");
			getDataEmptyView().showViewWaiting();
		}
		else
		{
			getDataEmptyView().showViewDataEmpty(true, true,
					ErrorCode.INT_QUERY_DATA_SUCCESS, "打开链接失败，请点击屏幕刷新");
			getDataEmptyView().getView().setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					loadUrl();
				}
			});
		}
	}
	
	public abstract String getUrl();
	public abstract String getTitle();
	public abstract String getHtml();

	private void initAudioManage()
	{
		mAudioMgr =
				(AudioManager) getActivity().getSystemService(
						Context.AUDIO_SERVICE);
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR_MR1)
		{
			mAudioFocusChangeListener =
					new AudioManager.OnAudioFocusChangeListener()
					{

						@Override
						public void onAudioFocusChange(int focusChange)
						{
							if (focusChange == AudioManager.AUDIOFOCUS_LOSS)
							{
								// 失去焦点之后的操作
							}
							else if (focusChange == AudioManager.AUDIOFOCUS_GAIN)
							{
								// 获得焦点之后的操作
							}
						}
					};
		}
	}

	@Override
	public void onPause()
	{
		requestAudioFocus();
		mWebView.onPause();
		super.onPause();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		abandonAudioFocus();
		mWebView.onResume();
	}

	@Override
	public void onDestroy()
	{
		mWebView.destroy();
		super.onDestroy();
	}

	@Override
	public void onDataEmptyClickRefresh()
	{
		getDataEmptyView().showViewWaiting();
		mWebView.loadUrl(getTitle());
	}

	private void requestAudioFocus()
	{
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ECLAIR_MR1)
		{
			return;
		}
		if (mAudioMgr == null)
			mAudioMgr =
					(AudioManager) getActivity().getSystemService(
							Context.AUDIO_SERVICE);
		if (mAudioMgr != null)
		{
			LogEx.i(TAG, "Request audio focus");
			int ret =
					mAudioMgr.requestAudioFocus(mAudioFocusChangeListener,
							AudioManager.STREAM_MUSIC,
							AudioManager.AUDIOFOCUS_GAIN);
			if (ret != AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
			{
				LogEx.i(TAG, "request audio focus fail. " + ret);
			}
		}

	}

	private void abandonAudioFocus()
	{
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ECLAIR_MR1)
		{
			return;
		}
		if (mAudioMgr != null)
		{
			LogEx.i(TAG, "Abandon audio focus");
			mAudioMgr.abandonAudioFocus(mAudioFocusChangeListener);
			mAudioMgr = null;
		}
	}

	private class MyWebViewClient extends WebViewClient
	{

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			if (url.contains("js-frame://"))
			{
				if (url.contains("closeWebView"))
				{
					getActivity().finish();
				}
			}
			return false;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon)
		{
			if (isHasTitle)
			{
				mProgressBar.setVisibility(View.VISIBLE);
			}
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url)
		{
			super.onPageFinished(view, url);
			if (NetSateUtil.isNetworkAvailable(getActivity()))
			{
				mWebView.setVisibility(View.VISIBLE);
				getDataEmptyView().setVisibility(View.GONE);
			}
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl)
		{
			LogEx.e("zjw", "onReceivedError");
			super.onReceivedError(view, errorCode, description, failingUrl);
			if (!NetSateUtil.isNetworkAvailable(getActivity()))
			{
				getDataEmptyView().showViewDataEmpty(true, true,
						ErrorCode.INT_NET_DISCONNECT,
						R.string.common_net_disconnect);
			}
			else
			{
				getDataEmptyView().showViewDataEmpty(true, true,
						ErrorCode.INT_QUERY_DATA_SUCCESS, "加载失败");
			}
			mProgressBar.setVisibility(View.GONE);
			mWebView.setVisibility(View.GONE);
		}

	}

	public boolean canGoBack()
	{
		return mWebView.canGoBack();
	}
	
	public void goBack()
	{
		mWebView.goBack();
	}
}
