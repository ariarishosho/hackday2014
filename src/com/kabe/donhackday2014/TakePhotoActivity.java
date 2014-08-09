package com.kabe.donhackday2014;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.app.Activity;
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
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.kabe.donhackday2014.view.CameraWipeView;

public class TakePhotoActivity extends Activity {
	private Handler mHandler = new Handler();
	private ImageView mImageView;
	private Camera mCam;
	private CameraWipeView mCamView;
	private FrameLayout mFrame;
	private Bitmap mMaskBitmap;
	private Bitmap mResultBitmap;
	private ImageButton mImageButton;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_takephoto);
		mCam = Camera.open(CameraInfo.CAMERA_FACING_BACK);
		mCam.setDisplayOrientation(90);
		mCam.setPreviewCallback(mPreviewCallback);
		mCamView = new CameraWipeView(this, mCam);
		mFrame = (FrameLayout) findViewById(R.id.frame);
		mFrame.addView(mCamView);

		mImageView = (ImageView) findViewById(R.id.image);
		mMaskBitmap = resizeBitmapToDisplaySize(this,
				BitmapFactory.decodeResource(getResources(),
				// R.drawable.face_mask));
						R.drawable.backlayer_mask));

		mImageButton = (ImageButton) findViewById(R.id.imageButton);
		mImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				try {
					HackPhotoUtils.takePhoto(TakePhotoActivity.this, mResultBitmap);
					Toast.makeText(TakePhotoActivity.this, "撮影完了！",
							Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					Toast.makeText(TakePhotoActivity.this, "保存できない！！",
							Toast.LENGTH_SHORT).show();

				}
			}
		});

	}

	private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			int previewWidth = camera.getParameters().getPreviewSize().width;
			int previewHeight = camera.getParameters().getPreviewSize().height;
			Bitmap bmp = getBitmapImageFromYUV(data, previewWidth,
					previewHeight);
			bmp = rotateBitmap(bmp, 90);
			maskAndSetImage(bmp, mMaskBitmap);

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

	private void maskAndSetImage(Bitmap original, Bitmap mask) {
		mResultBitmap = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(),
				Bitmap.Config.ARGB_8888);
		// Bitmap result = Bitmap.createBitmap(mask.getWidth(),
		// mask.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas mCanvas = new Canvas(mResultBitmap);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		mCanvas.drawBitmap(original, 0, 0, null);
		mCanvas.drawBitmap(mask, 0, 0, paint);
		paint.setXfermode(null);
		mImageView.setImageBitmap(mResultBitmap);
		// mImageView.setScaleType(ImageView.ScaleType.CENTER);
		mImageView.setBackgroundResource(R.drawable.ic_launcher);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mCam.setPreviewCallback(null);
		mCam.release();
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
	}

}