package com.netflix.paas.rest.dao;

import java.util.Map;

import com.netflix.paas.json.JsonObject;
import com.netflix.paas.rest.meta.entity.Entity;

public interface MetaDao {
    public String writeMetaEntity(Entity entity);
//    public Entity readMetaEntity(String rowKey);
//    public String writeRow(String db, String table, JsonObject rowObj);
//    public String listRow(String db, String table, String keycol, String key);
//    public String listSchemas();
//    public String listTablesInSchema(String schemaname);
//    public String listTimeseriesInSchema(String schemaname);
//    public String listStorage();
    public Map<String,String> getStorageMap();
    public Map<String, JsonObject> runQuery(String key, String col);
}
