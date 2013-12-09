package com.ameron32.chatreborn.chat.server;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Intent;
import android.os.IBinder;

import com.ameron32.chatreborn.chat.Global;
import com.ameron32.chatreborn.chat.Network;
import com.ameron32.chatreborn.chat.Utils;
import com.ameron32.chatreborn.chat.Network.ChatMessage;
import com.ameron32.chatreborn.chat.Network.MessageClass;
import com.ameron32.chatreborn.chat.Network.NamedClass;
import com.ameron32.chatreborn.chat.Network.RegisterName;
import com.ameron32.chatreborn.chat.Network.SystemMessage;
import com.ameron32.chatreborn.chat.Network.UpdateNames;
import com.ameron32.chatreborn.chat.client.ChatClient;
import com.ameron32.chatreborn.chat.client.ChatClient.MyClientBinder;
import com.ameron32.chatreborn.frmwk.ChatService;
import com.ameron32.chatreborn.frmwk.ChatService.MyBinder;
import com.ameron32.chatreborn.frmwk.NetworkTask;
import com.ameron32.chatreborn.frmwk.NetworkTask.Task;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
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
				// By providing our own connection implementation,
				// we can store per connection state
				// without a connection ID to state look up.
				return new ChatConnection();
			}
		});

		// For consistency, the classes to be sent over the network are
		// registered by the same method for both the client and server.
		Network.register(server);

		getServer().addListener(new Listener() {
			public void received(Connection c, Object object) {
				ChatConnection connection = (ChatConnection) c;
				handleMessage(connection, object);
			}

			public void disconnected(Connection c) {
				ChatConnection connection = (ChatConnection) c;
				if (connection.name != null) {
					// Announce to everyone that someone
					// (with a registered name) has left.
					final SystemMessage sysMessage = new SystemMessage();
					sysMessage.name = "Server:[" + Utils.getIPAddress(true)
							+ ":" + Network.port + "]";
					sysMessage.setText(connection.name + " connected.");
					getServer().sendToAllTCP(sysMessage);
					updateNames();
				}
			}
		});
	}
	
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

	private void handleMessage(ChatConnection c, Object object) {
		/**
		 * REGISTER NAME
		 */
		if (object instanceof RegisterName) {
			ChatConnection connection = (ChatConnection) c;
			// confirm connection is clear
			if (connection.name != null)
				return;

			String name = null;
			name = ((RegisterName) object).name;
			if (name == null)
				return;
			name = name.trim();
			if (name.length() == 0)
				return;
			connection.name = name;

			// create a new server notification
			final SystemMessage sysMessage = new SystemMessage();
			sysMessage.name = "Server:[" + Utils.getIPAddress(true) + ":"
					+ Network.port + "]";
			sysMessage.setText(name + " connected.");

			getServer().sendToAllTCP(sysMessage);
			
			// Send everyone a new list of connection names.
			updateNames();
			return;
		}

		/**
		 * CHAT MESSAGE
		 */
		if (object instanceof ChatMessage) {
			String message = null;
			message = ((ChatMessage) object).getText();
			if (message == null)
				return;
			message = message.trim();
			if (message.length() == 0)
				return;

			ChatMessage sourceCMessage = (ChatMessage) object;
			ChatMessage mMessage = new ChatMessage();
			mMessage.name = sourceCMessage.name;
			mMessage.setText(message);
			getServer().sendToAllTCP(mMessage);
			return;
		}

		/**
		 * SYSTEM MESSAGE
		 */
		if (object instanceof SystemMessage) {
			String message = null;
			message = ((SystemMessage) object).getText();
			if (message == null)
				return;
			message = message.trim();
			if (message.length() == 0)
				return;

			SystemMessage sourceSMessage = (SystemMessage) object;
			SystemMessage sMessage = new SystemMessage();
			sMessage.name = sourceSMessage.name;
			sMessage.setText(message);
			getServer().sendToAllTCP(sMessage);
			return;
		}
		
		// reserved for exceptions
	}

	// --------------------------------------
	// SERVICE Calls
	// --------------------------------------

	private boolean isRunning = false;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		com.esotericsoftware.minlog.Log
				.set(com.esotericsoftware.minlog.Log.LEVEL_DEBUG);
		startServer();
		return super.onStartCommand(intent, flags, startId);
	}

	private void startServer() {
		if (!isRunning) {
			init();
			isRunning = true;
		}
	}

	@Override
	public void onDestroy() {
		stopServer();
		super.onDestroy();
	}

	private void stopServer() {
		if (isRunning) {
			getServer().stop();
			getServer().close();
			isRunning = false;
		}
	}

	// --------------------------------------
	// BINDER
	// --------------------------------------
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	IBinder mBinder = new MyServerBinder();

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
}
