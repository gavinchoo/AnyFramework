package com.android.zcomponent.views;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.component.zframework.R;
import com.android.zcomponent.common.IDataEmpty;
import com.android.zcomponent.common.uiframe.FramewrokApplication;
import com.android.zcomponent.common.uiframe.NetWorkSettingDialog;
import com.android.zcomponent.http.api.model.MessageData;
import com.android.zcomponent.http.constant.ErrorCode;
import com.android.zcomponent.util.CommonUtil;
import com.android.zcomponent.util.LogEx;
import com.android.zcomponent.util.ShowMsg;


/**
 * 页面数据为空时，页面提示。使用该类需在布局文件中引入common_waiting_layout.xml布局文件
 * @ClassName:DataEmptyView 
 * @Description: include android:layout="@layout/common_waiting_layout"
 * @author: wei
 * @date: 2015-3-18
 *
 */
public class DataEmptyView implements IDataEmpty
{

    private static final String TAG = "DataEmptyView";

    private Activity mContext;

    private View mView;

    private TextView mtvewTitle;

    private TextView mtvewRefresh;

    private TextView mtvewSetting;

    private ImageView mimgvewIcon;

    private View mWaitingView;

    private View mViewFragment;

    private boolean isFragment;

    private boolean isNeedChange;

    private int miBgResId = -1;

    private LinearLayout mllayoutWaiting;

    public DataEmptyView(Context context)
    {
        init(context);
    }

