package com.dvd.android.headphonelistener;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dvd.android.headphonelistener.services.HeadsetService;

public class SettingsActivity extends PreferenceActivity {

	private Intent mServiceIntent;

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

		mServiceIntent = new Intent(this, HeadsetService.class);
		startService(mServiceIntent);

		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			findPreference("version").setSummary(
					getString(R.string.version) + ":  " + pInfo.versionName
							+ " (" + pInfo.versionCode + ")");
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.start_service:
				startService(mServiceIntent);
				break;
			case R.id.stop_service:
				stopService(mServiceIntent);
				break;
			case R.id.restart_service:
				stopService(mServiceIntent);
				startService(mServiceIntent);
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		switch (preference.getKey()) {
			case "webpage":
			case "source_code":
				Intent browserIntent = new Intent(Intent.ACTION_VIEW,
						Uri.parse(preference.getSummary().toString()));
				startActivity(browserIntent);
				break;
			case "cancel_default":
				Intent intentSetting = new Intent();
				Intent intent = Intent.makeMainSelectorActivity(
						Intent.ACTION_MAIN, Intent.CATEGORY_APP_MUSIC);

				PackageManager pm = getPackageManager();
				ResolveInfo mInfo = pm.resolveActivity(intent, 0);

				String packageName = mInfo.activityInfo.applicationInfo.packageName;
				if (packageName.equals("android")) {
					Toast.makeText(this, R.string.no_problems,
							Toast.LENGTH_LONG).show();
					return false;
				} else {
					Toast.makeText(this, R.string.cancel_default_toast,
							Toast.LENGTH_LONG).show();
				}

				Uri uri = Uri.fromParts("package", packageName, null);

				intentSetting
						.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				intentSetting.setData(uri);
				startActivity(intentSetting);
				break;
		}

		return false;
	}
}
