
package com.android.zcomponent.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.os.Message;

import com.android.zcomponent.common.uiframe.BroadcastReceiverActivity;
import com.android.zcomponent.communication.http.Context;
import com.android.zcomponent.communication.http.Progress;
import com.android.zcomponent.communication.http.Status;
import com.android.zcomponent.communication.http.annotation.HttpMsgType;
import com.android.zcomponent.communication.http.annotation.HttpMsgType.Type;
import com.android.zcomponent.constant.GlobalConst;
import com.android.zcomponent.delegate.Callback.DoubleParameterCallback;
import com.android.zcomponent.delegate.Callback.SingleParameterCallback;
import com.android.zcomponent.http.api.host.Endpoint;
import com.android.zcomponent.http.api.model.MessageData;
import com.android.zcomponent.http.api.model.MessageRequest;
import com.android.zcomponent.http.constant.ErrorCode;
import com.android.zcomponent.http.constant.NetConst;
import com.android.zcomponent.json.JSONParser;
import com.android.zcomponent.json.JsonSerializerFactory;
import com.android.zcomponent.thread.ThreadHandler;
import com.android.zcomponent.util.LogEx;
import com.android.zcomponent.util.SettingsBase;
import com.android.zcomponent.util.StringUtil;

public class HttpDataLoader extends BaseImpl
{

	private static final String TAG = "HttpDataLoader";

	private Handler m_Handler;

	private ThreadHandler<Context> handler;

	private String strDateResponse = "";

	private int returnCode = ErrorCode.INT_QUERY_DATA_SUCCESS;

	private static final boolean HTTP_TYPE_GET = true;

	private boolean isDataQuerying = false;

	private HttpRequest mHttpRequest;

	public HttpDataLoader(Handler handler)
	{
		super(handler);
		m_Handler = handler;
		mHttpRequest = new HttpRequest();
	}

	public boolean isQuerying()
	{
		return isDataQuerying;
	}

	public void close()
	{
		if (null != handler)
		{
			handler.close();
		}
	}

	public HttpRequest getHttpRequest()
	{
		return mHttpRequest;
	}

	public <T> void reloadFailRequest()
	{
		HttpRequest httpRequest = getHttpRequest();
		if (null == httpRequest)
		{
			return;
		}

		List<Object> messageRequests = httpRequest.getAllRequests();
		if (null == messageRequests || messageRequests.size() == 0)
		{
			return;
		}

		for (int i = 0; i < messageRequests.size(); i++)
		{
			if (messageRequests.size() > i)
			{
				MessageRequest<T> messageRequest =
						(MessageRequest<T>) messageRequests.get(i);
				doPostProcess(messageRequest.request, messageRequest.msgCode,
						messageRequest.rspObject, messageRequest.isDecode);
			}
		}
	}

