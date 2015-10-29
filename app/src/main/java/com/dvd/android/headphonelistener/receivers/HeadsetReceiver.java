package com.dvd.android.headphonelistener.receivers;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.widget.Toast;

import com.dvd.android.headphonelistener.R;

public class HeadsetReceiver extends BroadcastReceiver {

	private boolean showUINotification;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
			SharedPreferences prefs = context.getSharedPreferences(
					"com.dvd.android.headphonelistener_preferences",
					Context.MODE_PRIVATE);
			String packageName = prefs.getString("default_app_packageName",
					context.getString(R.string.none));
			showUINotification = prefs.getBoolean("notification_change", true);

			int enter_level = prefs.getInt("initial_volume", 15);
			int exit_level = prefs.getInt("exit_volume", 0);

			int state = intent.getIntExtra("state", -1);
			switch (state) {
				case 1:
					setLevelAudio(context, enter_level);
					if (prefs.getBoolean("choose_apps", true)) {
						startDefaultIntent(context);
					} else {
						if (isPackageInstalled(context, packageName)) {
							Intent launchIntent = context.getPackageManager()
									.getLaunchIntentForPackage(packageName);
							try {
								context.startActivity(launchIntent);
							} catch (ActivityNotFoundException e) {
								Toast.makeText(context,
										context.getString(R.string.error),
										Toast.LENGTH_LONG).show();
								startDefaultIntent(context);
							}
						} else {
							startDefaultIntent(context);
						}
					}
					break;
				case 0:
					setLevelAudio(context, exit_level);
			}
		}
	}

	private void setLevelAudio(Context context, int levelAudio) {
		int flags = showUINotification ? AudioManager.FLAG_SHOW_UI : 0;

		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, levelAudio,
				flags);
	}

	private void startDefaultIntent(Context context) {
		Intent playerIntent = new Intent(Intent.ACTION_MAIN);
		playerIntent.addCategory(Intent.CATEGORY_APP_MUSIC);
		playerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			context.startActivity(playerIntent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	private boolean isPackageInstalled(Context context, String packagename) {
		PackageManager pm = context.getPackageManager();
		try {
			pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
			return true;
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
	}
}