package com.ameron32.chatreborn.chat.client;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import com.ameron32.chatreborn.chat.Global;
import com.ameron32.chatreborn.chat.Network;
import com.ameron32.chatreborn.chat.Global.Local;
import com.ameron32.chatreborn.chat.Network.ChatMessage;
import com.ameron32.chatreborn.chat.Network.RegisterName;
import com.ameron32.chatreborn.chat.Network.SystemMessage;
import com.ameron32.chatreborn.chat.Network.UpdateNames;
import com.ameron32.chatreborn.frmwk.ChatService;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

public class ChatClient extends ChatService {
	private Client client;
	public Client getClient() {
		return client;
	}
	
	private void init() {
		client.start();

		Network.register(client);

		client.addListener(new Listener() {
			public void connected(Connection connection) {
				final RegisterName registerName = new RegisterName();
				registerName.name = Global.Local.username;
				client.sendTCP(registerName);
			}

			public void received(Connection connection, Object object) {
				if (object instanceof UpdateNames) {
					final UpdateNames updateNames = (UpdateNames) object;
					Global.Local.groupUsers = updateNames.names;
					return;
				}

				if (object instanceof ChatMessage) {
					final ChatMessage chatMessage = (ChatMessage) object;

					return;
				}
				
				if (object instanceof SystemMessage) {
					final SystemMessage sysMessage = (SystemMessage) object;
					// TODO what does a system message do?
					return;
				}
				
				if (!(object instanceof FrameworkMessage)) {
					Log.debug("", "ChatClient:" + object.toString());
				}
			}

			public void disconnected(Connection connection) {
				
			}
		});
	}
	
	public class RunListener extends AsyncTask<String, int[], String> {
		Runnable listener;

		public RunListener(Runnable listener) {
			this.listener = listener;
		}

		@Override
		protected String doInBackground(String... params) {
			listener.run();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		}
	}
	
	private void start() {
		client = new Client();
		init();
	}
	
	private boolean isConnected = false;
	private void connect() {
		if (!isConnected) {
			// TODO Auto-generated method stub
		}
	}
	
	private void disconnect() {
		if (isConnected) {
			// TODO Auto-generated method stub
		}
	}
	
	// --------------------------------------
	// SERVICE Calls
	// --------------------------------------
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		start();
		connect();
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		disconnect();
		super.onDestroy();
	}
	
	IBinder mBinder = new MyClientBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		setSTART_NOTIFICATION_ID(200);
		setSTOP_NOTIFICATION_ID(201);
		
		setMyBinder(new MyClientBinder());
	}
	
	public class MyClientBinder extends MyBinder {
		public ChatClient getService() {
			return ChatClient.this;
		}
	}

}
