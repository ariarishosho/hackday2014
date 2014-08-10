package com.kabe.donhackday2014;

import java.io.IOException;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

public class SlideMangaActivity extends Activity {
	private ImageView mImage1;
	private ImageView mImage2;
	private ImageView mImage3;
	private ImageView mImageLast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.activity_slide_manga);
		mImage1 = (ImageView) findViewById(R.id.image1);
		mImage2 = (ImageView) findViewById(R.id.image2);
		mImage3 = (ImageView) findViewById(R.id.image3);
		mImageLast = (ImageView) findViewById(R.id.imageLast);
		mAnimatorListener[0] = new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				mImage1.setVisibility(View.GONE);
				mImage2.setVisibility(View.VISIBLE);
				animateObject(mImage2, -100, 50, -50, 150, 1.6f, 1.6f, 1.3f,
						1.6f, 1, 1, 9000, mAnimatorListener[1]);
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}
		};
		mAnimatorListener[1] = new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				mImage2.setVisibility(View.GONE);
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						mImage3.setVisibility(View.VISIBLE);
						animateObject(mImage3, -0, -0, 0, 0, 2.4f, 1.45f, 2.4f,
								1.45f, 1, 1, 500, mAnimatorListener[2]);
					}
				}, 2000);
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}
		};
		mAnimatorListener[2] = new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						mImageLast.setVisibility(View.VISIBLE);
						animateObject(mImageLast, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1,
								800, null);
					}
				}, 4000);
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}
		};

		setupMangaImage();
	}

	private Handler mHandler = new Handler();

	private void setupMangaImage() {
		try {
			mImage1.setImageBitmap(HackPhotoUtils.getHackPhoto(10));
			mImage2.setImageBitmap(HackPhotoUtils.getHackPhoto(11));
			mImage3.setImageBitmap(HackPhotoUtils.getHackPhoto(12));
		} catch (IOException e) {

		}
	}

	private void runAnimation() {
		playFromMediaPlayer(SOUND_BGM, null);
		animateObject(mImage1, 0, 50, 0, 150, 1.3f, 1.5f, 1.3f, 1.5f, 1, 1,
				8000, mAnimatorListener[0]);
	}

	@Override
	protected void onResume() {
		super.onResume();
		runAnimation();
	}

	private AnimatorListener[] mAnimatorListener = new AnimatorListener[3];

	private void animateObject(View view, int fromScrollX, int toScrollX,
			int fromScrollY, int toScrollY, float fromScaleX, float toScaleX,
			float fromScaleY, float toScaleY, float fromAlpha, float toAlpha,
			int duration, AnimatorListener listener) {
		ObjectAnimator animScX = ObjectAnimator.ofInt(view, "scrollX",
				fromScrollX, toScrollX);
		ObjectAnimator animScY = ObjectAnimator.ofInt(view, "scrollY",
				fromScrollY, toScrollY);
		ObjectAnimator animSX = ObjectAnimator.ofFloat(view, "scaleX",
				fromScaleX, toScaleY);
		ObjectAnimator animSY = ObjectAnimator.ofFloat(view, "scaleY",
				fromScaleY, toScaleY);
		ObjectAnimator animAlpha = ObjectAnimator.ofFloat(view, "alpha",
				fromAlpha, toAlpha);
		animScX.setDuration(duration);
		animScY.setDuration(duration);
		animSX.setDuration(duration);
		animSY.setDuration(duration);
		animAlpha.setDuration(duration);
		animScX.start();
		animScY.start();
		animSX.start();
		animSY.start();
		animAlpha.start();
		if (listener != null) {
			animSY.addListener(listener);
		}
	}

	private final int SOUND_BGM = R.raw.sukitteiinayo;
	private MediaPlayer mMediaPlayer;
	private boolean mMediaCompleted;

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

}