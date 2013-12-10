package com.ameron32.knbasic.core.chat;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;

public class CustomSlidingLayer extends com.slidinglayer.SlidingLayer {

	// Registry of all CustomSlidingLayers
	private static final ArrayList<CustomSlidingLayer> customSlidingLayers 
		= new ArrayList<CustomSlidingLayer>();
	private static boolean isAnySlidingLayerOpen() {
		for (CustomSlidingLayer layer : customSlidingLayers) {
			if (layer.isSlidingLayerOpen()) return true;
		}
		return false;
	}
	
	private static void closeAllSlidingLayers() {
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
		// TODO Auto-generated constructor stub
	}
	
	public CustomSlidingLayer(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public CustomSlidingLayer(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
}
