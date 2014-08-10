package com.kabe.donhackday2014;

import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class SlideMangaActivity extends Activity {
	private View mImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.activity_slide_manga);
		mImage = findViewById(R.id.image);
	}

	@Override
	protected void onResume() {
		super.onResume();
		animateObject(mImage, 0, 50, 0, 150, 1.3f, 1.6f, 1.3f, 1.6f, 5000, null);
	}

	private void animateObject(View view, int fromScrollX, int toScrollX,
			int fromScrollY, int toScrollY, float fromScaleX, float toScaleX,
			float fromScaleY, float toScaleY, int duration,
			AnimatorListener listener) {
		ObjectAnimator animScX = ObjectAnimator.ofInt(mImage, "scrollX",
				fromScrollX, toScrollX);
		ObjectAnimator animScY = ObjectAnimator.ofInt(mImage, "scrollY",
				fromScrollY, toScrollY);
		ObjectAnimator animSX = ObjectAnimator.ofFloat(mImage, "scaleX",
				fromScaleX, toScaleY);
		ObjectAnimator animSY = ObjectAnimator.ofFloat(mImage, "scaleY",
				fromScaleY, toScaleY);
		animScX.setDuration(duration);
		animScY.setDuration(duration);
		animSX.setDuration(duration);
		animSY.setDuration(duration);
		animScX.start();
		animScY.start();
		animSX.start();
		animSY.start();
		if (listener != null) {
			animSY.addListener(listener);
		}
	}
}