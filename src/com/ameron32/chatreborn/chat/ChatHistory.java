package com.ameron32.chatreborn.chat;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import android.util.Log;

import com.ameron32.chatreborn.chat.MessageTemplates.MessageClass;
import com.ameron32.chatreborn.chat.MessageTemplates.*;

public class ChatHistory {

	private final TreeMap<Long, MessageClass> completeHistoryCore = new TreeMap<Long, MessageClass>();
	private final TreeMap<Long, MessageClass> filteredHistoryCore = new TreeMap<Long, MessageClass>();
	
	private final Map<Long, MessageClass> completeHistory;
	private final Map<Long, MessageClass> filteredHistory;
	
	public ChatHistory() {
		completeHistory = Collections.synchronizedMap(completeHistoryCore);
		filteredHistory = Collections.synchronizedMap(filteredHistoryCore);
		// filterTags.add(MessageTag.ServerChatter);
	}
	
	public void addToHistory(MessageClass mc) {
		Log.d("ChatHistory", mc.toString());//
		completeHistory.put(mc.getTimeStamp(), mc);
		addToFilteredHistory(mc);
	}
	
	public void addToHistory(TreeMap<Long, MessageClass> additions) {
		for (Long key : additions.keySet()) {
			addToHistory(additions.get(key));
		}
	}
	
	public void unpackServerHistory(TreeMap<Long, MessageClass> historyBundle) {
		clearChatHistory();
		addToHistory(historyBundle);
	}
	
	public void clearChatHistory() {
		completeHistory.clear();
		filteredHistory.clear();
	}
	
	
	
	
	
	// FILTER METHODS
	TreeSet<MessageTag> filterTags = new TreeSet<MessageTag>();
	
	private boolean addToFilteredHistory(MessageClass mc) {
		if (mc.hasAnyOfTags(filterTags.toArray(new MessageTag[0]))) {
			return false;
		}
		filteredHistory.put(mc.getTimeStamp(), mc);
		return true;
	}
	
	private void addToFilteredHistory(Map<Long, MessageClass> additions) {
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
	public Map<Long, MessageClass> getCompleteHistory() {
		return completeHistory;
	}

	public Map<Long, MessageClass> getFilteredHistory() {
		return filteredHistory;
	}
}
