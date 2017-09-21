package com.android.zcomponent.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import android.content.res.AssetManager;

import com.android.zcomponent.constant.GlobalConst;


/**
 * <p>
 * Description: 配置信息读取
 * </p>
 * 1.程序启动时， 在Application类中初始化
 * 执行代码{SettingsBase.getInstance().setAssetManager(getAssets())}
 * 
 * @ClassName:SettingsBase
 * @author: WEI
 * @date: 2014-7-28
 * 
 */
public class SettingsBase
{

    public static SettingsBase m_instace;

    private SettingsBase()
    {

    }

    public static SettingsBase getInstance()
    {
        if (m_instace == null)
        {
            m_instace = new SettingsBase();
        }

        return m_instace;

    }

    /**
     * Create SettingsBase Object
     * 
     * @param assetMgr
     *            AssetManager Object
     */
    public void setAssetManager(AssetManager assetMgr)
    {
        mAssetMgr = assetMgr;
    }

    /**
     * initilize data
     * 
     * @param path
     *            File path
     * @param filename
     *            File Name
     * @return String
     */
    public String initilize(String path, String filename)
    {

        Properties properties = new Properties();
        InputStream inStream = null;
        OutputStream outStream = null;

        mConfigFile = new File(path);

        if (mConfigFile.exists())
        {
            return SettingsBase.OK;
        }
        else
        {
            try
            {
                if (mConfigFile.createNewFile())
                {
                    if (null != mAssetMgr)
                    {
                        inStream = mAssetMgr.open(mAssetConfigFileName);

                        properties.load(inStream);
                        inStream.close();

                        outStream = new FileOutputStream(mConfigFile);

                        properties.store(outStream, "");
                        outStream.close();

                        return SettingsBase.OK;
                    }
                    else
                    {
                        return SettingsBase.ERROR;
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return SettingsBase.ERROR;
        }
    }

    /**
     * 向properties里面写入key和value
     * 
     * @param keyname
     *            key Name
     * @param value
     *            key Value
     */
    public void writeStringByKey(String keyname, String value)
    {

        if (keyname == null || value == null)
        {
            return;
        }
        Properties properties = new Properties();
        InputStream is = null;
        OutputStream os = null;
        if (initilize(mConfigFilePath, mAssetConfigFileName).equals(SettingsBase.OK))
        {
            try
            {
                is = (InputStream) (new FileInputStream(mConfigFile));
                properties.load(is);
                is.close();

                properties.setProperty(keyname, value);
                os = (OutputStream) (new FileOutputStream(mConfigFile));
                properties.store(os, "");
                os.close();

            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            return;
        }
    }
    
    /**
     * 向properties里面写入key和value
     * 
     * @param keyname
     *            key Name
     * @param value
     *            key Value
     */
    public void addStringByKey(String keyname, String value)
    {

        if (keyname == null || value == null)
        {
            return;
        }
        Properties properties = new Properties();
        InputStream is = null;
        OutputStream os = null;
        if (initilize(mConfigFilePath, mAssetConfigFileName).equals(SettingsBase.OK))
        {
            try
            {
                is = (InputStream) (new FileInputStream(mConfigFile));
                properties.load(is);
                is.close();

                if (!properties.containsKey(keyname))
                {
                	properties.put(keyname, value);	
                }
                os = (OutputStream) (new FileOutputStream(mConfigFile));
                properties.store(os, "");
                os.close();

            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            return;
        }
    }

    /**
     * 读取properties配置文件的key和value
     * 
     * @param keyname
     *            Key name
     * @return String
     */
    public String readStringByKey(String keyname)
    {

        Properties properties = new Properties();
        String value = "";
        if (initilize(mConfigFilePath, mAssetConfigFileName).equals(SettingsBase.OK))
        {
            try
            {
                InputStream stream = new FileInputStream(mConfigFile);
                properties.load(stream);
                stream.close();
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            value = properties.getProperty(keyname);
            if (value == null)
            {
                value = "";
            }
            return value;
        }
        else
        {
            return value;
        }
    }

    public static final String OK = "settingsbase.ok";

    public static final String ERROR = "settingsbase.err";

    private AssetManager mAssetMgr = null;

    private static final String mConfigFilePath = GlobalConst.STR_CONFIG_FILE
        + GlobalConst.STR_TARGET_FILE_NAME_INI;

    private static final String mAssetConfigFileName = GlobalConst.STR_TARGET_FILE_NAME_INI;

    private File mConfigFile;
}
