package com.ameron32.chatreborn.services;

import android.content.Intent;
import android.os.IBinder;

import com.ameron32.chatreborn.chat.Global;
import com.ameron32.chatreborn.chat.MessageTemplates.ChatMessage;
import com.ameron32.chatreborn.chat.MessageTemplates.MessageTag;
import com.ameron32.chatreborn.chat.MessageTemplates.RegisterName;
import com.ameron32.chatreborn.chat.MessageTemplates.ServerChatHistory;
import com.ameron32.chatreborn.chat.MessageTemplates.SystemMessage;
import com.ameron32.chatreborn.chat.MessageTemplates.UpdateNames;
import com.ameron32.chatreborn.chat.Network;
import com.ameron32.chatreborn.frmwk.ChatListener;
import com.ameron32.chatreborn.frmwk.ChatService;
import com.ameron32.chatreborn.frmwk.CustomChatListener;
import com.ameron32.chatreborn.frmwk.EmailListener;
import com.ameron32.chatreborn.frmwk.NotificationListener;
import com.ameron32.chatreborn.frmwk.PopupListener;
import com.ameron32.chatreborn.services.ChatServer.ChatConnection;
import com.esotericsoftware.kryonet.Client;

public class ChatClient extends ChatService {
	private Client client;
	public Client getClient() {
		return client;
	}
	
	private void init() {
		client.start();
//		Global.set();
		
    masterListener = new ChatListener(true) {
      
      @Override
      protected void connected() {
        final RegisterName registerName = new RegisterName();
        registerName.name = Global.Local.username;
        client.sendTCP(registerName);
        
        final SystemMessage request = new SystemMessage();
        request.name = Global.Local.username;
        request.setText("history request");
        // request.setIsHistoryRequest(true);
        request.attachTags(MessageTag.ClientHistoryRequest);
        client.sendTCP(request);
      }
      
      @Override
      protected void received(final ServerChatHistory serverChatHistory, final ChatConnection chatConnection) {
        super.received(serverChatHistory, chatConnection);
        Global.Local.unpackServerHistory(serverChatHistory.getHistoryBundle());
      }
      
      @Override
      protected void received(final SystemMessage systemMessage, final ChatConnection chatConnection) {
        Global.Local.addToHistory(systemMessage);
      }
      
      @Override
      protected void received(final ChatMessage chatMessage, final ChatConnection chatConnection) {
        Global.Local.addToHistory(chatMessage);
      }
      
      @Override
      protected void received(final UpdateNames updateNames, final ChatConnection chatConnection) {
        Global.Local.groupUsers = updateNames.names;
      }
      
      @Override
      protected void disconnected(final ChatConnection chatConnection) {
        Global.Local.clearChatHistory();
      }
    };
    
    chatListener = new NotificationListener(true) {
      
      @Override
      protected void received(final SystemMessage systemMessage, final ChatConnection chatConnection) {
        if (!isBound) {
          notifyMessage(systemMessage.name + "[" + systemMessage.getText() + "]");
        }
      }
      
      @Override
      protected void received(final ChatMessage chatMessage, final ChatConnection chatConnection) {
        if (!isBound) {
          notifyMessage(chatMessage.name + " says: " + chatMessage.getText());
        }
      }
      
      @Override
      protected void received(final UpdateNames updateNames, final ChatConnection chatConnection) {
        if (!isBound) {
          notifyMessage("Users Changed");
        }
      }
    };
	
    Network.register(client);

		for (ChatListener cl : ChatListener.getListeners()) {
		  client.addListener(cl);
		}
	}
	
  private NotificationListener chatListener;   
  
  private ChatListener         masterListener;
	
	
	private boolean isConnected = false;
	public boolean getIsConnected() {
		return isConnected;
	}
	public void setIsConnected(boolean state) {
		isConnected = state;
	}
	
	private boolean isPrepared = false;
	public boolean getIsPrepared() {
		return isPrepared;
	}
	public void setIsPrepared(boolean state) {
		isPrepared = state;
	}
	private void connect(String host) {
		if (!isPrepared) {
			Global.Local.hostname = host;
			client = new Client();
			init();
			
			isPrepared = true;
		}
	}
	
	private void disconnect() {
		if (isPrepared) {
			client.stop();
			client.close();
			client = null;
			
			isPrepared = false;
		}
	}
	
	// --------------------------------------
	// SERVICE Calls
	// --------------------------------------
	
	private boolean isStarted = false;
	public boolean getIsStarted() {
		return isStarted;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		com.esotericsoftware.minlog.Log
			.set(com.esotericsoftware.minlog.Log.LEVEL_DEBUG);
		if (!isStarted) {
			if (intent != null)	connect(intent.getStringExtra("host"));
			isStarted = true;
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		if (isStarted) {
			disconnect();
			isStarted = false;
		}
		super.onDestroy();
		clearNotification(getSTOP_NOTIFICATION_ID());
	}
	
	private IBinder mBinder = new MyClientBinder();

	@Override
	public IBinder onBind(Intent intent) {
		super.onBind(intent);
    // toggle Listeners
		ChatListener.setAllDisabledOf(false, CustomChatListener.class, PopupListener.class, ChatListener.class);
    ChatListener.setAllDisabledOf(true, EmailListener.class, NotificationListener.class);
		return mBinder;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
	  // toggle Listeners
	  ChatListener.setAllDisabledOf(true, CustomChatListener.class, PopupListener.class, ChatListener.class);
	  ChatListener.setAllDisabledOf(false, EmailListener.class, NotificationListener.class);
	  return super.onUnbind(intent);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		setSTART_NOTIFICATION_ID(200);
		setSTOP_NOTIFICATION_ID(201);
		
		setMyBinder(new MyClientBinder());
	}
	
	public class MyClientBinder extends MyBinder {
		public ChatClient getService() {
			return ChatClient.this;
		}
	}

}
