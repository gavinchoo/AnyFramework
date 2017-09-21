
package com.android.zcomponent.activity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Video;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.component.zframework.R;
import com.android.zcomponent.util.BitmapUtil;
import com.android.zcomponent.views.PreviewFrameLayout;

public class RecordActivity extends Activity
{

	public static String VIDEO_PATH = "video_path";

	public static String DURTION_LIMIT = "durtion_limit";

	/** 录制时间心跳 */
	private static final int RECORD_HEART = 1;

	/** 录制完成 */
	private static final int RECORD_COMPLETE = 2;

	private static final int START_PREVIEW = 3;

	public final static int VIDEO_CAPTURE = 109;

	private ImageButton btnComplete, btnStartScan, btnPlay;

	private TextView textViewTime, textViewTimeText;

	private PreviewFrameLayout mPreviewFrameLayout;
	
	private MediaRecorder mediaRecorder = null;

	private SurfaceView surfaceView = null;

	private ImageView mImgvewThumbnail;

	private Camera camera = null;

	private SurfaceHolder surfaceholder = null;

	private Timer timer = null;

	private TimerTask task = null;

	private int maxDurtion = 10;

	private int time;

	private File videoFile;

	private boolean isFocus;

	private CamcorderProfile mCamcorderProfile;
	
	private ContentValues mCurrentVideoValues;

	private Uri mCurrentVideoUri;

	private RecrodState mRecrodState = RecrodState.INIT;

	private enum RecrodState
	{
		INIT,

		START,

		STOP

	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_media_record);

