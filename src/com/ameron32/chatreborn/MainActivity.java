package com.ameron32.chatreborn;

import com.ameron32.chatreborn.chat.Utils;
import com.ameron32.chatreborn.chat.client.ChatClient;
import com.ameron32.chatreborn.chat.client.ChatClientFragment;
import com.ameron32.chatreborn.chat.server.ChatServer;
import com.ameron32.chatreborn.chat.server.ChatServerFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {
	private ChatClientFragment cFragment;
	private ChatServerFragment sFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		init();
		fm = getSupportFragmentManager();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private boolean isServerOn = false;
	private boolean isClientOn = false;
	private void init() {
		findViewById(R.id.bClientToggle).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isClientOn) {
					stopClient();
				} else {
					startClient();
				}
				isClientOn = !isClientOn;
				
				((CheckBox) findViewById(R.id.cbClient)).setChecked(isClientOn);
			}
		});
		findViewById(R.id.bServerToggle).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isServerOn) {
					stopServer();
				} else {
					startServer();
				}
				isServerOn = !isServerOn;
				
				((CheckBox) findViewById(R.id.cbServer)).setChecked(isServerOn);
			}
		});
	}
	
	private FragmentManager fm;
	private Intent cSs, cCs;
	private void startServer() {
		FragmentTransaction  ftServer = fm.beginTransaction();
		sFragment = new ChatServerFragment();
		ftServer.add(R.id.llServer, sFragment);
		ftServer.commit();

		cSs = new Intent(MainActivity.this, ChatServer.class);
		startService(cSs);
	}

	private void stopServer() {
		stopService(cSs);
		
		FragmentTransaction ftServer = fm.beginTransaction();
		ftServer.remove(sFragment);
		ftServer.commit();
	}	

	private void startClient() {
		FragmentTransaction ftClient = fm.beginTransaction();
		cFragment = new ChatClientFragment();
		ftClient.add(R.id.llClient, cFragment);
		ftClient.commit();

		cCs = new Intent(MainActivity.this, ChatClient.class);
		startService(cCs);
	}
	
	private void stopClient() {
		stopService(cCs);
		
		FragmentTransaction ftClient = fm.beginTransaction();
		ftClient.remove(cFragment);
		ftClient.commit();
	}
}
