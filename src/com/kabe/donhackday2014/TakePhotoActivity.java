package com.kabe.donhackday2014;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.kabe.donhackday2014.view.CameraWipeView;

public class TakePhotoActivity extends Activity {
	private ImageView mImageView;
	private Camera mCam;
	private CameraWipeView mCamView;
	private FrameLayout mFrame;
	private Bitmap mMaskBitmap;
	private Bitmap mResultBitmap;
	private View mImageButton;

	public static Bitmap resizeBitmapToDisplaySize(Activity activity,
			Bitmap src, float toWidth, float toHeight) {
		int srcWidth = src.getWidth();
		int srcHeight = src.getHeight();
		Log.d(TAG, "srcWidth = " + String.valueOf(srcWidth)
				+ " px, srcHeight = " + String.valueOf(srcHeight) + " px");

		Matrix matrix = new Matrix();
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		float widthScale = toWidth / srcWidth;
		float heightScale = toHeight / srcHeight;
		Log.d(TAG, "widthScale = " + String.valueOf(widthScale)
				+ ", heightScale = " + String.valueOf(heightScale));
		if (widthScale > heightScale) {
			matrix.postScale(heightScale, heightScale);
		} else {
			matrix.postScale(widthScale, widthScale);
		}
		// Bitmap dst = Bitmap.createBitmap(src, 0, 0, (int)screenWidth,
		// (int)screenHeight,
		// matrix, true);
		Bitmap dst = Bitmap.createBitmap(src, 0, 0, srcWidth, srcHeight,
				matrix, true);
		int dstWidth = dst.getWidth();
		int dstHeight = dst.getHeight();
		Log.d(TAG, "dstWidth = " + String.valueOf(dstWidth)
				+ " px, dstHeight = " + String.valueOf(dstHeight) + " px");
		src = null;
		return dst;
	}

	public static Bitmap resizeBitmapToDisplaySize(Activity activity, Bitmap src) {
		int srcWidth = src.getWidth();
		int srcHeight = src.getHeight();
		Log.d(TAG, "srcWidth = " + String.valueOf(srcWidth)
				+ " px, srcHeight = " + String.valueOf(srcHeight) + " px");

		Matrix matrix = new Matrix();
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		float screenWidth = (float) metrics.widthPixels;
		float screenHeight = (float) metrics.heightPixels;
		Log.d(TAG, "screenWidth = " + String.valueOf(screenWidth)
				+ " px, screenHeight = " + String.valueOf(screenHeight) + " px");

		float widthScale = screenWidth / srcWidth;
		float heightScale = screenHeight / srcHeight;
		Log.d(TAG, "widthScale = " + String.valueOf(widthScale)
				+ ", heightScale = " + String.valueOf(heightScale));
		if (widthScale > heightScale) {
			matrix.postScale(heightScale, heightScale);
		} else {
			matrix.postScale(widthScale, widthScale);
		}
		// Bitmap dst = Bitmap.createBitmap(src, 0, 0, (int)screenWidth,
		// (int)screenHeight,
		// matrix, true);
		Bitmap dst = Bitmap.createBitmap(src, 0, 0, srcWidth, srcHeight,
				matrix, true);
		int dstWidth = dst.getWidth();
		int dstHeight = dst.getHeight();
		Log.d(TAG, "dstWidth = " + String.valueOf(dstWidth)
				+ " px, dstHeight = " + String.valueOf(dstHeight) + " px");
		src = null;
		return dst;
	}

	private View mRelativeLight;
	private View mRootView;

	private void setupCamera() {
		if (mCam != null) {
			mCam.release();
		}
		mCam = Camera.open(CameraInfo.CAMERA_FACING_BACK);
		mCam.setDisplayOrientation(90);
		mCamView.setCamera(mCam);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_takephoto);
		mRootView = findViewById(R.id.container);
		getActionBar().hide();
		mCamView = new CameraWipeView(this);
		setupCamera();
		// mCam.setPreviewCallback(mPreviewCallback);
		mFrame = (FrameLayout) findViewById(R.id.frame);
		mFrame.addView(mCamView);
		mRelativeLight = findViewById(R.id.relative_light);

