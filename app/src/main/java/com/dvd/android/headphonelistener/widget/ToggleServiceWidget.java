package com.dvd.android.headphonelistener.widget;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.dvd.android.headphonelistener.R;
import com.dvd.android.headphonelistener.services.HeadsetService;

public class ToggleServiceWidget extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Class classService = HeadsetService.class;
		Intent intentService = new Intent(context, classService);

		for (int appWidgetId : appWidgetIds) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
					R.layout.toggle_service_widget);
			if (isMyServiceRunning(context, classService)) {
				context.stopService(intentService);
				remoteViews.setImageViewResource(R.id.imageViewWidget,
						R.drawable.service_disabled);
			} else {
				context.startService(intentService);
				remoteViews.setImageViewResource(R.id.imageViewWidget,
						R.drawable.service_enabled);
			}

			Intent intent = new Intent(context, ToggleServiceWidget.class);

			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

			PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
					0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.imageViewWidget,
					pendingIntent);
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

		}
	}

	private boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
}
