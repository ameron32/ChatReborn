package com.ameron32.knbasic.core.chat;

import java.io.File;
import java.net.InetAddress;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ameron32.chatreborn.chat.ChatListener;
import com.ameron32.chatreborn.chat.Network.ChatMessage;
import com.ameron32.chatreborn.chat.Network.MessageClass;
import com.ameron32.chatreborn.chat.Network.RegisterName;
import com.ameron32.chatreborn.chat.Network.ServerChatHistory;
import com.ameron32.chatreborn.chat.Network.SystemMessage;
import com.ameron32.chatreborn.chat.Network.UpdateNames;
import com.ameron32.chatreborn.fragments.ChatClientFragment;
import com.ameron32.chatreborn.fragments.ChatServerFragment;
import com.ameron32.chatreborn.helpers.SendBar;
import com.ameron32.chatreborn.services.ChatClient;
import com.ameron32.chatreborn.services.ChatServer;
import com.ameron32.chatreborn.services.ChatServer.ChatConnection;
import com.ameron32.knbasic.core.helpers.CustomSlidingLayer;
import com.ameron32.knbasic.core.helpers.InternalNotification;
import com.ameron32.knbasic.core.helpers.Loader;
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

		// Initializations by Library Name, 
		// see respective areas below for methods
		actionbarSherlockInit();
		messageBarInit();
		slidingLayerInit();
		universalImageLoaderInit();
		kryonetInit(savedInstanceState);

		// Load memory
		Loader.run(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
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
	
	int openSlidingLayerAtTimeOfOnPause = CustomSlidingLayer.NULL_ID;
	@Override
	protected void onPause() {
		// remember and close sliders for reopening in onResume
		openSlidingLayerAtTimeOfOnPause = CustomSlidingLayer.getIdOfOpenSlidingLayer();
		CustomSlidingLayer.closeAllSlidingLayers();
		
		kryonetOnPause();
		
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		kryonetOnResume();
		
		// reopen sliders closed when onPause
		if (openSlidingLayerAtTimeOfOnPause != CustomSlidingLayer.NULL_ID) {
			CustomSlidingLayer.openSlidingLayer(openSlidingLayerAtTimeOfOnPause);
		}
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
		
		boolean[] serverANDclientState = new boolean[] { getIsServerRunning(), getIsClientRunning()};
		outState.putBooleanArray("serverANDclientState", serverANDclientState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle inState) {
		super.onRestoreInstanceState(inState);
		if (inState.containsKey("mMessageBar"))
			mMessageBar.onRestoreInstanceState(inState.getBundle("mMessageBar"));
		
		boolean[] serverANDclientState = inState.getBooleanArray("serverANDclientState");
		sFragmentOn = serverANDclientState[0];
		cFragmentOn = serverANDclientState[1];
	}
	
	// ------------------------------------------------------------------------------------------------
	// MY KRYONET IMPLEMENTATION
	// ------------------------------------------------------------------------------------------------

	private FragmentManager fm;
	protected FragmentManager getMasterFragmentManager() {
		return fm;
	}
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
	
	private void promptClient() {
		final AutoCompleteTextView etHost = new AutoCompleteTextView(MasterActivity.this);
		etHost.setHint("Ex: \"192.168.254.254\" or \"localhost\"");
		final String[] values = { "localhost", "192.168.1.26", "192.168.1.13", "192.168.24.192" };
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, values);
		etHost.setThreshold(256);
		etHost.setAdapter(adapter);
		etHost.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					etHost.showDropDown();
				}
			}
		});
		
		final AlertDialog connection = new AlertDialog.Builder(MasterActivity.this)
		  .setTitle("Host IP")
		  .setPositiveButton("Connect", new Dialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					startConnect(etHost);
				}
			})
		  .setView(etHost)
		  .create();

		etHost.setOnKeyListener(new EditText.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)
						&& (!event.isShiftPressed())) {
					connection.dismiss();
					startConnect(etHost);
					return true;
				}
				return false;
			}
		});
		
		connection.show();
	}
	
	private void startConnect(EditText etHost) {
		final String hostIP = etHost.getText().toString().trim();
		if (hostIP.equalsIgnoreCase("localhost") || hostIP.equalsIgnoreCase("")) {
			startServer();
		}
				
		startClient(hostIP);
	}
	
	private void kryonetInit(Bundle savedInstanceState) {
		if (fm == null)
			fm = getSupportFragmentManager();
	}
	
	private void kryonetOnResume() {
		startServerFragment();
		startClientFragment();
	}
	
	private void kryonetOnPause() {
		stopServerFragment();
		stopClientFragment();
	}
	
	private boolean sFragmentOn = false;
	private boolean cFragmentOn = false;
	private void startServerFragment() {
//		if (!sFragmentOn) {
		
//		sFragment = (ChatServerFragment) getSupportFragmentManager().findFragmentByTag("sFragment");
//		
//		if (sFragment == null) {
			FragmentTransaction ftServer = fm.beginTransaction();
			sFragment = new ChatServerFragment();
//			sFragment.setArguments(getIntent().getExtras());
			ftServer.replace(R.id.llChatServerHolder, sFragment, "sFragment");
			ftServer.commit();
//		}   
		
//		}
	}
	
	private void stopServerFragment() {
//		if (sFragmentOn) {
			FragmentTransaction ftServer = fm.beginTransaction();
			ftServer.remove(sFragment);
			ftServer.commit();
//		}
	}
	
	private void startClientFragment() {
//		if (!cFragmentOn) {
		
//		cFragment = (ChatClientFragment) getSupportFragmentManager().findFragmentByTag("cFragment");
//		
//		if (cFragment == null) {
			FragmentTransaction ftClient = fm.beginTransaction();
			cFragment = new ChatClientFragment();
//			cFragment.setArguments(getIntent().getExtras());
			// replace?
			ftClient.replace(R.id.llChatClientHolder, cFragment, "cFragment");
			ftClient.commit();
//        }
		
//		}
	}
	
	private void stopClientFragment() {
//		if (cFragmentOn) {
			FragmentTransaction ftClient = fm.beginTransaction();
			ftClient.remove(cFragment);
			ftClient.commit();
//		}
	}
	
	private void startServer() {
		if (!isServerRunning) {
			startServerFragment();
			sFragmentOn = true;
			
			cSs = new Intent(MasterActivity.this, ChatServer.class);
			startService(cSs);

			isServerRunning = !isServerRunning;
		}
	}

	private void stopServer() {
		if (isServerRunning) {
			stopService(cSs);

			stopServerFragment();
			sFragmentOn = false;
			
			isServerRunning = !isServerRunning;
		}
	}	

	private void startClient(final String host) {
		if (!isClientRunning) {
			// startClient
			startClientFragment();
			cFragmentOn = true;

			cCs = new Intent(MasterActivity.this, ChatClient.class);
			cCs.putExtra("host", host);
			startService(cCs);
			
			initClient();
			
			isClientRunning = !isClientRunning;
		}
	}
	
	private void stopClient() {
		if (isClientRunning) {
			termClient();
			
			stopService(cCs);

			stopClientFragment();
			cFragmentOn = false;
			
			isClientRunning = !isClientRunning;
		}
	}
	
	private void termClient() {
		final SendBar stSendBar = (SendBar) findViewById(R.id.stSendBar);
		stSendBar.setConnected(false);
		stSendBar.setSendListener(new Runnable() {
			@Override
			public void run() {
				showMessage("Client Not Connected", true);
			}
		});
	}
	
	private InternalNotification iNotify;
	private void initClient() {
		// initSendBar
		// --------------------------------------------------------------
		final SendBar stSendBar = (SendBar) findViewById(R.id.stSendBar);
		stSendBar.setConnected(true);
		stSendBar.setSendListener(new Runnable() {
			@Override
			public void run() {
				final String msg = stSendBar.getText().toString().trim();
				if (msg != null && msg.length() > 0) {
					cFragment.sendMessage(msg);
				}
			}
		});


		// initNotificationFader
		// --------------------------------------------------------------
		iNotify = (InternalNotification) findViewById(R.id.iNotify);
		
		
		// requires slidingLayerInit()
		mChatSlidingLayer.setOnOpenRunnable(new Runnable() {
			@Override
			public void run() {
				iNotify.setDisabled(true);
			}
		});
		mChatSlidingLayer.setOnCloseRunnable(new Runnable() {
			@Override
			public void run() {
				iNotify.setDisabled(false);
			}
		});
		// ---------------------------
		
		
		ChatListener chatClientListener = new ChatListener() {
			@Override
			protected void received(final ChatMessage chatMessage, final ChatConnection chatConnection) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						iNotify.show(chatMessage.name + ": " + chatMessage.getText(), 3000);
					}
				});
			}
			
			@Override
			protected void disconnected(final ChatConnection chatConnection) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						iNotify.show("Exited: " + chatConnection.name, 2000);
					}
				});
			}
		};

		cFragment.addChatClientListener(chatClientListener);
	}
	

	// ------------------------------------------------------------------------------------------------
	// Universal Image Loader
	// ------------------------------------------------------------------------------------------------
	
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