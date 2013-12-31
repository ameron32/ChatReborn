package com.ameron32.chatreborn.services;

import java.util.ArrayList;
import java.util.TreeMap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import com.ameron32.chatreborn.chat.ChatListener;
import com.ameron32.chatreborn.chat.Global;
import com.ameron32.chatreborn.chat.Network;
import com.ameron32.chatreborn.chat.Utils;
import com.ameron32.chatreborn.chat.MessageTemplates.*;
import com.ameron32.chatreborn.frmwk.ChatService;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

public class ChatServer extends ChatService {
	public static class ChatConnection extends Connection {
		public String name;
	}

	private Server server;

	private void init() {
		setServer(new Server() {
			protected Connection newConnection() {
				return new ChatConnection();
			}
		});
		
		Network.register(server);

		getServer().addListener(serverListener);
		

		// restore chat log somehow 
	}
	
	private final ChatListener serverListener = new ChatListener() {
		@Override
		protected void received(final SystemMessage systemMessage, final ChatConnection chatConnection) {
			String message = null;
			SystemMessage sourceSMessage = systemMessage;
			message = sourceSMessage.getText();
			if (message == null)
				return;
			message = message.trim();
			if (message.length() == 0)
				return;
			
			if (isHistoryRequest(sourceSMessage)) {
				sendHistory(chatConnection.getID());
				
				final SystemMessage historyRequestPlaceholder = new SystemMessage();
				historyRequestPlaceholder.name = sourceSMessage.name;
				historyRequestPlaceholder.setText("requested server chat history");
				Global.Server.addToHistory(historyRequestPlaceholder);
				// do not store the original ChatHistory requests. OMG break everything!
				return;
			}

			SystemMessage sMessage = new SystemMessage();
			sMessage.name = sourceSMessage.name;
			sMessage.setText(message);
			Global.Server.addToHistory(sMessage);
			getServer().sendToAllTCP(sMessage);
		}
		
		@Override
		protected void received(final ChatMessage chatMessage, final ChatConnection chatConnection) {
			String message = null;
			message = chatMessage.getText();
			if (message == null)
				return;
			message = message.trim();
			if (message.length() == 0)
				return;

			final ChatMessage mMessage = new ChatMessage();
			mMessage.name = chatMessage.name;
			mMessage.setText(message);
			Global.Server.addToHistory(mMessage);
			getServer().sendToAllTCP(mMessage);
		}
		
		@Override
		protected void received(final RegisterName registerName, final ChatConnection chatConnection) {
			// confirm connection is clear
			if (chatConnection.name != null)
				return;

			String name = null;
			name = registerName.name;
			if (name == null)
				return;
			name = name.trim();
			if (name.length() == 0)
				return;
			chatConnection.name = name;

			// create a new server notification
			final SystemMessage sysMessage = new SystemMessage();
			sysMessage.name = serverName;
			sysMessage.setText(name + " connected.");

			getServer().sendToAllTCP(sysMessage);
			
			updateNames();
		}
		
		@Override
		protected void disconnected(final ChatConnection chatConnection) {
			if (chatConnection.name != null) {
				// Announce to everyone that someone
				// (with a registered name) has left.
				final SystemMessage sysMessage = new SystemMessage();
				sysMessage.name = "Server:[" + Utils.getIPAddress(true)
						+ ":" + Network.port + "]";
				sysMessage.setText(chatConnection.name + " disconnected.");
				getServer().sendToAllTCP(sysMessage);
				
				updateNames();
			}
		}
	};
	
	private void updateNames() {
		// Collect the names for each connection.
		final Connection[] connections = getServer().getConnections();
		final ArrayList<String> names = new ArrayList<String>(
				connections.length);
		for (int i = connections.length - 1; i >= 0; i--) {
			final ChatConnection connection = (ChatConnection) connections[i];
			names.add(connection.name);
		}
		// Send the names to everyone.
		final UpdateNames updateNames = new UpdateNames();
		updateNames.names = (String[]) names.toArray(new String[names.size()]);
		getServer().sendToAllTCP(updateNames);
	}

	private static final String serverName
			= "Server:[" + Utils.getIPAddress(true) + ":" + Network.port + "]";
	
	private boolean isHistoryRequest(SystemMessage sourceSMessage) {
		return sourceSMessage.getIsHistoryRequest();
	}
	
	private void sendHistory(int connectionId) {
		TreeMap<Long, MessageClass> sch = Global.Server.getServerChatHistory();
		ServerChatHistory schB = new ServerChatHistory();
		schB.name = serverName;
		schB.loadHistory(sch);
		getServer().sendToTCP(connectionId, schB);
	}

	// --------------------------------------
	// SERVICE Calls
	// --------------------------------------

	private boolean isPrepared = false;
	public boolean getIsPrepared() {
		return isPrepared;
	}
	private boolean isRunning = false;
	public boolean getIsRunning() {
		return isRunning;
	}
	public void setIsRunning(boolean state) {
		isRunning = state;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		com.esotericsoftware.minlog.Log
				.set(com.esotericsoftware.minlog.Log.LEVEL_DEBUG);
		if (intent != null) startServer();
		return super.onStartCommand(intent, flags, startId);
	}

	private void startServer() {
		if (!getIsPrepared()) {
			init();
			isPrepared = true;
		}
	}
	
	@Override
	public void onDestroy() {
		stopServerIn(10000);
		super.onDestroy();
	}

	private void stopServerIn(int millis) {
		if (getIsPrepared()) {
			term(millis);
			isPrepared = false;
		}
	}
	
	private void term(int millis) {
		AsyncTask<Integer, Integer, String> stopServer = new AsyncTask<Integer, Integer, String>() {
			@Override
			protected String doInBackground(Integer... params) {
				int totalTimeInMillis = params[0];
				int updates = 10;
				int timePerUpdate = totalTimeInMillis / updates;
				for (int u = 0; u < updates; u++) {
					sendChatMessage("Server shutting down in "
							+ ((totalTimeInMillis - (timePerUpdate * u)) / 1000)
							+ " seconds!");
					try {
						Thread.sleep(timePerUpdate);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				// store server log somehow

				getServer().stop();
				getServer().close();
				
				setIsRunning(false);
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				clearNotification(getSTOP_NOTIFICATION_ID());
				super.onPostExecute(result);
			}
		};
		stopServer.execute(millis);
	}

	// --------------------------------------
	// BINDER
	// --------------------------------------
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private IBinder mBinder = new MyServerBinder();

	@Override
	public void onCreate() {
		super.onCreate();
		setSTART_NOTIFICATION_ID(100);
		setSTOP_NOTIFICATION_ID(101);

		setMyBinder(new MyServerBinder());
	}

	public class MyServerBinder extends MyBinder {
		public ChatServer getService() {
			return ChatServer.this;
		}
	}
	
	// --------------------------------------
	// GETTER/SETTER
	// --------------------------------------
	
	public Server getServer() {
		return server;
	}

	private void setServer(Server server) {
		this.server = server;
	}
	
	// --------------------------------------
	// HELPER METHODS
	// --------------------------------------
	
	private void sendChatMessage(String msg) {
		final ChatMessage chatMessage = new ChatMessage();
		chatMessage.name = serverName;
		chatMessage.setText(msg);
		getServer().sendToAllTCP(chatMessage);
	}
}
