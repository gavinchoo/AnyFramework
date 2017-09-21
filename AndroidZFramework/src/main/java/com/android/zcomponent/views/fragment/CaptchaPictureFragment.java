package com.android.zcomponent.views.fragment;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.component.zframework.R;
import com.android.zcomponent.common.uiframe.fragment.BaseFragment;
import com.android.zcomponent.communication.http.Context.Method;
import com.android.zcomponent.communication.http.annotation.HttpHost;
import com.android.zcomponent.communication.http.annotation.HttpPortal;
import com.android.zcomponent.http.api.host.Endpoint;
import com.android.zcomponent.http.api.model.MessageData;
import com.android.zcomponent.json.JsonEncoder;
import com.android.zcomponent.util.BitmapUtil;


public class CaptchaPictureFragment extends BaseFragment
{

    private ImageView mWebViewCaptcha;

    private TextView mtvewChange;

    public enum CaptchaSystem
    {
        AD,

        RESTAURANT;
    }

    private CaptchaSystem mCaptchaSystem;

    public CaptchaPictureFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_picture_captcha, null,
            false);
        mtvewChange = (TextView) view.findViewById(R.id.captcha_tvew_change);
        mWebViewCaptcha = (ImageView) view.findViewById(R.id.captcha_webview);
        mtvewChange.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                loadCaptcha(mCaptchaSystem);
            }
        });
        return view;
    }

    public void loadCaptcha(CaptchaSystem system)
    {
        mCaptchaSystem = system;
        sendCmdQueryCaptchaPicture();
    }

    @Override
    public void onRecvMsg(MessageData msg, int msgCode) throws Exception
    {
        if (msgCode == 10000)
        {
            if (null != mCaptchaPictureListener)
            {
                mCaptchaPictureListener.onQuerySuccess();
            }
            Bitmap bitmap = BitmapFactory.decodeByteArray(msg.getContext().data(), 0, msg
                    .getContext().data().length, BitmapUtil.initOptions());
            mWebViewCaptcha.setImageBitmap(bitmap);
        }
    }

    private void sendCmdQueryCaptchaPicture()
    {
        if (mCaptchaSystem == CaptchaSystem.AD)
        {
            CaptchaPictureAdRequest request = new CaptchaPictureAdRequest();
            getHttpDataLoader().doPostProcess(request, 10000, String.class, false);
        }
        else
        {
            CaptchaPictureRequest request = new CaptchaPictureRequest();
            getHttpDataLoader().doPostProcess(request, 10000, String.class, false);
        }
    }

    @HttpPortal(path = "http://api.aizachi.com/restaurant/json/Captcha.ashx?width=70&height=30", method = Method.Post, encoder = JsonEncoder.class)
    @HttpHost(headers = {})
    public static class CaptchaPictureRequest extends Endpoint
    {
    }

    @HttpPortal(path = "http://api.aizachi.com/Ad/Json/Captcha.ashx?width=70&height=30", method = Method.Post, encoder = JsonEncoder.class)
    @HttpHost(headers = {})
    public static class CaptchaPictureAdRequest extends Endpoint
    {
    }

    private OnCaptchaPictureListener mCaptchaPictureListener;

    public void setOnCaptchaPictureListener(
            OnCaptchaPictureListener captchaPictureListener)
    {
        mCaptchaPictureListener = captchaPictureListener;
    }

    public interface OnCaptchaPictureListener
    {

        public void onQuerySuccess();
    }
}