		bindWidget();
		initSurfaceHolder();
		setClickListener();
	}

	private void bindWidget()
	{
		btnPlay = (ImageButton) findViewById(R.id.btn_play);
		surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		btnComplete = (ImageButton) findViewById(R.id.btnEnd);
		btnStartScan = (ImageButton) findViewById(R.id.btnRot);
		mImgvewThumbnail = (ImageView) findViewById(R.id.imgvew_thumbnail);
		textViewTimeText = (TextView) findViewById(R.id.textView);
		textViewTimeText.setTextColor(RecordActivity.this.getResources()
				.getColor(R.color.black));
		textViewTime = (TextView) findViewById(R.id.textViewTime);
		textViewTime.setTextColor(RecordActivity.this.getResources().getColor(
				R.color.black));
		surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		mPreviewFrameLayout = (PreviewFrameLayout) findViewById(R.id.preview_frame);
		
		isFocus = false;
		textViewTimeText.setVisibility(View.GONE);
		textViewTime.setVisibility(View.GONE);
		initCamcorderProfile();
		Intent intent = getIntent();
		maxDurtion = intent.getIntExtra(DURTION_LIMIT, 0);
		time = maxDurtion;
	}

	private void setClickListener()
	{
		btnStartScan.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (mRecrodState == RecrodState.INIT)
				{
					initRecrodViewState();
					createVideoFile();
					startTimer();
					startRecord();
				}
				else if (mRecrodState == RecrodState.START)
				{
					stopRecord();
				}
				else if (mRecrodState == RecrodState.STOP)
				{
					startTimer();
					startRecord();
				}
			}
		});

		btnComplete.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (mRecrodState == RecrodState.START)
				{
					btnComplete.setEnabled(false);
					
					if (mediaRecorder != null)
					{
						stopRecord();
						stopPreview();
					}
					btnComplete.setEnabled(true);
				}
				else if (mRecrodState == RecrodState.INIT)
				{
					setFinishBack();
				}
			}
		});

		btnPlay.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				play();
			}
		});
	}

	private void play()
	{
		Uri uri = getVideoFileUri();
		
		if (null != uri)
		{
			// 调用系统自带的播放器
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(uri, "video/mp4");
			startActivity(intent);
		}
	}
	
	private Uri getVideoFileUri()
	{
		Uri uri = null;
		if (null != mCurrentVideoUri)
		{
			uri = mCurrentVideoUri;
		}
		else
		{
			if (null != videoFile)
			{
				uri =
						loadVideoFrom(getContentResolver(),
								videoFile.getAbsolutePath());
			}
		}
		
		return uri;
	}

	public Uri loadVideoFrom(ContentResolver resolver, String path)
	{
		if (path != null)
		{
			path = Uri.decode(path);
			StringBuffer buff = new StringBuffer();
			buff.append("(").append(Video.VideoColumns.DATA)
					.append("=").append("'" + path + "'").append(")");
			Cursor cur =
					resolver.query(Video.Media.EXTERNAL_CONTENT_URI,
							new String[] { Video.VideoColumns._ID },
							buff.toString(), null, null);
			int index = 0;
			for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext())
			{
				index = cur.getColumnIndex(Video.VideoColumns._ID);
				// set _id value
				index = cur.getInt(index);
			}
			if (index == 0)
			{
				// do nothing
			}
			else
			{
				Uri uri_temp =
						Uri.parse("content://media/external/video/media/"
								+ index);
				return uri_temp;
			}
		}
		return null;
	}

	private Bitmap addVideoToMediaStore()
	{
		Uri videoTable = Uri.parse("content://media/external/video/media");
		mCurrentVideoValues.put(Video.Media.SIZE, videoFile.length());
		try
		{
			mCurrentVideoUri =
					getContentResolver()
							.insert(videoTable, mCurrentVideoValues);
			sendBroadcast(new Intent(Camera.ACTION_NEW_VIDEO,
					mCurrentVideoUri));
		}
		catch (Exception e)
		{
			// We failed to insert into the database. This can happen if
			// the SD card is unmounted.
			mCurrentVideoUri = null;
		}
		finally
		{
		}
		mCurrentVideoValues = null;
		
		if (null != videoFile)
		{
			Bitmap bitmap = createThumbnail(videoFile
					.getAbsolutePath());
			
			return BitmapUtil.rotateImage(bitmap, 90);
		}
		return null;
	}
	
	public class AddVideoToMediaStoreTask extends AsyncTask<Void, Void, Bitmap>
	{
		@Override
		protected void onPostExecute(Bitmap result)
		{
			super.onPostExecute(result);
			
			mImgvewThumbnail.setImageBitmap(result);
		    mImgvewThumbnail.setVisibility(View.VISIBLE);
		}

		@Override
		protected Bitmap doInBackground(Void... params)
		{
			return addVideoToMediaStore();
		}
	}
	
	private void initRecrodViewState()
	{
		textViewTime.setVisibility(View.VISIBLE);
		btnComplete.setVisibility(View.VISIBLE);
		textViewTimeText.setTextColor(RecordActivity.this.getResources()
				.getColor(R.color.white));

		textViewTime.setTextColor(RecordActivity.this.getResources().getColor(
				R.color.white));
	}

	private void initSurfaceHolder()
	{
		final SurfaceHolder holder2 = surfaceView.getHolder();
		holder2.addCallback(new Callback()
		{

			@Override
			public void surfaceDestroyed(SurfaceHolder holder)
			{
				if (camera != null)
				{
					camera.release();
					stopTimer();
					if (null == getVideoFileUri())
					{
						btnPlay.setVisibility(View.GONE);	
					}
				}
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder)
			{
				surfaceholder = holder;
				startPreview();
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height)
			{
				surfaceholder = holder2;
			}
		});

		/* 下面设置Surface不维护自己的缓冲区，而是等待屏幕的渲染引擎将内容推送到用户面前 */
		holder2.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder2.setKeepScreenOn(true);
		holder2.setFixedSize(getWindowManager().getDefaultDisplay().getWidth(),
				getWindowManager().getDefaultDisplay().getHeight());
		
	}

	private void initCamera(SurfaceHolder holder)
	{
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		int cameraCount = Camera.getNumberOfCameras();
		// get cameras number
		for (int camIdx = 0; camIdx < cameraCount; camIdx++)
		{
			Camera.getCameraInfo(camIdx, cameraInfo); // get
														// camerainfo
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
			{
				// 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
				try
				{
					camera = Camera.open(camIdx);
					Parameters parameters = camera.getParameters();// 实例化照相机参数
					parameters.setAutoExposureLock(false);// 自动曝光不锁定
					if (isFocus)
					{
						camera.autoFocus(null);
					}
					List<String> list = parameters.getSupportedFocusModes();
					if (list.contains(Parameters.FOCUS_MODE_AUTO))
					{
						isFocus = true;
						parameters
								.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
					}
					camera.setParameters(parameters);// 给获取的camera设定参数
				}
				catch (RuntimeException e)
				{
					e.printStackTrace();
				}
			}
		}

		camera.autoFocus(new AutoFocusCallback()
		{

			@Override
			public void onAutoFocus(boolean success, Camera camera)
			{
				if (success)
				{
					initCamera(surfaceholder);// 实现相机的参数初始化
					camera.cancelAutoFocus();// 只有加上了这一句，才会自动对焦。
				}
			}
		});

		int orientation = getDisplayOrientation(getDisplayRotation(this), 0);
		camera.cancelAutoFocus();
		camera.setDisplayOrientation(orientation);
		try
		{
			camera.setPreviewDisplay(holder);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public int getDisplayRotation(Activity activity)
	{
		int rotation =
				activity.getWindowManager().getDefaultDisplay().getRotation();
		switch (rotation)
		{
		case Surface.ROTATION_0:
			return 0;
		case Surface.ROTATION_90:
			return 90;
		case Surface.ROTATION_180:
			return 180;
		case Surface.ROTATION_270:
			return 270;
		}
		return 0;
	}

	public int getDisplayOrientation(int degrees, int cameraId)
	{
		// See android.hardware.Camera.setDisplayOrientation for
		// documentation.
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
		{
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		}
		else
		{ // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		return result;
	}

	Handler mHandler = new Handler()
	{

		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case RECORD_HEART:
			{
				if (time == 0)
				{
					Toast.makeText(RecordActivity.this, "录制时间到",
							Toast.LENGTH_SHORT).show();
					stopRecord();
					stopPreview();
					// mHandler.sendEmptyMessage(RECORD_COMPLETE);
				}
				textViewTime.setText(time + " 秒");
				break;
			}
			case RECORD_COMPLETE:
			{
				setFinishBack();
				break;
			}
			case START_PREVIEW:
			{
				startPreview();
				break;
			}
			default:
				break;
			}
		}
	};

	private void setFinishBack()
	{
		if (null != videoFile)
		{
			Intent intent = new Intent();
			intent.putExtra(VIDEO_PATH, videoFile.getAbsolutePath());
			setResult(RESULT_OK, intent);
		}
		
		finish();
	}

	public static Bitmap createThumbnail(String path)
	{
		Bitmap bitmap = null;
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		try
		{
			retriever.setDataSource(path);
			bitmap = retriever.getFrameAtTime(-1);
		}
		catch (IllegalArgumentException ex)
		{
			// Assume this is a corrupt video file
		}
		catch (RuntimeException ex)
		{
			// Assume this is a corrupt video file.
		}
		finally
		{
			try
			{
				retriever.release();
			}
			catch (RuntimeException ex)
			{
				// Ignore failures while cleaning up.
			}
		}

		return bitmap;
	}

	private void createVideoFile()
	{
		String path =
				Environment.getExternalStoragePublicDirectory(
						Environment.DIRECTORY_DCIM).toString()
						+ "/Camera/";

		long lTime = System.currentTimeMillis();// long now =
		// android.os.SystemClock.uptimeMillis();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		Date d1 = new Date(lTime);
		String sTime = format.format(d1);
		File path1 = new File(path);
		if (!path1.exists())
		{
			path1.mkdirs();
		}
		if (null != videoFile && videoFile.exists())
		{
			videoFile.delete();
		}
		videoFile = new File(path1, sTime + ".mp4");

		mCurrentVideoValues = new ContentValues(7);
		mCurrentVideoValues.put(Video.Media.TITLE, sTime);
		mCurrentVideoValues.put(Video.Media.DISPLAY_NAME, sTime + ".mp4");
		mCurrentVideoValues.put(Video.Media.DATE_TAKEN, lTime);
		mCurrentVideoValues.put(Video.Media.MIME_TYPE, "video/mp4");
		mCurrentVideoValues.put(Video.Media.DATA, videoFile.getAbsolutePath());
	}

	private void initMediaRecord()
	{
		if (null == camera)
		{
			initCamera(surfaceholder);
		}

		if (null == mediaRecorder)
		{
			mediaRecorder = new MediaRecorder();
		}
		camera.unlock();
		mediaRecorder.reset();
		// 设置录制视频源为Camera(相机)
		mediaRecorder.setCamera(camera);
		mediaRecorder.setPreviewDisplay(surfaceholder.getSurface());
		// 设置从麦克风采集声音(或来自录像机的声音AudioSource.CAMCORDER)
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		
		
		if (mCamcorderProfile != null)
		{
			mediaRecorder.setProfile(mCamcorderProfile);
			
			surfaceholder.setFixedSize(mCamcorderProfile.videoFrameWidth,
					mCamcorderProfile.videoFrameHeight);
		}

		mediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
		// 设置视频文件输出的路径
		mediaRecorder.setOutputFile(videoFile.getAbsolutePath());
	}
	
	private void initCamcorderProfile()
	{
		if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P))
		{
			mCamcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
		}
		else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P))
		{
			mCamcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
		}
		else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_1080P))
		{
			mCamcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_1080P);
		}
		else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_HIGH))
		{
			mCamcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
		}
		else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_LOW))
		{
			mCamcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
		}

		mPreviewFrameLayout.setAspectRatio((double) mCamcorderProfile.videoFrameHeight
				/ mCamcorderProfile.videoFrameWidth);
	}

	private void startRecord()
	{
		initMediaRecord();
		try
		{
			// 准备录制
			mediaRecorder.prepare();
			// 开始录制
			mediaRecorder.start();
		}
		catch (IllegalStateException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void stopRecord()
	{
		stopTimer();
		stopPreview();
		if (mediaRecorder != null)
		{
			mediaRecorder.stop();
			mediaRecorder.release();
			mediaRecorder = null;
		}
		
		new AddVideoToMediaStoreTask().execute();
	}

	private void stopPreview()
	{
		if (null != camera)
		{
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}

	private void startPreview()
	{
		initCamera(surfaceholder);

		Parameters params = camera.getParameters();
		params.setPictureFormat(PixelFormat.JPEG);
		camera.setDisplayOrientation(90);
		camera.setParameters(params);
		camera.startPreview();
	}

	@Override
	public void onBackPressed()
	{
		if (mRecrodState == RecrodState.START)
		{
			Toast.makeText(this, "请先结束视频摄像", Toast.LENGTH_SHORT).show();
		}
		else
		{
			if (null != videoFile)
			{
				videoFile.delete();
			}
			super.onBackPressed();
		}
	}

	public void startTimer()
	{
		if (timer == null)
		{
			timer = new Timer();
		}
		task = new TimerTask()
		{

			@Override
			public void run()
			{
				time--;
				mHandler.sendEmptyMessage(RECORD_HEART);
			}
		};
		timer.schedule(task, 1000, 1000);
		textViewTime.setText(time + " 秒");
		textViewTime.setVisibility(View.VISIBLE);
		textViewTimeText.setVisibility(View.VISIBLE);
		btnPlay.setVisibility(View.GONE);
		mImgvewThumbnail.setVisibility(View.GONE);
		btnStartScan.setBackgroundResource(R.drawable.record_btn_video_on);
		mRecrodState = RecrodState.START;
	}

	public void stopTimer()
	{
		if (timer != null)
		{
			time = maxDurtion;
			timer.cancel();
			task.cancel();
			timer = null;
			task = null;
		}

		btnPlay.setVisibility(View.VISIBLE);
		textViewTimeText.setVisibility(View.GONE);
		textViewTime.setVisibility(View.GONE);
		btnStartScan.setBackgroundResource(R.drawable.record_btn_video);
		mRecrodState = RecrodState.INIT;
	}

}
