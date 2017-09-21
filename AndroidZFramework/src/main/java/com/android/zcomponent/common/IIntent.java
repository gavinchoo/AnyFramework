package com.android.zcomponent.common;

import android.content.Intent;
import android.os.Bundle;


public interface IIntent
{
	public void intentToActivity(Bundle bundle, Class<?> cls,
								 boolean inFromBottom);
			
	public void intentToActivity(Class<?> cls, boolean inFromBottom);
	
	public void intentToActivity(Bundle bundle, Class<?> cls);
	
	public void intentToActivity(Class<?> cls);
	
	public void intentToActivityForResult(Bundle bundle, Class<?> cls);
	
	public void intentToActivityForResult(Class<?> cls);
	
	public void startActivityBottom(Intent intent);
	
	public boolean isIntentFrom(Class<?> activity);
}
