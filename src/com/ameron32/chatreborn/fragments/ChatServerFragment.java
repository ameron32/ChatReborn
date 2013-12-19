package com.ameron32.chatreborn.fragments;

import com.ameron32.knbasic.core.chat.R;
import com.ameron32.chatreborn.chat.ChatListener;
import com.ameron32.chatreborn.chat.Global;
import com.ameron32.chatreborn.chat.Network.ChatMessage;
import com.ameron32.chatreborn.chat.Network.MessageClass;
import com.ameron32.chatreborn.chat.Network.RegisterName;
import com.ameron32.chatreborn.chat.Network.ServerChatHistory;
import com.ameron32.chatreborn.chat.Network.SystemMessage;
import com.ameron32.chatreborn.chat.Network.UpdateNames;
import com.ameron32.chatreborn.helpers.NetworkTask;
import com.ameron32.chatreborn.helpers.NetworkTask.Task;
import com.ameron32.chatreborn.services.ChatClient;
import com.ameron32.chatreborn.services.ChatServer;
import com.ameron32.chatreborn.services.ChatClient.MyClientBinder;
import com.ameron32.chatreborn.services.ChatServer.ChatConnection;
import com.ameron32.chatreborn.services.ChatServer.MyServerBinder;
import com.ameron32.chatreborn.ui.ChatClientFrame;
import com.ameron32.chatreborn.ui.ChatServerFrame;
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

	}
	
	@Override
	public void onResume() {
		super.onResume();
		getActivity().bindService(getCSS(), mConnection, ContextWrapper.BIND_AUTO_CREATE);
		chatServerListener2.setDisabled(false);
		chatFrame = new ChatServerFrame(getActivity(), v);
		initChatServerFrame();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		getActivity().unbindService(mConnection);
		chatServerListener2.setDisabled(true);
		chatFrame = null;
	}
	
	@Override
	public void onStop() {
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
				chatServer.getServer().addListener(chatServerListener2);
			} else {
				Toast.makeText(getActivity(), "chatServer is Null", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			if (chatServer != null) {
				isBound_mConnection = false;
				chatServer.isBound = false;
				chatServer.getServer().removeListener(chatServerListener2);
			} else {
				Toast.makeText(getActivity(), "chatServer is Null", Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	
//	final Listener chatServerListener = new Listener() {
//		public void received(Connection connection, Object object) {
//			if (object instanceof RegisterName) {
//				Global.Server.connectedUsers.add(((RegisterName) object).name);
//				updateChatFrame(object, FrameProcess.ResetNames);
//				return;
//			}
//
//			if (object instanceof ChatMessage) {
//				final ChatMessage chatMessage = (ChatMessage) object;
////				Global.Local.clientChatHistory.put(chatMessage.getTimeStamp(), chatMessage);
//				updateChatFrame(object, FrameProcess.AddMessage);
//				return;
//			}
//			
//			if (object instanceof SystemMessage) {
//				final SystemMessage sysMessage = (SystemMessage) object;
//				// TODO what does a system message do?
//				updateChatFrame(object, FrameProcess.AddMessage);
//				return;
//			}
//			
//			if (!(object instanceof FrameworkMessage)) {
//				final SystemMessage unexpected = new SystemMessage();
//				unexpected.name = "System";
//				unexpected.setText("unexpected object: " + object.toString());
//				updateChatFrame(unexpected, FrameProcess.AddMessage);
//			}
//		}
//
//		public void disconnected(Connection connection) {
//			Global.Server.removeUser(((ChatConnection) connection).name);
//			updateChatFrame(null, FrameProcess.ResetNames);
//		}
//	};
	
	final ChatListener chatServerListener2 = new ChatListener() {
		
		@Override
		protected void received(ChatConnection chatConnection,
				ServerChatHistory serverChatHistory) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		protected void received(ChatConnection chatConnection,
				MessageClass messageClass) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		protected void received(ChatConnection chatConnection,
				SystemMessage systemMessage) {
			chatFrame.addMessage(systemMessage);
		}
		
		@Override
		protected void received(ChatConnection chatConnection,
				ChatMessage chatMessage) {
			chatFrame.addMessage(chatMessage);
		}
		
		@Override
		protected void received(ChatConnection chatConnection,
				UpdateNames updateNames) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		protected void received(ChatConnection chatConnection,
				RegisterName registerName) {
			Global.Server.connectedUsers.add(registerName.name);
			chatFrame.resetNames();
		}
		
		@Override
		protected void disconnected(ChatConnection chatConnection) {

		}
	
	};
	
//	private void updateChatFrame(Object object, FrameProcess process) {
//		if (chatFrame != null && getActivity() != null) {
//			switch (process) {
//			case ResetNames:
//				if (object != null) {
//					final RegisterName registerName = (RegisterName) object;
//				}
////				Global.Local.groupUsers = updateNames.names;
//				chatFrame.resetNames();
//				break;
//			case AddMessage:
//				final MessageClass chatMessage = (MessageClass) object;
//				Global.Server.serverChatHistory.put(chatMessage.getTimeStamp(), chatMessage);
//				chatFrame.addMessage(chatMessage);
//				break;
//			}
//		}
//	}
//	
//	private enum FrameProcess {
//		ResetNames, AddMessage
//	}
}
