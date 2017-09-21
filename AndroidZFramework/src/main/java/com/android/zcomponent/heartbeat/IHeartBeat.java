
package com.android.zcomponent.heartbeat;

/**
 * <p>
 * Description: 发起3次脉搏网络Head请求，根据返回的的数据包，判断网络状态
 * </p>
 * 
 * @ClassName:IHeartBeat
 * @author: WEI
 * @date: 2015-9-28
 * 
 */
public abstract class IHeartBeat
{

	public enum HeartState
	{
		FASTER(3), NORMAL(2), SLOW(1), STOPED(0);

		HeartState(int value)
		{
			this.value = value;
		}

		public int value;

		public static HeartState getState(int value)
		{
			HeartState[] states = HeartState.values();
			for (int i = 0; i < states.length; i++)
			{
				if (states[i].value == value)
				{
					return states[i];
				}
			}
			
			return null;
		}
	}
	
	public class Heart
	{
		public int stateCode;
		
		public int rate;
	}

	public abstract void pulse();
	
	public abstract void pause();
	
	public abstract void destroy();
	
	public abstract HeartState heartRate();
	
}
