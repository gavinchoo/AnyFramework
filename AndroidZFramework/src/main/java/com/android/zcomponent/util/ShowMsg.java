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

package com.android.zcomponent.util;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.component.zframework.R;
import com.android.zcomponent.cryptography.Base64;
import com.android.zcomponent.http.api.model.MessageData;
import com.android.zcomponent.http.constant.ErrorCode;
import com.android.zcomponent.views.AlertPopupWindow;
import com.android.zcomponent.views.AlertPopupWindow.OnAlertItemClickListener;

/**
 * @ClassName:ShowMsg
 * @Description: 弹出消息类，用于显示提示消息
 * @author: wei
 * @date: 2012-2-21
 * 
 */
public class ShowMsg
{

	/** 错误信息 */
	private static final String LOG_TAG = "ShowMsg";

	/** 提示框 */
	private static TipsToast m_toast = null;

	public static void showConfirmDialog(Activity activity,
			final IConfirmDialog confirmDialog, int resId)
	{
		showConfirmDialog(activity, confirmDialog, activity.getResources()
				.getString(resId));
	}

	public static void showConfirmDialog(Activity activity,
			final IConfirmDialog confirmDialog, String strMsg)
	{
		showConfirmDialog(activity, confirmDialog, "", "", strMsg);
	}

	public static void showConfirmDialog(Activity activity,
			final IConfirmDialog confirmDialog, String positiveText,
			String negativeText, int msgId)
	{
		showConfirmDialog(activity, confirmDialog, positiveText, negativeText,
				activity.getString(msgId));
	}

