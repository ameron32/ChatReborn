package com.ameron32.chatreborn.helpers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NetworkThreadPoolExecutor extends ThreadPoolExecutor {

	public enum NTPEType {
		standard
	}
	
	private static final SynchronousQueue<Runnable> networkQueue 
	  = new SynchronousQueue<Runnable>();
	public NetworkThreadPoolExecutor(NTPEType type) {
		this(1, 5, 1, TimeUnit.MILLISECONDS, networkQueue);
	}
	
	public NetworkThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	public void runRunnable(Runnable runnable) {
		try {
			networkQueue.put(runnable);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