	public <T> T doPostProcess(Endpoint request, final int msgCode,
							   final Class<T> rspObject, final boolean isDecode)
	{
		HttpMsgType type = null;
		try
		{
			type = HttpRequest.loadMsgType(msgCode);
			LogEx.d(TAG, "请求类型 = " + type);
			if (null != type
					&& (type.value() == Type.QUERY || type.value() == Type.QUERY_AUTHORIZED))
			{
				mHttpRequest.addMessageRequest(request, msgCode, rspObject,
						isDecode);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		if (!BroadcastReceiverActivity.onNetChanged())
		{
			MessageData data =
					HttpDataLoader.dataFactoryT(null, null,
							ErrorCode.INT_NET_DISCONNECT);
			Message msg = m_Handler.obtainMessage(msgCode, data);
			m_Handler.sendMessage(msg);
			return (T) null;
		}

		if (StringUtil.isEmptyString(Endpoint.Cookie)
				&& !StringUtil.isEmptyString(SettingsBase.getInstance()
						.readStringByKey(GlobalConst.STR_CONFIG_COOKIE)))
		{
			Endpoint.Cookie =
					SettingsBase.getInstance().readStringByKey(
							GlobalConst.STR_CONFIG_COOKIE);
		}

		// 需鉴权的接口，没有Cookie不调用接口。
		if (null != type && type.value() == Type.QUERY_AUTHORIZED)
		{
            if (StringUtil.isEmptyString(Endpoint.Cookie))
            {
            	return (T) null;
            }
		}
		
		isDataQuerying = true;
		handler =
				Endpoint.communicator()
						.send(request,
								new SingleParameterCallback.ThrowExceptionCallback<Context>()
								{

									@Override
									public void invoke(Context context)
											throws Exception
									{
										process(context, msgCode, rspObject,
												isDecode);
									}
								},
								new DoubleParameterCallback<Context, Exception>()
								{

									@Override
									public void invoke(Context context,
											Exception exception)
									{
										try
										{
											process(context, msgCode,
													rspObject, isDecode);
										}
										catch (Exception e)
										{
											e.printStackTrace();
										}
									}
								});
		handler.release();
		return (T) null;
	}

	public <T> T doPostProcess(Endpoint request, final int msgCode,
			final Class<T> rspObject)
	{
		doPostProcess(request, msgCode, rspObject, true);
		return (T) null;
	}

	private <T> T process(Context context, final int msgCode,
			final Class<T> rspObject, boolean isDecode) throws Exception
	{
		if (context.status().code() == Status.TimeOut)
		{
			if (BroadcastReceiverActivity.isNetConnected())
			{
				returnCode = ErrorCode.INT_NET_CONNECT_TIME_OUT;
			}
			else
			{
				returnCode = ErrorCode.INT_NET_DISCONNECT;
			}
		}
		else if (context.status().code() == Status.Unauthorized)
		{
			returnCode = ErrorCode.INT_NET_CONNECT_UNAUTHORIZED;
		}
		else if (context.status().code() == Status.InternalServerError)
		{
			returnCode = ErrorCode.INT_NET_CONNECT_UNKONW;
		}
		else if (context.status().code() == Status.SystemMaintain
				|| context.status().code() == 502)
		{
			returnCode = ErrorCode.INT_NET_SYSTEM_MAINTENANCE;
		}
		else if (context.status().code() == Status.BadRequest)
		{
			returnCode = ErrorCode.INT_NET_CONNECT_BADREQUEST;
		}
		else
		{
			mHttpRequest.removeMessageRequest(msgCode);
			returnCode = ErrorCode.INT_QUERY_DATA_SUCCESS;
		}
		MessageData data = null;
		if (isDecode)
		{
			data =
					BaseImpl.dataFactoryT(
							Endpoint.decoder().decode(context, rspObject),
							context, returnCode);
		}
		else
		{
			data = BaseImpl.dataFactoryT("", context, returnCode);
		}

		Message msg = m_Handler.obtainMessage(msgCode, data);
		m_Handler.sendMessage(msg);
		isDataQuerying = false;
		return (T) null;
	}

	public <T> T doPostFileProcess(Endpoint request, final int msgCode,
			String filePath, final Class<T> rspObject)
	{
		return doPostFileProcess(request, msgCode, filePath, rspObject, false);
	}
	
	public <T> T doPostFileProcess(Endpoint request, final int msgCode,
			String filePath, final Class<T> rspObject, final boolean isDecode)
	{
		if (!BroadcastReceiverActivity.onNetChanged())
		{
			MessageData data =
					HttpDataLoader.this.dataFactory(null, null,
							ErrorCode.INT_NET_DISCONNECT);
			Message msg = m_Handler.obtainMessage(msgCode, data);
			m_Handler.sendMessage(msg);
			return (T) null;
		}
		handler =
				Endpoint.communicator().upload(request, filePath,
						new SingleParameterCallback<Progress>()
						{

							@Override
							public void invoke(Progress parameter)
							{
							}
						}, new SingleParameterCallback<Context>()
						{

							@Override
							public void invoke(Context context)
							{
								try
								{
									process(context, msgCode, rspObject, isDecode);
								}
								catch (Exception e)
								{
									e.printStackTrace();
								}
							}
						}, new DoubleParameterCallback<Context, Exception>()
						{

							@Override
							public void invoke(Context context, Exception arg1)
							{
								try
								{
									process(context, msgCode, rspObject, isDecode);
								}
								catch (Exception e)
								{
									e.printStackTrace();
								}
							}
						});
		handler.release();
		return (T) null;
	}
}
