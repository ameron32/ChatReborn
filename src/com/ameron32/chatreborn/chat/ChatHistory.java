package com.ameron32.chatreborn.chat;

import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;

import com.ameron32.chatreborn.chat.MessageTemplates.MessageClass;
import com.ameron32.chatreborn.chat.MessageTemplates.*;

public class ChatHistory {

	private final TreeMap<Long, MessageClass> completeHistory = new TreeMap<Long, MessageClass>();
	private final TreeMap<Long, MessageClass> filteredHistory = new TreeMap<Long, MessageClass>();
	
	public ChatHistory() {
//		filterTags.add(MessageTag.ServerChatter);
	}
	
	public void addToHistory(MessageClass mc) {
		completeHistory.put(mc.getTimeStamp(), mc);
		addToFilteredHistory(mc);
	}
	
	public void addToHistory(TreeMap<Long, MessageClass> additions) {
		for (Long key : additions.keySet()) {
			addToHistory(completeHistory.get(key));
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
	
	private void addToFilteredHistory(TreeMap<Long, MessageClass> additions) {
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
	public TreeMap<Long, MessageClass> getCompleteHistory() {
		return completeHistory;
	}

	public TreeMap<Long, MessageClass> getFilteredHistory() {
		return filteredHistory;
	}
}
