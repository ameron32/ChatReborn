package com.ameron32.chatreborn.chat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import com.ameron32.chatreborn.chat.Network.MessageClass;

public class Global {

	public static class Server {
		public static final HashMap<Long, MessageClass> serverChatHistory = new HashMap<Long, MessageClass>();
	}

	public static class Local {
		public static String username = "user";
		public static String hostname = "localhost";
		
		public static String[] groupUsers = { "" };
		public static final HashMap<Long, MessageClass> clientChatHistory = new HashMap<Long, MessageClass>();
	}
	
	public static class ChatOrganizer {
		public static ArrayList<MessageClass> getClientChatHistory() {
			ArrayList<MessageClass> values = (ArrayList<MessageClass>) Global.Local.clientChatHistory.values();
			if (values != null && values.size() > 0) {
				Collections.sort(values, new Comparator<MessageClass>() {
					@Override
					public int compare(MessageClass lhs, MessageClass rhs) {
						return Long.signum(lhs.getTimeStamp()
								- rhs.getTimeStamp());
					}
				});
			}
			return values;
		}
	}
}
