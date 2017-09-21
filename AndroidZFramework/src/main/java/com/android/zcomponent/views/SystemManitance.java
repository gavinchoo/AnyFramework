package com.android.zcomponent.views;


import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.component.zframework.R;
import com.android.zcomponent.common.uiframe.FramewrokApplication;
import com.android.zcomponent.util.CustomDialog;
import com.android.zcomponent.util.StringUtil;


public class SystemManitance extends CustomDialog
{

    private WebView mWebView;

    public SystemManitance(Context context)
    {
        super(context, R.layout.dialog_system_manitance_layout, true,
            R.style.right_left, false);
        init();
    }

    public void init()
    {
        setTitleBarText("公告");
        mWebView = (WebView) findViewById(R.id.alert_dialog_webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultTextEncodingName("gbk");
        webSettings.setLoadWithOverviewMode(true);// 
        webSettings.setUseWideViewPort(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.setVerticalScrollbarOverlay(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title)
            {
                super.onReceivedTitle(view, title);
                if (!StringUtil.isEmptyString(title))
                {
                    setTitleBarText(title);
                }
            }
        });
        this.setOnDismissListener(new OnDismissListener() {
            
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                FramewrokApplication.getInstance().setManitanceShow(false);
            }
        });
    }

    public void showWebView(String url)
    {
        mWebView.loadUrl(url);
        show();
        FramewrokApplication.getInstance().setManitanceShow(true);
    }

    @Override
    public void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        FramewrokApplication.getInstance().setManitanceShow(false);
    }
    
    @Override
    public void onTitleBarBackClick()
    {
        super.onTitleBarBackClick();
        FramewrokApplication.exit();
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
                    dismiss();
                    FramewrokApplication.getInstance().setManitanceShow(false);
                    FramewrokApplication.exit();
                }
            }
            return false;
        }
    }

    @Override
    public boolean onKeyBack(int iKeyCode, KeyEvent event)
    {
        if (event.getAction() == KeyEvent.ACTION_UP)
        {
            if (mWebView.canGoBack())
            {
                mWebView.goBack();
                return true;
            }
        }
        FramewrokApplication.exit();
        FramewrokApplication.getInstance().setManitanceShow(false);
        return super.onKeyBack(iKeyCode, event);
    }
}
