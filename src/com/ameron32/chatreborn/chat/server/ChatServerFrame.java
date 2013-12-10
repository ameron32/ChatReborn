package com.ameron32.chatreborn.chat.server;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import com.ameron32.knbasic.core.chat.R;

import com.ameron32.chatreborn.chat.Global;
import com.ameron32.chatreborn.chat.Network.MessageClass;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class ChatServerFrame {
	
	final Context context;
	final View parentView;
	
	private TextView tvChat;
	private TextView tvUsers;
	private TextView tvHost;

//	private ImageButton ibSend;
//	private EditText etMessage;
//	private TextView tvChat, tvUsers, tvConnection;
	private ScrollView svChatRecord;
//	private ProgressBar pbMain;

	private void init() {
//		ibSend = (ImageButton) parentView.findViewById(R.id.ibSend2);
//
//		etMessage = (EditText) parentView.findViewById(R.id.etMessage2);
//
		tvChat = (TextView) parentView.findViewById(R.id.tvChat3);
		tvUsers = (TextView) parentView.findViewById(R.id.tvUsers3);
		tvHost = (TextView) parentView.findViewById(R.id.tvHost);
//
		svChatRecord = (ScrollView) parentView.findViewById(R.id.svChatRecord3);
//
//		pbMain = (ProgressBar) parentView.findViewById(R.id.pbMain2);
//
//		pbMain.setIndeterminate(true);
//		pbMain.setVisibility(View.GONE);
	}
	
	public ChatServerFrame(Context context, View v) {
		this.context = context;
		this.parentView = v;
		init();
	}
	
	public void resetNames() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final StringBuilder sb = new StringBuilder();
				for (String user : Global.Server.connectedUsers) {
					sb.append("\n");
					sb.append(user);
				}
				tvUsers.setText(sb.toString());
			}
		});
	}
	
	public void addMessage(final MessageClass m) {
		addMessage(m.name, m.getText(), m.getTimeStamp(), m);
	}

	public void addMessage(final String username, final String chatMessage,
			final long serverTimeStamp, final MessageClass m) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				String formattedDate = new SimpleDateFormat("HH:mm", Locale.US).format(serverTimeStamp);
				
				String allChat = tvChat.getText().toString();
				allChat += "\n" + username + "(" + formattedDate + "): "
						+ chatMessage;
				tvChat.setText(allChat);
				
				scrollToBottomChat();
			}
		});
	}
	
	private void scrollToBottomChat() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				svChatRecord.post(new Runnable() {
					@Override
					public void run() {
						svChatRecord.fullScroll(View.FOCUS_DOWN);
					}
				});
			}
		});
	}
	
	public void setHostText(String text) {
		tvHost.setText(text);
	}
	
	public void setHostAServerListener(final Runnable listener) {
		tvHost.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.run();
			}
		});
	}
	
	public void clearChatHistory() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				String connected = "You are connected!";
				tvChat.setText("\n" + connected + "\n");
				scrollToBottomChat();
			}
		});
	}
	
	private void runOnUiThread(final Runnable listener) {
		((Activity) context).runOnUiThread(listener);
	}

	private View findViewById(int id) {
		return ((Activity) context).findViewById(id);
	}
	
	private Activity getActivity() {
		return ((Activity) context);
	}
	
}
