package com.kabe.donhackday2014;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;

public class HackPhotoUtils {
	public static Bitmap getHackPhoto() throws IOException {
		return getHackPhoto(0);
	}

	public static Bitmap getHackPhoto(int number) throws IOException {
		final String fileName = "HACK" + number + ".png";
		final String SAVE_DIR = "/MyPhoto/";
		File file = new File(Environment.getExternalStorageDirectory()
				.getPath() + SAVE_DIR + fileName);
		try {
			if (!file.exists()) {
				return null;
			}
		} catch (SecurityException e) {
			e.printStackTrace();
			throw e;
		}

		try {
			FileInputStream is = new FileInputStream(file);
			Bitmap b = BitmapFactory.decodeStream(is);
			is.close();
			return b;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}

	}

	public static void takePhoto(Context context, Bitmap bm) throws IOException {
		takePhoto(context, bm, 0);
	}

	public static void takePhoto(Context context, Bitmap bm, int number)
			throws IOException {
		// DEMO用
//		if (true){
//			return;
//		}

		final String SAVE_DIR = "/MyPhoto/";
		File file = new File(Environment.getExternalStorageDirectory()
				.getPath() + SAVE_DIR);
		try {
			if (!file.exists()) {
				file.mkdir();
			}
		} catch (SecurityException e) {
			e.printStackTrace();
			throw e;
		}

		// Date mDate = new Date();
		// SimpleDateFormat fileNameDate = new
		// SimpleDateFormat("yyyyMMdd_HHmmss");
		final String fileName = "HACK" + number + ".png";
		// String fileName = fileNameDate.format(mDate) + ".png";
		String AttachName = file.getAbsolutePath() + "/" + fileName;

		try {
			FileOutputStream out = new FileOutputStream(AttachName);
			bm.compress(CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}

		// save index
		ContentValues values = new ContentValues();
		ContentResolver contentResolver = context.getContentResolver();
		values.put(MediaColumns.MIME_TYPE, "image/png");
		values.put(MediaColumns.TITLE, fileName);
		values.put("_data", AttachName);
		contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				values);
	}

}