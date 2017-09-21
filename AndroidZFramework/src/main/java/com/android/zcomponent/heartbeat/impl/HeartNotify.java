
package com.android.zcomponent.heartbeat.impl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.component.zframework.R;
import com.android.zcomponent.heartbeat.IHeartBeat.HeartState;
import com.android.zcomponent.heartbeat.IHeartNotify;
import com.android.zcomponent.util.LogEx;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class HeartNotify implements IHeartNotify
{

	private static final String TAG = HeartNotify.class.getSimpleName();

	private Context mContext;

	private View mRootView;

	private PopupWindow mPopupWindow;

	private TextView mtvewTitle;

	private boolean isNeedShow = false;

	private boolean isStopped = false;

	private boolean isActivityCreated;

	public HeartNotify(Context context)
	{
		mContext = context;
		mRootView =
				LayoutInflater.from(context).inflate(
						R.layout.pop_heart_notify_layout, null);
		mtvewTitle =
				(TextView) mRootView.findViewById(R.id.common_title_notify);
		mPopupWindow =
				new PopupWindow(mRootView, LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT);
	}

	/**
	 * <p>
	 * Description: 显示筛选框
	 * <p>
	 * 
	 * @date 2013-6-26
	 * @author zte
	 */
	public void show(View view)
	{
		try
		{
			if (null == view)
			{
				LogEx.e(TAG, "Show heart notify parent view NULL");
				return;
			}

			if (!isNeedShow())
			{
				return;
			}

			if (isStopped())
			{
				return;
			}
			
			if (null == mPopupWindow)
			{
				return;
			}

			if (!mPopupWindow.isShowing())
			{
				YoYo.with(Techniques.SlideInDown).duration(300)
						.playOn(mRootView);
				mPopupWindow.showAsDropDown(view, 0, 0);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * <p>
	 * Description: 判断筛选框是否显示
	 * <p>
	 * 
	 * @date 2014-2-28
	 * @author WEI
	 * @return
	 */
	public boolean isShowing()
	{
		if (null == mPopupWindow)
		{
			return false;
		}
		return mPopupWindow.isShowing();
	}

	/**
	 * <p>
	 * Description: 关闭筛选框
	 * <p>
	 * 
	 * @date 2014-2-28
	 * @author WEI
	 */
	public void dismiss()
	{
		if (null == mPopupWindow)
		{
			return;
		}
		try
		{
			if (mPopupWindow.isShowing())
			{
				YoYo.with(Techniques.SlideOutUp).duration(300).playOn(mRootView);
				mPopupWindow.dismiss();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean isNeedShow()
	{
		return isNeedShow;
	}

	public void setActivityCreated(boolean isActivityCreated)
	{
		this.isActivityCreated = isActivityCreated;
	}

	public boolean isActivityCreated()
	{
		return isActivityCreated;
	}

	@Override
	public void setState(boolean isNeedShow)
	{
		this.isNeedShow = isNeedShow;
	}

	public void showHeartState(HeartState state, View parent)
	{
		if (state == HeartState.SLOW || state == HeartState.STOPED)
		{
			if (isActivityCreated())
			{
				if (state == HeartState.SLOW)
				{
					mtvewTitle.setText(R.string.common_net_notify_slow);
				}
				else if (state == HeartState.STOPED)
				{
					mtvewTitle.setText(R.string.common_net_notify_stop);
				}
				setState(true);
				show(parent);
			}
		}
		else
		{
			LogEx.d(TAG, "网络恢复了, 移除提示！");
			if (isShowing())
			{
				setState(false);
				dismiss();
			}
		}
	}

	@Override
	public boolean isStopped()
	{
		return isStopped;
	}

	@Override
	public void setStop(boolean isStop)
	{
		isStopped = isStop;
	}
}
