package com.ameron32.knbasic.core.chat;

import com.ameron32.knbasic.core.helpers.Loader;
import com.ameron32.knbasic.core.helpers.Loader.Fonts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StarterFragment extends Fragment {
	
	TextView tv;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.starter_fragment, container, false);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		tv = (TextView) 	getView().findViewById(R.id.tvHello);
		
		// demo textview font-switch
		tv.setTypeface(Loader.fonts.get(Fonts.temphisdirty));
	}



}
