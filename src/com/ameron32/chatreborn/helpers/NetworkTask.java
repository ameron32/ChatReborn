package com.ameron32.chatreborn.helpers;

import java.io.IOException;

import com.ameron32.chatreborn.chat.Global;
import com.ameron32.chatreborn.chat.Network;
import com.ameron32.chatreborn.chat.Network.MessageClass;
import com.ameron32.chatreborn.chat.Network.ServerChatHistory;
import com.ameron32.chatreborn.chat.Utils;
import com.ameron32.chatreborn.chat.Global.Local;
import com.ameron32.chatreborn.services.ChatClient;
import com.ameron32.chatreborn.services.ChatServer;
import com.ameron32.chatreborn.ui.ChatClientFrame;
import com.ameron32.chatreborn.ui.ChatServerFrame;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

import android.os.AsyncTask;

public class NetworkTask extends AsyncTask<String, int[], String> {

	private ChatClient chatClient;
	private Client client;
	private ChatClientFrame chatClientFrame;
	private Task task;
	private String username;

	public NetworkTask (Task task, String username, ChatClient chatClient, ChatClientFrame chatFrame) {
		this.username = username;
		this.chatClient = chatClient;
		this.client = chatClient.getClient();
		this.chatClientFrame = chatFrame;
		this.task = task;
	}
	
	private ChatServer chatServer;
	private Server server;
	private ChatServerFrame chatServerFrame;
	public NetworkTask (Task task, ChatServer chatServer, ChatServerFrame chatServerFrame) {
		this.server = chatServer.getServer();
		this.chatServer = chatServer;
		this.chatServerFrame = chatServerFrame;
		this.task = task;
	}
	
	private MessageClass msg;
	public NetworkTask (Task task, ChatClient chatClient, MessageClass msg) {
		this.chatClient = chatClient;
		this.client = chatClient.getClient();
		this.task = task;
		this.msg = msg;
	}
	
	public enum Task {
		Discover, Connect,				// client 
		SendMessage, 					// BOTH
		StartServer, SendHistory 		// server
	}
	
	@Override
	protected String doInBackground(String... params) {
		switch (task) {
		case Discover:
		
			return null;
		case Connect:
			try {
				Log.error("CLIENT CONNECTING TO SERVER");
				client.connect(5000, Global.Local.hostname, Network.port);
				// Server communication after connection can go
				// here, or in listener#connected().
			
				chatClient.setIsConnected(true);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return null;
		case StartServer:
			try {
				Log.error("SERVER STARTING");
				server.bind(Network.port);
				server.start();
				
				chatServer.setIsRunning(true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		case SendMessage:
            client.sendTCP(msg);
			return null;
		case SendHistory:
			break;
		default:
			break;
		}
		return null;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		 
		 switch (task) {
		 case Discover:
			 
			 break;
		 case Connect:
			 String netInfo = Global.Local.hostname + ":" + Network.port;
			 if (Global.Local.hostname.equals("localhost")) {
				 netInfo += "\n" + "Local: " + Utils.getIPAddress(true);
			 }
			 chatClientFrame.setConnectionText(netInfo);
			 chatClientFrame.clearChatHistory();
			 return;
		 case StartServer:
			 String hostInfo = Utils.getIPAddress(true) + ":" + Network.port;
			 chatServerFrame.setHostText(hostInfo);
			 chatServerFrame.clearChatHistory();
			 return;
		case SendHistory:
			break;
		case SendMessage:
			break;
		default:
			break;
		 }

	}
}
