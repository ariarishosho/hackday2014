package com.kabe.donhackday2014;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class PagerActivity extends FragmentActivity {

	ViewPager viewPager;
	FragmentStatePagerAdapter mPagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manga);
		viewPager = (ViewPager) findViewById(R.id.pager);

		/*
		 * viewPager.setOnPageChangeListener(new OnPageChangeListener() {
		 * 
		 * @Override public void onPageSelected(int arg0) { Log.d("AA",
		 * "AAAAOnPageSE:"+arg0); }
		 * 
		 * @Override public void onPageScrolled(int arg0, float arg1, int arg2)
		 * { Log.d("AA", "AAAAOnPageSC:"+arg0); Log.d("AA",
		 * "AAAAOnPageSC2:"+arg2); }
		 * 
		 * @Override public void onPageScrollStateChanged(int arg0) {
		 * Log.d("AA", "AAAAOnPageSCSC:"+arg0); } });
		 */
		mPagerAdapter = new MyFragmentStatePagerAdapter(
				getSupportFragmentManager());
		viewPager.setAdapter(mPagerAdapter);
	}

}
