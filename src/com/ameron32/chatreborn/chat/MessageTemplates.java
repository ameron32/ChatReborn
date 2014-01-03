package com.ameron32.chatreborn.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;

public class MessageTemplates {
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

	
	static public class NamedBase {
		public String name;

		@Override
		public String toString() {
			return getClass().getSimpleName() + ":Name=" + name;
		}
	}
	
	static public class RegisterName extends NamedBase {

	}
	
	static public class MessageBase extends NamedBase {

		// FEATURES OF ALL MESSAGES
		private short revision;		  // newest revision displayed automatically by adapter.
		private long keyId;			  // no two messages will share the same keyId.

		/**
		 *	messages need a key.
		 *	the arrayAdapter should only show the most recent edit.
		 *
		 */

		private String originatorName;	// client who sent the message, or server
		private long originatingTimeStamp; // time when the client/server created the message
		long serverTimeStamp; // time when the server accepted and resent the message
			// date sorting in adapter. all overwrites keep this timeStamp.
		private long clientReceivedTimeStamp; // time when YOUR client received the message
		private String text; // message text

		private final TreeSet<MessageTag> tags = new TreeSet<MessageTag>(); 
			// like evernote tags, primarily intended for user-filtering and/or search
		
		
		// CONSTRUCTOR
		public MessageBase() {  
			setOriginatingTimeStamp();
		}
		
		// INTERACTIONS
		public long getTimeStamp() {  return serverTimeStamp;  }
		public String getText() {  return text;  }
		public void setText(String text) {  this.text = text;  }
		private void setOriginatingTimeStamp() {  originatingTimeStamp = System.currentTimeMillis();  }
		public void setServerRelayed() {  serverTimeStamp = System.currentTimeMillis();  }
		public void setClientReceived() {  clientReceivedTimeStamp = System.currentTimeMillis();  }
		
		// TAG INTERACTIONS
		public void attachTags(MessageTag...tags) {
			for (MessageTag tag : tags) {
				this.tags.add(tag);
			}
		}
		public boolean hasAnyOfTags(MessageTag...tags) {
			for (MessageTag hasTag : this.tags) {
				for (MessageTag searchTag : tags) {
					if (searchTag == hasTag)
						return true;
				}
			}
			return false;
		}
		public void removeTags(MessageTag... tags) {
			this.tags.removeAll(Arrays.asList(tags));
		}
		
		// OBJECT OVERRIDES
		@Override
		public String toString() {  return super.toString() + ":Message=" + text;  }
	}
	
	static public class ChatMessage extends MessageBase {
		// FEATURES OF CHAT-MESSAGES
	  String character; // character "tag"
		ArrayList<CommentMessage> comments; // like Google Docs comments, optionally attached to each message
		
		public void setCharacter(String character) {
		  this.character = character;
		}
	}
	
	static public class PrivateChatMessage extends ChatMessage {
		String targetName; // name of client to whom message is intended
		
	}
	
	static public class CommentMessage extends MessageBase { // attach to Chat-Message
		long attachToKeyId; // same number as keyId from messageClass
		public CommentMessage(MessageBase mc) {
			this.attachToKeyId = mc.keyId;
		}
		
	}
	
	static public class SystemMessage extends MessageBase {
//		private boolean isHistoryRequest = false;
//		public void setIsHistoryRequest(boolean b) {
//			isHistoryRequest = b;
//		}
//		public boolean getIsHistoryRequest() {
//			return isHistoryRequest;
//		}
	}
	
	static public class ServerChatHistory extends NamedBase {
		private long serverTimeStamp;
		private int part; private int totalParts;
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
		private final TreeMap<Long, MessageBase> chatHistoryBundle 
				= new TreeMap<Long, MessageBase>();
		public void loadHistory(TreeMap<Long, MessageBase> history) {
			Long[] keySet = history.keySet().toArray(new Long[0]);
			for (int i = 0; i < history.size(); i++) {
				long key = keySet[i];
				chatHistoryBundle.put(key, history.get(key));
			}
		}
		public TreeMap<Long, MessageBase> getHistoryBundle() {
			return chatHistoryBundle;
		}
    public int getPart() {
      return part;
    }
    private void setPart(int part) {
      this.part = part;
    }
    public int getTotalParts() {
      return totalParts;
    }
    private void setTotalParts(int totalParts) {
      this.totalParts = totalParts;
    }
    public void setPartOfTotalParts(int part, int totalParts) {
      setPart(part);
      setTotalParts(totalParts);
    }
	}
	
	
	
	
	public enum MessageTag {
		// SYSTEM-MESSAGE
		// from Server communication
		ServerChatter, // connection, disconnection, requestForHistory logging
		ServerInfo, // number connected, number of messages stored, traffic information
		ServerUpdate, // UpdateNames
		ServerDebug, // error/log specific markers. similar to a logcat.
		ServerStatus, // responses to requests for ServerStatus and regular interval server status posts
		// from Client to Server
		ClientStatus, // client status updates (related to "away" and "invisible" status)
		ClientRequestServerStatus, // initiation calling for a ServerStatus
		ClientDebug, // error/log specific markers. similar to a logcat.
		ClientConnection, // RegisterName
		ClientHistoryRequest, // Request for ServerChatHistory
		
		// USER INTERACTION
		HasStartedTypingMessage, // notification that a chat-based message is being typed by the client
		HasStoppedTypingMessage, // notification that client has stopped typing a chat-based message
		
		// CHAT-MESSAGE
		// message related Tags
		// from Server
		MessageReceived, // confirmation a chat-based message has been received on the server for distribution
			// TODO needs PerEdit
		// from Client
		MessageDelivered, // a chat-based message has been delivered to this client
			// TODO needs PerClient & PerEdit
		MessageViewed, // a chat-based message has been read/viewed
			// TODO needs PerClient & PerEdit
		MessageEdit, // this message is considered an edit to a previous message
			// TODO needs EditHistory
		MessageDelete, // this message is considered deleted
			// TODO needs EditHistory
		// message type
		CharacterAction, // player-submitted request to act
		CharacterSpeech, // player-submitted request to say this into the world
		CharacterInquiry, // player-submitted request to inquire about the world around him/her
		PlayerOOC, // Out-of-Character message between Players with no direct bearing on gameplay
		// extra tags
		CharacterUnknownKnowledge, // information player receives without character acquiring that knowledge
			// TODO needs PerCharacter
		PlayerPrivateMessage, // message was created to be viewed by only 1 target client
			// TODO needs "from" and "to"
		
		// GAME-MESSAGE
		// auto-generated message Tags
		GameLoot, // xp/gold/item acquisition
		GameBattleActionResults, // hit/miss & damage/healing results (turn-based)
		GameSkillCheck, // roll vs. skill. message includes full calc.
		GameCharacterStatusChange, // friendly/enemy/neutral character death, revival, incapacitate notification
		GameCombatStatusChange, // enter/exit combat
		GameWorldTime, // world day/time updates, by request and automatically when extended periods of time pass
		
		// Session note Tags (for player/GM review)
		SessionEnd, // GM/Player agreed end of play session
		SessionStart, // GM/Player agreed start of play session
			// this allows for a gap between sessions that might be trimmable during review
		
		none // not permanent tag
	}
}
