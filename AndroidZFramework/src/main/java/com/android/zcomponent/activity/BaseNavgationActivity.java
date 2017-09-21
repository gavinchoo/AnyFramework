
package com.android.zcomponent.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.LayoutParams;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.android.component.zframework.R;
import com.android.zcomponent.common.uiframe.BaseActivity;
import com.android.zcomponent.util.LogEx;

public abstract class BaseNavgationActivity extends BaseActivity implements
		OnCheckedChangeListener
{

	private RelativeLayout mrlayoutNavParent;

	private RadioGroup mradioGroupNav;

	private LinearLayout mllayoutNavMask;

	private FrameLayout mViewContent;

	private View mViewRoot;

	private View mTitleBar;

	private Fragment[] fragments;

	private String[] titles;

	private int[] drawables;

	private int mCurrentTabPosition;

	private int mLastTabPosition;

	@Override
	public void onCreate(Bundle arg0)
	{
		super.onCreate(arg0);
		setContentView(R.layout.activity_tabhost);
		bindWidget();
		initNav();
	}

	private void bindWidget()
	{
		mTitleBar = findViewById(R.id.top_layout);
		mViewContent = (FrameLayout) findViewById(R.id.activity_tabcontent);
		mViewRoot = findViewById(R.id.activity_tabhost_llayout_root);
		mrlayoutNavParent =
				(RelativeLayout) findViewById(R.id.activity_tabhost_rlayout_root);

		mradioGroupNav =
				(RadioGroup) findViewById(R.id.activity_tabhost_radio_group);

		mllayoutNavMask =
				(LinearLayout) findViewById(R.id.activity_tabhost_radio_mask);

		mradioGroupNav.setOnCheckedChangeListener(this);
	}

	public void setFragments(Fragment[] fragments, String[] titles,
			int[] drawables)
	{
		this.fragments = fragments;
		this.titles = titles;
		this.drawables = drawables;
		initNav();
	}

	public void setViewContentAboveNav()
	{
		mViewContent.setPadding(0, 0, 0, 0);
		RelativeLayout.LayoutParams params =
				(RelativeLayout.LayoutParams) mViewContent.getLayoutParams();
		params.addRule(RelativeLayout.ABOVE, R.id.activity_tabhost_rlayout_root);
		mViewContent.setLayoutParams(params);
	}

	private void setNavBackgroundResource(int resid)
	{
		if (0 == resid)
		{
			return;
		}
		mrlayoutNavParent.setBackgroundResource(resid);
	}

	private void setBackgroundResource(int resid)
	{
		if (0 == resid)
		{
			return;
		}
		mViewRoot.setBackgroundResource(resid);
	}

	public void setTitleBarVisibility(int visibility)
	{
		findViewById(R.id.top_layout).setVisibility(visibility);
	}

	private void initNav()
	{
		this.fragments = getFragments();
		this.titles = getTitles();
		this.drawables = getDrawables();

		if (null == titles)
		{
			return;
		}

		setNavBackgroundResource(getNavBackgroundResource());
		setBackgroundResource(getBackgroundResource());

		int height = getNavHeight();
		if (height != 0)
		{
			mrlayoutNavParent.getLayoutParams().height = height;

			if (isSetContentPadding())
			{
				mViewContent.setPadding(0, 0, 0, height);
			}
			else
			{
				mViewContent.setPadding(0, 0, 0, 0);
			}
		}

		mradioGroupNav.removeAllViews();

		for (int i = 0; i < titles.length; i++)
		{

			RadioButton radioButton =
					(RadioButton) LayoutInflater.from(this).inflate(
							R.layout.layout_nav_radio_item, null);
			radioButton.setLayoutParams(new LayoutParams(0,
					LayoutParams.MATCH_PARENT, 1));
			Drawable drawable = getResources().getDrawable(drawables[i]);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			radioButton.setCompoundDrawables(null, drawable, null, null);
			radioButton.setText(titles[i]);
			if (getTextDipSize() > 0)
			{
				radioButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,
						getTextDipSize());
			}
			int textColorId = getTabSelectedTextColor();
			if (textColorId != 0)
			{
				radioButton.setTextColor(getResources().getColorStateList(
						textColorId));
			}

			int bgResId = getTabSelectedBackground();
			if (bgResId != 0)
			{
				radioButton.setBackgroundResource(bgResId);
			}
			radioButton.setId(i);
			if (i == 0)
			{
				radioButton.setChecked(true);
			}
			mradioGroupNav.addView(radioButton);
		}

	}
	
	public void addNavItemDot(int position)
	{
		View view = LayoutInflater.from(this).inflate(R.layout.layout_nav_dot, null);
		addNavItemMaskView(view, position);
	}
	
	public void setNavItemDotVisibility(int position, int visibility)
	{
		mllayoutNavMask.getChildAt(position).setVisibility(visibility);
	}
	
	public void removeNavItemDot(int position)
	{
		if (mllayoutNavMask.getChildCount() > position)
		{
			mllayoutNavMask.removeViewAt(position);
		}
	}

	public void addNavItemMaskView(View view, int position)
	{
		for (int i = 0; i < titles.length; i++)
		{
			if (i == position)
			{
				view.setLayoutParams(new LinearLayout.LayoutParams(0,
						LayoutParams.MATCH_PARENT, 1));
				mllayoutNavMask.addView(view);
			}
			else
			{
				LinearLayout maskView = new LinearLayout(this);
				maskView.setLayoutParams(new LinearLayout.LayoutParams(0,
						LayoutParams.MATCH_PARENT, 1));
				mllayoutNavMask.addView(maskView);
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		// TODO Auto-generated method stub
		// super.onSaveInstanceState(outState);
	}

	@Override
	public void onCheckedChanged(RadioGroup radioGroup, int position)
	{
		if (!onTabStartCheck(position))
		{
			setLastTabSelected();
			return;
		}

		if (null == fragments || fragments.length <= position)
		{
			return;
		}
		Fragment fragment = fragments[position];
		FragmentTransaction ft = getAnimationTransaction(position);

		mLastTabPosition = mCurrentTabPosition;
		getCurrentFragment().onPause();

		if (fragment.isAdded())
		{
			fragment.onResume(); // 启动目标tab的onResume()
		}
		else
		{
			ft.add(R.id.activity_tabcontent, fragment);
		}
		showCurrentTab(position);
		ft.commitAllowingStateLoss();
	}

	public boolean onTabStartCheck(int position)
	{
		return true;
	}

	public void addFragment(int position)
	{
		if (null == fragments || fragments.length <= position)
		{
			return;
		}
		LogEx.e("zjw", "addFragment + " + position);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.activity_tabcontent, fragments[position]);
		ft.commit();
	}

	public void setSelectedTab(int position)
	{
		LogEx.e("zjw", position + "");

		int size = mradioGroupNav.getChildCount();
		if (position < size)
		{
			((RadioButton) mradioGroupNav.getChildAt(position))
					.setChecked(true);
		}
	}
	
	public int getSelectPosition()
	{
		return mCurrentTabPosition;
	}

	public void setLastTabSelected()
	{
		setSelectedTab(mLastTabPosition);
	}

	public void showCurrentTab(int position)
	{
		if (null == fragments)
		{
			return;
		}
		for (int i = 0; i < fragments.length; i++)
		{
			Fragment fragment = fragments[i];
			FragmentTransaction ft = getAnimationTransaction(position);

			if (position == i)
			{
				ft.show(fragment);
			}
			else
			{
				ft.hide(fragment);
			}
			ft.commitAllowingStateLoss();
		}

		// 更新目标tab为当前tab
		mCurrentTabPosition = position;
	}

	public Fragment getCurrentFragment()
	{
		if (null == fragments || fragments.length <= mCurrentTabPosition)
		{
			return null;
		}
		return fragments[mCurrentTabPosition];
	}

	private FragmentTransaction getAnimationTransaction(int index)
	{
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		// 设置切换动画
		if (index > mCurrentTabPosition)
		{
			// 当下一个页面在当前页面的右边。则下一个页面从右侧进入，当前页面从左侧滑出(整体从右往左滑动)
			ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left);
		}
		else
		{
			// 当下一个页面在当前页面的左边。则下一个页面从左侧进入，当前页面从右侧滑出(整体从左往右滑动)
			ft.setCustomAnimations(R.anim.in_from_left, R.anim.out_to_right);
		}
		return ft;
	}

	public abstract Fragment[] getFragments();

	public abstract int[] getDrawables();

	public abstract String[] getTitles();

	public abstract int getTextDipSize();

	public abstract int getTabSelectedBackground();

	public abstract int getNavBackgroundResource();

	public abstract int getBackgroundResource();

	public abstract int getNavHeight();

	public abstract boolean isSetContentPadding();

	public abstract int getTabSelectedTextColor();

}
