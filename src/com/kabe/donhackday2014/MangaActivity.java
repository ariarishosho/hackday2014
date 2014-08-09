package com.kabe.donhackday2014;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import com.kabe.donhackday2014.listener.DragViewListener;

public class MangaActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_manga);
		// ドラッグ対象Viewとイベント処理クラスを紐付ける
		ImageView dragView = (ImageView) findViewById(R.id.imageView1);
		DragViewListener listener = new DragViewListener(dragView);
		dragView.setOnTouchListener(listener);
	}

}
