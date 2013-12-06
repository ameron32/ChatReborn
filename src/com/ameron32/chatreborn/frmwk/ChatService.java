package com.ameron32.chatreborn.frmwk;

import com.ameron32.chatreborn.MainActivity;
import com.ameron32.chatreborn.R;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class ChatService extends Service {

	private int START_NOTIFICATION_ID, STOP_NOTIFICATION_ID;
	public boolean isBound = false;

	public int getSTOP_NOTIFICATION_ID() {
		return STOP_NOTIFICATION_ID;
	}

	public void setSTOP_NOTIFICATION_ID(int sTOP_NOTIFICATION_ID) {
		STOP_NOTIFICATION_ID = sTOP_NOTIFICATION_ID;
	}

	public int getSTART_NOTIFICATION_ID() {
		return START_NOTIFICATION_ID;
	}

	public void setSTART_NOTIFICATION_ID(int sTART_NOTIFICATION_ID) {
		START_NOTIFICATION_ID = sTART_NOTIFICATION_ID;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return getMyBinder();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		startNotification(getSTART_NOTIFICATION_ID());
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopNotification(getSTOP_NOTIFICATION_ID());
	}

	// --------------------------------------
	// SERVICE RELATED NOTIFICATIONS
	// --------------------------------------

	private final Context context = this;

	private void startNotification(int id) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.send_now)
				.setContentTitle(getSimpleName() + " Started")
				.setContentText(getSimpleName() + " Service Running");
		Intent targetIntent = new Intent(context, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(contentIntent);
		NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nManager.notify(getSTART_NOTIFICATION_ID(), builder.build());
		nManager.cancel(getSTOP_NOTIFICATION_ID());
	}

	private void stopNotification(int id) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.send_now)
				.setContentTitle(getSimpleName() + " Stopped")
				.setContentText(getSimpleName() + " Service Stopped");

		Intent targetIntent = new Intent(context, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(contentIntent);
		NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nManager.notify(getSTOP_NOTIFICATION_ID(), builder.build());
		nManager.cancel(getSTART_NOTIFICATION_ID());
	}

	//
	private String getSimpleName() {
		return getClass().getSimpleName();
	}

	public class MyBinder extends Binder {
		public ChatService getService() {
			return ChatService.this;
		}
	}

	private IBinder myBinder = new MyBinder();

	protected IBinder getMyBinder() {
		return myBinder;
	}

	protected void setMyBinder(IBinder myBinder) {
		this.myBinder = myBinder;
	}

}
