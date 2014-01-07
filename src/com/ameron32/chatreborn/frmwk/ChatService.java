package com.ameron32.chatreborn.frmwk;

import com.ameron32.chatreborn.activities.MainActivity;
import com.ameron32.knbasic.core.chat.R;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class ChatService extends Service {

	private int START_NOTIFICATION_ID;
	private int STOP_NOTIFICATION_ID;
	public boolean isBound = false;
	private NotificationManager nManager;
	
	@Override
	public IBinder onBind(Intent intent) {
		isBound = true;
		return getMyBinder();
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		isBound = false;
		return super.onUnbind(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		startNotification(getSTART_NOTIFICATION_ID());
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		stopNotification(getSTOP_NOTIFICATION_ID());
		super.onDestroy();
	}

	// --------------------------------------
	// SERVICE RELATED NOTIFICATIONS
	// --------------------------------------

	private final Context context = this;

	private NotificationManager init(NotificationManager nManager) {
		if (nManager == null) 
			return (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		return nManager;
	}
	
	private void startNotification(int id) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.like)
				.setContentTitle(getSimpleName() + " Started")
				.setContentText("Click to Open Application");

		Intent targetIntent = new Intent(context, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(contentIntent);
		nManager = init(nManager);
		nManager.notify(getSTART_NOTIFICATION_ID(), builder.build());

		clearNotification(getSTOP_NOTIFICATION_ID());
	}

	private void stopNotification(int id) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.delete)
				.setContentTitle(getSimpleName() + " Stopped")
				.setContentText("Click to Open Application");

		Intent targetIntent = new Intent(context, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(contentIntent);
		init(nManager);
		nManager.notify(getSTOP_NOTIFICATION_ID(), builder.build());

		clearNotification(getSTART_NOTIFICATION_ID());
		
	}

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
	
	private static final int MESSAGE_NOTIFICATIONS = 99;
	protected void notifyMessage(String msg) {
		createNotification(msg, "Click to OpenApplication", MESSAGE_NOTIFICATIONS);
	}
	
	protected void clearNotification(int id) {
		init(nManager);
		nManager.cancel(id);
	}
	
	private void createNotification(String title, String text, int id) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.chess)
				.setContentTitle(title)
				.setContentText(text);
		Intent targetIntent = new Intent(context, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(contentIntent);
		builder.setAutoCancel(true);
		
		init(nManager);
		nManager.notify(getSTOP_NOTIFICATION_ID(), builder.build());
	}

	// --------------------------------------
	// NETWORK THREAD HANDLING
	// --------------------------------------
	

	
	// --------------------------------------
	// GETTER / SETTER
	// --------------------------------------
	
	protected int getSTART_NOTIFICATION_ID() {
		return START_NOTIFICATION_ID;
	}

	protected void setSTART_NOTIFICATION_ID(int sTART_NOTIFICATION_ID) {
		START_NOTIFICATION_ID = sTART_NOTIFICATION_ID;
	}

	protected int getSTOP_NOTIFICATION_ID() {
		return STOP_NOTIFICATION_ID;
	}

	protected void setSTOP_NOTIFICATION_ID(int sTOP_NOTIFICATION_ID) {
		STOP_NOTIFICATION_ID = sTOP_NOTIFICATION_ID;
	}
}
