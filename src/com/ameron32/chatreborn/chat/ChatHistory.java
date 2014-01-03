package com.ameron32.chatreborn.chat;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import android.util.Log;

import com.ameron32.chatreborn.chat.MessageTemplates.MessageBase;
import com.ameron32.chatreborn.chat.MessageTemplates.*;

public class ChatHistory {

	private final TreeMap<Long, MessageBase> completeHistoryCore = new TreeMap<Long, MessageBase>();
	private final TreeMap<Long, MessageBase> filteredHistoryCore = new TreeMap<Long, MessageBase>();
	
	private final Map<Long, MessageBase> completeHistory;
	private final Map<Long, MessageBase> filteredHistory;
	
	public ChatHistory() {
		completeHistory = Collections.synchronizedMap(completeHistoryCore);
		filteredHistory = Collections.synchronizedMap(filteredHistoryCore);
		// filterTags.add(MessageTag.ServerChatter);
	}
	
	public void addToHistory(MessageBase mc) {
		Log.d("ChatHistory", mc.toString());//
		completeHistory.put(mc.getTimeStamp(), mc);
		addToFilteredHistory(mc);
	}
	
	public void addToHistory(TreeMap<Long, MessageBase> additions) {
		for (Long key : additions.keySet()) {
			addToHistory(additions.get(key));
		}
	}
	
	public void unpackServerHistory(TreeMap<Long, MessageBase> historyBundle) {
		clearChatHistory();
		addToHistory(historyBundle);
	}
	
	public void clearChatHistory() {
		completeHistory.clear();
		filteredHistory.clear();
	}
	
	
	
	
	
	// FILTER METHODS
	TreeSet<MessageTag> filterTags = new TreeSet<MessageTag>();
	
	private boolean addToFilteredHistory(MessageBase mc) {
		if (mc.hasAnyOfTags(filterTags.toArray(new MessageTag[0]))) {
			return false;
		}
		filteredHistory.put(mc.getTimeStamp(), mc);
		return true;
	}
	
	private void addToFilteredHistory(Map<Long, MessageBase> additions) {
		for (Long key : additions.keySet()) {
			addToFilteredHistory(additions.get(key));
		}
	}
	
	public void setFilters(MessageTag...tags) {
		filterTags.clear();
		filterTags.addAll(Arrays.asList(tags));
		resetFilteredChatHistory();
		// need to notifyDataSetChanged
	}
	
	private void resetFilteredChatHistory() {
		filteredHistory.clear();
		
		addToFilteredHistory(completeHistory);
	}

	
	// GETTERS / SETTERS
	public Map<Long, MessageBase> getCompleteHistory() {
		return completeHistory;
	}

	public Map<Long, MessageBase> getFilteredHistory() {
		return filteredHistory;
	}
}
