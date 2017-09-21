
package com.android.zcomponent.http;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.android.zcomponent.common.uiframe.FramewrokApplication;
import com.android.zcomponent.communication.http.annotation.HttpMsgType;
import com.android.zcomponent.http.api.host.Endpoint;
import com.android.zcomponent.http.api.model.MessageRequest;
import com.android.zcomponent.util.LogEx;

public class HttpRequest
{
	private static final String TAG = HttpRequest.class.getSimpleName();

	private List<Object> messageRequests = new ArrayList<Object>();

	public <T> void removeMessageRequest(int msgCode)
	{
		for (int i = 0; i < messageRequests.size(); i++)
		{
			MessageRequest<T> messageRequest =
					(MessageRequest<T>) messageRequests.get(i);
			if (messageRequest.msgCode == msgCode)
			{
				messageRequests.remove(i);
				break;
			}
		}
		LogEx.d(TAG, "remove size = " + messageRequests.size());
	}

	public <T> void addMessageRequest(Endpoint request, final int msgCode,
									  final Class<T> rspObject, final boolean isDecode)
	{
		if (contains(msgCode))
		{
			return;
		}

		MessageRequest<T> messageRequest = new MessageRequest<T>();
		messageRequest.isDecode = isDecode;
		messageRequest.msgCode = msgCode;
		messageRequest.request = request;
		messageRequest.rspObject = rspObject;
		messageRequests.add(messageRequest);
		LogEx.d(TAG, "add size = " + messageRequests.size());
	}

	public <T> boolean contains(int msgCode)
	{
		for (int i = 0; i < messageRequests.size(); i++)
		{
			MessageRequest<T> messageRequest =
					(MessageRequest<T>) messageRequests.get(i);
			if (messageRequest.msgCode == msgCode)
			{
				return true;
			}
		}
		return false;
	}

	public List<Object> getAllRequests()
	{
		return messageRequests;
	}

	public static <T> HttpMsgType loadMsgType(int msgCode) throws SecurityException,
			ClassNotFoundException
	{
		String[] msgCodes = FramewrokApplication.getInstance().getMsgCodeClass();
		if (null == msgCodes)
		{
			return null;
		}
		for (int i = 0; i < msgCodes.length; i++)
		{
			Class cls = Class.forName(msgCodes[i]);
			Field[] fields = cls.getFields();
			for (Field field : fields)
			{
				try
				{
					if (msgCode == field.getInt(cls))
					{
						HttpMsgType msgType = field.getAnnotation(HttpMsgType.class);
						if (null != msgType)
						{
							return msgType;
						}
						else
						{
							return null;
						}
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
