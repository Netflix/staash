package com.netflix.staash.rest.util;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple class that encapsulates a ThreadLocal of a map of context. 
 * This context can be used by various classes that service the request and 
 * can store vital info that can be used for debugging.
 * 
 * @author poberai
 *
 */
public class StaashRequestContext {

	private static final Logger Logger = LoggerFactory.getLogger(StaashRequestContext.class);
	
	private static final ThreadLocal<ConcurrentHashMap<String, String>> requestContext =
			new ThreadLocal<ConcurrentHashMap<String, String>>() {
		@Override protected ConcurrentHashMap<String, String> initialValue() {
			return new ConcurrentHashMap<String, String>();
		}
	};
	
	public static void resetRequestContext() {
		requestContext.set(new ConcurrentHashMap<String, String>());
	}
	 

	public static void flushRequestContext() {
		
		ConcurrentHashMap<String, String> map = requestContext.get();
		if (map == null) {
			return;
		}
		
		StringBuilder sb = new StringBuilder("\n========================STAASH REQUEST CONTEXT===========================================");
		for (String key : map.keySet()) {
			sb.append("\n").append(key).append(":").append(map.get(key));
		}
		sb.append("\n======================================================================================");
		
		Logger.info(sb.toString());
	}

	public static void addContext(String key, String value) {
		ConcurrentHashMap<String, String> map = requestContext.get();
		map.put(key, value);
	}
}
