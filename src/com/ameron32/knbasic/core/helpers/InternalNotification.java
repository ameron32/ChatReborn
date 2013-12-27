package com.ameron32.knbasic.core.helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ameron32.knbasic.core.chat.R;

public class InternalNotification extends RelativeLayout {

	private boolean isDisabled = false;
	public void setDisabled(boolean state) {
		isDisabled = state;
	}
	
	public void show(final String msg, final int delay) {
		if (!isDisabled) {
			final AsyncTask<Integer, Integer, String> hide = new AsyncTask<Integer, Integer, String>() {
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					setVisibility(VISIBLE);
					tvNotificationMessage.setText(msg);

					pbTimer.setIndeterminate(false);
					pbTimer.setMax(max);
					pbTimer.setProgress(0);
				}

				private final int updates = 20;
				private int progressInterval = delay / updates;
				private int max = 100;

				@Override
				protected String doInBackground(Integer... params) {
					// 1 to updates (20)
					for (int cycle = 0; cycle < updates; ++cycle) { 
						try {
							Thread.sleep(progressInterval);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						publishProgress(cycle);
					}
					return null;
				}

				@Override
				protected void onPostExecute(String results) {
					super.onPostExecute(results);
					hide();
				}

				@Override
				protected void onProgressUpdate(Integer... values) {
					super.onProgressUpdate(values);
					int cycle = values[0];
					pbTimer.setProgress((cycle * 100) / updates);
				}
			};

			hide.execute(delay);

		} else {
			// do nothing
		}
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
	private ProgressBar pbTimer;
	
	private void init() {
		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.chat_notification_overlay, this, true);

		tvNotificationMessage = (TextView) findViewById(R.id.tvNotificationMessage);
		ivNotificationIcon = (ImageView) findViewById(R.id.ivNotificationIcon);
		pbTimer = (ProgressBar) findViewById(R.id.pbTimer);
		
		hide();
	}
}