	public static void showConfirmDialog(Activity activity,
			final IConfirmDialog confirmDialog, String positiveText,
			String negativeText, String strMsg)
	{
		final CustomDialog mCustomDialog =
				new CustomDialog(activity, R.layout.dialog_confirm_layout,
						false);
		TextView tvewMsg =
				(TextView) mCustomDialog.findViewById(R.id.confirm_msg);
		Button btnSure =
				(Button) mCustomDialog.findViewById(R.id.confirm_dialog_sure);
		Button btnCancel =
				(Button) mCustomDialog.findViewById(R.id.confirm_dialog_cancel);
		if (!StringUtil.isEmptyString(positiveText))
		{
			btnSure.setText(positiveText);
		}
		if (!StringUtil.isEmptyString(negativeText))
		{
			btnCancel.setText(negativeText);
		}
		tvewMsg.setText(strMsg);
		btnSure.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				mCustomDialog.dismiss();
				if (null != confirmDialog)
				{
					confirmDialog.onConfirm(true);
				}
			}
		});
		mCustomDialog.setOnCancelListener(new OnCancelListener()
		{

			@Override
			public void onCancel(DialogInterface dialog)
			{
				LogEx.d(LOG_TAG, "onCancel");
				mCustomDialog.dismiss();
				if (null != confirmDialog)
				{
					confirmDialog.onConfirm(false);
				}
			}
		});
		btnCancel.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				mCustomDialog.dismiss();
				if (null != confirmDialog)
				{
					confirmDialog.onConfirm(false);
				}
			}
		});
		if (!activity.isFinishing() && !mCustomDialog.isShowing())
		{
			mCustomDialog.show();
		}
	}

	public static void showConfirmDialog(Activity activity,
			final IConfirmDialogBool confirmDialog, String positiveText,
			String negativeText, View view)
	{
		final CustomDialog mCustomDialog =
				new CustomDialog(activity,
						R.layout.dialog_confirm_custom_layout, false);
		LinearLayout llayoutContent =
				(LinearLayout) mCustomDialog.findViewById(R.id.llayout_content);
		Button btnSure =
				(Button) mCustomDialog.findViewById(R.id.confirm_dialog_sure);
		Button btnCancel =
				(Button) mCustomDialog.findViewById(R.id.confirm_dialog_cancel);
		llayoutContent.addView(view);
		if (!StringUtil.isEmptyString(positiveText))
		{
			btnSure.setText(positiveText);
		}
		if (!StringUtil.isEmptyString(negativeText))
		{
			btnCancel.setText(negativeText);
		}
		btnSure.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				if (null != confirmDialog)
				{
					if (confirmDialog.onConfirm(true))
					{
						mCustomDialog.dismiss();
					}
				}
				else
				{
					mCustomDialog.dismiss();
				}
			}
		});
		mCustomDialog.setOnCancelListener(new OnCancelListener()
		{

			@Override
			public void onCancel(DialogInterface dialog)
			{
				LogEx.d(LOG_TAG, "onCancel");
				mCustomDialog.dismiss();
				if (null != confirmDialog)
				{
					confirmDialog.onConfirm(false);
				}
			}
		});
		btnCancel.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				mCustomDialog.dismiss();
				if (null != confirmDialog)
				{
					confirmDialog.onConfirm(false);
				}
			}
		});
		if (!activity.isFinishing() && !mCustomDialog.isShowing())
		{
			mCustomDialog.show();
		}
	}

	public static void showConfirmDialog(Activity activity,
			final IConfirmDialogKeyBack confirmDialog, String positiveText,
			String negativeText, String strMsg)
	{
		final CustomDialog mCustomDialog =
				new CustomDialog(activity, R.layout.dialog_confirm_layout,
						false);
		TextView tvewMsg =
				(TextView) mCustomDialog.findViewById(R.id.confirm_msg);
		Button btnSure =
				(Button) mCustomDialog.findViewById(R.id.confirm_dialog_sure);
		Button btnCancel =
				(Button) mCustomDialog.findViewById(R.id.confirm_dialog_cancel);
		if (!StringUtil.isEmptyString(positiveText))
		{
			btnSure.setText(positiveText);
		}
		if (!StringUtil.isEmptyString(negativeText))
		{
			btnCancel.setText(negativeText);
		}
		tvewMsg.setText(strMsg);
		btnSure.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				mCustomDialog.dismiss();
				if (null != confirmDialog)
				{
					confirmDialog.onConfirm(true, false);
				}
			}
		});
		mCustomDialog.setOnCancelListener(new OnCancelListener()
		{

			@Override
			public void onCancel(DialogInterface dialog)
			{
				LogEx.d(LOG_TAG, "onCancel");
				mCustomDialog.dismiss();
				if (null != confirmDialog)
				{
					confirmDialog.onConfirm(false, true);
				}
			}
		});
		btnCancel.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				mCustomDialog.dismiss();
				if (null != confirmDialog)
				{
					confirmDialog.onConfirm(false, false);
				}
			}
		});
		if (!activity.isFinishing() && !mCustomDialog.isShowing())
		{
			mCustomDialog.show();
		}
	}

	public static void showConfirmDialogNative(Activity activity,
			final IConfirmDialog confirmDialog, int resId,
			boolean isCancelable, boolean isNeedCallBack)
	{
		showConfirmDialogNative(activity, confirmDialog, ((Activity) activity)
				.getResources().getString(resId), isCancelable, isNeedCallBack);
	}

	public static void showConfirmDialogNative(Activity activity,
			final IConfirmDialog confirmDialog, String strMsg,
			boolean isCancelable, final boolean isNeedCallBack)
	{
		final CustomDialog mCustomDialog =
				new CustomDialog(activity,
						R.layout.dialog_confirm_native_layout, false);
		mCustomDialog.setCancelable(isCancelable);
		TextView tvewMsg =
				(TextView) mCustomDialog.findViewById(R.id.confirm_msg);
		Button btnSure =
				(Button) mCustomDialog.findViewById(R.id.confirm_dialog_sure);
		tvewMsg.setText(strMsg);
		btnSure.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				mCustomDialog.dismiss();
				if (isNeedCallBack)
				{
					if (null != confirmDialog)
					{
						confirmDialog.onConfirm(true);
					}
				}
			}
		});
		mCustomDialog.setOnCancelListener(new OnCancelListener()
		{

			@Override
			public void onCancel(DialogInterface dialog)
			{
				LogEx.d(LOG_TAG, "onCancel");
				mCustomDialog.dismiss();
			}
		});
		if (!activity.isFinishing())
		{
			mCustomDialog.show();
		}
	}

	public static void showConfirmDialog(Context context,
			final Handler handler, int iMsgResID)
	{
		final CustomDialog mCustomDialog =
				new CustomDialog((Activity) context,
						R.layout.dialog_confirm_layout, false);
		TextView tvewMsg =
				(TextView) mCustomDialog.findViewById(R.id.confirm_msg);
		Button btnSure =
				(Button) mCustomDialog.findViewById(R.id.confirm_dialog_sure);
		Button btnCancel =
				(Button) mCustomDialog.findViewById(R.id.confirm_dialog_cancel);
		tvewMsg.setText(iMsgResID);
		btnSure.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				mCustomDialog.dismiss();
				Message msg = new Message();
				msg.obj = true;
				handler.sendMessage(msg);
			}
		});
		mCustomDialog.setOnCancelListener(new OnCancelListener()
		{

			@Override
			public void onCancel(DialogInterface dialog)
			{
				LogEx.d(LOG_TAG, "onCancel");
				mCustomDialog.dismiss();
				Message msg = new Message();
				msg.obj = false;
				handler.sendMessage(msg);
			}
		});
		btnCancel.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				mCustomDialog.dismiss();
				Message msg = new Message();
				msg.obj = false;
				handler.sendMessage(msg);
			}
		});
		if (!((Activity) context).isFinishing())
		{
			mCustomDialog.show();
		}
	}

	public static void showAlertDialog(Context context, String title,
			String message)
	{
		showAlertDialog(context, (int) (330 * MyLayoutAdapter.getInstance()
				.getDensityRatio()), title, message);
	}

	public static void showAlertDialog(Context context, int height,
			String title, int message)
	{
		showAlertDialog(context, height, title, context.getResources()
				.getString(message));
	}

	public static void showAlertDialog(Context context, int height,
			String title, String message)
	{
		final CustomDialog alertDialog =
				new CustomDialog(context, R.layout.dialog_alert_layout, false);
		alertDialog.setCanceledOnTouchOutside(true);

		RelativeLayout rlayoutContent =
				(RelativeLayout) alertDialog
						.findViewById(R.id.alert_dialog_rlayout_content);
		int maxWidth =
				(int) (480 * MyLayoutAdapter.getInstance().getDensityRatio());
		if (MyLayoutAdapter.getInstance().getScreenWidth() > maxWidth)
		{
			rlayoutContent.getLayoutParams().width = maxWidth;
		}

		TextView tvewTitle =
				(TextView) alertDialog.findViewById(R.id.alert_dialog_title);
		TextView tvewContent =
				(TextView) alertDialog.findViewById(R.id.alert_dialog_content);
		LinearLayout llayoutPositive =
				(LinearLayout) alertDialog
						.findViewById(R.id.alert_dialog_llayout_positive);
		Button btnPositve =
				(Button) alertDialog
						.findViewById(R.id.alert_dialog_btn_positive);
		ScrollView scrollView =
				(ScrollView) alertDialog
						.findViewById(R.id.alert_dialog_scrollview_content);
		int defaultHeight =
				(int) (330 * MyLayoutAdapter.getInstance().getDensityRatio());
		if (defaultHeight > height)
		{
			scrollView.getLayoutParams().height = height;
		}
		RelativeLayout rlayoutParent =
				(RelativeLayout) alertDialog
						.findViewById(R.id.alert_dialog_parent);
		ListView lvewContent =
				(ListView) alertDialog.findViewById(R.id.alert_dialog_lvew);
		tvewTitle.setText(title);
		llayoutPositive.setVisibility(View.VISIBLE);
		tvewContent.setVisibility(View.VISIBLE);
		lvewContent.setVisibility(View.GONE);
		scrollView.setVisibility(View.VISIBLE);
		tvewContent.setText(message);
		btnPositve.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				alertDialog.dismiss();
			}
		});
		alertDialog.show();
	}

	public static void showAlertDialog(Context context, String title,
			String[] items,
			final DialogInterface.OnClickListener onClickListener)
	{
		final CustomDialog alertDialog =
				new CustomDialog(context, R.layout.dialog_alert_layout, false);
		alertDialog.setCanceledOnTouchOutside(true);
		RelativeLayout rlayoutContent =
				(RelativeLayout) alertDialog
						.findViewById(R.id.alert_dialog_rlayout_content);
		int maxWidth =
				(int) (480 * MyLayoutAdapter.getInstance().getDensityRatio());
		if (MyLayoutAdapter.getInstance().getScreenWidth() > maxWidth)
		{
			rlayoutContent.getLayoutParams().width = maxWidth;
		}
		TextView tvewTitle =
				(TextView) alertDialog.findViewById(R.id.alert_dialog_title);
		TextView tvewContent =
				(TextView) alertDialog.findViewById(R.id.alert_dialog_content);
		LinearLayout llayoutPositive =
				(LinearLayout) alertDialog
						.findViewById(R.id.alert_dialog_llayout_positive);
		Button btnPositve =
				(Button) alertDialog
						.findViewById(R.id.alert_dialog_btn_positive);
		ScrollView scrollView =
				(ScrollView) alertDialog
						.findViewById(R.id.alert_dialog_scrollview_content);
		RelativeLayout rlayoutParent =
				(RelativeLayout) alertDialog
						.findViewById(R.id.alert_dialog_parent);
		ListView lvewContent =
				(ListView) alertDialog.findViewById(R.id.alert_dialog_lvew);
		tvewTitle.setText(title);
		llayoutPositive.setVisibility(View.GONE);
		tvewContent.setVisibility(View.GONE);
		scrollView.setVisibility(View.GONE);
		lvewContent.setVisibility(View.VISIBLE);
		rlayoutParent.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				alertDialog.dismiss();
			}
		});
		AlertDialogAdapter adapter =
				new AlertDialogAdapter(context,
						StringUtil.stringArrayToList(items));
		lvewContent.setAdapter(adapter);
		lvewContent.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3)
			{
				if (null != onClickListener)
				{
					onClickListener.onClick(alertDialog, position);
				}
				alertDialog.dismiss();
			}
		});
		alertDialog.show();
	}

	public static void showAlertPopupWindow(Context context, String[] title,
			OnAlertItemClickListener itemClickListener)
	{
		AlertPopupWindow alertPopupWindow = new AlertPopupWindow(context);
		alertPopupWindow.setAlertTitles(title);
		alertPopupWindow.setOnAlertItemClickListener(itemClickListener);
		alertPopupWindow.show();
	}

	private static class AlertDialogAdapter extends BaseAdapter
	{

		private List<String> items;

		private LayoutInflater mLayoutInflater;

		public AlertDialogAdapter(Context context, List<?> list)
		{
			items = (List<String>) list;
			mLayoutInflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			convertView =
					mLayoutInflater.inflate(R.layout.item_list_dialog_alert,
							null);
			TextView tvewItem =
					(TextView) convertView
							.findViewById(R.id.alert_dialog_item_name);
			tvewItem.setText(items.get(position));
			return convertView;
		}

		@Override
		public int getCount()
		{
			if (null == items)
			{
				return 0;
			}
			return items.size();
		}

		@Override
		public Object getItem(int arg0)
		{
			return items.get(arg0);
		}

		@Override
		public long getItemId(int arg0)
		{
			return arg0;
		}
	}

	/**
	 * <p>
	 * Description: toast形式显示消息。
	 * <p>
	 * 
	 * @date 2012-2-20
	 * @param ctxActivity
	 *            activity实例
	 * @param strMsg
	 *            需要显示的消息
	 */
	public static void showToast(Context ctxActivity, String strMsg)
	{
		if (StringUtil.isEmptyString(strMsg))
		{
			return;
		}
		if (null == ctxActivity)
		{
			return;
		}
		if (m_toast == null)
		{
			m_toast = TipsToast.makeText(ctxActivity, strMsg, Toast.LENGTH_LONG);
			m_toast.setIcon(R.drawable.tips_smile);
			m_toast.show();
		}
		else
		{
			m_toast.setIcon(R.drawable.tips_smile);
			m_toast.setText(strMsg);
			m_toast.setDuration(Toast.LENGTH_LONG);
			m_toast.show();
		}
	}

	public static void showToast(Context ctxActivity, String strMsg,
			int tipsResId)
	{
		if (StringUtil.isEmptyString(strMsg))
		{
			return;
		}
		if (null == ctxActivity)
		{
			return;
		}
		if (m_toast == null)
		{
			m_toast = TipsToast.makeText(ctxActivity, strMsg, Toast.LENGTH_LONG);
			m_toast.setIcon(tipsResId);
			m_toast.show();
		}
		else
		{
			m_toast.setIcon(tipsResId);
			m_toast.setText(strMsg);
			m_toast.setDuration(Toast.LENGTH_LONG);
			m_toast.show();
		}
	}

	/**
	 * <p>
	 * Description: toast形式显示消息。
	 * <p>
	 * 
	 * @date 2012-2-20
	 * @param ctxActivity
	 *            activity实例
	 * @param iMsgResID
	 *            消息的资源编号
	 */
	public static void showToast(Context ctxActivity, int iMsgResID)
	{
		if (null == ctxActivity)
		{
			return;
		}
		showToast(ctxActivity, ctxActivity.getResources().getString(iMsgResID));
	}

	public static void showToast(Context ctxActivity, int returnCode,
			String strMsg)
	{
		if (returnCode == ErrorCode.INT_NET_DISCONNECT)
		{
			showToast(ctxActivity, R.string.common_net_disconnect);
		}
		else if (returnCode == ErrorCode.INT_NET_CONNECT_TIME_OUT)
		{
			showToast(ctxActivity, R.string.common_net_time_out);
		}
		else
		{
			showToast(ctxActivity, strMsg);
		}
	}

	public static void showToast(Context ctxActivity, MessageData msg,
			String strMsg)
	{
		int returnCode = msg.getReturnCode();
		if (returnCode == ErrorCode.INT_NET_DISCONNECT)
		{
			showToast(ctxActivity,
					ctxActivity.getString(R.string.common_net_disconnect),
					R.drawable.tips_error);
		}
		else if (returnCode == ErrorCode.INT_NET_CONNECT_TIME_OUT)
		{
			showToast(ctxActivity,
					ctxActivity.getString(R.string.common_net_time_out),
					R.drawable.tips_error);
		}
		else if (returnCode == ErrorCode.INT_NET_SYSTEM_MAINTENANCE)
		{
			showToast(ctxActivity,
					ctxActivity
							.getString(R.string.common_net_system_maintenance),
					R.drawable.tips_error);
		}
		else if (returnCode == ErrorCode.INT_NET_CONNECT_BADREQUEST)
		{
			if (null != msg.getContext().headers().get("Message"))
			{
				String strErrorMsg =
						Base64.decodeString(
								msg.getContext().headers().get("Message")
										.toString()).toString();
				LogEx.d("zjw", "strErrorMsg = " + strErrorMsg);
				showToast(ctxActivity, strErrorMsg.replace("\"", "")
						.replaceAll(EmojilHelper.regexStr, ""),
						R.drawable.tips_error);
			}
			else
			{
				showToast(ctxActivity, strMsg, R.drawable.tips_smile);
			}
		}
		else
		{

			if (null != msg.getRspObject()
					&& Boolean.class.equals(msg.getRspObject().getClass()))
			{
				Boolean rsp = (Boolean) msg.getRspObject();
				if (null != rsp && rsp)
				{
					showToast(ctxActivity, strMsg, R.drawable.tips_smile);
				}
				else
				{
					showToast(ctxActivity, strMsg, R.drawable.tips_error);
				}
			}
			else
			{
				showToast(ctxActivity, strMsg, R.drawable.tips_smile);
			}

		}
	}

	public static void showToast(Context ctxActivity, MessageData msg,
			int iMsgResID)
	{
		showToast(ctxActivity, msg,
				ctxActivity.getResources().getString(iMsgResID));
	}

	public static void showToast(Context ctxActivity, int returnCode,
			int iMsgResID)
	{
		showToast(ctxActivity, returnCode, ctxActivity.getResources()
				.getString(iMsgResID));
	}

	public static void showCallDialog(final Activity activity,
			final String strPhone)
	{
		ShowMsg.showConfirmDialog(activity, new IConfirmDialog()
		{

			@Override
			public void onConfirm(boolean confirmValue)
			{
				if (confirmValue)
				{
					Uri uri = Uri.parse("tel:" + strPhone);
					Intent intent = new Intent(Intent.ACTION_DIAL, uri);
					activity.startActivity(intent);
				}
			}
		}, "呼叫", "取消", strPhone);
	}

	public interface IConfirmDialog
	{

		/**
		 * <p>
		 * Description: 提示框点击确认或取消事件回调接口
		 * <p>
		 * 
		 * @date 2013-8-23
		 * @author WEI
		 * @param confirmValue
		 */
		public void onConfirm(boolean confirmValue);
	}

	public interface IConfirmDialogBool
	{

		/**
		 * <p>
		 * Description: 提示框点击确认或取消事件回调接口
		 * <p>
		 * 
		 * @date 2013-8-23
		 * @author WEI
		 * @param confirmValue
		 */
		public boolean onConfirm(boolean confirmValue);
	}

	public interface IConfirmDialogKeyBack
	{

		/**
		 * <p>
		 * Description: 提示框点击确认或取消事件回调接口
		 * <p>
		 * 
		 * @date 2013-8-23
		 * @author WEI
		 * @param confirmValue
		 */
		public void onConfirm(boolean confirmValue, boolean onKeyBack);
	}
}
