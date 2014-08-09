package com.kabe.donhackday2014;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;

import com.kabe.donhackday2014Gesture.RotationGestureDetector;
import com.kabe.donhackday2014Gesture.RotationGestureListener;
import com.kabe.donhackday2014Gesture.TranslationGestureDetector;
import com.kabe.donhackday2014Gesture.TranslationGestureListener;

public class MangaActivity extends Activity implements OnClickListener {

	final static private String TAG = "GestureSample";
	private MySurfaceView mSurfaceView;
	Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// LayoutParamsにセットするパラメータを準備
		final int FP = ViewGroup.LayoutParams.FILL_PARENT;
		final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
		// 　FrameLayoutを準備
		FrameLayout fl = new FrameLayout(this);
		setContentView(fl);

		mSurfaceView = new MySurfaceView(getApplicationContext());
		fl.addView(mSurfaceView, new ViewGroup.LayoutParams(WC, WC));

//		button = new Button(this);
//		button.setText("カメラ");
//		LayoutParams lp1 = new LayoutParams(LayoutParams.WRAP_CONTENT,
//				LayoutParams.WRAP_CONTENT);
//		button.setGravity(Gravity.LEFT);
//		fl.addView(button, lp1);
	}

	@Override
	public void onClick(View v) {
		if (v == button) {
			Intent intent = new Intent(getApplicationContext(),
					TakePhotoActivity.class);
			intent.setAction(Intent.ACTION_VIEW);
			startActivity(intent);
		}
	}

	public Bitmap resizeBitmapToDisplaySize(Bitmap src) {
		int srcWidth = src.getWidth(); // 元画像のwidth
		int srcHeight = src.getHeight(); // 元画像のheight
		Log.d(TAG, "srcWidth = " + String.valueOf(srcWidth)
				+ " px, srcHeight = " + String.valueOf(srcHeight) + " px");

		// 画面サイズを取得する
		Matrix matrix = new Matrix();
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
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

	class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback,
			View.OnTouchListener {
		private Bitmap mBitmap;
		private Bitmap mBackImage;
		private SurfaceHolder mHolder;
		private Matrix mMatrix;
		private Paint mPaint;
		private float mScale;
		private float mTranslateX, mTranslateY;
		private float mAngle;

		private RotationGestureDetector mRotationGestureDetector;
		private TranslationGestureDetector mTranslationGestureDetector;
		private ScaleGestureDetector mScaleGestureDetector;

		public MySurfaceView(Context context) {
			super(context);

			// 画像を読み込み( カメラ側で保存したもの)
			// AssetManager manager = getAssets();
			// InputStream is = null;
			try {
				// is = manager.open("test.png");
				// mBitmap = BitmapFactory.decodeStream(is);
				mBitmap =
						HackPhotoUtils.getHackPhoto();
				mBackImage = BitmapFactory.decodeResource(getResources(),
						R.drawable.backlayer);

			} catch (Exception e) {
			} finally {
				// try {
				// is.close();
				// } catch (IOException e) {
				// }
			}

			// ジェスチャー用の変数初期化
			mMatrix = new Matrix();
			mPaint = new Paint();
			mScale = 1.0f;

			mScaleGestureDetector = new ScaleGestureDetector(context,
					mOnScaleListener);
			mTranslationGestureDetector = new TranslationGestureDetector(
					mTranslationListener);
			mRotationGestureDetector = new RotationGestureDetector(
					mRotationListener);
			mBackImage = resizeBitmapToDisplaySize(mBackImage);
			getHolder().addCallback(this);
			setOnTouchListener(this);
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			mHolder = holder;

			mTranslateX = width / 2;
			mTranslateY = height / 2;

			present();
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
		}

		/**
		 * タッチ処理
		 */
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			mRotationGestureDetector.onTouchEvent(event);
			mTranslationGestureDetector.onTouch(event);
			mScaleGestureDetector.onTouchEvent(event);
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mPaint.setAlpha(100);
				break;
			case MotionEvent.ACTION_UP:
				mPaint.setAlpha(255);
				break;
			}

			present();
			return true;
		}

		/**
		 * 描画する。
		 */
		public void present() {
			Canvas canvas = mHolder.lockCanvas();

			mMatrix.reset();
			mMatrix.postScale(mScale, mScale);
			mMatrix.postTranslate(-mBitmap.getWidth() / 2 * mScale,
					-mBitmap.getHeight() / 2 * mScale);
			mMatrix.postRotate(mAngle);
			mMatrix.postTranslate(mTranslateX, mTranslateY);

			canvas.drawColor(Color.BLACK);
			canvas.drawBitmap(mBitmap, mMatrix, null);
			canvas.drawBitmap(mBackImage, 0, 0, mPaint);
			mHolder.unlockCanvasAndPost(canvas);
		}

		/**
		 * 拡大縮小処理
		 */
		private SimpleOnScaleGestureListener mOnScaleListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
			@Override
			public boolean onScaleBegin(ScaleGestureDetector detector) {
				Log.i(TAG, "scale begin");
				return super.onScaleBegin(detector);
			}

			@Override
			public void onScaleEnd(ScaleGestureDetector detector) {
				Log.i(TAG, "scale end");
				super.onScaleEnd(detector);
			}

			@Override
			public boolean onScale(ScaleGestureDetector detector) {
				mScale *= detector.getScaleFactor();
				return true;
			};
		};

		/**
		 * 移動処理
		 */
		private TranslationGestureListener mTranslationListener = new TranslationGestureListener() {
			@Override
			public void onTranslationEnd(TranslationGestureDetector detector) {
				Log.i(TAG, "translation end:" + detector.getX() + ","
						+ detector.getY());
			}

			@Override
			public void onTranslationBegin(TranslationGestureDetector detector) {
				Log.i(TAG, "translation begin:" + detector.getX() + ","
						+ detector.getY());
			}

			@Override
			public void onTranslation(TranslationGestureDetector detector) {
				mTranslateX += detector.getDeltaX();
				mTranslateY += detector.getDeltaY();
			}
		};

		/**
		 * 回転処理
		 */
		private RotationGestureListener mRotationListener = new RotationGestureListener() {
			@Override
			public void onRotation(RotationGestureDetector detector) {
				mAngle += detector.getDeltaAngle();
			}

			@Override
			public void onRotationBegin(RotationGestureDetector detector) {
				Log.i(TAG, "rotation begin");
			}

			@Override
			public void onRotationEnd(RotationGestureDetector detector) {
				Log.i(TAG, "rotation end");
			}
		};
	}

}
