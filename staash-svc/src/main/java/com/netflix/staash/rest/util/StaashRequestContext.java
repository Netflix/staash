package com.netflix.staash.rest.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
	
	private static final DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS z");

	private static final ThreadLocal<StaashRequestContext> requestContext = new ThreadLocal<StaashRequestContext>() {
		@Override 
		protected StaashRequestContext initialValue() {
			return new StaashRequestContext();
		}
	};

	public static void resetRequestContext() {
		requestContext.set(new StaashRequestContext());
	}

	public static void flushRequestContext() {
		Logger.info(requestContext.get().getMapContents());
	}

	public static void addContext(String key, String value) {
		requestContext.get().addContextToMap(key, value);
	}
	
	public static void logDate() {
		requestContext.get().addDateToMap();
	}

	public static void recordRequestStart() {
		requestContext.get().startTime.set(System.currentTimeMillis());
	}
	
	public static void recordRequestEnd() {
		Long begin = requestContext.get().startTime.get();
		Long now = System.currentTimeMillis();
		requestContext.get().addContextToMap("Duration", String.valueOf(now - begin));
	}

	private final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
	private final AtomicLong startTime = new AtomicLong(0L);
	
	private StaashRequestContext() {
		
	}
	
	private void addContextToMap(String key, String value) {
		map.put(key, value);
	}
	
	private void addDateToMap() {
		DateTime dt = new DateTime();
		map.put("Date", dateFormat.print(dt));
	}
	
	private String getMapContents() {
		
		if (map == null) {
			return null;
		}
		
		StringBuilder sb = new StringBuilder("\n========================STAASH REQUEST CONTEXT===========================================");
		for (String key : map.keySet()) {
			sb.append("\n").append(key).append(":").append(map.get(key));
		}
		sb.append("\n======================================================================================");
		
		return sb.toString();
	}

}
