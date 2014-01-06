package com.ameron32.chatreborn.frmwk;

import java.util.ArrayList;

import android.util.Log;

import com.ameron32.chatreborn.chat.MessageTemplates.*;
import com.ameron32.chatreborn.services.ChatServer.ChatConnection;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;

public abstract class ChatListener extends Listener {

  // STATIC ACCESS TO ALL CHATLISTENERS
  // -----------------------------------------------------------------
	private Connection connection;
	private Object object;
	
	public Connection getConnection() {	return connection; }
	public void setConnection(Connection connection) { this.connection = connection; }
	public Object getObject() { return object; }
	public void setObject(Object object) { this.object = object; }
	
	
	// STATIC ACCESS TO ALL CHATLISTENERS
	// -----------------------------------------------------------------
	private static ArrayList<ChatListener> chatListeners = new ArrayList<ChatListener>();
	public static void setAllDisabled(boolean state) {
	  for (ChatListener cl : chatListeners) {
	    cl.setDisabled(state);
	  }
	}
	public static void setAllDisabledOf(boolean state, Class<?> type) {
	   for (ChatListener cl : chatListeners) {
	     if (type.isInstance(cl)) cl.setDisabled(state);
	   }
	}
	public static void setAllDisabledOf(boolean state, Class<?>... types) {
	  for (Class<?> type : types) {
	    setAllDisabledOf(state, type);
	  }
	}
  public static ArrayList<ChatListener> getListeners() {
    return chatListeners;
  }
  
	
	// CONSTRUCTOR RELATED METHODS
  // -----------------------------------------------------------------
	public ChatListener(boolean register) {
	  setDisabled(true);
	  if (register) register();
	}
	private void register() {
	  chatListeners.add(this);
	}
	public void enable() {
	  setDisabled(false);
	}
	
	
	// STATE MANAGEMENT
  // -----------------------------------------------------------------
	private boolean isDisabled = false;
	public void setDisabled(Boolean state) {
	  isDisabled = state;
	}
	public boolean isDisabled() {
	  return isDisabled;
	}
	
	
	// CORE HANDLING
	// -----------------------------------------------------------------
	private void prepare(final Connection connection) {
		this.setConnection(connection);
	}
	
	private void prepare(final Connection connection, final Object object) {
		prepare(connection);
		this.setObject(object);
	}
	
	private void forget() {
		this.setConnection(null);
		this.setObject(null);
	}
	
	public void connected(final Connection connection) {
		prepare(connection);
		
		connected();
		
		forget();
	}
	
	public void received(final Connection connection, final Object object) {
		if (isDisabled()) 
			return;
		if (object instanceof FrameworkMessage)
			return;
		
		prepare(connection, object);
		
		onReceivedStart(object, connection);
		
		ChatConnection cc = null;
		if (connection instanceof ChatConnection) {
			cc = (ChatConnection) connection;
		}
		
		boolean chatObjectReceived = false;
		if (object instanceof RegisterName) {
			chatObjectReceived = true;
			final RegisterName registerName = (RegisterName) object;
			received(registerName, cc);
		}
		
		if (object instanceof UpdateNames) {
			chatObjectReceived = true;
			final UpdateNames updateNames = (UpdateNames) object;
			received(updateNames, cc);
		}
		
		if (object instanceof ChatMessage) {
			chatObjectReceived = true;
			final ChatMessage chatMessage = (ChatMessage) object;
			received(chatMessage, cc);
		}
		
		if (object instanceof SystemMessage) {
			chatObjectReceived = true;
			final SystemMessage systemMessage = (SystemMessage) object;
			received(systemMessage, cc);
		} 
		
		if (object instanceof MessageBase) {
			chatObjectReceived = true;
			final MessageBase messageClass = (MessageBase) object;
			received(messageClass, cc);
		}
		
		if (object instanceof ServerChatHistory) {
			chatObjectReceived = true;
			final ServerChatHistory serverChatHistory = (ServerChatHistory) object;
			received(serverChatHistory, cc);
		}
				
		onReceivedComplete(chatObjectReceived);
		
		forget();
	}
	
	public void disconnected(final Connection connection) {
		prepare(connection);
		
		if (connection instanceof ChatConnection) {
			final ChatConnection cc = (ChatConnection) connection;
			disconnected(cc);
		}
		
		forget();
	}
	
	
	// OVERRIDES
  // -----------------------------------------------------------------
	protected void connected() {
		
	}

	protected void received(final RegisterName registerName, final ChatConnection chatConnection) {
		
	}
	
	protected void received(final UpdateNames updateNames, final ChatConnection chatConnection) {
		
	}
	
	protected void received(final ChatMessage chatMessage, final ChatConnection chatConnection) {
		
	}
	
	protected void received(final SystemMessage systemMessage, final ChatConnection chatConnection) {
		
	}
	
	protected void received(final MessageBase messageClass, final ChatConnection chatConnection) {
		
	}
	
	protected void received(final ServerChatHistory serverChatHistory, final ChatConnection chatConnection) {
		String progress = serverChatHistory.getPart() + " of " + serverChatHistory.getTotalParts();
	  Log.e("ChatListener", "SCH: " + progress + " received.");
	}
	
	protected void disconnected(final ChatConnection chatConnection) {
		
	}
	
	/**
	 * First Override-Accessible method. Constants are available, if needed.
	 * 
	 * @param object
	 * @param connection
	 */
	protected void onReceivedStart(final Object object, final Connection connection) {
		
	}
	
	/**
	 * Final Override-Accessible method. Boolean
	 * 
	 * @param says if an object defined as a ChatObject was received.
	 */
	protected void onReceivedComplete(final boolean wasChatObjectReceived) {
		
	}
	
}
