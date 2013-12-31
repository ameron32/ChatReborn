package com.ameron32.chatreborn.chat;

import java.util.TreeMap;

import com.ameron32.chatreborn.chat.MessageTemplates.*;

public class ChatHistory {

	private final TreeMap<Long, MessageClass> completeHistory = new TreeMap<Long, MessageClass>();
	private final TreeMap<Long, MessageClass> filteredHistory = new TreeMap<Long, MessageClass>();
	
	public ChatHistory() {
		
	}
	
}
