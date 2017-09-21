
package com.android.zcomponent.common.uiframe;

/** Activity通用功能包 */
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.android.zcomponent.annotation.ZMainActivity;
import com.android.zcomponent.annotation.ZTitleMore;
import com.android.zcomponent.common.uiframe.FramewrokApplication.OnActivityResultListener;
import com.android.zcomponent.common.uiframe.FramewrokApplication.OnLoginListener;
import com.android.zcomponent.constant.GlobalConst;
import com.android.zcomponent.util.ClientInfo;
import com.android.zcomponent.util.ImageLoaderUtil;
import com.android.zcomponent.util.InstanceState;
import com.android.zcomponent.util.LogEx;
import com.android.zcomponent.util.SharedPreferencesUtil;
import com.android.zcomponent.util.ShowMsg;
import com.android.zcomponent.util.update.AppUpdate;
import com.android.zcomponent.views.DataEmptyView;
import com.android.zcomponent.views.DataEmptyView.DataEmptyRefreshListener;
import com.android.zcomponent.views.IntentHandle;
import com.android.zcomponent.views.KeyboardListenRelativeLayout;
import com.android.zcomponent.views.KeyboardListenRelativeLayout.onKeyboardStateChangedListener;
import com.android.zcomponent.views.TitleBar;
import com.android.zcomponent.views.TitleBar.TitleBarClickListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

/**
 * 基础Activity
 * 
 * @param <T>
 * @param <T>
 * 
 * @ClassName:BaseActivity
 * @Description: 用于规范Activity的一些基础操作
 * @date: 2012-2-17
 * 
 */
