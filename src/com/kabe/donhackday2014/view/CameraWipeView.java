package com.kabe.donhackday2014.view;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * TODO: document your custom view class.
 */
public class CameraWipeView extends SurfaceView implements
		SurfaceHolder.Callback {

	private Camera mCam;

	public CameraWipeView(Context context) {
		super(context);
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public CameraWipeView(Context context, Camera cam) {
		this(context);
		setCamera(cam);
	}

	public void setCamera(Camera cam) {
		mCam = cam;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.e(TAG, "SURFACE CREATED");
		try {
			mCam.setPreviewDisplay(holder);
			mCam.startPreview();
		} catch (IOException e) {
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.e(TAG, "SURFACE CHANGED");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
Log.e(TAG, "SURFACE DESTROYED");
	}
	
	private static final String TAG = "CameraWipeView";
}
