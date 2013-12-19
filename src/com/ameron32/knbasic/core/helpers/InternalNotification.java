package com.ameron32.knbasic.core.helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ameron32.knbasic.core.chat.R;

public class InternalNotification extends RelativeLayout {

	public void show(final String msg, int delay) {
	
		final AsyncTask<Integer, Integer, String> hide = new AsyncTask<Integer, Integer, String>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				setVisibility(VISIBLE);
				tvNotificationMessage.setText(msg);
			}
			
			@Override
			protected String doInBackground(Integer... params) {
				try {
					Thread.sleep(params[0]);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(String results) {
				super.onPostExecute(results);
				hide();
			}

		};
		
		hide.execute(delay);
	}
	
	private void hide() {
		setVisibility(INVISIBLE);
	}

	public InternalNotification(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	public InternalNotification(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public InternalNotification(Context context) {
		super(context);
		init();
	}
	
	private LayoutInflater inflater;
	private TextView tvNotificationMessage;
	private ImageView ivNotificationIcon;
	
	private void init() {
		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.chat_notification_overlay, this, true);

		tvNotificationMessage = (TextView) findViewById(R.id.tvNotificationMessage);
		ivNotificationIcon = (ImageView) findViewById(R.id.ivNotificationIcon);
		
		hide();
	}
}
