package com.ameron32.chatreborn.fragments;

import java.util.ArrayList;

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

import com.ameron32.chatreborn.chat.Global;
import com.ameron32.chatreborn.chat.MessageTemplates.*;
import com.ameron32.chatreborn.frmwk.ChatListener;
import com.ameron32.chatreborn.frmwk.CustomChatListener;
import com.ameron32.chatreborn.helpers.NetworkTask;
import com.ameron32.chatreborn.helpers.NetworkTask.Task;
import com.ameron32.chatreborn.services.ChatClient;
import com.ameron32.chatreborn.services.ChatClient.MyClientBinder;
import com.ameron32.chatreborn.services.ChatServer.ChatConnection;
import com.ameron32.chatreborn.ui.ChatClientFrame;
import com.ameron32.knbasic.core.chat.R;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

public class ChatClientFragment extends Fragment {
	
	// ----------------------------------------
	// LIFECYCLE CODE
	// ----------------------------------------
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.chat_client, container, false);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	private ChatClientFrame chatFrame;
	private ChatClient chatClient;
	private String username = Global.Local.username; 
	
	@Override
	public void onStart() {
		super.onStart();
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}
	
	private void initChatClientFrame() {
		if (chatClient != null && chatClient.getIsConnected())
			chatFrame.updateUIConnected();
	}
	
	public boolean isChatClientConnected() {
		if (chatClient != null)
			return chatClient.getIsConnected();
		return false;
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	public void bindClientService() {
		
		getActivity().bindService(new Intent(getActivity(), ChatClient.class), mConnection, ContextWrapper.BIND_AUTO_CREATE);
//		chatClientListener2.setDisabled(false);
		if (chatFrame == null) 
			chatFrame = new ChatClientFrame(getActivity(), getView());

		initChatClientFrame();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	public void unbindClientService() {

		getActivity().unbindService(mConnection);
//		chatClientListener2.setDisabled(true);
		
		chatFrame = null;
	}
	
	private void initiateConnectionToServer() {
		connectToServer.run();
	}
	
	private void initiateDisconnectFromServer() {
		// nothing yet
	}
	
	
	// ----------------------------------------
	// CHATLISTENER HANDLING
	// ----------------------------------------
//	private final ArrayList<Listener> chatClientListeners = new ArrayList<Listener>();
//
//	private void addDefaultListeners() {
//		addChatClientListener(chatClientListener2);
//	}
//
//	public void addChatClientListener(Listener l) {
//		chatClientListeners.add(l);
//	}

	private final CustomChatListener chatClientListener2 = new CustomChatListener(true) {
		@Override
		protected void received(final UpdateNames updateNames, final ChatConnection chatConnection) {
			chatFrame.setNames(updateNames.names);
		}
		
		@Override
		protected void received(final SystemMessage systemMessage,
				final ChatConnection chatConnection) {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (systemMessage.getText().equals("isTyping")) {
						chatFrame.setIsTyping(systemMessage.name, true);
					} else if (systemMessage.getText().equals("isNotTyping")) {
						chatFrame.setIsTyping(systemMessage.name, false);
					}
				}
			});
		}

		@Override
		protected void disconnected(final ChatConnection chatConnection) {
			
		}
		
		@Override
		protected void onReceivedComplete(boolean wasChatObjectReceived) {
				refreshChatHistory();
		}
	};
	
	public void refreshChatHistory() {
		chatFrame.refreshChatHistory();
	}

	public void sendMessage(final String msg) {
		final ChatMessage chatMessage = new ChatMessage();
		chatMessage.name = username;
		chatMessage.setText(msg);
		sendMessage(chatMessage);
	}
	
	public void sendSystemMessage(final String msg) {
		final SystemMessage systemMessage = new SystemMessage();
		systemMessage.name = username;
		systemMessage.setText(msg);
		sendMessage(systemMessage);
	}
	
	public void sendMessage(final MessageBase mc) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				if (isBound_mConnection) {
					NetworkTask task = new NetworkTask(Task.SendMessage,
							chatClient, mc);
					task.execute();
				} else {
					Log.error(getClass().getSimpleName(),
							"ChatClient not bound");
				}
			}
		};
		r.run();
	}

	private Runnable connectToServer = new Runnable() {
		@Override
		public void run() {
			NetworkTask task = new NetworkTask(Task.Connect, username,
					chatClient, chatFrame);
			task.execute();
		}
	};
	
	
	// ----------------------------------------
	// SERVICECONNECTION HANDLING
	// ----------------------------------------
	private MyServiceConnection mConnection = new MyServiceConnection();
	private boolean isBound_mConnection = false;
	public class MyServiceConnection implements ServiceConnection {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MyClientBinder mBinder = (MyClientBinder) service;
			chatClient = (ChatClient) mBinder.getService();
			if (chatClient != null) {
				isBound_mConnection = true;
				chatClient.isBound = true;

				// TODO surround this with a "isConnected" catch (to prevent
				// unnecessary restarts of the client)
//				if (!chatClient.getIsConnected() && chatClient.getIsPrepared()) {
				if (chatClient.getIsPrepared() && !chatClient.getIsConnected()) {
//					addDefaultListeners();
//					for (Listener l : chatClientListeners) {
//						chatClient.getClient().addListener(l);
//					}
//				  chatClientListener2.enable();

					initiateConnectionToServer();
				}
			} else {
				Toast.makeText(getActivity(), 
						"chatServer is Null",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			if (chatClient != null) {
				isBound_mConnection = false;
				chatClient.isBound = false;
		
//				if (chatClient.getIsConnected()) {
//					for (Listener l : chatClientListeners) {
//						chatClient.getClient().removeListener(l);
//					}
//				}
			} else {
				Toast.makeText(getActivity(), 
						"chatServer is Null",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

}
