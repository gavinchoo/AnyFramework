
package com.android.zcomponent.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.android.component.zframework.R;
import com.android.zcomponent.util.ImageLoaderUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CommonExpandabelAdapter extends BaseExpandableListAdapter
{

	protected Context mContext;

	protected LayoutInflater layoutInflater = null;

	protected List<?> mList = null;

	protected ImageLoader mImageLoader;

	protected DisplayImageOptions options;
	
	protected int miSelectPosition;

	public CommonExpandabelAdapter(Context context, List<?> list)
	{
		if (null != context)
		{
			layoutInflater = LayoutInflater.from(context);
		}
		mList = list;
		mContext = context;
		mImageLoader = ImageLoader.getInstance();
		options =
				ImageLoaderUtil
						.getDisplayImageOptions(R.drawable.img_empty_logo_small);

	}
	
	public void setSelectPosition(int position)
	{
		miSelectPosition = position;
		notifyDataSetChanged();
	}

	public void setFailImageRes(int resId)
	{
		options = ImageLoaderUtil.getDisplayImageOptions(resId);
	}

	public int getColor(int resId)
	{
		return mContext.getResources().getColor(resId);
	}
	
	public ColorStateList getColorStateList(int resId)
	{
		return mContext.getResources().getColorStateList(resId);
	}

	public void refreshAdapterData(List<?> newListData)
	{
		mList = newListData;
		this.notifyDataSetChanged();
	}

	public <T extends View> T findViewById(View view, int id)
	{
		SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
		if (viewHolder == null)
		{
			viewHolder = new SparseArray<View>();
			view.setTag(viewHolder);
		}
		View childView = viewHolder.get(id);
		if (childView == null)
		{
			childView = view.findViewById(id);
			viewHolder.put(id, childView);
		}
		return (T) childView;
	}

	public void intentToActivity(Bundle bundle, Class<?> cls)
	{
		Intent intent = new Intent();

		if (null != bundle)
		{
			intent.putExtras(bundle);
		}
		intent.setClass(mContext, cls);
		mContext.startActivity(intent);
	}

	public void intentToActivity(Class<?> cls)
	{
		intentToActivity(null, cls);
	}

	@Override
	public int getGroupCount()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getChildrenCount(int groupPosition)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getGroupId(int groupPosition)
	{
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition)
	{
		return childPosition;
	}

	@Override
	public boolean hasStableIds()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition)
	{
		// TODO Auto-generated method stub
		return false;
	}
}
