package com.ameron32.chatreborn.helpers;

import com.ameron32.chatreborn.adapters.ChatAdapter;
import com.ameron32.chatreborn.ui.ChatClientFrame;

import android.app.Activity;
import android.os.AsyncTask;

public class UITask extends AsyncTask<String, Integer, String> {

	private Activity activity;
	private ChatClientFrame frame;

	public UITask (Activity activity, ChatClientFrame frame) {
		this.activity = activity;
		this.frame = frame;
	}
	
	@Override
	protected String doInBackground(String... params) {
		
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (activity != null) {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					frame.refreshChatHistory();
				}
			});
		}
	}
}
