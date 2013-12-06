package com.ameron32.chatreborn.frmwk;

import java.io.IOException;

import com.ameron32.chatreborn.chat.Global;
import com.ameron32.chatreborn.chat.Network;
import com.ameron32.chatreborn.chat.Utils;
import com.ameron32.chatreborn.chat.Global.Local;
import com.ameron32.chatreborn.chat.client.ChatClientFrame;
import com.ameron32.chatreborn.chat.server.ChatServerFrame;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;

import android.os.AsyncTask;

public class NetworkTask extends AsyncTask<String, int[], String> {

	private Client client;
	private ChatClientFrame chatFrame;
	private Task task;

	public NetworkTask (Task task, Client client, ChatClientFrame chatFrame) {
		this.client = client;
		this.chatFrame = chatFrame;
		this.task = task;
	}
	
	private Server server;
	private ChatServerFrame chatServerFrame;
	public NetworkTask (Task task, Server server, ChatServerFrame chatServerFrame) {
		this.server = server;
		this.chatServerFrame = chatServerFrame;
		this.task = task;
	}
	
	public enum Task {
		Connect, StartServer
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
			 
			 chatFrame.setConnectionText(netInfo);
			 chatFrame.setNames(Global.Local.groupUsers);
			 chatFrame.clearChatHistory();
			 return;
		 case StartServer:
			 String hostInfo = Utils.getIPAddress(true) + ":" + Network.port;
			 chatServerFrame.setHostText(hostInfo);
			 return;
		 }

	}
}
