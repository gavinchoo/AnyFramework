/**
 * <p>
 * Copyright: Copyright (c) 2012
 * Company: ZTE
 * Description:异常信息监听实现类，捕获异常信息并录入异常信息文件
 * </p>
 * @Title CrashHandler.java
 * @Package com.zte.iptvclient.android.common
 * @version 1.0
 * @author LiBingWu6005000224
 * @date 2012-2-27
 */
package com.android.zcomponent.common.uiframe;


import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Looper;


/** 
 * @ClassName:CrashHandler 
 * @Description: 异常捕获监听实现类
 * @author: LiBingWu6005000224
 * @date: 2012-2-27
 *  
 */

public class CrashHandler implements UncaughtExceptionHandler
{

    /** 异常日志文件路径 */
    private static String m_strErrorFilePath = null;

    /** 时间格式 */
    private final static String DATE_FORMATE = "yyyyMMdd_HHmm";

    /** 异常捕获监听 */
    private UncaughtExceptionHandler m_handlerDefault;

    /** CrashHandler实例 */
    private static CrashHandler m_instance = null;

    /** 上下文句柄 */
    private Context m_ctxHandler;

    /** 异常文件目录对象 */
    private File m_fileDir;

    /** 异常文件对象 */
    private File m_fileRecord;

    /**
     * @param strFilePaht 日志保存路径
     */
    private CrashHandler(String strFilePaht)
    {
        m_strErrorFilePath = strFilePaht;
    }
    
    /**
     * 
     * <p>
     * Description: 获取CrashHandler实例
     * <p>
     * @date 2012-2-27 
     * @return CrashHandler实例
     */
    public static CrashHandler getInstance(String strFilePaht)
    {
        if(null == m_instance)
        {
            m_instance = new CrashHandler(strFilePaht);
        }
        return m_instance;
    }

    /**
     * 
     * <p>
     * Description: 初始化方法
     * <p>
     * @date 2012-2-27 
     * @param context Context句柄
     */
    public void init(Context context)
    {
        if (null != context)
        {
            m_ctxHandler = context;
            m_handlerDefault = Thread.getDefaultUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler(this);

            // 创建异常文件目录
            m_fileDir = new File(m_strErrorFilePath);
            m_fileDir.mkdirs();
        }
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex)
    {
        // 若异常未处理或为空
        if (!handleException(ex) && null != m_handlerDefault)
        {
            m_handlerDefault.uncaughtException(thread, ex);
        }
        else
        {
            // 退出应用程序
            android.os.Process.killProcess(android.os.Process.myPid());
            FramewrokApplication.exit();
        }
    }

    /**
     * 
     * <p>
     * Description: 异常处理方法
     * <p>
     * @date 2012-2-27 
     * @param ex Throwable异常
     * @return 是否进行了处理
     */
    private boolean handleException(Throwable ex)
    {
        if (null == ex)
        {
            return false;
        }

        // Log打印异常信息
        ex.printStackTrace();
        // 输出流，用于异常信息文件写入
        DataOutputStream dos = null;

        try
        {
            // 获取时间Data对象实例
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMATE);
            // 获取日期格式，用于文件名
            String strFormatDate = simpleDateFormat.format(date);
            // 创建文件实例
            m_fileRecord = new File(m_strErrorFilePath.concat(strFormatDate).concat(
                ".txt"));
            // 创建异常信息存储文件
            if (m_fileRecord.createNewFile())
            {
                dos = new DataOutputStream(new FileOutputStream(m_fileRecord));
                ex.printStackTrace(new PrintStream(dos));
            }
        }
        catch (FileNotFoundException exFile)
        {
            exFile.printStackTrace();
        }
        catch (IOException exIO)
        {
            exIO.printStackTrace();
        }
        finally
        {
            // 关闭输出流
            if (null != dos)
            {
                try
                {
                    dos.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        // 线程Toast提示异常信息
        new Thread() {

            @Override
            public void run()
            {
                Looper.prepare();
                
                Looper.loop();
            }
        }.start();

        return true;
    }
}
