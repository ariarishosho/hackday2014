package com.kabe.donhackday2014;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class PagerActivity extends FragmentActivity {

	  ViewPager viewPager;

	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_manga);
	    viewPager = (ViewPager) findViewById(R.id.pager);
	    viewPager.setAdapter(
	      new MyFragmentStatePagerAdapter(
	        getSupportFragmentManager()));
	  }

	}
