package com.ameron32.knbasic.core.helpers;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomSlidingLayer extends com.slidinglayer.SlidingLayer {

	public static final int NULL_ID = -1;
	private Runnable onOpenRunnable = null;
	public void setOnOpenRunnable(final Runnable r) {
		onOpenRunnable = r;
	}
	private Runnable onCloseRunnable = null;
	public void setOnCloseRunnable(final Runnable r) {
		onCloseRunnable = r;
	}
	
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
	private boolean isTouchEnabled = false;
	public void setTouchEnabled(boolean state) {
		isTouchEnabled = state;
	}
	
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
			if (onCloseRunnable != null)
				onCloseRunnable.run();
		} else {
			openLayer(true);
			if (onOpenRunnable != null)
				onOpenRunnable.run();
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

	@Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (!isTouchEnabled) return false;
		return super.onInterceptTouchEvent(ev);
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent ev) {
		if (!isTouchEnabled) return false;
		return super.onTouchEvent(ev);
	}
}
