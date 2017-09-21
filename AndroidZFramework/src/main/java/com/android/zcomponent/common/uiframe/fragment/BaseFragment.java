
package com.android.zcomponent.common.uiframe.fragment;

/** Activity通用功能包 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.android.zcomponent.common.uiframe.BaseActivity;
import com.android.zcomponent.common.uiframe.FramewrokApplication;
import com.android.zcomponent.common.uiframe.FramewrokApplication.OnActivityResultListener;
import com.android.zcomponent.common.uiframe.FramewrokApplication.OnLoginListener;
import com.android.zcomponent.common.uiframe.WaitingMsgDialog.IShowMsg;
import com.android.zcomponent.util.ClientInfo;
import com.android.zcomponent.util.ImageLoaderUtil;
import com.android.zcomponent.util.InstanceState;
import com.android.zcomponent.util.LogEx;
import com.android.zcomponent.views.DataEmptyView;
import com.android.zcomponent.views.DataEmptyView.DataEmptyRefreshListener;
import com.android.zcomponent.views.IntentHandle;
import com.android.zcomponent.views.TitleBar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 基础Activity
 * 
 * @ClassName:BaseActivity
 * @Description: 用于规范Activity的一些基础操作
 * @date: 2012-2-17
 * 
 */
public class BaseFragment extends MsgProcessFragment implements IShowMsg,
		OnLoginListener, OnActivityResultListener, DataEmptyRefreshListener
{

	private static final String TAG = "BaseFragment";

	private boolean isFragmentCreated = false;

	private DataEmptyView mDataEmptyView;
	
	private IntentHandle mIntentHandle;
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		InstanceState.loadState(savedInstanceState);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		InstanceState.saveState(outState);
	}
	
	public void addFragment(int resId, Fragment fragment)
	{
		FragmentTransaction ft = getChildFragmentManager().beginTransaction();
		ft.replace(resId, fragment);
		ft.commit();
	}

	/**
	 * <p>
	 * Description:获取屏幕密度
	 * <p>
	 * 
	 * @date 2014-3-31
	 * @author WEI
	 * @param context
	 * @return
	 */
	protected float getDensity(Context context)
	{
		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		return dm.density;
	}
	
	@Override
	public void startActivity(Intent intent)
	{
		super.startActivity(intent);
		if (null != getActivity().getParent())
		{
			ClientInfo.overridePendingTransitionInRight(getActivity().getParent());
		}
		else
		{
			ClientInfo.overridePendingTransitionInRight(getActivity());
		}
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode)
	{
		super.startActivityForResult(intent, requestCode);
		if (null != getActivity().getParent())
		{
			ClientInfo.overridePendingTransitionInRight(getActivity().getParent());
		}
		else
		{
			ClientInfo.overridePendingTransitionInRight(getActivity());
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		LogEx.d(TAG, "onActivityCreated ");
		
		getDataEmptyView();
		
		if (null != mDataEmptyView)
		{
			mDataEmptyView.setFragmentView(getView());
		}
		((FramewrokApplication) getActivity().getApplication())
				.registerLoginSuccessListener(this);
		((FramewrokApplication) getActivity().getApplication())
				.registerActivityResultListener(this);
		
		if (null != activityCreatedListener)
		{
			activityCreatedListener.onFragmentCreated();
		}
		
		refreshFragment();
		
		isFragmentCreated = true;
	}

	public void refreshFragment()
	{
		
	}
	
	@Override
	public void onDestroy()
	{
		LogEx.d(TAG, "Fragment onDestroy");
		super.onDestroy();
		((FramewrokApplication) getActivity().getApplication())
				.unregisterLoginSuccessListener(this);
		((FramewrokApplication) getActivity().getApplication())
				.unregisterActivityResultListener(this);
	}

	public boolean isCreated()
	{
		return isFragmentCreated;
	}

	private OnFragmentCreatedListener activityCreatedListener;

	public void setOnFragmentCreatedListener(
			OnFragmentCreatedListener activityCreatedListener)
	{
		this.activityCreatedListener = activityCreatedListener;
	}

	public interface OnFragmentCreatedListener
	{

		public void onFragmentCreated();
	}

	@Override
	public void onLoginSuccess()
	{

	}

	@Override
	public void onLoginCancel()
	{

	}

	@Override
	public void onLoginOut()
	{

	}
	
	@Override
	public void onActivityResultCallBack(int resultCode, Intent intent)
	{

	}
	
	public TitleBar getParentTitleBar()
	{
		if (null != getActivity() && getActivity() instanceof BaseActivity)
		{
			return ((BaseActivity)getActivity()).getTitleBar();
		}
		
		return null;
	}

	@Override
	public View getTitleView()
	{
		if (null != getActivity() && getActivity() instanceof BaseActivity)
		{
			return ((BaseActivity)getActivity()).getTitleView();
		}
		
		return null;
	}
	
	protected void loadImage(ImageView imageView, String path, int emptyId)
	{
		DisplayImageOptions options =
				ImageLoaderUtil.getDisplayImageOptions(emptyId);
		ImageLoader.getInstance().displayImage(path, imageView, options);
	}
	
	/**
	 * <p>
	 * Description: 页面数据为空时，页面提示。使用该类需在布局文件中引入common_waiting_layout.xml布局文件
	 * <p>
	 * 
	 * @date 2015-3-18
	 * @author wei
	 * @return
	 */
	public DataEmptyView getDataEmptyView()
	{
		if (null == mDataEmptyView)
		{
			mDataEmptyView = new DataEmptyView(getActivity());
			mDataEmptyView.setOnClickRefreshListener(this);
		}
		return mDataEmptyView;
	}
	
	public IntentHandle getIntentHandle()
	{
		if (null == mIntentHandle)
		{
			mIntentHandle = new IntentHandle((BaseActivity)getActivity());
		}
		return mIntentHandle;
	}

	/**
	 * <p>
	 * Description: 数据为空时，点击刷新回调监听
	 * <p>
	 * 
	 * @date 2015-3-18
	 * @author wei
	 */
	@Override
	public void onDataEmptyClickRefresh()
	{

	}

	/**
	 * <p>
	 * Description: 数据为空时，点击刷新回调监听
	 * <p>
	 * 
	 * @date 2015-3-18
	 * @author wei
	 */
	@Override
	public void onDataEmptyClickChange()
	{

	}
}