    private void init(Context context)
    {
        if (null == context)
        {
            return;
        }
        mContext = (Activity) context;
        
        if (context instanceof DataEmptyRefreshListener)
        {
        	mClickRefreshListener  = (DataEmptyRefreshListener) context;
        }
        
        mView = LayoutInflater.from(mContext).inflate(
            R.layout.data_empty_layout, null);
        mtvewTitle = (TextView) mView.findViewById(R.id.data_empty_title);
        mtvewRefresh = (TextView) mView.findViewById(R.id.data_empty_refresh);
        mtvewSetting = (TextView) mView.findViewById(R.id.data_empty_setting);
        mimgvewIcon = (ImageView) mView.findViewById(R.id.data_empty_logo);
        mtvewRefresh.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v)
            {
                LogEx.d(TAG, "mClickRefreshListener start");
                if (null != mClickRefreshListener)
                {
                    if (isNeedChange)
                    {
                        mClickRefreshListener.onDataEmptyClickChange();
                    }
                    else
                    {
                        mClickRefreshListener.onDataEmptyClickRefresh();
                    }
                }
                else
                {
                    LogEx.w(TAG, "mClickRefreshListener null");
                }
            }
        });
    }

    public void setFragmentView(View viewFragment)
    {
        mViewFragment = viewFragment;
        isFragment = true;
    }

    private void initDataEmptyView()
    {
        if (null == mllayoutWaiting)
        {
            if (isFragment)
            {
                if (null != mViewFragment)
                {
                    mllayoutWaiting = (LinearLayout) mViewFragment
                            .findViewById(R.id.common_data_empty_view);
                }
            }
            else
            {
                if (null != mContext)
                {
                    mllayoutWaiting = (LinearLayout) mContext
                            .findViewById(R.id.common_data_empty_view);
                }
            }

            if (null != mllayoutWaiting && -1 != miBgResId)
            {
                mllayoutWaiting.setBackgroundResource(miBgResId);
            }
        }
    }

    @Override
    public LinearLayout getView()
    {
        initDataEmptyView();
        return mllayoutWaiting;
    }

    @Override
    public void removeAllViews()
    {
        initDataEmptyView();
        if (null != mllayoutWaiting)
        {
            mllayoutWaiting.removeAllViews();
        }
    }

    @Override
    public void setVisibility(int visibility)
    {
        initDataEmptyView();
        if (null != mllayoutWaiting)
        {
            mllayoutWaiting.setVisibility(visibility);
        }
    }

    @Override
    public void showViewWaiting()
    {
        initDataEmptyView();
        if (null != mllayoutWaiting)
        {
            mllayoutWaiting.removeAllViews();
            mllayoutWaiting.addView(getWaitingView(), getLayoutParams());
            mllayoutWaiting.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showViewDataEmpty(boolean isClickable, boolean isSuportRefresh,
            int returnCode, int resEmptyId)
    {
        showViewDataEmpty(isClickable, isSuportRefresh, returnCode,
            mContext.getString(resEmptyId));
    }

    @Override
    public void showViewDataEmpty(boolean isClickable, boolean isSuportRefresh,
            int returnCode, String resEmpty)
    {
        showViewDataEmpty(isClickable, isSuportRefresh, false, returnCode, resEmpty, "");
    }

    @Override
    public void showViewDataEmpty(boolean isClickable, boolean isSuportRefresh,
            MessageData msg, int resEmptyId)
    {
        showViewDataEmpty(isClickable, isSuportRefresh, msg.getReturnCode(),
            mContext.getString(resEmptyId));
    }

    @Override
    public void showViewDataEmpty(boolean isClickable, boolean isSuportRefresh,
            MessageData msg, String resEmpty)
    {
        showViewDataEmpty(isClickable, isSuportRefresh, msg.getReturnCode(), resEmpty);
    }

    @Override
    public void showViewDataEmpty(boolean isClickable, boolean isSuportRefresh,
            boolean isNeedChange, int returnCode, String resEmpty, String strChangeTitle)
    {
        initDataEmptyView();
        if (null != mllayoutWaiting)
        {
            View view = getEmptyView(isClickable, returnCode, resEmpty, isSuportRefresh,
                isNeedChange, strChangeTitle);
            mllayoutWaiting.removeAllViews();
            
            if (null != view)
            {
            	 mllayoutWaiting.addView(view, getLayoutParams());
            }
            mllayoutWaiting.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showViewDataEmpty(boolean isClickable, boolean isSuportRefresh,
            boolean isNeedChange, int returnCode, int resEmpty, String strChangeTitle)
    {
        showViewDataEmpty(isClickable, isSuportRefresh, isNeedChange, returnCode,
            mContext.getString(resEmpty), strChangeTitle);
    }

    @Override
    public void showViewDataEmpty(boolean isClickable, boolean isSuportRefresh,
            boolean isNeedChange, int returnCode, int resEmpty, int strChangeTitle)
    {
        showViewDataEmpty(isClickable, isSuportRefresh, isNeedChange, returnCode,
            mContext.getString(resEmpty), mContext.getString(strChangeTitle));
    }

    @Override
    public void showViewDataEmpty(boolean isClickable, boolean isSuportRefresh,
            boolean isNeedChange, int returnCode, String resEmpty, int strChangeTitle)
    {
        showViewDataEmpty(isClickable, isSuportRefresh, isNeedChange, returnCode,
            resEmpty, mContext.getString(strChangeTitle));
    }

    public void setBackgroundResource(int resid)
    {
        if (null == mView)
        {
            return;
        }
        miBgResId = resid;
        mView.setBackgroundResource(resid);
        if (null != mllayoutWaiting)
        {
            mllayoutWaiting.setBackgroundResource(resid);
        }
    }

    public void setImageIcon(int resid)
    {
        mimgvewIcon.setBackgroundResource(resid);
    }

    public LayoutParams getLayoutParams()
    {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    /**
     * <p>
     * Description:
     * <p>
     * 
     * @date 2013-8-21
     * @author WEI
     * @param isClickable
     *            是否接收点击事件
     * @param returnCode
     *            错误返回码
     * @param resEmptyId
     *            数据为空提示信息
     * @param isSuportRefresh
     *            是否可以下拉刷新， 如果不是接收点击刷新
     * @return
     */
    public View getEmptyView(boolean isClickable, int returnCode, int resEmptyId,
            boolean isSuportRefresh)
    {
        return getEmptyView(isClickable, returnCode,
            mContext.getResources().getString(resEmptyId), isSuportRefresh, false, "");
    }

    /**
     * <p>
     * Description:
     * <p>
     * 
     * @date 2013-8-21
     * @author WEI
     * @param isClickable
     *            是否接收点击事件
     * @param returnCode
     *            错误返回码
     * @param resEmptyId
     *            数据为空提示信息
     * @param isSuportRefresh
     *            是否可以下拉刷新， 如果不是接收点击刷新
     * @return
     */
    public View getEmptyView(boolean isClickable, int returnCode, String resEmpty,
            boolean isSuportRefresh)
    {
        return getEmptyView(isClickable, returnCode, resEmpty, isSuportRefresh, false, "");
    }

    /**
     * <p>
     * Description:
     * <p>
     * 
     * @date 2013-8-21
     * @author WEI
     * @param isClickable
     *            是否接收点击事件
     * @param returnCode
     *            错误返回码
     * @param resEmptyMsg
     *            数据为空提示信息
     * @param isSuportRefresh
     *            是否可以下拉刷新， 如果不是接收点击刷新
     * @return
     */
    public View getEmptyView(boolean isClickable, int returnCode, String resEmptyMsg,
            boolean isPullRefresh, boolean isNeedChange, String strChangeTitle)
    {
        if (null == mtvewTitle)
        {
            return null;
        }
        if (returnCode == ErrorCode.INT_NET_DISCONNECT)
        {
            if (isPullRefresh)
            {
                mtvewTitle.setText(mContext.getResources().getString(
                    R.string.common_net_disconnect));
            }
            else
            {
                mtvewTitle.setText(mContext.getResources().getString(
                    R.string.common_net_disconnect_click_refresh));
            }
            mtvewRefresh.setVisibility(View.VISIBLE);
            mtvewSetting.setVisibility(View.VISIBLE);
            mtvewSetting.setText("设置网络");
            mtvewSetting.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0)
                {
                    new NetWorkSettingDialog(mContext).show();
                }
            });
        }
        else if (returnCode == ErrorCode.INT_NET_SYSTEM_MAINTENANCE)
        {
            mtvewTitle.setText(mContext.getResources().getString(
                R.string.common_net_system_maintenance));
            mtvewRefresh.setVisibility(View.VISIBLE);
            mtvewSetting.setVisibility(View.GONE);

        }
        else if (returnCode == ErrorCode.INT_NET_CONNECT_UNAUTHORIZED)
        {
            mtvewTitle.setText(mContext.getResources().getString(
                R.string.common_net_unauthorized));
            mtvewRefresh.setVisibility(View.VISIBLE);
            mtvewSetting.setVisibility(View.VISIBLE);
            mtvewSetting.setText("重新登录");
            mtvewSetting.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0)
                {
                    if (!CommonUtil.isLeastSingleClick())
                    {
                        return;
                    }
                    if (!FramewrokApplication.isLogin())
                    {
                        ((FramewrokApplication) mContext.getApplication()).reLogin();
                    }
                    else
                    {
                        ShowMsg.showToast(mContext, "已登录，请刷新数据试试！");
                    }
                }
            });
        }
        else if (returnCode == ErrorCode.INT_NET_CONNECT_TIME_OUT)
        {
            if (isPullRefresh)
            {
                mtvewTitle.setText(mContext.getResources().getString(
                    R.string.common_net_time_out_pull_refresh));
            }
            else
            {
                mtvewTitle.setText(mContext.getResources().getString(
                    R.string.common_net_time_out_click_refresh));
            }
            mtvewRefresh.setVisibility(View.VISIBLE);
            mtvewSetting.setVisibility(View.VISIBLE);
            mtvewSetting.setText("设置网络");
            mtvewSetting.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0)
                {
                    if (!CommonUtil.isLeastSingleClick())
                    {
                        return;
                    }
                    new NetWorkSettingDialog(mContext).show();
                }
            });
        }
        else
        {
            if (isNeedChange)
            {
                this.isNeedChange = isNeedChange;
                mtvewRefresh.setVisibility(View.VISIBLE);
                mtvewRefresh.setText(strChangeTitle);
            }
            else
            {
                mtvewRefresh.setVisibility(View.GONE);
            }

            mtvewSetting.setVisibility(View.GONE);
            mtvewTitle.setText(resEmptyMsg);
        }

        if (isPullRefresh)
        {
            setBackgroundResource(R.drawable.transparent);
        }
        if (!isClickable)
        {
            mView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v)
                {
                }
            });
        }
        return mView;
    }

    public View getEmptyView(boolean isClickable, String resMsg)
    {
        if (null == mtvewTitle)
        {
            return null;
        }
        mtvewTitle.setText(resMsg);
        if (!isClickable)
        {
            mView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v)
                {
                }
            });
        }
        return mView;
    }

    public View getEmptyView(boolean isClickable, int resid)
    {
        return getEmptyView(isClickable, mContext.getResources().getString(resid));
    }

    public View getEmptyView(boolean isClickable)
    {
        return getEmptyView(isClickable,
            mContext.getResources().getString(R.string.common_data_empty));
    }

    public View getWaitingView()
    {
        return new WaitingView(mContext).getView();
    }

    /**
     * <p>
     * Description: 设置标题显示内容
     * <p>
     * 
     * @date 2013-8-25
     * @author WEI
     * @param resid
     */
    public void setTitle(int resid)
    {
        if (null == mContext)
        {
            return;
        }
        setTitle(mContext.getResources().getString(resid));
    }

    public void setTitle(String resMsg)
    {
        if (null == mtvewTitle)
        {
            return;
        }
        mtvewTitle.setText(resMsg);
    }

    public void setTitleColor(int resColor)
    {
        if (null == mtvewTitle)
        {
            return;
        }
        mtvewTitle.setTextColor(resColor);
    }

    private DataEmptyRefreshListener mClickRefreshListener;

    /**
     * <p>
     * Description: 设置点击图标刷新数据监听器
     * <p>
     * 
     * @date 2013-8-25
     * @author WEI
     * @param clickRefreshListener
     */
    public void setOnClickRefreshListener(DataEmptyRefreshListener clickRefreshListener)
    {
        mClickRefreshListener = clickRefreshListener;
    }

    /**
     * 图标点击事件接口
     * 
     * @ClassName:ClickRefreshListener
     * @Description: 当数据查询失败，需要刷新数据时，点击海马图标，刷新数据
     * @author: WEI
     * @date: 2013-8-25
     */
    public interface DataEmptyRefreshListener
    {

        public void onDataEmptyClickRefresh();

        public void onDataEmptyClickChange();
    }

    private class WaitingView
    {

        private Context mContext;

        private View mView;

        // private LinearLayout mLinearWaiting;
        public WaitingView(Context context)
        {
            mContext = context;
        }

        public View getView()
        {
            if (mView == null)
            {
                init();
            }
            return mView;
        }

        private void init()
        {
            initUI();
            bindListener();
        }

        private void initUI()
        {
            if (mView == null)
            {
                mView = LayoutInflater.from(mContext).inflate(
                    R.layout.waiting_view_layout, null);
            }
        }

        private void bindListener()
        {
            mView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v)
                {
                }
            });
        }
    }

	@Override
	public void dismiss()
	{
		initDataEmptyView();
        if (null != mllayoutWaiting)
        {
        	removeAllViews();
            mllayoutWaiting.setVisibility(View.GONE);
        }
	}
}
