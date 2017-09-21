
package com.android.zcomponent.activity;

import java.io.IOException;
import java.util.Map;
import java.util.Vector;

import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.component.zframework.R;
import com.android.zcomponent.qccode.IDecodeResult;
import com.android.zcomponent.qccode.ViewfinderView;
import com.android.zcomponent.qccode.camera.CameraManager;
import com.android.zcomponent.qccode.utils.AmbientLightManager;
import com.android.zcomponent.qccode.utils.BeepManager;
import com.android.zcomponent.qccode.utils.CaptureActivityHandler;
import com.android.zcomponent.qccode.utils.InactivityTimer;
import com.android.zcomponent.views.swipebacklayout.lib.app.SwipeBackActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;

/**
 * 
 * 二维码扫描类
 * 
 * @ClassName:BarcodeActivity
 * @Description: 二维码扫描
 * @author:yyb
 * @date: 2012-11-30
 * 
 */
public class ComponentBarcodeActivity extends SwipeBackActivity implements
		SurfaceHolder.Callback, IDecodeResult
{

	private final static String TAG = "ComponentBarcodeActivity";

	/** 解码结果字符串 */
	// private static String ResultText;
	/** 条码位图 */
	public static Bitmap Barcode;

	/** 取景器控件 */
	private ViewfinderView mViewfinderView;

	/** 捕获成功后定时退出Activity的计时器 */
	private InactivityTimer inactivityTimer;

	/** 蜂鸣器 */
	private BeepManager beepManager;

	/** 感光灯 */
	private AmbientLightManager mAmbientLightManager;

	/** 捕获照片消息处理Handler */
	private CaptureActivityHandler handler = null;

	/** SurfaceHolder是否已创建 */
	private boolean hasSurface = false;

	/** 字符编码格式 */
	private String characterSet = "UTF-8";

	/** 解码格式数组 */
	private Vector<BarcodeFormat> decodeFormats;

	private Map<DecodeHintType, ?> decodeHints;

	private ImageView mimgvewLight;

	private ImageView mimgvewBack;

	private TextView mtvewWarn;

	private LinearLayout mllayoutLightSwitch;

	private boolean isFlashLightOpen = false;

	private Camera m_Camera;

	public Handler getCaptureHandler()
	{
		return handler;
	}

	public ViewfinderView getViewfinderView()
	{
		return mViewfinderView;
	}

	public void drawViewfinder()
	{
		mViewfinderView.drawViewfinder();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height)
	{
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		if (!hasSurface)
		{
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		hasSurface = false;
	}

	/**
	 * onCreate
	 * <p>
	 * Description: activity第一次启动时调用
	 * <p>
	 * 
	 * @date 2012-11-30
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_barcode_main);
		mViewfinderView =
				(ViewfinderView) findViewById(R.id.barcodemain_viewfinder);
		mtvewWarn = (TextView) findViewById(R.id.barcode_warn);
		mimgvewLight = (ImageView) findViewById(R.id.barcode_icon_light);
		mllayoutLightSwitch =
				(LinearLayout) findViewById(R.id.barcode_llayout_icon_light);
		mimgvewBack = (ImageView) findViewById(R.id.barcode_imgvew_back);
		mimgvewLight.setBackgroundResource(R.drawable.qrcode_icon_light_close);
		mimgvewBack.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				finish();
			}
		});
		mimgvewLight.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				openOrCloseLight();
			}
		});
		mllayoutLightSwitch.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				openOrCloseLight();
			}
		});
		CameraManager.init(getApplication());
		inactivityTimer = new InactivityTimer(this);
		beepManager = new BeepManager(this);
		mAmbientLightManager = new AmbientLightManager(this);
	}

	public void setBarcodeWarnTitle(String msg)
	{
		mtvewWarn.setText(msg);
	}

	public void setBarcodeWarnTitle(int msgId)
	{
		mtvewWarn.setText(getResources().getString(msgId));
	}

	private void openOrCloseLight()
	{
		isFlashLightOpen = !isFlashLightOpen;
		if (isFlashLightOpen)
		{
			PackageManager pm = this.getPackageManager();
			if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
			{
				openLight();
			}
			else
			{
				FeatureInfo[] features = pm.getSystemAvailableFeatures();
				for (FeatureInfo f : features)
				{
					if (PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)) // 判断设备是否支持闪光灯
					{
						openLight();
					}
				}
			}
		}
		else
		{
			closeLight();
		}
	}

	private void openLight()
	{
		if (CameraManager.get() != null)
		{
			CameraManager.get().setTorch(true);
			mimgvewLight
					.setBackgroundResource(R.drawable.qrcode_icon_light_open);
			isFlashLightOpen = true;
		}
	}

	private void closeLight()
	{
		if (null != CameraManager.get())
		{
			CameraManager.get().setTorch(false);
			mimgvewLight
					.setBackgroundResource(R.drawable.qrcode_icon_light_close);
			isFlashLightOpen = false;
		}
	}

	/**
	 * onDestroy
	 * <p>
	 * Description: 销毁
	 * <p>
	 * 
	 * @date 2012-11-30
	 */
	@Override
	protected void onDestroy()
	{
		if (handler != null)
		{
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.shutdown();
		CameraManager.get().closeDriver();
		super.onDestroy();
	}

	/**
	 * onPause
	 * <p>
	 * Description: 暂停
	 * <p>
	 * 
	 * @date 2012-11-30
	 */
	@Override
	protected void onPause()
	{
		super.onPause();
		if (handler != null)
		{
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		CameraManager.get().closeDriver();

		if (handler != null)
		{
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		mAmbientLightManager.stop();
		beepManager.close();
		CameraManager.get().closeDriver();
		if (!hasSurface)
		{
			SurfaceView surfaceView =
					(SurfaceView) findViewById(R.id.barcodemain_surface_preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
	}

	/**
	 * onResume
	 * <p>
	 * Description: 恢复
	 * <p>
	 * 
	 * @date 2012-11-30
	 */
	@Override
	protected void onResume()
	{
		super.onResume();
		SurfaceView surfaceView =
				(SurfaceView) findViewById(R.id.barcodemain_surface_preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface)
		{
			initCamera(surfaceHolder);
		}
		else
		{
			surfaceHolder.addCallback(this);
		}
		decodeFormats = null;
		mAmbientLightManager.start(CameraManager.get());
		beepManager.updatePrefs();
		inactivityTimer.onResume();
	}

	/**
	 * 
	 * initCamera
	 * <p>
	 * Description: 初始化摄像头
	 * <p>
	 * 
	 * @date 2012-11-30
	 * @param surfaceHolder
	 */
	private void initCamera(SurfaceHolder surfaceHolder)
	{
		if (surfaceHolder == null)
		{
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (CameraManager.get().isOpen())
		{
			Log.w(TAG,
					"initCamera() while already open -- late SurfaceView callback?");
			return;
		}
		try
		{
			CameraManager.get().openDriver(surfaceHolder);
			if (null == handler)
			{
				handler =
						new CaptureActivityHandler(this, mViewfinderView,
								decodeFormats, decodeHints, characterSet, this);
			}
		}
		catch (IOException ioe)
		{
			displayFrameworkBugMessageAndExit();
		}
		catch (RuntimeException e)
		{
			displayFrameworkBugMessageAndExit();
		}
		catch (Exception e)
		{
			displayFrameworkBugMessageAndExit();
		}
	}

	/**
	 * displayFrameworkBugMessageAndExit 报错退出
	 */
	private void displayFrameworkBugMessageAndExit()
	{
		finish();
	}

	/**
	 * 
	 * OpenQcodeResult
	 * <p>
	 * Description:进入二维码扫描结果窗口
	 * <p>
	 * 
	 * @date 2012-11-30
	 * @param _ResultText
	 */
	public void OpenQcodeResult(String _ResultText)
	{
	}

	private boolean isScanAble = true;

	public void startScan()
	{
		isScanAble = true;
		if (null != getCaptureHandler())
		{
			getCaptureHandler().sendEmptyMessageDelayed(
					R.id.qccode_restart_preview, 1000);
		}
	}

	public void stopScan()
	{
		isScanAble = false;
	}

	@Override
	public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor)
	{
		if (!isScanAble)
		{
			return;
		}
		inactivityTimer.onActivity();
		beepManager.playBeepSoundAndVibrate();
		Barcode = barcode;
		OpenQcodeResult(rawResult.getText());
		if (null != getCaptureHandler())
		{
			getCaptureHandler().sendEmptyMessageDelayed(
					R.id.qccode_restart_preview, 1000);
		}
	}
}
