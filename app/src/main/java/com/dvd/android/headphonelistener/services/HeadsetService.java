package com.dvd.android.headphonelistener.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;

import com.dvd.android.headphonelistener.R;
import com.dvd.android.headphonelistener.SettingsActivity;
import com.dvd.android.headphonelistener.receivers.HeadsetReceiver;

public class HeadsetService extends Service {

	private int NOTIFICATION_ID = 22;

	@Override
	public void onCreate() {
		super.onCreate();
		createNotification(true);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		createNotification(false);
	}

	private void createNotification(boolean created) {
		int id = created ? R.string.service_started : R.string.service_stopped;

		final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(22);

		Intent notificationIntent = new Intent(this, SettingsActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		Notification notification = new Notification.Builder(this)
				.setContentTitle(getString(R.string.app_name))
				.setContentText(getString(id)).setAutoCancel(true)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setContentIntent(pendingIntent)
				.setWhen(System.currentTimeMillis()).build();

		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(NOTIFICATION_ID, notification);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				notificationManager.cancel(NOTIFICATION_ID);
			}
		}, 10000);
	}

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