/**
 * <p>
 * Copyright: Copyright (c) 2014
 * Company: 
 * Description: 这里写这个文件是干什么用的
 * </p>
 * @Title JPushUtil.java
 * @Package com.android.aizachi.jpush
 * @version 1.0
 * @author WEI
 * @date 2014-8-13
 */

package com.android.zcomponent.jpush;


import java.util.LinkedHashSet;
import java.util.Set;

import android.content.Context;
import cn.jpush.android.api.JPushInterface;


public class JPushUtil
{

    public static final String MESSAGE_LIST_URL = "http://notification.aizachi.com:8888/jpush/send_getWaiterMessages.action";

    public static final String MESSAGE_DEL_URL = "http://notification.aizachi.com:8888/jpush/send_deleteMessage.action";
    
    public static final String MESSAGE_MODIFY_URL = "http://notification.aizachi.com:8888/jpush/send_ModifyMessageStatus.action";
    
    public static final String MESSAGE_DEL_ALL_URL = "http://notification.aizachi.com:8888/jpush/send_deleteMessage.action";
    
    public static final String MESSAGE_COUNT = "http://notification.aizachi.com:8888/jpush/send_getMessagesCount.action";
    
    public static void stopPushServices(Context context)
    {
        final String alias = "";
        Set<String> tags = new LinkedHashSet<String>();
        JPushInterface.setAliasAndTags(context, alias, tags);
        JPushInterface.stopPush(context);
    }
    
    public static void resumePushServices(Context context)
    {
        JPushInterface.resumePush(context);
    }

    public static void startPushServices(Context context, String accout, String group)
    {
        JPushInterface.resumePush(context);
        try
        {
            // 设置推送别名
            final String alias = getJpushAlias(accout);
            Set<String> tags = new LinkedHashSet<String>();
            tags.add(group);
            JPushInterface.setAliasAndTags(context, alias, tags);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static void init(Context context)
    {
    	// 初始化极光推送
        JPushInterface.setDebugMode(true); // 设置开启日志,发布时请关闭日志
        JPushInterface.init(context); // 初始化 JPush
        JPushInterface.resumePush(context);
    }
    
    public static String getJpushAlias(String account)
    {
        String alias = account.replace("$", "");

        if (alias.length() > 40)
        {
            alias = alias.substring(alias.length() - 40, alias.length());
        }
        return alias;
    }
}
