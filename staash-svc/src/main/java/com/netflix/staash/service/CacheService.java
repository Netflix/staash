/*******************************************************************************
 * /*
 *  *
 *  *  Copyright 2013 Netflix, Inc.
 *  *
 *  *     Licensed under the Apache License, Version 2.0 (the "License");
 *  *     you may not use this file except in compliance with the License.
 *  *     You may obtain a copy of the License at
 *  *
 *  *         http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *     Unless required by applicable law or agreed to in writing, software
 *  *     distributed under the License is distributed on an "AS IS" BASIS,
 *  *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *     See the License for the specific language governing permissions and
 *  *     limitations under the License.
 *  *
 *  *
 ******************************************************************************/
package com.netflix.staash.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.netflix.staash.json.JsonArray;
import com.netflix.staash.json.JsonObject;
import com.netflix.staash.rest.dao.MetaDao;
import com.netflix.staash.rest.meta.entity.Entity;
import com.netflix.staash.rest.meta.entity.EntityType;
import com.netflix.staash.rest.util.MetaConstants;

public class CacheService {
    List<String> dbHolder = new ArrayList<String>();
    Map<String, String> tableToStorageMap = new ConcurrentHashMap<String, String>();
    Map<String, JsonObject> storageMap = new ConcurrentHashMap<String, JsonObject>();
    Map<String, List<String>> dbToTableMap = new ConcurrentHashMap<String, List<String>>();
    Map<String, List<String>> dbToTimeseriesMap = new ConcurrentHashMap<String, List<String>>();
    private MetaDao meta;

    public CacheService(MetaDao meta) {
        this.meta = meta;
//        LoadStorage();
//        LoadDbNames();
//        LoadTableMaps();
//        LoadDbToTimeSeriesMap();
    }

    private void LoadTableMaps() {
        Map<String, JsonObject> tblmap = meta.runQuery(
                MetaConstants.PAAS_TABLE_ENTITY_TYPE, "*");
        for (String tableName : tblmap.keySet()) {
            JsonObject tblPay = tblmap.get(tableName);
            String storage = tblPay.getString("storage");
            tableToStorageMap.put(tableName, storage);
            String key = tableName.split("\\.")[0];
            String table = tableName.split("\\.")[1];
            List<String> currval = null;
            currval = dbToTableMap.get(key);
            if (currval == null) {
                currval = new ArrayList<String>();
            }
            currval.add(table);
            dbToTableMap.put(key, currval);
        }
    }

    public void LoadStorage() {
        storageMap = meta.runQuery(MetaConstants.PAAS_STORAGE_TYPE_ENTITY, "*");
    }

    private void LoadDbNames() {
        Map<String, JsonObject> dbmap = meta.runQuery(
                MetaConstants.PAAS_DB_ENTITY_TYPE, "*");
        for (String key : dbmap.keySet()) {
            dbHolder.add(key);
        }
    }

    private void LoadDbToTimeSeriesMap() {
        Map<String, JsonObject> tblmap = meta.runQuery(
                MetaConstants.PAAS_TS_ENTITY_TYPE, "*");
        for (String tableName : tblmap.keySet()) {
            JsonObject tblPay = tblmap.get(tableName);
            String storage = tblPay.getString("storage");
            if (storage != null && storage.length() > 0)
                tableToStorageMap.put(tableName, storage);
            String key = tableName.split("\\.")[0];
            String table = tableName.split("\\.")[1];
            List<String> currval = null;
            currval = dbToTimeseriesMap.get(key);
            if (currval == null) {
                currval = new ArrayList<String>();
            }
            currval.add(table);
            dbToTimeseriesMap.put(key, currval);
        }
    }

    // public void updateMaps(EntityType etype, JsonObject obj) {
    // switch (etype) {
    // case STORAGE:
    // storageMap.put(obj.getString("name"), obj);
    // break;
    // case DB:
    // String dbname = obj.getString("name");
    // dbHolder.add(dbname);
    // break;
    // case TABLE:
    // String tblname = obj.getString("name");
    // List<String> currval = dbToTableMap.get(tblname);
    // if (currval == null) {
    // currval = new ArrayList<String>();
    // }
    // currval.add(tblname);
    // dbToTableMap.put(tblname, currval);
    // String storage = obj.getString("storage");
    // tableToStorageMap.put(tblname, storage);
    // break;
    // case SERIES:
    // String seriesname = obj.getString("name");
    // List<String> currseries = dbToTimeseriesMap.get(seriesname);
    // if (currseries == null) {
    // currseries = new ArrayList<String>();
    // }
    // currseries.add(seriesname);
    // dbToTimeseriesMap.put(seriesname, currseries);
    // String tsstorage = obj.getString("storage");
    // tableToStorageMap.put(seriesname, tsstorage);
    // break;
    // }
    // }
    public boolean checkUniqueDbName(String dbName) {
//        return dbHolder.contains(dbName);
        Map<String,JsonObject> names = meta.runQuery(MetaConstants.PAAS_DB_ENTITY_TYPE, dbName);
        if (names!=null && !names.isEmpty())
            return names.containsKey(dbName);
        else
            return false;
    }

    public List<String> getDbNames() {
//        return dbHolder;
        Map<String,JsonObject> dbmap = meta.runQuery(MetaConstants.PAAS_DB_ENTITY_TYPE, "*");
        List<String> dbNames = new ArrayList<String>();
        for (String key : dbmap.keySet()) {
            dbNames.add(key);
        }
        return dbNames;
    }

