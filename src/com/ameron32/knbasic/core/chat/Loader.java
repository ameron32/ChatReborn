package com.ameron32.knbasic.core.chat;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Static Loader Class, load various elements all at once through the run() method.
 * 
 * @author klemeilleur
 *
 */

public class Loader {

	// STATIC LOADER CLASS
	
	private static Context context;
	
	/**
	 * @param context
	 */
	public static void run(Context context) {
		Loader.context = context;
		typefaces();
	}
	
	public static final HashMap<Fonts, Typeface> fonts = new HashMap<Fonts, Typeface>();
		
	private static void typefaces() {
	    putFont("daedra");
	    putFont("dragonscript");
	    putFont("anirm");
	    putFont("elvencommonspeak");
	    putFont("elvishringnfi");
	    putFont("ringm");
	    putFont("dwarvesc");
	    putFont("dethekstone");
	    putFont("temphisdirty");
	}
	
	private static void putFont(String font) {
		fonts.put(Fonts.valueOf(font), Typeface.createFromAsset(context.getAssets(),
				"fonts/" + font + ".ttf"));
	}
	
	public enum Fonts {
	    daedra, 
	    dragonscript, 
	    anirm, 
	    elvencommonspeak, 
	    elvishringnfi, 
	    ringm, 
	    dwarvesc, 
	    dethekstone, 
	    temphisdirty
	}
	
}
