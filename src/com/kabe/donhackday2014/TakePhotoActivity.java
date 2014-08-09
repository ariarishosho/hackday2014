package com.kabe.donhackday2014;

import java.io.ByteArrayOutputStream;

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
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.kabe.donhackday2014.view.CameraWipeView;

public class TakePhotoActivity extends Activity {
	private Handler mHandler = new Handler();
	private ImageView mImageView;
	private Camera mCam;
	private CameraWipeView mCamView;
	private FrameLayout mFrame;
	private Bitmap mMaskBitmap;

	public static Bitmap resizeBitmapToDisplaySize(Activity activity, Bitmap src) {
		int srcWidth = src.getWidth(); // 元画像のwidth
		int srcHeight = src.getHeight(); // 元画像のheight
		Log.d(TAG, "srcWidth = " + String.valueOf(srcWidth)
				+ " px, srcHeight = " + String.valueOf(srcHeight) + " px");

		// 画面サイズを取得する
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
		// リサイズ
		Bitmap dst = Bitmap.createBitmap(src, 0, 0, srcWidth, srcHeight,
				matrix, true);
		int dstWidth = dst.getWidth(); // 変更後画像のwidth
		int dstHeight = dst.getHeight(); // 変更後画像のheight
		Log.d(TAG, "dstWidth = " + String.valueOf(dstWidth)
				+ " px, dstHeight = " + String.valueOf(dstHeight) + " px");
		src = null;
		return dst;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_takephoto);

		mCam = Camera.open();
		mCam.setDisplayOrientation(90);
		mCam.setPreviewCallback(mPreviewCallback);
		mCamView = new CameraWipeView(this, mCam);
		mFrame = (FrameLayout) findViewById(R.id.frame);
		mFrame.addView(mCamView);

		mImageView = (ImageView) findViewById(R.id.image);
		mMaskBitmap = resizeBitmapToDisplaySize(this,
				BitmapFactory.decodeResource(getResources(),
						R.drawable.face_mask));

	}

	private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			// 読み込む範囲
			int previewWidth = camera.getParameters().getPreviewSize().width;
			int previewHeight = camera.getParameters().getPreviewSize().height;
			// プレビューデータから Bitmap を生成
			Bitmap bmp = getBitmapImageFromYUV(data, previewWidth,
					previewHeight);
			maskAndSetImage(bmp, mMaskBitmap);

		}
	};

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
		Bitmap result = Bitmap.createBitmap(original.getWidth(),
				original.getHeight(), Bitmap.Config.ARGB_8888);
		// Bitmap result = Bitmap.createBitmap(mask.getWidth(),
		// mask.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas mCanvas = new Canvas(result);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		mCanvas.drawBitmap(original, 0, 0, null);
		mCanvas.drawBitmap(mask, 0, 0, paint);
		paint.setXfermode(null);
		mImageView.setImageBitmap(result);
		mImageView.setScaleType(ImageView.ScaleType.CENTER);
		mImageView.setBackgroundResource(R.drawable.ic_launcher);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

}