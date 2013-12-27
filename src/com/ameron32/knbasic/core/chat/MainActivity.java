package com.ameron32.knbasic.core.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ameron32.knbasic.core.helpers.Loader;
import com.ameron32.knbasic.core.helpers.Loader.Fonts;
import com.google.android.voiceime.VoiceRecognitionTrigger;
import com.loopj.android.image.SmartImageView;

import fr.castorflex.android.flipimageview.library.FlipImageView;
import fr.castorflex.android.flipimageview.library.FlipImageView.OnFlipListener;

public class MainActivity extends MasterActivity {

	// random demonstration textview for custom fonts
	private TextView tv;

	// store the current font sequence, needed for "Cycle Font"
	private int currentFont = 0;

	public void showMessage(String message) {
		super.showMessage(message, true);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		// create and configure new custom settings buttons in the sliding menu
		// -------------------------------------------------------------------------
		addMenuButton("Toggle Server/Client", 2, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!getIsClientRunning()) {
					chatConnect();
				} else {
					chatDisconnect();
				}
			}
		});
//		addMenuButton("Toggle Server/Client", 2, new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				chatDisconnect();
//			}
//		});
//		addMenuButton("Cycle Font", 2, new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				int max = Fonts.values().length;
//				int newFont = 0;
//				if (currentFont != max - 1) {
//					currentFont++;
//					newFont = currentFont;
//				} else {
//					currentFont = 0;
//				}
//
//				tv.setTypeface(Loader.fonts.get(Fonts.values()[newFont]));
//			}
//		});
		addMenuButton("Settings", 1, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showMessage("Not yet implemented", true);
			}
		});
		

		// change the font of the titlebar in ActionBar Sherlock
		// -------------------------------------------------------------------------
		getCustomTitle().setTypeface(Loader.fonts.get(Fonts.temphisdirty));
		getCustomTitle().setTextSize(getCustomTitle().getTextSize() * 1.5f);

		
		// instantiate primary fragment
		// -------------------------------------------------------------------------
		fm = getSupportFragmentManager();
		FragmentTransaction ftStarter = fm.beginTransaction();
		StarterFragment sf = new StarterFragment();
		ftStarter.add(R.id.llPrimary, sf);
		ftStarter.commit();
	}
	FragmentManager fm;

	@Override
	protected void onResume() {
		super.onResume();
		
		setFragment();
	}
	
	@Override
	protected void onPostResume() {
		super.onPostResume();
		// Testing commands (comment out or delete prior to production)
		performTest();
	}
	
	@Override
	protected void onPause() {
		unsetFragment();
		
		super.onPause();
	}
	
	private StarterFragment sf;
	private void setFragment() {
		FragmentTransaction ftStarter = fm.beginTransaction();
		sf = new StarterFragment();
		
		// why do i have to have this or it duplicates the ftStarter?
		ftStarter.replace(R.id.llPrimary, sf);
		ftStarter.addToBackStack(null);
		ftStarter.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ftStarter.commit();
	}	
	private void unsetFragment() {
		FragmentTransaction ftStarter = fm.beginTransaction();
//		StarterFragment sf = new StarterFragment();
		
		// why do i have to have this or it duplicates the ftStarter?
		ftStarter.remove(sf);
		ftStarter.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ftStarter.commit();
	}

	// ------------------------------------------------------------------------------------------------
	// TESTING, safe to remove
	// ------------------------------------------------------------------------------------------------

	private void performTest() {
//		fillDummyData();
//		generateSmartImageView();
//		instanceChromeView();
	}

	// ------------------------------------------------------------------------------------------------
	// SMARTIMAGEVIEW testing
	// ------------------------------------------------------------------------------------------------

	private int counter = 0;
	private boolean random = true;

	private void generateSmartImageView() {
		final LinearLayout primary = ((LinearLayout) findViewById(R.id.llPrimary));
		// primary.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// // generateNewCard(primary, 10);
		// }
		// });
		generateNewCard(primary, 2);
	}

	private void generateNewCard(LinearLayout primary, int times) {
		final String[] cards = null; // getResources().getStringArray(R.array.cards);
		if (random)
			counter = new java.util.Random().nextInt(cards.length);
		for (int i = 0; i < times; i++) {
			final FlipImageView smartImageView = new FlipImageView(this);
			smartImageView

			.setImageUrl("http://wow.tcgbrowser.com/images/cards/hd/"
					+ cards[counter] + ".jpg");
			primary.addView(smartImageView);
			counter++;
		}
	}

	// ------------------------------------------------------------------------------------------------
	// DUMMY DATA FOR LISTVIEW
	// ------------------------------------------------------------------------------------------------

	private void fillDummyData() {
		final String[] dummyList = new String[] { "Item 1", "Item 2", "Item 3",
				"Item 4", "Item 5" };

		ListView listView = (ListView) findViewById(R.id.listView1);
		listView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dummyList));

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final String item = (String) parent.getItemAtPosition(position);
				showMessage(item, false);
			}
		});
	}

	// ------------------------------------------------------------------------------------------------
	// CHROMEVIEW testing
	// ------------------------------------------------------------------------------------------------

	private void instanceChromeView() {
		// ChromeView chromeView = (ChromeView)findViewById(R.id.cvMain);
		// chromeView.getSettings().setJavaScriptEnabled(true);
		// chromeView.loadUrl("http://www.google.com");
	}
}