    public List<String> getTableNames(String db) {
        Map<String, JsonObject> tblmap = meta.runQuery(
                MetaConstants.PAAS_TABLE_ENTITY_TYPE, "*");
        List<String> tableNames = new ArrayList<String>();
        for (String tableName : tblmap.keySet()) {
            if (tableName.startsWith(db+".")) {
            String table = tableName.split("\\.")[1];
            tableNames.add(table);
            }
        }
        return tableNames;
    }

    public Set<String> getStorageNames() {
//        return storageMap.keySet();
        Map<String, JsonObject> storages = meta.runQuery(MetaConstants.PAAS_STORAGE_TYPE_ENTITY, "*");
        if (storages != null)
          return storages.keySet();
        else 
            return Collections.emptySet();
    }

    public JsonObject getStorage(String storage) {
//        return storageMap.get(storage);
        Map<String, JsonObject> storages = meta.runQuery(MetaConstants.PAAS_STORAGE_TYPE_ENTITY, "*");
        if (storages != null)
          return storages.get(storage);
        else 
            return null;
    }

    public List<String> getSeriesNames(String db) {
//        return dbToTimeseriesMap.get(db);
        Map<String, JsonObject> tblmap = meta.runQuery(
                MetaConstants.PAAS_TS_ENTITY_TYPE, "*");
        List<String> tableNames = new ArrayList<String>();
        for (String tableName : tblmap.keySet()) {
            if (tableName.startsWith(db+".")) {
            String table = tableName.split("\\.")[1];
            tableNames.add(table);
            }
        }
        return tableNames;
    }

    public void addEntityToCache(EntityType etype, Entity entity) {
//        switch (etype) {
//        case STORAGE:
//            storageMap.put(entity.getName(),
//                    new JsonObject(entity.getPayLoad()));
//            break;
//        case DB:
//            dbHolder.add(entity.getName());
//            break;
//        case TABLE:
//            JsonObject payobject = new JsonObject(entity.getPayLoad());
//            tableToStorageMap.put(entity.getName(),
//                    payobject.getString("storage"));
//            String db = payobject.getString("db");
//            List<String> tables = dbToTableMap.get(db);
//            if (tables == null || tables.size() == 0) {
//                tables = new ArrayList<String>();
//                tables.add(payobject.getString("name"));
//            } else {
//                tables.add(payobject.getString("name"));
//            }
//            dbToTableMap.put(db, tables);
//            break;
//
//        case SERIES:
//            JsonObject tsobject = new JsonObject(entity.getPayLoad());
//            tableToStorageMap.put(entity.getName(),
//                    tsobject.getString("storage"));
//            String dbname = tsobject.getString("db");
//            List<String> alltables = dbToTableMap.get(dbname);
//            if (alltables == null || alltables.size() == 0) {
//                alltables = new ArrayList<String>();
//                alltables.add(entity.getName());
//            } else {
//                alltables.add(entity.getName());
//            }
//            dbToTimeseriesMap.put(dbname, alltables);
//            break;
//        }
    }

    public JsonObject getStorageForTable(String tableParam) {
        Map<String, JsonObject> tblmap = meta.runQuery(
                MetaConstants.PAAS_TABLE_ENTITY_TYPE, "*");
        List<String> tableNames = new ArrayList<String>();
        for (String tableName : tblmap.keySet()) {
            if (tableName.equals(tableParam)) {
            JsonObject tblPayload = tblmap.get(tableParam);
            String storage = tblPayload.getString("storage");
            return getStorage(storage);
            }
        }
        tblmap = meta.runQuery(
                MetaConstants.PAAS_TS_ENTITY_TYPE, "*");
        tableNames = new ArrayList<String>();
        for (String tableName : tblmap.keySet()) {
            if (tableName.equals(tableParam)) {
            JsonObject tblPayload = tblmap.get(tableParam);
            String storage = tblPayload.getString("storage");
            return getStorage(storage);
            }
        }
        return null;
//        String storage = tableToStorageMap.get(table);
//        JsonObject storageConf = storageMap.get(storage);
//        return storageConf;
    }

    public String listStorage() {
//        Set<String> allStorage = storageMap.keySet();
        Set<String> allStorage = getStorageNames();
        JsonObject obj = new JsonObject();
        JsonArray arr = new JsonArray();
        for (String storage : allStorage) {
            arr.addString(storage);
        }
        obj.putArray("storages", arr);
        return obj.toString();
    }

    public String listSchemas() {
        JsonObject obj = new JsonObject();
        JsonArray arr = new JsonArray();
        List<String> allDbNames = getDbNames();
        for (String db : allDbNames) {
            arr.addString(db);
        }
        obj.putArray("schemas", arr);
        return obj.toString();
    }

    public String listTablesInSchema(String db) {
        List<String> tables = getTableNames(db);
        JsonObject obj = new JsonObject();
        JsonArray arr = new JsonArray();
        for (String table : tables) {
            arr.addString(table);
        }
        obj.putArray(db, arr);
        return obj.toString();
    }

    public String listTimeseriesInSchema(String db) {
        List<String> tables = getSeriesNames(db);
        JsonObject obj = new JsonObject();
        JsonArray arr = new JsonArray();
        obj.putArray(db, arr);
        if (tables != null) {
            for (String table : tables) {
                arr.addString(table);
            }
            obj.putArray(db, arr);
        }
        return obj.toString();
    }
}
