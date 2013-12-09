package com.ameron32.chatreborn.chat.client;

import java.io.IOException;

import android.content.ComponentName;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import com.ameron32.chatreborn.R;
import com.ameron32.chatreborn.R.layout;
import com.ameron32.chatreborn.chat.Global;
import com.ameron32.chatreborn.chat.Network;
import com.ameron32.chatreborn.chat.Network.ChatMessage;
import com.ameron32.chatreborn.chat.Network.MessageClass;
import com.ameron32.chatreborn.chat.Network.RegisterName;
import com.ameron32.chatreborn.chat.Network.SystemMessage;
import com.ameron32.chatreborn.chat.Network.UpdateNames;
import com.ameron32.chatreborn.chat.Utils;
import com.ameron32.chatreborn.chat.client.ChatClient.MyClientBinder;
import com.ameron32.chatreborn.frmwk.ChatService.MyBinder;
import com.ameron32.chatreborn.frmwk.NetworkTask;
import com.ameron32.chatreborn.frmwk.NetworkTask.Task;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

public class ChatClientFragment extends Fragment {

	private View v;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.chat_client, container, false);
		return v;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	private void initChatFrame() {
		chatFrame.setSendListener(sendMessage);
		chatFrame.setActionListener(sendAction);
		chatFrame.setConnectToServerListener(connectToServer);
	}

	private Runnable sendMessage = new Runnable() {
		public void run() {
			final String message = chatFrame.getSendText();
			final ChatMessage chatMessage = 
					new ChatMessage();
			chatMessage.name = username;
			chatMessage.setText(message);
			if (isBound_mConnection) {
				chatClient.getClient().sendTCP(chatMessage);
			} else {
				Log.error(getClass().getSimpleName(), "ChatClient not bound");
			}
		}
	};
	private Runnable sendAction = new Runnable() {
		public void run() {
			final SystemMessage actionMessage = 
					new SystemMessage();
			actionMessage.name = username;
			actionMessage.setText("test Action");
			if (isBound_mConnection) {
				chatClient.getClient().sendTCP(actionMessage);
			} else {
				Log.error(getClass().getSimpleName(), "ChatClient not bound");
			}
		}
	};
	private Runnable connectToServer = new Runnable() {
		@Override
		public void run() {
			new NetworkTask(Task.Connect, username, chatClient.getClient(),
					chatFrame).execute();
		}
	};

	private ChatClientFrame chatFrame;
	private ChatClient chatClient;
	private String username = Global.Local.username; 
	
	@Override
	public void onStart() {
		super.onStart();
		getActivity().bindService(new Intent(getActivity(), ChatClient.class), mConnection, ContextWrapper.BIND_AUTO_CREATE);
		chatFrame = new ChatClientFrame(getActivity(), getView());
		initChatFrame();
	}
	
	@Override
	public void onStop() {
		getActivity().unbindService(mConnection);	
		super.onStop();
		chatFrame = null;
	}

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
				chatClient.getClient().addListener(chatClientListener);
			} else {
				Toast.makeText(getActivity(), "chatServer is Null",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			if (chatClient != null) {
				isBound_mConnection = false;
				chatClient.isBound = false;
				chatClient.getClient().removeListener(chatClientListener);
			} else {
				Toast.makeText(getActivity(), "chatServer is Null",
						Toast.LENGTH_SHORT).show();
			}
		}

	}
	
	final Listener chatClientListener = new Listener() {
		public void connected(Connection connection) {
			
		}

		public void received(Connection connection, Object object) {
			if (object instanceof UpdateNames) {
				updateChatFrame(object, FrameProcess.SetNames);
				return;
			}

			if (object instanceof ChatMessage) {
				updateChatFrame(object, FrameProcess.AddMessage);
				return;
			}
			
			if (object instanceof SystemMessage) {
				updateChatFrame(object, FrameProcess.MakeToast);
				return;
			}
		}

		public void disconnected(Connection connection) {

		}
	};
	
	private void updateChatFrame(Object object, FrameProcess process) {
		if (chatFrame != null && getActivity() != null) {
			switch (process) {
			case SetNames:
				final UpdateNames updateNames = (UpdateNames) object;
//				Global.Local.groupUsers = updateNames.names;
				chatFrame.setNames(updateNames.names);
				break;
			case AddMessage:
				final ChatMessage chatMessage = (ChatMessage) object;
				Global.Local.clientChatHistory.put(chatMessage.getTimeStamp(), chatMessage);
				chatFrame.addMessage(chatMessage);
				break;
			case MakeToast:
				final SystemMessage sysMessage = (SystemMessage) object;
				if (getActivity() != null) {
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
Toast.makeText(getActivity(), "CC-Sys: " + sysMessage.getText(), Toast.LENGTH_SHORT).show();
						}
					});
				}
				break;
			}
		}
	}
	
	private enum FrameProcess {
		SetNames, AddMessage, MakeToast
	}
}
