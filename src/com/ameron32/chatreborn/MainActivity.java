package com.ameron32.chatreborn;

import com.ameron32.chatreborn.chat.Utils;
import com.ameron32.chatreborn.chat.server.ChatServer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {
	private Intent chatServerService;
	private ChatClientFragment cFragment;
	private ChatServerFragment sFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		FragmentManager fm = getSupportFragmentManager();
		
		FragmentTransaction ft2 = fm.beginTransaction();
		sFragment = new ChatServerFragment();
		ft2.add(R.id.llMain, sFragment);
		ft2.commit();
		
		FragmentTransaction ft1 = fm.beginTransaction();
		cFragment = new ChatClientFragment();
		ft1.add(R.id.llMain, cFragment);
		ft1.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onResume() {
		startService(sFragment.chatServerService);
		startService(cFragment.chatClientService);
		super.onResume();
	}

	@Override
	protected void onPause() {
		stopService(chatServerService); 
		stopService(cFragment.chatClientService);
		super.onPause();
	}

}
