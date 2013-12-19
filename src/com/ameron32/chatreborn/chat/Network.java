package com.ameron32.chatreborn.chat;

import java.util.TreeMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Network {

	static public final int port = 54555;

	// This registers objects that are going to be sent over the network.
	static public void register(EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(RegisterName.class);
		kryo.register(String[].class);
		kryo.register(UpdateNames.class);
		kryo.register(ChatMessage.class);
		kryo.register(SystemMessage.class);
		kryo.register(ServerChatHistory.class);
		kryo.register(TreeMap.class);
	}

	static public class UpdateNames {
		public String[] names;
		
		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			for (String s: names) {
				sb.append(":[" + s + "]");
			}
			return sb.toString();
		}
	}

	
	static public class NamedClass {
		public String name;

		@Override
		public String toString() {
			return getClass().getSimpleName() + ":Name=" + name;
		}
	}
	
	static public class RegisterName extends NamedClass {

	}
	
	static public class MessageClass extends NamedClass {
		private long serverTimeStamp;
		private String text;
		
		public MessageClass() {
			setTime();
		}
		
		private void setTime() {
			serverTimeStamp = System.currentTimeMillis();
		}
		public long getTimeStamp() {
			return serverTimeStamp;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		
		@Override
		public String toString() {
			return super.toString() + ":Message=" + text;
		}
	}
	
	static public class ChatMessage extends MessageClass {

	}
	
	// NEW
	static public class SystemMessage extends MessageClass {
		private boolean isHistoryRequest = false;
		public void setIsHistoryRequest(boolean b) {
			isHistoryRequest = b;
		}
		public boolean getIsHistoryRequest() {
			return isHistoryRequest;
		}
	}
	
	static public class ServerChatHistory extends NamedClass {
		private long serverTimeStamp;
		public ServerChatHistory() {
			setTime();
		}
		private void setTime() {
			serverTimeStamp = System.currentTimeMillis();
		}
		public long getTimeStamp() {
			return serverTimeStamp;
		}
		
		// History
		private final TreeMap<Long, MessageClass> chatHistoryBundle 
				= new TreeMap<Long, MessageClass>();
		public void loadHistory(TreeMap<Long, MessageClass> history) {
			Long[] keySet = history.keySet().toArray(new Long[0]);
			for (int i = 0; i < history.size(); i++) {
				long key = keySet[i];
				chatHistoryBundle.put(key, history.get(key));
			}
		}
		public TreeMap<Long, MessageClass> getHistoryBundle() {
			return chatHistoryBundle;
		}
	}
	
}
