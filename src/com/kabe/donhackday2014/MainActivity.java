package com.kabe.donhackday2014;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class MainActivity extends Activity implements OnClickListener {
	public static final String ACTION_COMPLETE_PHOTOSHOOT = "COMPLETE_PHOTOSHOOT";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().hide();
		if (savedInstanceState == null) {
//			getFragmentManager().beginTransaction()
//					.add(R.id.container, new PlaceholderFragment()).commit();
		}

		// buttonを取得
		findViewById(R.id.imageCamera).setOnClickListener(this);
		findViewById(R.id.linearManga).setOnClickListener(this);

		// DEBUG用 本番のときは消しましょう！
		findViewById(R.id.linearTodayKabedon).setVisibility(View.VISIBLE);

	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent != null && intent.getAction() == ACTION_COMPLETE_PHOTOSHOOT) {
			findViewById(R.id.linearTodayKabedon).setVisibility(View.VISIBLE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.imageCamera:
			Intent intent = new Intent(getApplicationContext(),TakePhotoActivity.class);		
			intent.setAction(Intent.ACTION_VIEW);
			startActivity(intent);
			break;
		case R.id.linearManga:
			intent = new Intent(getApplicationContext(), PagerActivity.class);		
			intent.setAction(Intent.ACTION_VIEW);
			startActivity(intent);
			break;
		}
	}

}
