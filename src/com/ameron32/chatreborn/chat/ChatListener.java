package com.ameron32.chatreborn.chat;

import com.ameron32.chatreborn.chat.Network.ChatMessage;
import com.ameron32.chatreborn.chat.Network.MessageClass;
import com.ameron32.chatreborn.chat.Network.RegisterName;
import com.ameron32.chatreborn.chat.Network.ServerChatHistory;
import com.ameron32.chatreborn.chat.Network.SystemMessage;
import com.ameron32.chatreborn.chat.Network.UpdateNames;
import com.ameron32.chatreborn.services.ChatServer.ChatConnection;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public abstract class ChatListener extends Listener {

	private Connection connection;
	private Object object;
	
	public Connection getConnection() {	return connection; }
	public void setConnection(Connection connection) { this.connection = connection; }
	public Object getObject() { return object; }
	public void setObject(Object object) { this.object = object; }

	private void init(Connection connection) {
		this.setConnection(connection);
	}
	
	private void init(Connection connection, Object object) {
		init(connection);
		this.setObject(object);
	}
	
	private void term() {
		this.setConnection(null);
		this.setObject(null);
	}
	
	private boolean chatObjectReceived = false;
	
	private boolean isDisabled = false;
	public void setDisabled(Boolean state) {
		isDisabled = state;
	}
	public boolean isDisabled() {
		return isDisabled;
	}
	
	public void connected(final Connection connection) {
		init(connection);
		
//		if (connection instanceof ChatConnection) {
//			final ChatConnection cc = (ChatConnection) connection;
//			connected(cc);
//		}
		connected();
		
		term();
	}
	
	protected void connected() {
		
	}

	public void received(final Connection connection, final Object object) {
		if (isDisabled()) 
			return;
		
		init(connection, object);
		
		ChatConnection cc = null;
		if (connection instanceof ChatConnection) {
			cc = (ChatConnection) connection;
		}
		
		if (object instanceof RegisterName) {
			chatObjectReceived = true;
			final RegisterName registerName = (RegisterName) object;
			received(cc, registerName);
		}
		
		if (object instanceof UpdateNames) {
			chatObjectReceived = true;
			final UpdateNames updateNames = (UpdateNames) object;
			received(cc, updateNames);
		}
		
		if (object instanceof ChatMessage) {
			chatObjectReceived = true;
			final ChatMessage chatMessage = (ChatMessage) object;
			received(cc, chatMessage);
		}
		
		if (object instanceof SystemMessage) {
			chatObjectReceived = true;
			final SystemMessage systemMessage = (SystemMessage) object;
			received(cc, systemMessage);
		} 
		
		if (object instanceof MessageClass) {
			chatObjectReceived = true;
			final MessageClass messageClass = (MessageClass) object;
			received(cc, messageClass);
		}
		
		if (object instanceof ServerChatHistory) {
			chatObjectReceived = true;
			final ServerChatHistory serverChatHistory = (ServerChatHistory) object;
			received(cc, serverChatHistory);
		}
		
		if (chatObjectReceived)	
			onReceivedComplete();
		
		term();
	}
	
	protected abstract void received(final ChatConnection chatConnection, final RegisterName registerName);
	
	protected abstract void received(final ChatConnection chatConnection, final UpdateNames updateNames);
	
	protected abstract void received(final ChatConnection chatConnection, final ChatMessage chatMessage);

	protected abstract void received(final ChatConnection chatConnection, final SystemMessage systemMessage);
	
	protected abstract void received(final ChatConnection chatConnection, final MessageClass messageClass);
	
	protected abstract void received(final ChatConnection chatConnection, final ServerChatHistory serverChatHistory);
	
	public void disconnected(final Connection connection) {
		init(connection);
		
		if (connection instanceof ChatConnection) {
			final ChatConnection cc = (ChatConnection) connection;
			disconnected(cc);
		}
		
		term();
	}
	
	protected abstract void disconnected(final ChatConnection chatConnection);
	
	protected void onReceivedComplete() {
		
	}
}
