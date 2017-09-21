
package com.android.zcomponent.heartbeat.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;

import com.android.zcomponent.heartbeat.IHeartBeat;
import com.android.zcomponent.util.LogEx;

public class HeartBeat extends IHeartBeat
{

	private static final String TAG = HeartBeat.class.getSimpleName();

	private static final String HEART_HOST = "http://www.baidu.com";

	/** Handler 消息， 心跳请求返回网络状态 */
	private static final int HANDLER_HEART_CHANGE = 0;

	/** Handler 消息， 重新发起心跳请求 */
	private static final int HANDLER_HEART_REPULSE = 1;

	private static final int HEART_COUNT = 4;

	/** 心跳请求超时时间 */
	private static final int TIME_OUT = 1 * 1000;

	/** 重新发起心跳请求时间间隔 */
	private static final int REPULSE_TIME = 3 * 1000;

	private int heartRateSuccess;

	private int heartRateFail;

	private int totalTime;

	private boolean isTimeout;

	private List<OnHeartRateChangeListener> mHeartRateChangeListener =
			new ArrayList<OnHeartRateChangeListener>();

	private Timer mTimer = new Timer();

	private TimeoutTask mTimeoutTask;

	private RepulseTask mRepulseTask;

	private static HeartBeat mHeartBeat;

	public static HeartBeat getInstance()
	{
		if (null == mHeartBeat)
		{
			mHeartBeat = new HeartBeat();
		}
		return mHeartBeat;
	}

	private HeartBeat()
	{

	}

	@Override
	public void pulse()
	{
		heartRateSuccess = 0;
		heartRateFail = 0;
		totalTime = 0;
		setTimeout(false);
		for (int i = 0; i < HEART_COUNT; i++)
		{
			sendHeartRequest();
		}
		if (null != mTimeoutTask)
		{
			mTimeoutTask.cancel();
		}
		mTimeoutTask = new TimeoutTask();
		mTimer.schedule(mTimeoutTask, TIME_OUT);
	}

	@Override
	public HeartState heartRate()
	{
		HeartState state = HeartState.getState(heartRateSuccess);
		// 网络不好，继续检测
		if (state == HeartState.SLOW || state == HeartState.STOPED)
		{
			if (null != mRepulseTask)
			{
				mRepulseTask.cancel();
			}
			mRepulseTask = new RepulseTask();
			mTimer.schedule(mRepulseTask, REPULSE_TIME);
		}
		return state;
	}

	public boolean isTimeout()
	{
		return isTimeout;
	}

	public void setTimeout(boolean isTimeout)
	{
		this.isTimeout = isTimeout;
	}

	class TimeoutTask extends TimerTask
	{

		@Override
		public void run()
		{
			handler.sendEmptyMessage(HANDLER_HEART_CHANGE);
		}
	}

	class RepulseTask extends TimerTask
	{

		@Override
		public void run()
		{
			handler.sendEmptyMessage(HANDLER_HEART_REPULSE);
		}
	}

	Handler handler = new Handler(new Callback()
	{

		@Override
		public boolean handleMessage(Message msg)
		{
			if (msg.what == HANDLER_HEART_CHANGE)
			{
				setTimeout(true);
				nofityHeartRateChange();
			}
			else if (msg.what == HANDLER_HEART_REPULSE)
			{
				pulse();
			}
			return false;
		}
	});

	private void sendHeartRequest()
	{
		HeartRequest heartRequest = new HeartRequest();
		heartRequest.execute(HEART_HOST);
	}

	class HeartRequest extends AsyncTask<String, Integer, Heart>
	{

		@Override
		protected Heart doInBackground(String... arg0)
		{
			try
			{
				DefaultHttpClient httpClient = new DefaultHttpClient();
				// 请求超时
				httpClient.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, 300);
				HttpHead httpHead = new HttpHead(arg0[0]);
				Heart heart = new Heart();

				long startTime = System.currentTimeMillis();
				HttpResponse response = httpClient.execute(httpHead);
				// 获取HTTP状态码
				int statusCode = response.getStatusLine().getStatusCode();
				httpHead.abort();
				long endTime = System.currentTimeMillis();
				heart.stateCode = statusCode;
				heart.rate = (int) (endTime - startTime);
				return heart;
			}
			catch (Exception e)
			{
				LogEx.e(TAG, "Heart Lose");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Heart result)
		{
			super.onPostExecute(result);

			if (null != result && result.stateCode == 200)
			{
				heartRateSuccess++;
			}
			else
			{
				heartRateFail++;
			}

			if (null != result)
			{
				totalTime += result.rate;
			}

			if (HEART_COUNT == heartRateSuccess + heartRateFail && !isTimeout())
			{
				LogEx.i(TAG,
						String.format(
								"Head 发送 = %1$d , 接收 = %2$d , 丢失 = %3$d, 总时间长 = %4$d",
								HEART_COUNT, heartRateSuccess, heartRateFail,
								totalTime));

				mTimeoutTask.cancel();
				nofityHeartRateChange();
			}
		}
	}

	private void nofityHeartRateChange()
	{
		if (null == mHeartRateChangeListener
				|| mHeartRateChangeListener.size() == 0)
		{
			return;
		}
		HeartState heartState = heartRate();

		for (OnHeartRateChangeListener listener : mHeartRateChangeListener)
		{
			if (null != listener)
			{
				listener.onHeartRateChange(heartState);
			}
		}
	}

	public interface OnHeartRateChangeListener
	{

		public void onHeartRateChange(HeartState state);
	}

	public void registerHeartRateChangeListener(
			OnHeartRateChangeListener onHeartRateChangeListener)
	{
		if (null != mHeartRateChangeListener)
		{
			if (!mHeartRateChangeListener.contains(onHeartRateChangeListener))
			{
				mHeartRateChangeListener.add(onHeartRateChangeListener);
			}
			LogEx.d(TAG, "HeartRateChangeListener size "
					+ mHeartRateChangeListener.size());
		}

	}

	public void unregisterHeartRateChangeListener(
			OnHeartRateChangeListener onHeartRateChangeListener)
	{
		if (null != mHeartRateChangeListener)
		{
			mHeartRateChangeListener.remove(onHeartRateChangeListener);
			LogEx.d(TAG, "HeartRateChangeListener size "
					+ mHeartRateChangeListener.size());
		}

	}

	@Override
	public void pause()
	{
		
	}

	@Override
	public void destroy()
	{
		
	}
}
