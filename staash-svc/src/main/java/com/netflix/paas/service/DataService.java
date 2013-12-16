package com.netflix.paas.service;

import com.google.inject.Inject;
import com.netflix.paas.json.JsonArray;
import com.netflix.paas.json.JsonObject;
import com.netflix.paas.rest.dao.MetaDao;

public interface DataService {
    public String writeRow(String db, String table, JsonObject rowObj);
    public String listRow(String db, String table, String keycol, String key);
    public String writeEvent(String db, String table, JsonObject rowObj);
    public String writeEvents(String db, String table, JsonArray rowObj);
    public String readEvent(String db, String table, String eventTime);
    public String readEvent(String db, String table, String prefix,String eventTime);
    public String readEvent(String db, String table, String prefix,String startTime, String endTime);
    public String doJoin(String db, String table1, String table2,
            String joincol, String value);
    public String writeToKVStore(String db, String table, JsonObject obj);
	public byte[] fetchValueForKey(String db, String table,
			String keycol, String key);
}
