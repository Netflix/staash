package com.netflix.paas.rest.dao;

import com.netflix.paas.json.JsonObject;

public interface DataDao {
    public String writeRow(String db, String table, JsonObject rowObj);
    public String listRow(String db, String table, String keycol, String key);
    public String writeEvent(String db, String table, JsonObject rowObj);
    public String readEvent(String db, String table, String eventTime);
    public String doJoin(String db, String table1, String table2,
            String joincol, String value);
}
