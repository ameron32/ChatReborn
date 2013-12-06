package com.ameron32.chatreborn;

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
import android.widget.Toast;

import com.ameron32.chatreborn.chat.Global;
import com.ameron32.chatreborn.chat.Network;
import com.ameron32.chatreborn.chat.Network.ChatMessage;
import com.ameron32.chatreborn.chat.Network.MessageClass;
import com.ameron32.chatreborn.chat.Network.RegisterName;
import com.ameron32.chatreborn.chat.Network.SystemMessage;
import com.ameron32.chatreborn.chat.Network.UpdateNames;
import com.ameron32.chatreborn.chat.client.ChatClient;
import com.ameron32.chatreborn.chat.client.ChatClientFrame;
import com.ameron32.chatreborn.chat.client.ChatClient.MyClientBinder;
import com.ameron32.chatreborn.chat.Utils;
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
		v = inflater.inflate(R.layout.chat_fragment2, container, false);
		return v;
	}

	public Intent chatClientService; 
	private ChatClientFrame chatFrame;
	private ChatClient chatClient;
	
	@Override
	public void onStart() {
		super.onStart();
		
		chatClientService = new Intent(getActivity(), ChatClient.class);
		getActivity().bindService(chatClientService, mConnection, ContextWrapper.BIND_AUTO_CREATE);
		chatFrame = new ChatClientFrame(getActivity(), v);
		initChatFrame();
	}
		
	@Override
	public void onStop() {
		getActivity().unbindService(mConnection);
		super.onStop();
	}

	private Runnable sendMessage = new Runnable() {
		public void run() {
			final String message = chatFrame.getSendText();
			final ChatMessage chatMessage = 
					new ChatMessage();
			chatMessage.name = Global.Local.username;
			chatMessage.setText(message);
			if (isBound_mConnection) {
				chatClient.getClient().sendTCP(chatMessage);
			} else {
				Log.debug(getClass().getSimpleName(), "ChatClient not bound");
			}
		}
	};
	private Runnable sendAction = new Runnable() {
		public void run() {
			final SystemMessage actionMessage = 
					new SystemMessage();
			actionMessage.name = Global.Local.username;
			actionMessage.setText("test Action");
			if (isBound_mConnection) {
				chatClient.getClient().sendTCP(actionMessage);
			} else {
				Log.debug(getClass().getSimpleName(), "ChatClient not bound");
			}
		}
	};
	
	private void initChatFrame() {
		// FIXME chatFrame integration into ChatClient
		// chatFrame = new ChatFrame(((RelativeLayout) getView().findViewById(
		// R.id.rlAll2)).getContext());
		//

		chatFrame.setSendListener(sendMessage);
		chatFrame.setActionListener(sendAction);
//		chatFrame.setEditorActionListener(r);
//		chatFrame.setKeyListener(r);
		
		chatFrame.setConnectToServerListener(new Runnable() {
			@Override
			public void run() {
				new NetworkTask(Task.Connect, chatClient.getClient(), chatFrame).execute();
			}
		});
	}

	private MyServiceConnection mConnection = new MyServiceConnection();
	private boolean isBound_mConnection = false;
	public class MyServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MyClientBinder mBinder = (MyClientBinder) service;
			chatClient = (ChatClient) mBinder.getService();
			isBound_mConnection = true;
			chatClient.isBound = true;
			
			chatClient.getClient().addListener(chatFrameListener);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			isBound_mConnection = false;
			chatClient.isBound = false;
			
			chatClient.getClient().removeListener(chatFrameListener);	
		}
		
	}
	
	final Listener chatFrameListener = new Listener() {
		public void connected(Connection connection) {
//			final RegisterName registerName = new RegisterName();
//			registerName.name = Global.Local.username;
//			client.sendTCP(registerName);
			
		}

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
			Log.debug("", "ChatClientFragment " + object.toString());
			}
		}

		public void disconnected(Connection connection) {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					 chatFrame = null;
				}
			});
		}
	};

//	public void setClientUpdateListener(Runnable r) {
//		chatFrame.updateChat();
//	}
}
