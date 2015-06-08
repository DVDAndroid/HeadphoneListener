package com.dvd.android.headphonelistener;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.dvd.android.headphonelistener.services.HeadsetService;

public class SettingsActivity extends PreferenceActivity {

	@Override
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);

		int actionBarColor = Color.parseColor("#FF9800");
		int statusBarColor = Color.parseColor("#F57C00");

		assert getActionBar() != null;
		getActionBar().setBackgroundDrawable(new ColorDrawable(actionBarColor));

		if (Build.VERSION.SDK_INT >= 21)
			getWindow().setStatusBarColor(statusBarColor);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent service = new Intent(this, HeadsetService.class);
		switch (item.getItemId()) {
			case R.id.start_service:
				startService(service);
				break;
			case R.id.stop_service:
				stopService(service);
				break;
			case R.id.restart_service:
				stopService(service);
				startService(service);
				break;
		}

		return super.onOptionsItemSelected(item);
	}
}