		mImageView = (ImageView) findViewById(R.id.image);
		// mMaskBitmap = resizeBitmapToDisplaySize(this,
		// BitmapFactory.decodeResource(getResources(),
		// R.drawable.face_yoko_mask));

		// R.drawable.backlayer_mask));

		mImageButton = findViewById(R.id.imageButton);
		mImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				v.setEnabled(false);
				ObjectAnimator anim = ObjectAnimator.ofFloat(mRelativeLight,
						"alpha", 0f, 1f);
				anim.setDuration(1000);
				anim.start();
				playFromMediaPlayer(getPhotoShootSound(),
						mShootCompletionListener);
			}
		});

	}

	private int mPhotoNum = 0;

	private boolean updatePhotoNum() {
		mPhotoNum++;
		mPhotoNum %= 2;
		return mPhotoNum == 0;
	}

	private int getPhotoShootSound() {
		return mPhotoNum == 0 ? SOUND_SHOOT_1 : SOUND_SHOOT_2;
	}

	private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			int previewWidth = camera.getParameters().getPreviewSize().width;
			int previewHeight = camera.getParameters().getPreviewSize().height;
			Bitmap bmp = getBitmapImageFromYUV(data, previewWidth,
					previewHeight);
			bmp = rotateBitmap(bmp, 90);
			try {
				maskAndSetImage(bmp, mMaskBitmap);
			} catch (IOException e) {
				return;
			}

		}
	};

	public static Bitmap rotateBitmap(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
				source.getHeight(), matrix, true);
	}

	public static final String TAG = "TakePhotoActivity";

	public Bitmap getBitmapImageFromYUV(byte[] data, int width, int height) {
		YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, width, height,
				null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		yuvimage.compressToJpeg(new Rect(0, 0, width, height), 80, baos);
		byte[] jdata = baos.toByteArray();
		BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();
		bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
		Bitmap bmp = BitmapFactory.decodeByteArray(jdata, 0, jdata.length,
				bitmapFatoryOptions);
		return bmp;
	}

	private void maskAndSetImage(Bitmap original, Bitmap mask)
			throws IOException {
		mResultBitmap = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas mCanvas = new Canvas(mResultBitmap);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		mCanvas.drawBitmap(original, 0, 0, null);
		mCanvas.drawBitmap(mask, 0, 0, paint);
		paint.setXfermode(null);
		// mImageView.setImageBitmap(mResultBitmap);
		HackPhotoUtils.takePhoto(this, mResultBitmap, mPhotoNum);
	}

	//
	// private void maskAndSetImage(Bitmap original, Bitmap mask) {
	// mResultBitmap = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(),
	// Bitmap.Config.ARGB_8888);
	// // Bitmap result = Bitmap.createBitmap(mask.getWidth(),
	// // mask.getHeight(), Bitmap.Config.ARGB_8888);
	// Canvas mCanvas = new Canvas(mResultBitmap);
	// Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	// paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
	// mCanvas.drawBitmap(original, 0, 0, null);
	// mCanvas.drawBitmap(mask, 0, 0, paint);
	// paint.setXfermode(null);
	// mImageView.setImageBitmap(mResultBitmap);
	// // mImageView.setScaleType(ImageView.ScaleType.CENTER);
	// // mImageView.setBackgroundResource(R.drawable.ic_launcher);
	// }

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mCam.setPreviewCallback(null);
		mCam.release();
		stopMediaPlayer();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
			Bitmap bm = HackPhotoUtils.getHackPhoto();
			if (bm != null) {
				mImageView.setImageBitmap(bm);
			}
			mCam.reconnect();
		} catch (IOException e) {
		}
		playFromMediaPlayer(SOUND_READY, null);
	}

	private MediaPlayer mMediaPlayer;
	private final int SOUND_READY = R.raw.kincho;
	// private final int SOUND_SHOOT = R.raw.sukitteiinayo;
	// private final int SOUND_READY = R.raw.se_033a;
	private final int SOUND_SHOOT_1 = R.raw.herahera;
	private final int SOUND_SHOOT_2 = R.raw.tanoshimi;
	private final int SOUND_SHUTTER = R.raw.se_033a;

	/**
	 * 写真プレビュー表示時は機能しない
	 */
	private void playFromMediaPlayer(int sound, OnCompletionListener listener) {
		if (mMediaPlayer != null) {
			stopMediaPlayer();
		}
		mMediaPlayer = MediaPlayer.create(getApplicationContext(), sound);
		// mMediaPlayer.setLooping(true); // ループ設定
		mMediaPlayer.seekTo(0); // 再生位置を0ミリ秒に指定
		mMediaCompleted = true;
		if (listener != null) {
			mMediaPlayer.setOnCompletionListener(listener);
		}
		mMediaPlayer.start();
	}

	private void stopMediaPlayer() {
		mMediaPlayer.stop();
	}

	private boolean mMediaCompleted = false;

	private OnCompletionListener mShootCompletionListener = new OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
			mMediaCompleted = true;
			try {
				takePhoto2();
				// HackPhotoUtils.takePhoto(TakePhotoActivity.this,
				// mResultBitmap);
//				Toast.makeText(TakePhotoActivity.this, "撮影完了！",
//						Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				Toast.makeText(TakePhotoActivity.this, "ERROR!保存できない！！",
						Toast.LENGTH_SHORT).show();
			}

		}
	};

	private void takePhoto2() {
		mCam.takePicture(mShutterCallback, null, mPictureCallback);
	}

	private PictureCallback mPictureCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			if (data != null) {
				ObjectAnimator animator = ObjectAnimator.ofFloat(mRootView, "alpha", 0, 1);
				animator.setDuration(1000);
				animator.start();

				mResultBitmap = BitmapFactory.decodeByteArray(data, 0,
						data.length);
				Matrix m = new Matrix();
				m.postRotate(90);
				mResultBitmap = Bitmap.createBitmap(mResultBitmap, 0, 0,
						mResultBitmap.getWidth(), mResultBitmap.getHeight(), m,
						true);
				DisplayMetrics dm = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);
				int height = dm.heightPixels;
				int width = dm.widthPixels;
				mResultBitmap = Bitmap.createScaledBitmap(mResultBitmap, width,
						height, true);

				mMaskBitmap = Bitmap.createScaledBitmap(BitmapFactory
						.decodeResource(getResources(), R.drawable.mask_test),
						width, height, true);
				//
				// mMaskBitmap =
				// resizeBitmapToDisplaySize(TakePhotoActivity.this,
				// BitmapFactory.decodeResource(getResources(),
				// R.drawable.face_yoko_mask), mResultBitmap.getWidth(),
				// mResultBitmap.getHeight());

				try {
					maskAndSetImage(mResultBitmap, mMaskBitmap);
					mCam.startPreview();
					boolean isFinal = updatePhotoNum();
					if (!isFinal) {
						mHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								playFromMediaPlayer(getPhotoShootSound(),
										mShootCompletionListener);
							}
						}, 200);
					} else {
						Toast.makeText(TakePhotoActivity.this, "本日の壁ドン漫画が完成しました！", Toast.LENGTH_SHORT).show();
						animator = ObjectAnimator.ofFloat(mRootView, "alpha", 1, 0);
						animator.setDuration(1000);
						animator.addListener(new AnimatorListener() {
							
							@Override
							public void onAnimationStart(Animator animation) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onAnimationRepeat(Animator animation) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onAnimationEnd(Animator animation) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(getApplicationContext(), MainActivity.class);		
								intent.setAction(MainActivity.ACTION_COMPLETE_PHOTOSHOOT);
								startActivity(intent);
								finish();

							}
							
							@Override
							public void onAnimationCancel(Animator animation) {
								// TODO Auto-generated method stub
								
							}
						});
						animator.start();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	};

	private Handler mHandler = new Handler();

	private ShutterCallback mShutterCallback = new ShutterCallback() {
		@Override
		public void onShutter() {
			playFromMediaPlayer(SOUND_SHUTTER, null);
		}
	};

	private OnCompletionListener mCompletionListener = new OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
			mMediaCompleted = true;
		}
	};
}