package com.kabe.donhackday2014;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.kabe.donhackday2014.Fragment0.MySurfaceView;

public class Fragment3 extends HackFragment {

	final static private String TAG = "GestureSample";
	private MySurfaceView mSurfaceView;
	Button button;

	public void saveResultBitmap() {
		mSurfaceView.saveBitmap();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = getActivity().getLayoutInflater().inflate(
				R.layout.fragment3, null);
		view.findViewById(R.id.imagePlay).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(getActivity(),
								SlideMangaActivity.class);
						intent.setAction(Intent.ACTION_VIEW);
						startActivity(intent);
						getActivity().finish();

					}
				});
		return view;
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

}
