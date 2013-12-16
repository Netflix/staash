package com.netflix.paas.connection;

import com.netflix.paas.json.JsonObject;

public interface PaasConnection {
    public String insert(String db, String table, JsonObject payload);
    public String read(String db, String table, String keycol, String key, String... keyvals);
    public String createDB(String dbname);
    public String createTable(JsonObject payload);
    public String createRowIndexTable(JsonObject payload);
    public void closeConnection();
}
