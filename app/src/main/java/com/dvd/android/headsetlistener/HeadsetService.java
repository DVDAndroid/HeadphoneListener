package com.dvd.android.headsetlistener;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class HeadsetService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		IntentFilter receiverFilter = new IntentFilter(
				Intent.ACTION_HEADSET_PLUG);
		HeadsetReceiver receiver = new HeadsetReceiver();
		registerReceiver(receiver, receiverFilter);

		return START_STICKY;
	}

}