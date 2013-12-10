package com.ameron32.chatreborn.chat.server;

import com.ameron32.knbasic.core.chat.R;
import com.ameron32.chatreborn.chat.Global;
import com.ameron32.chatreborn.chat.Network.ChatMessage;
import com.ameron32.chatreborn.chat.Network.RegisterName;
import com.ameron32.chatreborn.chat.Network.SystemMessage;
import com.ameron32.chatreborn.chat.Network.UpdateNames;
import com.ameron32.chatreborn.chat.client.ChatClient;
import com.ameron32.chatreborn.chat.client.ChatClientFrame;
import com.ameron32.chatreborn.chat.client.ChatClient.MyClientBinder;
import com.ameron32.chatreborn.chat.client.ChatClientFragment.MyServiceConnection;
import com.ameron32.chatreborn.chat.server.ChatServer.ChatConnection;
import com.ameron32.chatreborn.chat.server.ChatServer.MyServerBinder;
import com.ameron32.chatreborn.frmwk.NetworkTask;
import com.ameron32.chatreborn.frmwk.NetworkTask.Task;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

import android.content.ComponentName;
import android.content.Context;
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
		v = inflater.inflate(R.layout.chat_server, container, false);
		return v;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	private Intent chatServerService;
	private Intent getCSS() {
		if (chatServerService == null) {
			chatServerService = new Intent(getActivity(), ChatServer.class);
		}
		return chatServerService;
	}
	private ChatServerFrame chatFrame;
	private ChatServer chatServer;
	
	@Override
	public void onStart() {
		super.onStart();
		getActivity().bindService(getCSS(), mConnection, ContextWrapper.BIND_AUTO_CREATE);
		chatFrame = new ChatServerFrame(getActivity(), v);
		initChatServerFrame();
	}
	
	@Override
	public void onStop() {
		getActivity().unbindService(mConnection);
		chatFrame = null;
		super.onStop();
	}
	
	private void initChatServerFrame() {
		chatFrame.setHostAServerListener(new Runnable() {
			@Override
			public void run() {
				NetworkTask task = new NetworkTask(
						Task.StartServer, 
						chatServer.getServer(), 
						chatFrame);
				task.execute();
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
			if (chatServer != null) {
				isBound_mConnection = true;
				chatServer.isBound = true;
				chatServer.getServer().addListener(chatServerListener);
			} else {
				Toast.makeText(getActivity(), "chatServer is Null", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			if (chatServer != null) {
				isBound_mConnection = false;
				chatServer.isBound = false;
				chatServer.getServer().removeListener(chatServerListener);
			} else {
				Toast.makeText(getActivity(), "chatServer is Null", Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	
	final Listener chatServerListener = new Listener() {
		public void received(Connection connection, Object object) {
			if (object instanceof RegisterName) {
				Global.Server.connectedUsers.add(((RegisterName) object).name);
				updateChatFrame(object, FrameProcess.ResetNames);
				return;
			}

			if (object instanceof ChatMessage) {
				final ChatMessage chatMessage = (ChatMessage) object;
//				Global.Local.clientChatHistory.put(chatMessage.getTimeStamp(), chatMessage);
				updateChatFrame(object, FrameProcess.AddMessage);
				return;
			}
			
			if (object instanceof SystemMessage) {
				final SystemMessage sysMessage = (SystemMessage) object;
				// TODO what does a system message do?
				updateChatFrame(object, FrameProcess.AddMessage);
				return;
			}
		}

		public void disconnected(Connection connection) {
			Global.Server.removeUser(((ChatConnection) connection).name);
			updateChatFrame(null, FrameProcess.ResetNames);
		}
	};
	
	private void updateChatFrame(Object object, FrameProcess process) {
		if (chatFrame != null && getActivity() != null) {
			switch (process) {
			case ResetNames:
				if (object != null) {
					final RegisterName registerName = (RegisterName) object;
				}
//				Global.Local.groupUsers = updateNames.names;
				chatFrame.resetNames();
				break;
			case AddMessage:
				final ChatMessage chatMessage = (ChatMessage) object;
				Global.Local.clientChatHistory.put(chatMessage.getTimeStamp(), chatMessage);
				chatFrame.addMessage(chatMessage);
				break;
			}
		}
	}
	
	private enum FrameProcess {
		ResetNames, AddMessage
	}
}
