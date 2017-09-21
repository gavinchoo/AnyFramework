package com.android.zcomponent.util.update.res;

import java.io.File;


public interface IResUpdate
{
	
	public File searchFile(String keyword);
	
    public void updateFile(String newResUrl, String fileName);
    
    public void deleteAll();
    
    public void delete(String fileName);
}
