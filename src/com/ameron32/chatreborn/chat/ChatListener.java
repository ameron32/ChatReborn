package com.ameron32.chatreborn.chat;

import com.ameron32.chatreborn.chat.MessageTemplates.*;
import com.ameron32.chatreborn.services.ChatServer.ChatConnection;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;

public abstract class ChatListener extends Listener {

	private Connection connection;
	private Object object;
	
	public Connection getConnection() {	return connection; }
	public void setConnection(Connection connection) { this.connection = connection; }
	public Object getObject() { return object; }
	public void setObject(Object object) { this.object = object; }

	private void init(final Connection connection) {
		this.setConnection(connection);
	}
	
	private void init(final Connection connection, final Object object) {
		init(connection);
		this.setObject(object);
	}
	
	private void term() {
		this.setConnection(null);
		this.setObject(null);
	}
	
//	private boolean chatObjectReceived = false;
	
	private boolean isDisabled = false;
	public void setDisabled(Boolean state) {
		isDisabled = state;
	}
	public boolean isDisabled() {
		return isDisabled;
	}
	
	public void connected(final Connection connection) {
		init(connection);
		
		connected();
		
		term();
	}
	
	public void received(final Connection connection, final Object object) {
		if (isDisabled()) 
			return;
		if (object instanceof FrameworkMessage)
			return;
		
		init(connection, object);
		
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
		
		if (object instanceof MessageClass) {
			chatObjectReceived = true;
			final MessageClass messageClass = (MessageClass) object;
			received(messageClass, cc);
		}
		
		if (object instanceof ServerChatHistory) {
			chatObjectReceived = true;
			final ServerChatHistory serverChatHistory = (ServerChatHistory) object;
			received(serverChatHistory, cc);
		}
				
		onReceivedComplete(chatObjectReceived);
		
		term();
	}
	
	public void disconnected(final Connection connection) {
		init(connection);
		
		if (connection instanceof ChatConnection) {
			final ChatConnection cc = (ChatConnection) connection;
			disconnected(cc);
		}
		
		term();
	}
	
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
	
	protected void received(final MessageClass messageClass, final ChatConnection chatConnection) {
		
	}
	
	protected void received(final ServerChatHistory serverChatHistory, final ChatConnection chatConnection) {
		
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
