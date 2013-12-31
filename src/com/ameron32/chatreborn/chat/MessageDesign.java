package com.ameron32.chatreborn.chat;

public class MessageDesign // edits are new Messages, the adapter will sort it out
{
	
	class MessageClass {
		// FEATURES OF ALL MESSAGES
		long serverTimeStamp; // date sorting in adapter. all overwrites keep this timeStamp.
		short revision;		  // newest revision displayed automatically by adapter.
		long keyId;			  // no two messages will share the same keyId.

		/**
		 *	messages need a key.
		 *	the arrayAdapter should only show the most recent edit.
		 *
		 */

		String originatorName;	// client who sent the message, or server
		long originatingTimeStamp; // time when the client/server created the message
//		long serverTimeStamp; // time when the server accepted and resent the message
		long clientReceivedTimeStamp; // time when YOUR client received the message
		String message; // message text

		Tag[] tags; // like evernote tags, primarily intended for user-filtering and/or search

	}
	
	class ChatMessage extends MessageClass {
		// FEATURES OF CHAT-MESSAGES
		CommentMessage[] comments; // like Google Docs comments, optionally attached to each message
		
	}
	
	class PrivateChatMessage extends ChatMessage {
		String targetName; // name of client to whom message is intended
	
	}
	
	class CommentMessage extends MessageClass { // attach to Chat-Message
		long attachToKeyId; // same number as keyId from messageClass
		public CommentMessage(MessageClass mc) {
			this.attachToKeyId = mc.keyId;
		}
		
	}
	
	class SystemMessage extends MessageClass {
		
		
	}
	
	enum Tag {
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
		
		ElFin // not a real tag
	}

}
