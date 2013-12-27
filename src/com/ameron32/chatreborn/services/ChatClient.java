package com.ameron32.chatreborn.services;

import android.content.Intent;
import android.os.IBinder;

import com.ameron32.chatreborn.chat.ChatListener;
import com.ameron32.chatreborn.chat.Global;
import com.ameron32.chatreborn.chat.Network;
import com.ameron32.chatreborn.chat.Network.ChatMessage;
import com.ameron32.chatreborn.chat.Network.MessageClass;
import com.ameron32.chatreborn.chat.Network.RegisterName;
import com.ameron32.chatreborn.chat.Network.ServerChatHistory;
import com.ameron32.chatreborn.chat.Network.SystemMessage;
import com.ameron32.chatreborn.chat.Network.UpdateNames;
import com.ameron32.chatreborn.frmwk.ChatService;
import com.ameron32.chatreborn.services.ChatServer.ChatConnection;
import com.esotericsoftware.kryonet.Client;

public class ChatClient extends ChatService {
	private Client client;
	public Client getClient() {
		return client;
	}
	
	private void init() {
		client.start();
//		Global.set();
		
		Network.register(client);

		client.addListener(chatListener);
	}
	
	private final ChatListener chatListener = new ChatListener() {
		@Override
		protected void connected() {
			final RegisterName registerName = new RegisterName();
			registerName.name = Global.Local.username;
			client.sendTCP(registerName);
			
			final SystemMessage request = new SystemMessage();
			request.name = Global.Local.username;
			request.setText("history request");
			request.setIsHistoryRequest(true);
			client.sendTCP(request);
		}
		
		@Override
		protected void received(final ServerChatHistory serverChatHistory, final ChatConnection chatConnection) {
			Global.Local.unpackServerHistory(serverChatHistory.getHistoryBundle());
		}
		
		@Override
		protected void received(final SystemMessage systemMessage, final ChatConnection chatConnection) {
			Global.Local.addToHistory(systemMessage);
			if (!isBound) {
				notifyMessage(systemMessage.name + "[" + systemMessage.getText() + "]");
			}
		}
		
		@Override
		protected void received(final ChatMessage chatMessage, final ChatConnection chatConnection) {
			Global.Local.addToHistory(chatMessage);
			if (!isBound) {
				notifyMessage(chatMessage.name + " says: " + chatMessage.getText());
			}
		}
		
		@Override
		protected void received(final UpdateNames updateNames, final ChatConnection chatConnection) {
			if (!isBound) {
				notifyMessage("Users Changed");
			}
		}
		
		@Override
		protected void disconnected(final ChatConnection chatConnection) {
			Global.Local.clearChatHistory();
		}
	};
	
	private boolean isConnected = false;
	public boolean getIsConnected() {
		return isConnected;
	}
	public void setIsConnected(boolean state) {
		isConnected = state;
	}
	
	private boolean isPrepared = false;
	public boolean getIsPrepared() {
		return isPrepared;
	}
	public void setIsPrepared(boolean state) {
		isPrepared = state;
	}
	private void connect(String host) {
		if (!isPrepared) {
			Global.Local.hostname = host;
			client = new Client();
			init();
			
			isPrepared = true;
		}
	}
	
	private void disconnect() {
		if (isPrepared) {
			client.stop();
			client.close();
			client = null;
			
			isPrepared = false;
		}
	}
	
	// --------------------------------------
	// SERVICE Calls
	// --------------------------------------
	
	private boolean isStarted = false;
	public boolean getIsStarted() {
		return isStarted;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		com.esotericsoftware.minlog.Log
			.set(com.esotericsoftware.minlog.Log.LEVEL_DEBUG);
		if (!isStarted) {
			connect(intent.getStringExtra("host"));
			isStarted = true;
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		if (isStarted) {
			disconnect();
			isStarted = false;
		}
		super.onDestroy();
		clearNotification(getSTOP_NOTIFICATION_ID());
	}
	
	private IBinder mBinder = new MyClientBinder();

	@Override
	public IBinder onBind(Intent intent) {
		super.onBind(intent);
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
