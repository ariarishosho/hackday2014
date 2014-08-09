package com.kabe.donhackday2014.listener;

import com.kabe.donhackday2014Gesture.RotationGestureDetector;
import com.kabe.donhackday2014Gesture.RotationGestureListener;
import com.kabe.donhackday2014Gesture.TranslationGestureDetector;
import com.kabe.donhackday2014Gesture.TranslationGestureListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class DragViewListener implements OnTouchListener {

	// ドラッグ対象のView
	private ImageView dragView;
	// ドラッグ中に移動量を取得するための変数
	private int oldx;
	private int oldy;


	public DragViewListener(ImageView dragView) {
		this.dragView = dragView;

	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {

		// タッチしている位置取得
		int x = (int) event.getRawX();
		int y = (int) event.getRawY();

		int count = event.getPointerCount();
		for (int i = 0; i < count; i++) {
			if (i == event.getActionIndex()) {

				// はじめて画面にタッチした点の処理
			} else {
				// すでに画面にタッチしている点の処理
			}
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			// 今回イベントでのView移動先の位置
			int left = dragView.getLeft() + (x - oldx);
			int top = dragView.getTop() + (y - oldy);
			// Viewを移動する
			dragView.layout(left, top, left + dragView.getWidth(), top
					+ dragView.getHeight());
			break;
		}

		// 今回のタッチ位置を保持
		oldx = x;
		oldy = y;
		// イベント処理完了
		return true;
	}
}