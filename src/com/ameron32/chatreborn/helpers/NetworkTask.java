package com.ameron32.chatreborn.helpers;

import java.io.IOException;

import com.ameron32.chatreborn.chat.Global;
import com.ameron32.chatreborn.chat.Network;
import com.ameron32.chatreborn.chat.Network.MessageClass;
import com.ameron32.chatreborn.chat.Network.ServerChatHistory;
import com.ameron32.chatreborn.chat.Utils;
import com.ameron32.chatreborn.chat.Global.Local;
import com.ameron32.chatreborn.ui.ChatClientFrame;
import com.ameron32.chatreborn.ui.ChatServerFrame;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;

import android.os.AsyncTask;

public class NetworkTask extends AsyncTask<String, int[], String> {

	private Client client;
	private ChatClientFrame chatClientFrame;
	private Task task;
	private String username;

	public NetworkTask (Task task, String username, Client client, ChatClientFrame chatFrame) {
		this.username = username;
		this.client = client;
		this.chatClientFrame = chatFrame;
		this.task = task;
	}
	
	private Server server;
	private ChatServerFrame chatServerFrame;
	public NetworkTask (Task task, Server server, ChatServerFrame chatServerFrame) {
		this.server = server;
		this.chatServerFrame = chatServerFrame;
		this.task = task;
	}
	
	private MessageClass msg;
	public NetworkTask (Task task, Client client, MessageClass msg) {
		this.client = client;
		this.task = task;
		this.msg = msg;
	}
	
//	private ServerChatHistory history;
//	public NetworkTask (Task task, Server server, ServerChatHistory history) {
//		this.server = server;
//		this.task = task;
//		this.history = history;
//	}
	
	public enum Task {
		Connect,						// client 
		SendMessage, 					// BOTH
		StartServer, SendHistory 		// server
	}
	
	@Override
	protected String doInBackground(String... params) {
		switch (task) {
		case Connect:
			try {
				client.connect(5000, Global.Local.hostname, Network.port);
				// Server communication after connection can go
				// here, or in listener#connected().
			
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return null;
		case StartServer:
			try {
				server.bind(Network.port);
				server.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		case SendMessage:
            client.sendTCP(msg);
			return null;
//		case SendHistory:
//			server.sendToAllTCP(history);
//			return null;
		}
		return null;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// pbMain.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		// pbMain.setVisibility(View.INVISIBLE);
		//

		 
		 switch (task) {
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
		 }

	}
}
