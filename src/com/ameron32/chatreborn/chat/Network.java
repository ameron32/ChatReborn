package com.ameron32.chatreborn.chat;

import java.util.TreeMap;
import java.util.TreeSet;

import com.ameron32.chatreborn.chat.MessageTemplates.*;
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
		kryo.register(TreeSet.class);
		kryo.register(MessageTag.class);
	}

	
	
}
