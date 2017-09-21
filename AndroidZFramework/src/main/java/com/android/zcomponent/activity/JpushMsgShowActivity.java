
package com.android.zcomponent.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

import com.android.component.zframework.R;
import com.android.zcomponent.common.uiframe.BaseActivity;
import com.android.zcomponent.util.MyLayoutAdapter;
import com.android.zcomponent.util.anim.AnimationFactory;

/**
 * 极光推送消息显示
 * 
 * @ClassName:JpushMsgShowActivity
 * @Description: 当服务端对订单进行操作：接单、分配预订等，接受推送的消息进行展示。
 * @author: wei
 * @date: 2015-4-14
 * 
 */
public class JpushMsgShowActivity extends BaseActivity
{

	TextView mtvewMsg;

	Button mbtnSure;

	LinearLayout mllayoutContent;

	RelativeLayout mrlayoutBg;

	@Override
	public void onCreate(Bundle arg0)
	{
		super.onCreate(arg0);
		setContentView(R.layout.activity_show_jpush_msg);
		init();
	}

	private void init()
	{
		mtvewMsg = (TextView) findViewById(R.id.confirm_msg);
		mbtnSure = (Button) findViewById(R.id.confirm_dialog_sure);
		mllayoutContent = (LinearLayout) findViewById(R.id.jpush_msg_content);
		mrlayoutBg = (RelativeLayout) findViewById(R.id.jpush_msg_bg);
		mllayoutContent.startAnimation(AnimationFactory.fadeInAnimation(300,
				300));
		mrlayoutBg.startAnimation(AnimationFactory.fadeInAnimation(300, 300));
		getWindow()
				.setLayout(
						LayoutParams.FILL_PARENT,
						(int) (MyLayoutAdapter.getInstance().getScreenHeight() - MyLayoutAdapter
								.getInstance().getStatusBarHeight()));
		setFinishOnTouchOutside(false);
		Intent intent = getIntent();
		showMsg(intent.getExtras());
		mbtnSure.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				close();
			}
		});
	}

	public void showMsg(Bundle intent)
	{
		if (null == intent)
		{
			return;
		}
		String msg = intent.getString(JPushInterface.EXTRA_ALERT);
		mtvewMsg.setText(msg);
	}

	public void close()
	{
		finish();
		overridePendingTransition(R.anim.scale_dialog_enter,
				R.anim.scale_dialog_out);
	}
}
