package com.netflix.paas.rest.test;

import org.junit.Test;

import com.netflix.paas.json.JsonArray;
import com.netflix.paas.json.JsonObject;

public class TestPaasApis {
	@Test
	public void testJsonArr() {
		String eventsStr = "[{\"name\":\"me\"},{\"name\":\"you\"}]";
		String eventStr = "{\"name\":\"me\"}";
		try {
		JsonObject jObj = new JsonObject(eventStr);
		int i = 0;
		System.out.println(jObj.isArray());
		}catch (Exception e) {
			e.printStackTrace();
		}
		JsonArray evts1 = new JsonArray(eventsStr);
		JsonArray events = new JsonArray();
		events.addString(eventsStr);
//		if (events.isArray()) {
//    		JsonArray eventsArr = events.asArray();
//    		JsonObject obj;
//    		for (int i=0;obj = events.get(i); i++) {
//    			JsonObject obj = (JsonObject) event;    			
//    		}
//    	}
		Object obj1 = evts1.get(0);
		Object obj2 = evts1.get(1);
		int len = evts1.size();
		int i  = 0;
	}

}
