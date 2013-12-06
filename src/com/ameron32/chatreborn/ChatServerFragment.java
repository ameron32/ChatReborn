package com.ameron32.chatreborn;

import com.ameron32.chatreborn.ChatClientFragment.MyServiceConnection;
import com.ameron32.chatreborn.chat.Network.ChatMessage;
import com.ameron32.chatreborn.chat.Network.SystemMessage;
import com.ameron32.chatreborn.chat.Network.UpdateNames;
import com.ameron32.chatreborn.chat.client.ChatClient;
import com.ameron32.chatreborn.chat.client.ChatClientFrame;
import com.ameron32.chatreborn.chat.client.ChatClient.MyClientBinder;
import com.ameron32.chatreborn.chat.server.ChatServer;
import com.ameron32.chatreborn.chat.server.ChatServer.MyServerBinder;
import com.ameron32.chatreborn.chat.server.ChatServerFrame;
import com.ameron32.chatreborn.frmwk.NetworkTask;
import com.ameron32.chatreborn.frmwk.NetworkTask.Task;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

import android.content.ComponentName;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class ChatServerFragment extends Fragment {
	
	private View v;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.chat_fragment3, container, false);
		return v;
	}
	
	public Intent chatServerService; 
	private ChatServerFrame chatFrame;
	private ChatServer chatServer;
	
	@Override
	public void onStart() {
		super.onStart();
		
		chatServerService = new Intent(getActivity(), ChatServer.class);
		getActivity().bindService(chatServerService, mConnection, ContextWrapper.BIND_AUTO_CREATE);
		chatFrame = new ChatServerFrame(getActivity(), v);
		initChatServerFrame();
	}
		
	@Override
	public void onStop() {
		getActivity().unbindService(mConnection);
		super.onStop();
	}
	
	private void initChatServerFrame() {

//		chatFrame.setSendListener(sendMessage);
//		chatFrame.setActionListener(sendAction);

		
//		chatFrame.setConnectToServerListener(new Runnable() {
//			@Override
//			public void run() {
//				new NetworkTask(Task.Connect, chatClient.getClient(), chatFrame).execute();
//			}
//		});
		
		chatFrame.setHostAServerListener(new Runnable() {
			@Override
			public void run() {
				new NetworkTask(Task.StartServer, chatServer.getServer(), chatFrame).execute();
			}
		});
	}
	
	private MyServiceConnection mConnection = new MyServiceConnection();
	private boolean isBound_mConnection = false;
	public class MyServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MyServerBinder mBinder = (MyServerBinder) service;
			chatServer = (ChatServer) mBinder.getService();
			isBound_mConnection = true;
			chatServer.isBound = true;
			
			chatServer.getServer().addListener(chatServerListener);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			isBound_mConnection = false;
			chatServer.isBound = false;
			
			chatServer.getServer().removeListener(chatServerListener);	
		}
		
	}
	
	final Listener chatServerListener = new Listener() {
		public void received(Connection connection, Object object) {
			if (object instanceof UpdateNames) {
				final UpdateNames updateNames = (UpdateNames) object;
//				Global.Local.groupUsers = updateNames.names;
				chatFrame.setNames(updateNames.names);
//				return;
			}

			if (object instanceof ChatMessage) {
				final ChatMessage chatMessage = (ChatMessage) object;
//				Global.Local.clientChatHistory.put(chatMessage.getTimeStamp(), chatMessage);
				chatFrame.addMessage(chatMessage);
//				return;
			}
			
			if (object instanceof SystemMessage) {
				final SystemMessage sysMessage = (SystemMessage) object;
				// TODO what does a system message do?
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getActivity(), "Sys: " + sysMessage.getText(), Toast.LENGTH_SHORT).show();
					}
				});
//				return;
			}
			
			if (!(object instanceof FrameworkMessage)) {
			Log.debug("", "ChatServerFragment " + object.toString());
			}
		}

		public void disconnected(Connection connection) {

		}
	};
}
