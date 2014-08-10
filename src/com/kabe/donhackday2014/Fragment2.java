package com.kabe.donhackday2014;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kabe.donhackday2014.Fragment0.MySurfaceView;
import com.kabe.donhackday2014Gesture.RotationGestureDetector;
import com.kabe.donhackday2014Gesture.RotationGestureListener;
import com.kabe.donhackday2014Gesture.TranslationGestureDetector;
import com.kabe.donhackday2014Gesture.TranslationGestureListener;

public class Fragment2 extends HackFragment {

	final static private String TAG = "GestureSample";
	private MySurfaceView mSurfaceView;
	Button button;
	
	public void saveResultBitmap() {
		mSurfaceView.saveBitmap();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mSurfaceView = new MySurfaceView(getActivity().getApplicationContext());
		return mSurfaceView;
	}

	public Bitmap resizeBitmapToDisplaySize(Bitmap src) {
		int srcWidth = src.getWidth(); // 元画像のwidth
		int srcHeight = src.getHeight(); // 元画像のheight
		Log.d(TAG, "srcWidth = " + String.valueOf(srcWidth)
				+ " px, srcHeight = " + String.valueOf(srcHeight) + " px");

		// 画面サイズを取得する
		Matrix matrix = new Matrix();
		DisplayMetrics metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);
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
		private Bitmap mEditImage;
		private Bitmap mResultImage;
		private SurfaceHolder mHolder;
		private Matrix mMatrix;
		private Paint mPaint;
		private Paint mEditPaint;
		private float mScale;
		private float mTranslateX, mTranslateY;
		private float mAngle;

		private RotationGestureDetector mRotationGestureDetector;
		private TranslationGestureDetector mTranslationGestureDetector;
		private ScaleGestureDetector mScaleGestureDetector;
		
		public void saveBitmap() {
			try {
				HackPhotoUtils.takePhoto(getContext(), mResultImage, 12);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public MySurfaceView(Context context) {
			super(context);

			// 画像を読み込み( カメラ側で保存したもの)
			// AssetManager manager = getAssets();
			// InputStream is = null;
			try {
				// is = manager.open("test.png");
				// mBitmap = BitmapFactory.decodeStream(is);
				mBitmap = HackPhotoUtils.getHackPhoto(1);
				mBackImage = BitmapFactory.decodeResource(getResources(),
						R.drawable.majichu03);
				mEditImage = BitmapFactory.decodeResource(getResources(),
						R.drawable.edit);
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
			mEditPaint = new Paint();
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
			mTranslateX = 345;
			mTranslateY = 593;
			present();
			saveBitmap();
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
		}

		boolean edit = false;

		/**
		 * タッチ処理
		 */
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getX() < 200 && event.getY() < 100) {
				if (edit) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_UP:
						edit = false;
						mEditPaint.setAlpha(255);
						break;
					}
				} else {
					switch (event.getAction()) {
					case MotionEvent.ACTION_UP:
						edit = true;
						mEditPaint.setAlpha(80);
						break;
					}
				}
			} else if (edit) {
				mRotationGestureDetector.onTouchEvent(event);
				mTranslationGestureDetector.onTouch(event);
				mScaleGestureDetector.onTouchEvent(event);
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mPaint.setAlpha(100);
					break;
				case MotionEvent.ACTION_UP:
					mPaint.setAlpha(255);
					saveBitmap();
					break;

				}
			}
			present();
			if (edit) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					saveBitmap();
					break;
				}

			}

			return true;
		}

		/**
		 * 描画する。
		 */
		public void present() {
			Canvas canvas = mHolder.lockCanvas();
			
			mResultImage = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
			Canvas canvasResult = new Canvas(mResultImage);

			mMatrix.reset();
			mMatrix.postScale(mScale, mScale);
			mMatrix.postTranslate(-mBitmap.getWidth() / 2 * mScale,
					-mBitmap.getHeight() / 2 * mScale);
			mMatrix.postRotate(mAngle);
			mMatrix.postTranslate(mTranslateX, mTranslateY);
			
			canvasResult.drawColor(Color.BLACK);
			canvasResult.drawBitmap(mBitmap, mMatrix, null);
			canvasResult.drawBitmap(mBackImage, 0, 0, mPaint);

/*
			canvas.drawColor(Color.BLACK);
			canvas.drawBitmap(mBitmap, mMatrix, null);
			canvas.drawBitmap(mBackImage, 0, 0, mPaint);

			canvas.drawBitmap(mEditImage, 0, 0, mEditPaint);
			*/
			canvas.drawBitmap(mResultImage, 0, 0, null);
			canvas.drawBitmap(mEditImage, 0, 0, mEditPaint);

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
