package com.netflix.paas.service;

import java.util.Map;

import com.netflix.paas.exception.PaasException;
import com.netflix.paas.exception.StorageDoesNotExistException;
import com.netflix.paas.json.JsonObject;
import com.netflix.paas.rest.dao.DataDao;
import com.netflix.paas.rest.dao.MetaDao;
import com.netflix.paas.rest.meta.entity.Entity;
import com.netflix.paas.rest.meta.entity.EntityType;

public interface MetaService {
    public String writeMetaEntity(EntityType etype, String entity) throws StorageDoesNotExistException;
//    public Entity readMetaEntity(String rowKey);
//    public String writeRow(String db, String table, JsonObject rowObj);
//    public String listRow(String db, String table, String keycol, String key);
    public String listSchemas();
    public String listTablesInSchema(String schemaname);
    public String listTimeseriesInSchema(String schemaname);
    public String listStorage();
    public Map<String,String> getStorageMap();
    public String CreateDB();
    public String createTable();
    public JsonObject runQuery(EntityType etype, String col);
    public JsonObject getStorageForTable(String table);
    public JsonObject extenddb(String db, String region);
}
