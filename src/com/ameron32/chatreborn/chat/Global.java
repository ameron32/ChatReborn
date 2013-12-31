package com.ameron32.chatreborn.chat;

import java.util.ArrayList;
import java.util.TreeMap;

import com.ameron32.chatreborn.chat.MessageTemplates.MessageClass;

public class Global {
	/**
	 * WARNING, THIS CLASS IS NOT THREAD-SAFE. THAT'S PROBABLY WHY ON RARE OCCASSIONS
	 * THE APPLICATION WILL CRASH WHEN THE TREEMAPS ARE ALTERED FROM MULTIPLE THREADS.
	 */

	/**
	 * 
	 */
	public static void set() {
//		Local.username = "user" + (new java.util.Random().nextInt(90) + 10);
//		Local.hostname = "localhost";
	}
	
	public static class Server {
		private static final TreeMap<Long, MessageClass> serverChatHistory 
			= new TreeMap<Long, MessageClass>();
		public static void addToHistory(MessageClass mc) {
			serverChatHistory.put(mc.getTimeStamp(), mc);
		}
		public static TreeMap<Long, MessageClass> getServerChatHistory() {
			return new TreeMap<Long, MessageClass>(serverChatHistory);
		}
		public static final ArrayList<String> connectedUsers 
			= new ArrayList<String>();
		public static void removeUser(String s) {
			for (String user : connectedUsers) {
				if (user.equalsIgnoreCase(s)) connectedUsers.remove(user);
			}
		}
	}

	public static class Local {
		public static String username = "user"
				+ (new java.util.Random().nextInt(90) + 10);
		public static String hostname = "localhost";

		public static String[] groupUsers = { "" };
		private static final TreeMap<Long, MessageClass> clientChatHistory = new TreeMap<Long, MessageClass>();

		public static TreeMap<Long, MessageClass> getClientChatHistory() {
			return clientChatHistory;
		}

		private static final TreeMap<Long, MessageClass> clientChatHistoryFiltered = new TreeMap<Long, MessageClass>();

		public static void addToHistory(TreeMap<Long, MessageClass> additions) {
			clientChatHistory.putAll(additions);
		}

		public static void addToHistory(MessageClass mc) {
			clientChatHistory.put(mc.getTimeStamp(), mc);
		}

		public static void unpackServerHistory(TreeMap<Long, MessageClass> historyBundle) {
			clientChatHistory.clear();
			addToHistory(historyBundle);
		}

		public static void clearChatHistory() {
			clientChatHistory.clear();
		}
	}
	
}
