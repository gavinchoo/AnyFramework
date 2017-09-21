
package com.android.zcomponent.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.android.component.zframework.R;
import com.android.zcomponent.common.uiframe.FramewrokApplication;
import com.android.zcomponent.util.ClientInfo;
import com.android.zcomponent.util.CommonUtil;
import com.android.zcomponent.views.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * <p>
 * Description: 内容编辑
 * </p>
 * 
 * @ClassName:EditActivity
 * @author: wei
 * @date: 2015-11-10
 * 
 */
public class EditActivity extends SwipeBackActivity
{

	public static final int CODE_EDIT_COMPLETE = 1;
	
	public static final String CONTENT = "content";

	public static final String TITLE = "title";

	public static final String HINT = "hint";

	public static final String MIN_LINE = "minline";

	EditText mEditTvewContent;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		initUI();
	}

	void initUI()
	{
		Intent intent = getIntent();
		getTitleBar().setTitleText(intent.getStringExtra(TITLE));
		int line = intent.getIntExtra(MIN_LINE, 1);
		String content = intent.getStringExtra(CONTENT);

		mEditTvewContent = (EditText) findViewById(R.id.editvew_content_show);
		
		if (!TextUtils.isEmpty(content))
		{
			mEditTvewContent.setText(content);
			CommonUtil.setEditViewSelectionEnd(mEditTvewContent);
		}
		mEditTvewContent.setMinLines(line);
		if (null != intent.getStringExtra(HINT))
		{
			mEditTvewContent.setHint(intent.getStringExtra(HINT));
		}
		else
		{
			mEditTvewContent.setHint("");
		}
		
		findViewById(R.id.tvew_submit_click).setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				onClickTvewSubmit();
			}
		});
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		
		ClientInfo.closeSoftInput(mEditTvewContent, this);
	}

	void onClickTvewSubmit()
	{
		ClientInfo.closeSoftInput(mEditTvewContent, this);
		Intent intent = new Intent();
		intent.putExtra("content", mEditTvewContent.getText().toString());
		FramewrokApplication.getInstance().setActivityResult(
				CODE_EDIT_COMPLETE, intent);
		finish();
	}

}