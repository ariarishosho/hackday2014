package com.kabe.donhackday2014;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MyFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

	public MyFragmentStatePagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int i) {

		switch (i) {
		case 0:
			return new Fragment0();
		case 1:
			return new Fragment1();
		case 2:
			return new Fragment2();
		default:
			return new Fragment3();
		}

	}

	@Override
	public int getCount() {
		return 4;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return "Page " + position + 1;
	}

}
