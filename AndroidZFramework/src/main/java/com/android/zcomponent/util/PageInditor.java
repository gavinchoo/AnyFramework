
package com.android.zcomponent.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.widget.AbsListView;

import com.android.zcomponent.adapter.CommonAdapter;

public class PageInditor<T>
{

	private int mPageNum = 1;

	private boolean isPullRefresh;

	private List<T> mlistFavorite = new ArrayList<T>();

	private CommonAdapter mAdapter;

	private AbsListView mListView;

	private int iSelectPosition;

	public PageInditor()
	{
	}

	public void bindAdapter(AbsListView listView, CommonAdapter adapter)
	{
		mListView = listView;
		mAdapter = adapter;
		if (null == mListView.getAdapter())
		{
			mListView.setAdapter(mAdapter);
		}
	}

	public void add(List<T> data)
	{
		if (null != mlistFavorite)
		{
			mlistFavorite.addAll(data);
		}

		if (null == mListView)
		{
			return;
		}

		if (null == mListView.getAdapter())
		{
			if (null != mAdapter)
			{
				mListView.setAdapter(mAdapter);
			}
		}
		else
		{
			mAdapter.notifyDataSetChanged();
		}
	}

	public void add(T[] data)
	{
		add(Arrays.asList(data));
	}

	public int size()
	{
		if (null == getAll())
		{
			return 0;
		}

		return getAll().size();
	}

	public void remove(int position)
	{
		if (mlistFavorite.size() > position)
		{
			mlistFavorite.remove(position);

			if (null != mAdapter)
			{
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	public boolean isEmpty()
	{
		if (null == getAll() || size() == 0)
		{
			return true;
		}

		return false;
	}

	public List<T> getAll()
	{
		return mlistFavorite;
	}

	public T get(int position)
	{
		iSelectPosition = position;
		return mlistFavorite.get(position);
	}

	public int getSelectPosition()
	{
		return iSelectPosition;
	}

	public void updateSelectPosition(int position)
	{
		iSelectPosition = position;
	}

	public T getSelectItem()
	{
		return mlistFavorite.get(iSelectPosition);
	}

	public void updateSelectItem(T t)
	{
		mlistFavorite.set(iSelectPosition, t);
		if (null != mAdapter)
		{
			mAdapter.notifyDataSetChanged();
		}
	}

	public void setPullRefresh(boolean isPullRefresh)
	{
		this.isPullRefresh = isPullRefresh;
	}

	public void clear()
	{
		if (isPullRefresh)
		{
			mlistFavorite.clear();
			if (null != mAdapter)
			{
				mAdapter.notifyDataSetChanged();
			}

			if (null != mListView)
			{
				mListView.setAdapter(null);
			}
		}
	}

	public CommonAdapter getAdapter()
	{
		return mAdapter;
	}

	public int getPageNum()
	{
		if (isPullRefresh)
		{
			mPageNum = 1;
		}
		else
		{
			if (null != mlistFavorite)
			{
				mPageNum = mlistFavorite.size() / 10 + 1;
			}
		}

		return mPageNum;
	}
}
