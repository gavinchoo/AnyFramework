/**
 * <p>
 * Copyright: Copyright (c) 2012
 * Company: ZTE
 * Description:弹出消息类，用来弹出消息
 * </p>
 * @Title ShowMsg.java
 * @Package com.zte.iptvclient.android.ui
 * @version 1.0
 * @author 0043200560
 * @date 2012-2-21
 */

package com.android.zcomponent.common.uiframe;

import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.android.component.zframework.R;
import com.android.zcomponent.util.CustomDialog;

/**
 * @ClassName:ShowMsg
 * @Description: 弹出消息类，用于显示提示消息
 * @author: WEI
 * @date: 2012-2-21
 * 
 */
public class WaitingMsgDialog
{

	/** 错误信息 */
	private static final String LOG_TAG = "WaitingMsgDialog";

	/** 弹出等待框，一般用来提示正在请求SDK数据 */
	private CustomDialog m_progressDialog = null;

	private TextView mtvewMsg;

	/** 目标Activity */
	private Activity m_activity = null;

	private boolean isDialogShow = false;

	private static WaitingMsgDialog m_instance;

	/**
	 * 构造函数
	 * 
	 * @param ctx
	 *            内容上下文
	 */
	public WaitingMsgDialog(Activity activity)
	{
		m_activity = activity;
		if (null != activity)
		{
			m_progressDialog =
					new CustomDialog(activity, R.layout.dialog_waiting_layout,
							R.style.DialogCustomTheme, false)
					{

						@Override
						public boolean onKeyUp(int iKeyCode, KeyEvent event)
						{
							if (KeyEvent.KEYCODE_BACK == iKeyCode)
							{
								IShowMsg m_instance = (IShowMsg) m_activity;
								if (null != m_instance)
								{
									return m_instance.onShowMsgKey(iKeyCode,
											event);
								}
							}
							if (KeyEvent.KEYCODE_SEARCH == iKeyCode)
							{
								return true;
							}
							return super.onKeyUp(iKeyCode, event);
						}

					};

			mtvewMsg =
					(TextView) m_progressDialog
							.findViewById(R.id.waiting_dailog_title);
			m_progressDialog.setCanceledOnTouchOutside(false);
		}
		else
		{
			Log.w(LOG_TAG, "ctx is null!");
		}
	}

	public boolean isDialogShow()
	{
		return isDialogShow;
	}

	/**
	 * <p>
	 * Description: 弹出等待框，一般用来提示正在请求数据
	 * <p>
	 * 
	 * @date 2012-2-20
	 * @param strTitle
	 *            标题
	 * @param strMsg
	 *            提示信息
	 */
	public void showWaitDialog()
	{
		if (null != m_progressDialog)
		{
			isDialogShow = true;
			if (!m_progressDialog.isShowing())
			{
				m_progressDialog.show();
			}
		}
	}

	/**
	 * <p>
	 * Description: 弹出等待框，一般用来提示正在请求数据
	 * <p>
	 * 
	 * @date 2012-2-20
	 * @param strTitle
	 *            标题
	 * @param strMsg
	 *            提示信息
	 */
	public void showWaitDialog(String strMsg)
	{

		if (null != m_progressDialog)
		{
			mtvewMsg.setText(strMsg);
			isDialogShow = true;

			if (!m_progressDialog.isShowing())
			{
				m_progressDialog.show();
			}
		}
	}

	/**
	 * <p>
	 * Description: 弹出等待框，一般用来提示正在请求数据
	 * <p>
	 * 
	 * @date 2012-2-20
	 * @param strTitle
	 *            标题
	 * @param strMsg
	 *            提示信息
	 */
	public void showWaitDialog(String strMsg, boolean isCanceable)
	{

		if (null != m_progressDialog)
		{
			mtvewMsg.setText(strMsg);
			isDialogShow = true;
			m_progressDialog.setCancelable(isCanceable);
			if (!m_progressDialog.isShowing())
			{
				m_progressDialog.show();
			}
		}
	}

	/**
	 * <p>
	 * Description: 弹出等待框，一般用来提示正在请求数据
	 * <p>
	 * 
	 * @date 2012-2-20
	 * @param strTitle
	 *            标题
	 * @param strMsg
	 *            提示信息
	 */
	public void showWaitDialog(int strMsg)
	{
		showWaitDialog(m_activity.getResources().getString(strMsg));
	}

	/**
	 * <p>
	 * Description: 弹出等待框，一般用来提示正在请求数据
	 * <p>
	 * 
	 * @date 2012-2-20
	 * @param strTitle
	 *            标题
	 * @param strMsg
	 *            提示信息
	 */
	public void showWaitDialog(int strMsg, boolean isCanceable)
	{
		showWaitDialog(m_activity.getResources().getString(strMsg), isCanceable);
	}

	/**
	 * <p>
	 * Description: 取消显示等待请求数据提示框
	 * <p>
	 * 
	 * @date 2012-2-20
	 */
	public void dismissWaitDialog()
	{
		if (null != m_progressDialog)
		{
			isDialogShow = false;
			m_progressDialog.dismiss();
		}
	}

	/**
	 * ShowMsg接口类
	 * 
	 * @ClassName:IShowMsg
	 * @Description: ShowMsg的接口汇总
	 * @date: 2012-4-1
	 */
	public interface IShowMsg
	{

		/**
		 * ShowMsg界面的按键消息
		 * <p>
		 * Description: ShowMsg界面的按键消息，相当于onKeyDown
		 * <p>
		 * 
		 * @date 2012-4-1
		 * @param keyCode
		 *            按键
		 * @param event
		 *            事件
		 */
		public boolean onShowMsgKey(int keyCode, KeyEvent event);
	}
}
