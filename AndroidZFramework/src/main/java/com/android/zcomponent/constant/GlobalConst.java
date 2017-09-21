/**
 *GlobalConst
 *
 *2012-8-17
 */

package com.android.zcomponent.constant;

import android.telephony.TelephonyManager;

import com.android.zcomponent.common.uiframe.FramewrokApplication;

/**
 * @package com.zte.android.common.constant
 * @filename GlobalConst.java
 * @description 全局常量类
 * 
 * @author ZhuJianWei
 * @date 2013-5-17
 */
public class GlobalConst
{

	/*************************** 全局开关变量 *********************************/

	/** 是否是第三方插件版本 */
	public final static boolean IS_PLUGIN_VERSION = false;

	/** 是否是UI演示版本 */
	public final static boolean IS_UI_DEMO_VERSION = false;

	/** 是否traceview性能测试 */
	public final static boolean IS_TRACEVIEW_PERFORMANCE_TEST = false;

	/** 是否性能测试 */
	public final static boolean IS_PERFORMANCE_TEST = true;

	/** 图片最大尺寸1MB */
	public final static int INT_MAX_BITMAP_FILE_SIZE = 1 * 1024 * 1024;

	/** 呼叫服务最大延迟时间 */
	public final static int INT_MAX_CALL_SERVICE_DELAY = 1000 * 30;

	/** 页面点击事件阻塞最大延迟时间 */
	public final static int INT_MAX_CLICK_DELAY = 2000;

	/** 页面点击事件阻塞最小延迟时间 */
	public final static int INT_MIN_CLICK_DELAY = 1000;
	
	public static final int INT_NUM_PAGE = 10;

	/** 配置文件位置 */
	public static final String STR_CONFIG_FILE = "/data/data/" + FramewrokApplication.PackageName + "/";

	public static final String STR_TARGET_FILE_NAME_INI = "config.ini";

	public static final String STR_CONFIG_COOKIE = "cookie";
	
	/** 系统错误信息日志文件地址 */
	public static final String STR_SYSTEM_EXCEPTION_FILE_PATH = "/Logs/";

	/** SD卡错误信息日志文件地址 */
	public static final String STR_SDCARD_EXCEPTION_FILE_PATH = "/JuZbao/Logs/";
	
	/**************************** 广播事件 ********************************/
	/** 来电广播 */
	public final static String BROADCAST_ACTION_PHONE_STATE_CHANGED =
			TelephonyManager.ACTION_PHONE_STATE_CHANGED;

	/** 心跳保活广播 */
	public final static String BROADCAST_ACTION_HEARTBEAT =
			"com.android.broadcast.action.heartbeat";

	/** 注销广播 */
	public final static String BROADCAST_ACTION_LOGOUT = "com.android.broadcast.action.logout";

	/** 广播参数字段 */
	public final static String BROADCAST_PARAM_FIELD = "BroadcastParamField";

	/** 注销广播参数值 - 注销 */
	public final static String LOGOUT_BROADCAST_PARAM_VALUE_LOGOUT = "Logout";

	/** 注销广播参数值 - 退出 */
	public final static String LOGOUT_BROADCAST_PARAM_VALUE_EXIT = "Exit";

}
