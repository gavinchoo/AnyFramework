package com.android.zcomponent.adapter;

import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * ViewPager适配器的类
 * 
 * @author zhouwu
 */
public class ViewPagerAdapter extends PagerAdapter
{

	List<View> mListData;

	/**
	 * ViewPager适配器的类的构造方法
	 * 
	 * @param 存储View的List集合 
	 */
	public ViewPagerAdapter(List<View> data)
	{
		mListData = data;
	}

	@Override
	public int getCount()
	{
		return mListData.size();
	}

	@Override
	public void destroyItem(View collection, int position, Object object)
	{
		((ViewPager) collection).removeView(mListData.get(position));
	}

	@Override
	public Object instantiateItem(View collection, int position)
	{
		((ViewPager) collection).addView(mListData.get(position), 0);

		return mListData.get(position);
	}

	@Override
	public boolean isViewFromObject(View view, Object object)
	{

		return view == (object);
	}

	@Override
	public void finishUpdate(View arg0)
	{
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1)
	{
	}

	@Override
	public Parcelable saveState()
	{
		return null;
	}

	@Override
	public void startUpdate(View arg0)
	{
	}

}
