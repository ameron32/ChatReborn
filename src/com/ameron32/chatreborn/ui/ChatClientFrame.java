package com.ameron32.chatreborn.ui;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ameron32.chatreborn.adapters.ChatAdapter;
import com.ameron32.chatreborn.chat.Global;
import com.ameron32.knbasic.core.chat.R;

public class ChatClientFrame {

	final Context context;
	final View parentView;

	private TextView tvUsers, tvConnection;
	private ScrollView svChatRecord;
	private ProgressBar pbMain;

	private void init() {
		tvUsers = (TextView) parentView.findViewById(R.id.tvUsers2);
		tvConnection = (TextView) parentView.findViewById(R.id.tvConnection2);

		pbMain = (ProgressBar) parentView.findViewById(R.id.pbMain2);

		lvChatHistory = (ListView) parentView.findViewById(R.id.lvChatHistory);
		chatAdapter = new ChatAdapter(context, Global.Local.getClientChatHistory());
		lvChatHistory.setAdapter(chatAdapter);
		lvChatHistory.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		lvChatHistory.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, 
					View v, int position, long id) {
				Toast.makeText(context, 
						position + " clicked. [" + chatAdapter.getItem(position).getText() + "]", 
						Toast.LENGTH_LONG).show();
			}
		});
		
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

	Runnable notifyChange;
	public void setUITask(final Runnable listener) {
		notifyChange = listener;
	}

	public void scrollToBottomChat() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				svChatRecord.post(new Runnable() {
					@Override
					public void run() {
						svChatRecord.fullScroll(View.FOCUS_DOWN);
						lvChatHistory.setSelection(chatAdapter.getCount() - 1);
					}
				});
			}
		});
	}
	
	private ListView lvChatHistory;
	public ChatAdapter chatAdapter;
	public void clearChatHistory() {
		if (lvChatHistory != null) {
			chatAdapter.clear();
		}
	}
	
	public void refreshChatHistory() {
		if (chatAdapter != null) {
			lvChatHistory.post(new Runnable() {
				@Override
				public void run() {
					chatAdapter.notifyDataSetChanged();
				}
			});
		}
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
