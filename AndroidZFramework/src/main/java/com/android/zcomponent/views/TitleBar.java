
package com.android.zcomponent.views;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.component.zframework.R;
import com.android.zcomponent.annotation.ZTitleMore;
import com.android.zcomponent.common.ITitleBar;
import com.android.zcomponent.common.uiframe.FramewrokApplication;

/**
 * 公共标题栏 ， 使用该类方法需布局文件引入common_title_layout.xml布局文件
 * 
 * @ClassName:TitleBar
 * @Description: 在布局文件中引入公共标题栏include
 *               android:layout="@layout/common_title_layout" 调用该类中统一的方法做页面操作。
 * 
 * @author: wei
 * @date: 2015-3-18
 *
 */
public class TitleBar implements ITitleBar
{

	private Activity mContext;

	RelativeLayout rlayoutParent;

	View mMorePop;

	public TitleBar(Activity context)
	{
		mContext = context;

		rlayoutParent =
				(RelativeLayout) mContext
						.findViewById(R.id.common_title_rlayout_parent);
		if (null != rlayoutParent)
		{
			rlayoutParent.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View arg0)
				{

				}
			});
		}

		if (context instanceof TitleBarClickListener)
		{
			mTitleBarClickListener = (TitleBarClickListener) context;
		}

		if (null != getBackTextView())
		{
			getBackTextView().setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View view)
				{
					if (null != mTitleBarClickListener)
					{
						mTitleBarClickListener.onTitleBarBackClick(view);
					}

				}
			});
		}

		if (null != getFirstRightImageButton())
		{
			getFirstRightImageButton().setOnClickListener(
					new OnClickListener()
					{

						@Override
						public void onClick(View view)
						{
							if (null != mTitleBarClickListener)
							{
								mTitleBarClickListener
										.onTitleBarRightFirstViewClick(view);
							}
						}
					});
		}

		if (null != getSecondRightImageButton())
		{
			getSecondRightImageButton().setOnClickListener(
					new OnClickListener()
					{

						@Override
						public void onClick(View view)
						{
							if (null != mTitleBarClickListener)
							{
								mTitleBarClickListener
										.onTitleBarRightSecondViewClick(view);
							}
						}
					});
		}

		if (null != getRightTextView())
		{
			getRightTextView().setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View view)
				{
					if (null != mTitleBarClickListener)
					{
						mTitleBarClickListener
								.onTitleBarRightFirstViewClick(view);
					}
				}
			});
		}

		if (null != getRightMoreImageButton())
		{
			getRightMoreImageButton().setOnClickListener(
					new OnClickListener()
					{

						@Override
						public void onClick(View view)
						{
							if (isOptionWindShowing())
							{
								dismissOptionWindow();
							}
							else
							{
								showOptionWindow();
							}
						}
					});

			if (FramewrokApplication.getInstance().isTitleBarMoreShow()
					&& ZTitleMore.Helper.isEnable(context))
			{
				getRightMoreImageButton().setVisibility(View.VISIBLE);
				getRightMore().setVisibility(View.VISIBLE);
				initMorePopWindow();
			}
			else
			{
				getRightMoreImageButton().setVisibility(View.GONE);
				getRightMore().setVisibility(View.GONE);
			}
		}

		onCreateView(rlayoutParent, mMorePop);
	}

	@Override
	public void setTitleText(int titleId)
	{
		setTitleText(mContext.getString(titleId));
	}

	@Override
	public void setTitleText(String title)
	{
		TextView tvewTitle =
				(TextView) mContext.findViewById(R.id.common_title_tvew_txt);
		if (null != tvewTitle)
		{
			tvewTitle.setText(title);
		}
	}

	@Override
	public void setTitleColor(int color)
	{
		TextView tvewTitle =
				(TextView) mContext.findViewById(R.id.common_title_tvew_txt);
		tvewTitle.setTextColor(color);
	}

	@Override
	public void setTitleRightTextColor(int color)
	{
		TextView tvewTitleRightBtn =
				(TextView) mContext
						.findViewById(R.id.common_title_tvew_right_btn);
		tvewTitleRightBtn.setTextColor(color);
	}

	@Override
	public void showRightTextView(int rightTitle)
	{
		showRightTextView(mContext.getString(rightTitle));

		getRightMoreImageButton().setVisibility(View.GONE);
		getRightMore().setVisibility(View.GONE);
	}

	@Override
	public void showRightTextView(String rightTitle)
	{
		dismissRightMoreButton();

		TextView tvewTitleRightBtn =
				(TextView) mContext
						.findViewById(R.id.common_title_tvew_right_btn);
		ImageButton imgvewTitleRightBtn =
				(ImageButton) mContext
						.findViewById(R.id.common_title_imgvew_right_btn);

		tvewTitleRightBtn.setText(rightTitle);
		tvewTitleRightBtn.setVisibility(View.VISIBLE);
		imgvewTitleRightBtn.setVisibility(View.GONE);
	}

	@Override
	public void showFirstRightImageButton(int imgRes)
	{
		dismissRightMoreButton();

		TextView tvewTitleRightBtn =
				(TextView) mContext
						.findViewById(R.id.common_title_tvew_right_btn);
		ImageButton imgvewTitleRightBtn =
				(ImageButton) mContext
						.findViewById(R.id.common_title_imgvew_right_btn);

		tvewTitleRightBtn.setVisibility(View.GONE);
		imgvewTitleRightBtn.setImageResource(imgRes);
		imgvewTitleRightBtn.setVisibility(View.VISIBLE);
	}

	@Override
	public void showSecondRightImageButton(int imgRes)
	{
		dismissRightMoreButton();

		TextView tvewTitleRightBtn =
				(TextView) mContext
						.findViewById(R.id.common_title_tvew_right_btn);
		ImageButton imgvewTitleRightBtn =
				(ImageButton) mContext
						.findViewById(R.id.common_title_imgvew_right_btn1);

		tvewTitleRightBtn.setVisibility(View.GONE);
		imgvewTitleRightBtn.setImageResource(imgRes);
		imgvewTitleRightBtn.setVisibility(View.VISIBLE);
	}

	@Override
	public void showRightMoreImageButton(int imgRes)
	{
		if (imgRes > 0)
		{
			getRightMoreImageButton().setImageResource(imgRes);
		}
		getRightMoreImageButton().setVisibility(View.VISIBLE);
		getRightMore().setVisibility(View.VISIBLE);
	}

	@Override
	public TextView getTitleTextView()
	{
		TextView tvewTitle =
				(TextView) mContext.findViewById(R.id.common_title_tvew_txt);
		return tvewTitle;
	}

	@Override
	public TextView getRightTextView()
	{
		TextView tvewTitleRightBtn =
				(TextView) mContext
						.findViewById(R.id.common_title_tvew_right_btn);
		return tvewTitleRightBtn;
	}

	@Override
	public ImageButton getFirstRightImageButton()
	{
		ImageButton imgvewTitleRightBtn =
				(ImageButton) mContext
						.findViewById(R.id.common_title_imgvew_right_btn);
		return imgvewTitleRightBtn;
	}

	@Override
	public ImageButton getSecondRightImageButton()
	{
		ImageButton imgvewTitleRightBtn =
				(ImageButton) mContext
						.findViewById(R.id.common_title_imgvew_right_btn1);
		return imgvewTitleRightBtn;
	}

	@Override
	public ImageButton getRightMoreImageButton()
	{
		ImageButton imgvewTitleMoreBtn =
				(ImageButton) mContext.findViewById(R.id.common_title_more);
		return imgvewTitleMoreBtn;
	}
	
	public RelativeLayout getRightMore()
	{
		RelativeLayout rlayoutTitleMore =
				(RelativeLayout) mContext.findViewById(R.id.rlayout_title_more_show);
		return rlayoutTitleMore;
	}

	@Override
	public TextView getBackTextView()
	{
		TextView tvewBack =
				(TextView) mContext.findViewById(R.id.common_title_tvew_back);
		return tvewBack;
	}

	@Override
	public View getView()
	{
		return rlayoutParent;
	}

	public View getMoreView()
	{
		return mMorePop;
	}

	private void dismissRightMoreButton()
	{
		getRightMoreImageButton().setVisibility(View.GONE);
		getRightMore().setVisibility(View.GONE);
	}

	private PopupWindow mMorePopupWindow;

	private void initMorePopWindow()
	{
		if (null == mMorePopupWindow)
		{
			mMorePop =
					LayoutInflater.from(mContext).inflate(
							R.layout.pop_title_more, null);
			mMorePopupWindow =
					new PopupWindow(mMorePop, LayoutParams.MATCH_PARENT,
							LayoutParams.MATCH_PARENT);

			TextView tvewAuction =
					(TextView) mMorePop.findViewById(R.id.tvew_auction_show);
			TextView tvewClose =
					(TextView) mMorePop.findViewById(R.id.tvew_close_show);
			tvewAuction.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					dismissOptionWindow();

					if (null != mTitleBarClickListener)
					{
						mTitleBarClickListener.onTitleBarMorePopItemClick(v, 0);
					}
				}
			});

			tvewClose.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					dismissOptionWindow();
					if (null != mTitleBarClickListener)
					{
						mTitleBarClickListener.onTitleBarMorePopItemClick(v, 1);
					}
				}
			});

			mMorePop.setOnTouchListener(new OnTouchListener()
			{

				@Override
				public boolean onTouch(View view, MotionEvent event)
				{
					if (event.getAction() == MotionEvent.ACTION_UP)
					{
						mMorePopupWindow.dismiss();
					}
					return false;
				}
			});
		}
	}

	/**
	 * <p>
	 * Description: 显示筛选框
	 * <p>
	 * 
	 * @date 2013-6-26
	 * @author zte
	 */
	private void showOptionWindow()
	{
		if (!mMorePopupWindow.isShowing())
		{
			mMorePopupWindow.showAsDropDown(getRightMoreImageButton(), 0, 0);
			mMorePopupWindow.update();
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
	public boolean isOptionWindShowing()
	{
		if (null == mMorePopupWindow)
		{
			return false;
		}
		return mMorePopupWindow.isShowing();
	}

	/**
	 * <p>
	 * Description: 关闭筛选框
	 * <p>
	 * 
	 * @date 2014-2-28
	 * @author WEI
	 */
	public void dismissOptionWindow()
	{
		if (null != mMorePopupWindow && isOptionWindShowing())
		{
			mMorePopupWindow.dismiss();
		}
	}

	private TitleBarClickListener mTitleBarClickListener;

	public interface TitleBarClickListener
	{

		/**
		 * <p>
		 * Description: 获取标题栏操作对象，使用该方法需布局文件引入common_title_layout.xml文件
		 * <p>
		 * 
		 * @date 2015-3-18
		 * @author wei
		 * @return
		 */
		public void onTitleBarBackClick(View view);

		/**
		 * <p>
		 * Description: 标题栏返回按钮点击回调，使用该方法需布局文件引入common_title_layout.xml文件
		 * <p>
		 * 
		 * @date 2015-2-5
		 * @author wei
		 * @param view
		 */
		public void onTitleBarRightFirstViewClick(View view);

		/**
		 * <p>
		 * Description: 标题栏右侧按钮点击回调，使用该方法需布局文件引入common_title_layout.xml文件
		 * <p>
		 * 
		 * @date 2015-3-18
		 * @author wei
		 * @param view
		 */
		public void onTitleBarRightSecondViewClick(View view);

		public void onTitleBarMorePopItemClick(View view, int position);
	}

	@Override
	public void onCreateView(View titlebar, View morepop)
	{

	}

	@Override
	public void onDestory(View titlebar, View morepop)
	{

	}

	@Override
	public void onResume()
	{

	}
}
