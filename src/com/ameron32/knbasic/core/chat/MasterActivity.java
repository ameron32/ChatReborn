package com.ameron32.knbasic.core.chat;

import java.io.File;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ameron32.chatreborn.chat.client.ChatClient;
import com.ameron32.chatreborn.chat.client.ChatClientFragment;
import com.ameron32.chatreborn.chat.server.ChatServer;
import com.ameron32.chatreborn.chat.server.ChatServerFragment;
import com.michaelflisar.messagebar.MessageBar;
import com.michaelflisar.messagebar.messages.BaseMessage;
import com.michaelflisar.messagebar.messages.TextMessage;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class MasterActivity 
	extends SherlockFragmentActivity 
		implements View.OnClickListener {

	// ------------------------------------------------------------------------------------------------
	// AVAILABLE FUNCTIONS
	// ------------------------------------------------------------------------------------------------
	protected void showMessage(String message, boolean includeOK) {
		BaseMessage baseMessage;
		if (includeOK) {
			baseMessage = new TextMessage(message, "OK", null);
		} else {
			baseMessage = new TextMessage(message);
		}
		getMessageBar().show(baseMessage);
	}

	protected void requestExit() {
		final AlertDialog.Builder d = new AlertDialog.Builder(this);
		d.setMessage("Close the application?");

		final DialogInterface.OnClickListener l = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					finish();
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					// do nothing
					break;
				}
				dialog.dismiss();
			}
		};

		d.setPositiveButton("Exit", l);
		d.setNegativeButton("Cancel", l);
		d.create();
		d.show();
	}
	
	protected void addMenuButton(String title, int buttonId, View.OnClickListener listener) {
		final int master_key = 978979798;
					
		// Create and attach button to top of settings drawer
		LinearLayout llSettings = (LinearLayout) findViewById(R.id.llCustomMenu);
		Button customButton = ((Button) (LayoutInflater.from(this).inflate(R.layout.settings_button, null)));
			customButton.setText(title);
			customButton.setTag(master_key, buttonId);
			customButton.setOnClickListener(listener);
		
		llSettings.addView(customButton);
	}
	
	protected void chatConnect() {
		if (fm == null)
			fm = getSupportFragmentManager();
		
		promptClient();
//		startServer();
//		startClient(host);
	}
	
	protected void chatDisconnect() {
		stopClient();
		stopServer();
	}

	// ------------------------------------------------------------------------------------------------
	// STANDARD ACTIVITY FUNCTIONS
	// ------------------------------------------------------------------------------------------------

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_master);

		// Initializations by Library Name, see respective areas below for
		// methods
		actionbarSherlockInit();
		messageBarInit();
		slidingLayerInit();
		universalImageLoaderInit();

		// Load memory
		Loader.run(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_master, menu);

		// create my chat button to slide my chat slider
		// ----------------------------------------
		MenuItem chatSlider = menu.add(Menu.NONE, 
				CHAT_BUTTON_ID,
				Menu.NONE, 
				"Chat");
		chatSlider.setIcon(R.drawable.abs__ic_menu_share_holo_dark);
		chatSlider.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		// create my fake settings button to slide my settings slider
		// ----------------------------------------
		MenuItem settingsSlider = menu.add(Menu.NONE, 
				SETTINGS_BUTTON_ID,
				Menu.NONE, 
				"Settings");
		settingsSlider.setIcon(R.drawable.abs__ic_menu_moreoverflow_normal_holo_dark);
		settingsSlider.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon in action bar clicked
			mSlidingLayer.toggleSlidingLayer();
			break;
		case SETTINGS_BUTTON_ID:
			// menu button in action bar clicked
			mSettingsSlidingLayer.toggleSlidingLayer();
			break;
		case CHAT_BUTTON_ID:
			// menu button in action bar clicked
			mChatSlidingLayer.toggleSlidingLayer();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonExit:
			requestExit();
			break;
		}
	}

	@Override
	public void onBackPressed() {
		if (mSlidingLayer.isSlidingLayerOpen()
				|| mSettingsSlidingLayer.isSlidingLayerOpen()) {
			if (mSlidingLayer.isSlidingLayerOpen())
				mSlidingLayer.closeSlidingLayer();
			if (mSettingsSlidingLayer.isSlidingLayerOpen())
				mSettingsSlidingLayer.closeSlidingLayer();
			if (mChatSlidingLayer.isSlidingLayerOpen())
				mChatSlidingLayer.closeSlidingLayer();
		} else {
			requestExit();
		}
	}

	// ------------------------------------------------------------------------------------------------
	// MY ACTIONBAR SHERLOCK IMPLEMENTATION
	// ------------------------------------------------------------------------------------------------

	private static final int SETTINGS_BUTTON_ID = 2;
	private static final int CHAT_BUTTON_ID = 3;

	private TextView customTitle;
	protected TextView getCustomTitle() {
		return customTitle;
	}
	
	private void actionbarSherlockInit() {
		// turn app icon into "up" button
		ActionBar mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);

		// custom titlebar
		// ----------------------------------------
		mActionBar.setDisplayShowTitleEnabled(false);
		customTitle = (TextView) LayoutInflater.from(this)
				.inflate(R.layout.title_view, null)
				.findViewById(R.id.action_custom_title);
		mActionBar.setCustomView(customTitle);
		mActionBar.setDisplayShowCustomEnabled(true);
		
		// STYLE for ACTIONBAR SHERLOCK
		// This is a workaround for http://b.android.com/15340 from
		// http://stackoverflow.com/a/5852198/132047
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			BitmapDrawable bg = (BitmapDrawable) getResources().getDrawable(
					R.drawable.bg_striped);
			bg.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
			getSupportActionBar().setBackgroundDrawable(bg);

			BitmapDrawable bgSplit = (BitmapDrawable) getResources()
					.getDrawable(R.drawable.bg_striped_split_img);
			bgSplit.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
			getSupportActionBar().setSplitBackgroundDrawable(bgSplit);
		}
	}

	// ------------------------------------------------------------------------------------------------
	// MY MESSAGEBAR IMPLEMENTATION
	// ------------------------------------------------------------------------------------------------

	private MessageBar mMessageBar = null;

	private MessageBar getMessageBar() {
		return mMessageBar;
	}

	private void messageBarInit() {
		mMessageBar = new MessageBar(this, true);
	}

	// ------------------------------------------------------------------------------------------------
	// MY SLIDINGLAYER IMPLEMENTATION
	// ------------------------------------------------------------------------------------------------

	private void slidingLayerInit() {
		mSlidingLayer = (CustomSlidingLayer) findViewById(R.id.left_slidebar);
		mSettingsSlidingLayer = (CustomSlidingLayer) findViewById(R.id.settings_slidebar);
		mChatSlidingLayer = (CustomSlidingLayer) findViewById(R.id.chat_slidebar);
		mSlidingLayer.register();
		mSettingsSlidingLayer.register();
		mChatSlidingLayer.register();

		findViewById(R.id.buttonExit).setOnClickListener(this);
	}

	// LEFT SLIDINGLAYER
	// ------------------------------
	private CustomSlidingLayer mSlidingLayer = null;

	// SETTINGS SLIDINGLAYER
	// ------------------------------
	private CustomSlidingLayer mSettingsSlidingLayer = null;

	// CHAT SLIDINGLAYER
	// ------------------------------
	private CustomSlidingLayer mChatSlidingLayer = null;
	
	// ------------------------------------------------------------------------------------------------
	// REQUIRED FOR MESSAGEBAR
	// ------------------------------------------------------------------------------------------------

	@Override
	public void setContentView(int layoutResId) {
		setContentView(getLayoutInflater().inflate(layoutResId, null));
	}

	@Override
	public void setContentView(View layout) {
		setContentView(layout, null);
	}

	@Override
	public void setContentView(View layout, LayoutParams params) {
		super.setContentView(layout, params == null ? new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT) : params);
		mMessageBar = new MessageBar(layout, true);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mMessageBar != null)
			outState.putBundle("mMessageBar", mMessageBar.onSaveInstanceState());
	}

	@Override
	protected void onRestoreInstanceState(Bundle inState) {
		super.onRestoreInstanceState(inState);
		if (inState.containsKey("mMessageBar"))
			mMessageBar
					.onRestoreInstanceState(inState.getBundle("mMessageBar"));
	}
	
	// ------------------------------------------------------------------------------------------------
	// MY KRYONET IMPLEMENTATION
	// ------------------------------------------------------------------------------------------------

	private FragmentManager fm;
	private Intent cSs, cCs;
	private ChatServerFragment sFragment;
	private ChatClientFragment cFragment;
	
	private boolean isServerRunning = false;
	protected boolean getIsServerRunning() {
		return isServerRunning;
	}
	private boolean isClientRunning = false;
	protected boolean getIsClientRunning() {
		return isClientRunning;
	}
	
	protected void promptClient() {
		final EditText etHost = new EditText(MasterActivity.this);
		etHost.setHint("Ex: \"192.168.254.254\" or \"localhost\"");
		
		new AlertDialog.Builder(MasterActivity.this)
		.setTitle("Host IP")
		.setPositiveButton("Connect", new Dialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				
				final String hostIP = etHost.getText().toString().trim();
				if (hostIP.equalsIgnoreCase("localhost")) {
					startServer();
				}
						
				startClient(hostIP);
			}
		})
		.setView(etHost)
		.create()
		.show();
	}
	
	private void startServer() {
		if (!isServerRunning) {
			FragmentTransaction ftServer = fm.beginTransaction();
			sFragment = new ChatServerFragment();
			ftServer.add(R.id.llChatServerHolder, sFragment);
			ftServer.commit();

			cSs = new Intent(MasterActivity.this, ChatServer.class);
			startService(cSs);
			
			isServerRunning = !isServerRunning;
		}
	}

	private void stopServer() {
		if (isServerRunning) {
			stopService(cSs);

			FragmentTransaction ftServer = fm.beginTransaction();
			ftServer.remove(sFragment);
			ftServer.commit();
			
			isServerRunning = !isServerRunning;
		}
	}	

	private void startClient(final String host) {
		if (!isClientRunning) {
			// startClient
			FragmentTransaction ftClient = fm.beginTransaction();
			cFragment = new ChatClientFragment();
			ftClient.add(R.id.llChatClientHolder, cFragment);
			ftClient.commit();

			cCs = new Intent(MasterActivity.this, ChatClient.class);
			cCs.putExtra("host", host);
			startService(cCs);
			
			isClientRunning = !isClientRunning;
		}
	}
	
	
	private void stopClient() {
		if (isClientRunning) {
			stopService(cCs);

			FragmentTransaction ftClient = fm.beginTransaction();
			ftClient.remove(cFragment);
			ftClient.commit();
			
			isClientRunning = !isClientRunning;
		}
	}

	// Universal Image Loader
	
	private ImageLoader imageLoader;
	public ImageLoader getImageLoader() {
		return imageLoader;
	}
	private void universalImageLoaderInit() {
		File cacheDir = StorageUtils.getCacheDirectory(MasterActivity.this);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(MasterActivity.this)
			.memoryCacheExtraOptions(320, 448) // default = device screen dimensions
			.build();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);
	}

}