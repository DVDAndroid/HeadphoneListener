package com.dvd.android.headsetlistener;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class HeadsetReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
			int state = intent.getIntExtra("state", -1);
			if (state == 1) {
				Intent playerIntent = new Intent(Intent.ACTION_MAIN);
				playerIntent.addCategory(Intent.CATEGORY_APP_MUSIC);
				playerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				try {
					context.startActivity(playerIntent);
				} catch (ActivityNotFoundException e) {
					Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT)
							.show();
				}
			}
		}
	}
}