public class BaseActivity extends MsgProcessActivity implements
		OnLoginListener, OnActivityResultListener, DataEmptyRefreshListener,
		TitleBarClickListener
{

	private static final String TAG = "BaseActivity";

	private long exitTime;

	/** 键盘是否打开 */
	private boolean isKeyboardShow = false;

	private boolean inFromBottom;

	/** 布局最外层KeyboardListenRelativeLayout控件 */
	private KeyboardListenRelativeLayout keyboardListenRelativeLayout;

	private DataEmptyView mDataEmptyView;

	private TitleBar mTitleBar;

	private IntentHandle mIntentHandle;

	private View mTitleview;

	protected void setKeyboardState(boolean isShow)
	{
		isKeyboardShow = isShow;

	}

	/**
	 * <p>
	 * Description: 当页面最外层布局为KeyboardListenRelativeLayout， 设置键盘打开与关闭状态监听
	 * <p>
	 * 
	 * @date 2014-3-31
	 * @author WEI
	 * @param resLayoutId
	 *            KeyboardListenRelativeLayout控件ID
	 */
	protected void setKeyboardListener(int resLayoutId)
	{
		keyboardListenRelativeLayout =
				(KeyboardListenRelativeLayout) findViewById(resLayoutId);
		keyboardListenRelativeLayout
				.setOnKeyboardStateChangedListener(new onKeyboardStateChangedListener()
				{

					@Override
					public void onKeyboardStateChanged(boolean isKeyboardShow)
					{
						setKeyboardState(!isKeyboardShow);
						onKeyboardState(isKeyboardShow);
					}
				});
	}

	/**
	 * <p>
	 * Description: 键盘打开与关闭状态回调方法
	 * <p>
	 * 
	 * @date 2014-3-31
	 * @author WEI
	 * @param isKeyboardShow
	 */
	protected void onKeyboardState(boolean isKeyboardShow)
	{
		LogEx.d(TAG, "onKeyboardState " + isKeyboardShow);
	}

	@Override
	public void onTitleBarBackClick(View view)
	{
		if (null != view)
		{
			ClientInfo.closeSoftInput(view, this);
		}
		finish();
	}

	@Override
	public void onTitleBarRightFirstViewClick(View view)
	{

	}

	@Override
	public void onTitleBarRightSecondViewClick(View view)
	{

	}

	@Override
	public void onTitleBarMorePopItemClick(View view, int position)
	{
		((FramewrokApplication) getApplication()).onTitleBarMoreItemClick(view,
				position);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event)
	{
		if (isKeyboardShow)
		{
			isKeyboardShow = false;
			return true;
		}
		Log.d("BaseActivity", "onKeyDown");
		switch (event.getKeyCode())
		{
		case KeyEvent.KEYCODE_BACK: // 退出键
		{
			// 如果用户按下返回键，则交由BaseActivity处理。
			if (onKeyBack(event.getKeyCode(), event))
			{
				return true;
			}
			break;
		}
		case KeyEvent.KEYCODE_HOME: // 返回主页按钮
		{
			onGotoSysHome();
			break;
		}
		case KeyEvent.KEYCODE_SEARCH: // 返回搜索按钮
		{
			break;
		}
		case KeyEvent.KEYCODE_MENU: // 返回菜单按钮
		{
			break;
		}
		default: // 其他按键消息
		{
			break;
		}
		}
		return super.dispatchKeyEvent(event);
	}

	public boolean killProcess(KeyEvent event)
	{
		if ((System.currentTimeMillis() - exitTime) > 2000)
		{
			ShowMsg.showToast(this, "再按一次退出程序");
			exitTime = System.currentTimeMillis();
		}
		else
		{
			// 清除登录信息
			SharedPreferencesUtil.clear();
			// onDestroy();
			// android.os.Process.killProcess(android.os.Process.myPid());
			super.onBackPressed();
		}
		return true;
	}

	public void addFragment(int resId, Fragment fragment)
	{
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(resId, fragment);
		ft.commit();
	}

	@Override
	public void startActivity(Intent intent)
	{
		super.startActivity(intent);
		if (null != getParent())
		{
			ClientInfo.overridePendingTransitionInRight(getParent());
		}
		else
		{
			ClientInfo.overridePendingTransitionInRight(this);
		}
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode)
	{
		super.startActivityForResult(intent, requestCode);
		if (null != getParent())
		{
			ClientInfo.overridePendingTransitionInRight(getParent());
		}
		else
		{
			ClientInfo.overridePendingTransitionInRight(this);
		}
	}

	@Override
	public void finish()
	{
		super.finish();

		if (inFromBottom)
		{
			ClientInfo.overridePendingTransitionOutTop(this);
		}
		else
		{
			ClientInfo.overridePendingTransitionOutLeft(this);
		}

	}

	/**
	 * 
	 * 返回键被点击
	 * <p>
	 * Description: 返回键被点击的响应函数
	 * <p>
	 * 
	 * @date 2012-3-19
	 * @return
	 */
	public boolean onKeyBack(int iKeyCode, KeyEvent event)
	{
		LogEx.d("BaseActivity", "onKeyBack");
		if (event.getAction() == KeyEvent.ACTION_UP)
		{
			if (m_showWaitingDialog.isDialogShow())
			{
				m_showWaitingDialog.dismissWaitDialog();
				return true;
			}
			else
			{
				if (ZMainActivity.Helper.isMainActivity(this)
						&& !GlobalConst.IS_PLUGIN_VERSION)
				{
					killProcess(event);
				}
				else
				{
					this.finish();
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * 取消某个操作的响应函数
	 * <p>
	 * Description: 在注销、按返回按键时调用，页面需要跑自行判断当前正在进行何种操作。
	 * <p>
	 * 
	 * @date 2012-5-1
	 * @author
	 * @return 返回true代表此消息已经被消耗掉，底层不再继续处理（如果是按返回键的话，则不会执行返回操作）。
	 */
	protected boolean onCancelOperation()
	{
		return false;
	}

	/**
	 * 
	 * 返回主页按钮时候的响应函数
	 * <p>
	 * Description: 一般需要进行暂停操作
	 * <p>
	 * 
	 * @date 2012-3-9
	 */
	protected void onGotoSysHome()
	{
	}

	/**
	 * 
	 * 注销前操作
	 * <p>
	 * Description: 系统即将注销，用于释放系统资源、停止播放、停止等待等操作。
	 * <p>
	 * 
	 * @date 2012-3-9
	 */
	protected void onLogoutBefore()
	{
		// 如果正在进行某种操作，则取消
		onCancelOperation();
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		InstanceState.loadState(savedInstanceState);
		// 初始化应用程序
		// ActivityMgr.getActivityMgr().init();
		((FramewrokApplication) getApplication()).addActivity(this);
		((FramewrokApplication) getApplication())
				.registerLoginSuccessListener(this);
		((FramewrokApplication) getApplication())
				.registerActivityResultListener(this);
		if (null != getIntent())
		{
			inFromBottom = getIntent().getBooleanExtra("inFromBottom", false);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		InstanceState.saveState(outState);
	}

	@Override
	protected void onStart()
	{
		super.onStart();
	}

	@Override
	protected void onDestroy()
	{
		if (ZMainActivity.Helper.isMainActivity(this))
		{
			AppUpdate.cancel();
		}

		if (ImageLoader.getInstance().isInited())
		{
			ImageLoader.getInstance().clearMemoryCache();
		}

		if (null != getTitleBar())
		{
			getTitleBar().onDestory(getTitleBar().getView(),
					getTitleBar().getMoreView());
		}

		((FramewrokApplication) getApplication()).removeActivity(this);
		((FramewrokApplication) getApplication())
				.unregisterLoginSuccessListener(this);
		((FramewrokApplication) getApplication())
				.unregisterActivityResultListener(this);
		super.onDestroy();
	}

	@Override
	protected void onStop()
	{
		super.onStop();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		MobclickAgent.onResume(this);
		getTitleBar();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		MobclickAgent.onPause(this);
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

	protected void loadImage(ImageView imageView, String path, int emptyId)
	{
		DisplayImageOptions options =
				ImageLoaderUtil.getDisplayImageOptions(emptyId);
		ImageLoader.getInstance().displayImage(path, imageView, options);
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

	/**
	 * <p>
	 * Description: 获取标题栏操作对象，使用该方法需布局文件引入common_title_layout.xml文件
	 * <p>
	 * 
	 * @date 2015-3-18
	 * @author wei
	 * @return
	 */
	public TitleBar getTitleBar()
	{
		if (null == mTitleBar)
		{
			mTitleBar = new TitleBar(this)
			{

				@Override
				public void onCreateView(View titlebar, View morepop)
				{
					super.onCreateView(titlebar, morepop);
					
					((FramewrokApplication) getApplication()).onTitleBarCreate(
							titlebar, morepop, ZTitleMore.Helper.isEnable(BaseActivity.this));
				}
				
				@Override
				public void onDestory(View titlebar, View morepop)
				{
					((FramewrokApplication) getApplication()).onTitleBarDestory(
							titlebar, morepop, ZTitleMore.Helper.isEnable(BaseActivity.this));
				}
				
				@Override
				public void onResume()
				{
					super.onResume();
					((FramewrokApplication) getApplication()).onTitleBarResume();
				}
			};
		}
		return mTitleBar;
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
			mDataEmptyView = new DataEmptyView(this);
		}
		return mDataEmptyView;
	}

	public IntentHandle getIntentHandle()
	{
		if (null == mIntentHandle)
		{
			mIntentHandle = new IntentHandle(this);
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
	 * Description: 数据为空，自定义事件，例如添加数据
	 * <p>
	 * 
	 * @date 2015-4-21
	 * @author wei
	 */
	@Override
	public void onDataEmptyClickChange()
	{

	}

	@Override
	public void onActivityResultCallBack(int resultCode, Intent intent)
	{

	}

	@Override
	public View getTitleView()
	{
		if (null != mTitleview)
		{
			return mTitleview;
		}
		return getTitleBar().getView();
	}

	public void setTitleView(View view)
	{
		mTitleview = view;
	}
}
