package com.ameron32.knbasic.core.helpers;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;

public class CustomSlidingLayer extends com.slidinglayer.SlidingLayer {

	public static final int NULL_ID = -1;
	
	// Registry of all CustomSlidingLayers
	private static final ArrayList<CustomSlidingLayer> customSlidingLayers 
		= new ArrayList<CustomSlidingLayer>();
	private static boolean isAnySlidingLayerOpen() {
		if (getIdOfOpenSlidingLayer() == NULL_ID) 
			return false;
		return true;
	}
	public static int getIdOfOpenSlidingLayer() {
		for (CustomSlidingLayer layer : customSlidingLayers) {
			if (layer.isSlidingLayerOpen()) return layer.getId();
		}
		return NULL_ID;
	}
	
	public static void closeAllSlidingLayers() {
		for (CustomSlidingLayer layer : customSlidingLayers) {
			if (layer.isSlidingLayerOpen()) layer.closeSlidingLayer();
		}
	}
	
	private boolean isSlidingLayerOpen = false;
	
	public void register() {
		customSlidingLayers.add(this);
	}
	
	public boolean isSlidingLayerOpen() {
		return isSlidingLayerOpen;
	}
		
	public void closeSlidingLayer() {
		if (isSlidingLayerOpen()) {
			toggleSlidingLayer();
		}
	}
	
	public void openSlidingLayer() {
		if (!isSlidingLayerOpen()) {
			toggleSlidingLayer();
		}
	}
	
	public static void openSlidingLayer(int id) {
		for (CustomSlidingLayer csl : customSlidingLayers) {
			if (csl.getId() == id) {
				csl.openSlidingLayer();
			}
		}
	}
	
	public void toggleSlidingLayer() {
		slidingLayerToggle();
	}
	
	/**
	 * If not null, switch case on view IDs. If null, auto-open sliding layer.
	 */
	private void slidingLayerToggle() {
		if (isSlidingLayerOpen) {
			closeLayer(true);
		} else {
			openLayer(true);
		}
		isSlidingLayerOpen = !isSlidingLayerOpen;
	}
	
	/**
	 * Close all registered layers before opening this layer
	 */
	@Override
	public void openLayer(boolean smoothAnim) {
		// close all open layers to prevent overlap
		if (CustomSlidingLayer.isAnySlidingLayerOpen()) {
			CustomSlidingLayer.closeAllSlidingLayers();
		}
		
		super.openLayer(smoothAnim);
	}
	
	public CustomSlidingLayer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	public CustomSlidingLayer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public CustomSlidingLayer(Context context) {
		super(context);
		init();
	}
	
	private void init() {
		// TODO
	}

}
