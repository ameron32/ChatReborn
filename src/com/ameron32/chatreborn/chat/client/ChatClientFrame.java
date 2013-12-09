package com.ameron32.chatreborn.chat.client;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

import com.ameron32.chatreborn.R;
import com.ameron32.chatreborn.chat.Global;
import com.ameron32.chatreborn.chat.Network;
import com.ameron32.chatreborn.chat.Network.ChatMessage;
import com.ameron32.chatreborn.chat.Network.MessageClass;
import com.ameron32.chatreborn.chat.Network.RegisterName;
import com.ameron32.chatreborn.chat.Network.SystemMessage;
import com.ameron32.chatreborn.chat.Network.UpdateNames;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class ChatClientFrame {

	final Context context;
	final View parentView;

	private ImageButton ibSend;
	private EditText etMessage;
	private TextView tvChat, tvUsers, tvConnection;
	private ScrollView svChatRecord;
	private ProgressBar pbMain;

	private void init() {
		ibSend = (ImageButton) parentView.findViewById(R.id.ibSend2);

		etMessage = (EditText) parentView.findViewById(R.id.etMessage2);

		tvChat = (TextView) parentView.findViewById(R.id.tvChat2);
		tvUsers = (TextView) parentView.findViewById(R.id.tvUsers2);
		tvConnection = (TextView) parentView.findViewById(R.id.tvConnection2);

		svChatRecord = (ScrollView) parentView.findViewById(R.id.svChatRecord2);

		pbMain = (ProgressBar) parentView.findViewById(R.id.pbMain2);

		pbMain.setIndeterminate(true);
		pbMain.setVisibility(View.GONE);
	}

	public ChatClientFrame(Context context, View v) {
		this.context = context;
		this.parentView = v;
		init();
	}

	public void setNames(final String[] userNames) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final StringBuilder sb = new StringBuilder();
				for (int i = 0; i < userNames.length; i++) {
					if (i != 0)
						sb.append("\n");
					sb.append(userNames[i]);
				}
				tvUsers.setText(sb.toString());
			}
		});
	}

	public void setSendListener(final Runnable listener) {
		// THREE WAYS to send the message
		ibSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.ibSend2:
				default:
					sendMessage(listener);
					break;
				}
			}
		});
		etMessage.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)
						&& (!event.isShiftPressed())) {
					sendMessage(listener);
					return true;
				}
				return false;
			}
		});
		etMessage.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEND) {
					sendMessage(listener);
					return true;
				}
				return false;
			}
		});
	}
	
	public void setActionListener(final Runnable listener) {
		tvChat.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new RunListener(listener).execute();
			}
		});
	}

	public void sendMessage(final Runnable listener) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (getSendText().length() == 0) {
					return;
				}
				new RunListener(listener).execute();
				resetChatFeatures();
			}
		});
	}

	public class RunListener extends AsyncTask<String, int[], String> {
		Runnable listener;

		public RunListener(Runnable listener) {
			this.listener = listener;
		}

		@Override
		protected String doInBackground(String... params) {
			listener.run();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
//			updateScreen();
		}
	}

	public String getSendText() {
		Editable e = etMessage.getText();
		String s = e.toString();
		s = s.trim();
		return s;
	}

	public void resetChatFeatures() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// clearEditText
				etMessage.setText("");
				
				//scrollToBottomChat
				scrollToBottomChat();
			}
		});
	}

	public void scrollToBottomChat() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				svChatRecord.post(new Runnable() {
					@Override
					public void run() {
						svChatRecord.fullScroll(View.FOCUS_DOWN);
						etMessage.requestFocus();
					}
				});
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
				// determine chain or new
				View chatBubble = null;
				LayoutInflater i = LayoutInflater.from(context);
				boolean restart = true;
				
				final ArrayList<MessageClass> chatHistory = Global.ChatOrganizer.getClientChatHistory();
				if (chatHistory.size() <= 1) {
					chatBubble = i.inflate(R.layout.chat_bubble_ui, null);
				} else {
					final int last = chatHistory.size() - 1;
					String lastChatter = chatHistory.get(last).name;

					if (lastChatter != null && lastChatter.equals(username)) {
						chatBubble = i.inflate(R.layout.chat_bubble_continue,
								null);
						restart = false;
					} else {
						chatBubble = i.inflate(R.layout.chat_bubble_ui, null);
					}
				}

				TextView msg, time;
				if (restart) {
					msg = (TextView) chatBubble
							.findViewById(R.id.tvMsg);
					time = (TextView) chatBubble
							.findViewById(R.id.tvTimeStamp);

					TextView name = (TextView) chatBubble
							.findViewById(R.id.tvUsr);
					name.setText(username);
				} else {
					msg = (TextView) chatBubble
							.findViewById(R.id.tvMsgC);
					time = (TextView) chatBubble
							.findViewById(R.id.tvTimeStampC);
				}
				msg.setText(chatMessage);
				time.setText(new SimpleDateFormat("h:mma", Locale.US)
						.format(serverTimeStamp));

				final LinearLayout chatStack = (LinearLayout) parentView.findViewById(
						R.id.llChat2);
				chatStack.addView(chatBubble);
				Global.Local.clientChatHistory.put(serverTimeStamp, m);
				
				scrollToBottomChat();
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

	public void setConnectToServerListener(final Runnable listener) {
		tvConnection.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.run();
			}
		});
	}

	public void setConnectionText(String text) {
		tvConnection.setText(text);
	}

	private void runOnUiThread(final Runnable listener) {
		((Activity) context).runOnUiThread(listener);
	}
}
