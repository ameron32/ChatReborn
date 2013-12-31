package com.ameron32.chatreborn.fragments;

import com.ameron32.knbasic.core.chat.R;
import com.ameron32.chatreborn.chat.ChatListener;
import com.ameron32.chatreborn.chat.Global;
import com.ameron32.chatreborn.chat.MessageTemplates.*;
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
	}
	
	public void bindServerService() {
		getActivity().bindService(getCSS(), mConnection, ContextWrapper.BIND_AUTO_CREATE);
		chatServerListener2.setDisabled(false);
		if (chatFrame == null) chatFrame = new ChatServerFrame(getActivity(), v);
		
		initChatServerFrame();
	}
	
	private void initiateServerStart() {
		startServer = new Runnable() {
			@Override
			public void run() {
				NetworkTask task = new NetworkTask(
						Task.StartServer, 
						chatServer, 
						chatFrame);
				task.execute();
			}
		};
		startServer.run();
	}
	
	private void initiateServerStop() {
		// nothing yet
	}
	
	@Override
	public void onPause() {
		super.onPause();

	}
	
	public void unbindServerService() {
		getActivity().unbindService(mConnection);
		chatServerListener2.setDisabled(true);

		chatFrame = null;
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}

	private Runnable startServer;
	
	private void initChatServerFrame() {
		if (chatServer != null && chatServer.getIsRunning())
			chatFrame.updateUIHosting();
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
				
				if (chatServer.getIsPrepared() && !chatServer.getIsRunning()) {
					chatServer.getServer().addListener(chatServerListener2);
					initiateServerStart();
				}
			} else {
				Toast.makeText(getActivity(), "chatServer is Null", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			if (chatServer != null) {
				if (chatServer.getIsRunning()) {
					initiateServerStop();
					chatServer.getServer().removeListener(chatServerListener2);
				}

				isBound_mConnection = false;
				chatServer.isBound = false;
			} else {
				Toast.makeText(getActivity(), "chatServer is Null", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	final ChatListener chatServerListener2 = new ChatListener() {
		@Override
		protected void received(final SystemMessage systemMessage, final ChatConnection chatConnection) {
			chatFrame.addMessage(systemMessage);
		}
		
		@Override
		protected void received(final ChatMessage chatMessage, final ChatConnection chatConnection) {
			chatFrame.addMessage(chatMessage);
		}
		
		@Override
		protected void received(final RegisterName registerName, final ChatConnection chatConnection) {
			Global.Server.connectedUsers.add(registerName.name);
			chatFrame.resetNames();
		}
		
		@Override
		protected void disconnected(final ChatConnection chatConnection) {

		}
	};
	
}
