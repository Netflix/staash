package com.netflix.paas.rest.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.AlreadyExistsException;
import com.datastax.driver.core.exceptions.DriverException;
import com.netflix.paas.json.JsonArray;
import com.netflix.paas.json.JsonObject;
import com.netflix.paas.rest.meta.entity.Entity;
import com.netflix.paas.rest.meta.entity.EntityType;
import com.netflix.paas.rest.util.MetaConstants;
import com.netflix.paas.rest.util.PaasUtils;

public class CqlMetaDaoImplNew implements MetaDao {
    private Cluster cluster;
    private Session session;
//    List<String> dbHolder = new ArrayList<String>();
//    Map<String, String> tableToStorageMap = new ConcurrentHashMap<String, String>();
//    Map<String,JsonObject> storageMap = new ConcurrentHashMap<String,JsonObject>();
//    Map<String, List<String>> dbToTableMap = new ConcurrentHashMap<String, List<String>>();
//    Map<String, List<String>> dbToTimeseriesMap = new ConcurrentHashMap<String, List<String>>();
    private boolean schemaCreated = false;

    public CqlMetaDaoImplNew(Cluster cluster) {
        this.cluster = cluster;
        this.session = this.cluster.connect();
//        LoadStorage();
//        LoadDbNames();
//        LoadDbToTableMap();
//        LoadDbToTimeSeriesMap();
//        LoadTableToStorage();
        // TODO Auto-generated constructor stub
    }
    private void maybeCreateMetaSchema() {

        try {
            if (schemaCreated)
                return;

            try {
                session.execute(String.format(
                        PaasUtils.CREATE_KEYSPACE_SIMPLE_FORMAT, MetaConstants.META_KEY_SPACE, 1));
            } catch (AlreadyExistsException e) {
                // It's ok, ignore
            }

            session.execute("USE " + MetaConstants.META_KEY_SPACE);

            for (String tableDef : getTableDefinitions()) {
                try {
                    session.execute(tableDef);
                } catch (AlreadyExistsException e) {
                    // It's ok, ignore
                }
            }

            schemaCreated = true;
        } catch (DriverException e) {
            throw e;
        }
    }

    protected Collection<String> getTableDefinitions() {

        String metaDynamic = "CREATE TABLE metacf (\n" + "    key text,\n"
                + "    column1 text,\n" + "    value text,\n"
                + "    PRIMARY KEY (key, column1)\n"
                + ") WITH COMPACT STORAGE;";
        List<String> allDefs = new ArrayList<String>();
        allDefs.add(metaDynamic);
        return allDefs;
    }

    public String writeMetaEntity(Entity entity) {
        session.execute(String.format(PaasUtils.INSERT_FORMAT, MetaConstants.META_KEY_SPACE + "."
                + MetaConstants.META_COLUMN_FAMILY, entity.getRowKey(), entity.getName(),
                entity.getPayLoad()));
        //addEntityToCache(entity.getRowKey(), entity);
        return "{\"msg\":\"ok\"";
    }
//    public String listStorage() {
//        Set<String> allStorage =  storageMap.keySet();
//        JsonObject obj = new JsonObject();
//        JsonArray arr = new JsonArray();
//        for (String storage: allStorage) {
//            arr.addString(storage);
//        }
//        obj.putArray("storages", arr);
//        return obj.toString();
//    }
//    public String listSchemas(){
//        JsonObject obj = new JsonObject();
//        JsonArray arr = new JsonArray();
//        for (String db: dbHolder) {
//            arr.addString(db);
//        }
//        obj.putArray("schemas", arr);
//        return obj.toString();
//    }
//    public String listTablesInSchema(String db) {
//        List<String> tables = dbToTableMap.get(db);
//        JsonObject obj = new JsonObject();
//        JsonArray arr = new JsonArray();
//        for (String table: tables) {
//            arr.addString(table);
//        }
//        obj.putArray(db, arr);
//        return obj.toString();
//    }
//    public String listTimeseriesInSchema(String db) {
//        List<String> tables = dbToTimeseriesMap.get(db);
//        JsonObject obj = new JsonObject();
//        JsonArray arr = new JsonArray();
//        for (String table: tables) {
//            arr.addString(table);
//        }
//        obj.putArray(db, arr);
//        return obj.toString();
//    }
    
//    private void addEntityToCache(String rowkey, Entity entity) {
//        switch (EntityType.valueOf(rowkey)) {
//        case STORAGE:
//            storageMap.put(entity.getName(), new JsonObject(entity.getPayLoad()));
//            break;
//        case DB:
//            dbHolder.add(entity.getName());
//            break;
//        case TABLE:
//            JsonObject payobject = new JsonObject(entity.getPayLoad());
//            tableToStorageMap.put(entity.getName(), payobject.getString("storage"));
//            String db = payobject.getString("db");
//            List<String> tables = dbToTableMap.get(db);
//            if (tables == null || tables.size() == 0) {
//                tables = new ArrayList<String>();
//                tables.add(entity.getName());
//            } else {
//                tables.add(entity.getName());
//            }
//            dbToTableMap.put(db, tables);
//            break;
//            
//        case SERIES:
//            JsonObject tsobject = new JsonObject(entity.getPayLoad());
//            tableToStorageMap.put(entity.getName(), tsobject.getString("storage"));
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
//    }
//    private void LoadTableToStorage() {
//        ResultSet rs = session
//                .execute("select column1, value from paasmetaks.metacf where key='"+MetaConstants.PAAS_TABLE_ENTITY_TYPE+"';");
//        List<Row> rows = rs.all();
//        for (Row row : rows) {
//            String field = row.getString(0);
//            JsonObject val = new JsonObject(row.getString(1));
//            String storage = val.getField("storage");
//            tableToStorageMap.put(field, storage);
//        }
//    }
//    public  Map<String,JsonObject> LoadStorage() {
//        ResultSet rs = session
//                .execute("select column1, value from paasmetaks.metacf where key='"+MetaConstants.PAAS_STORAGE_TYPE_ENTITY+"';");
//        List<Row> rows = rs.all();
//        for (Row row : rows) {
//            String field = row.getString(0);
//            JsonObject val = new JsonObject(row.getString(1));
//            storageMap.put(field, val);
//        }
//        return storageMap;
//    }
//
//    private void LoadDbNames() {
//        ResultSet rs = session
//                .execute("select column1 from paasmetaks.metacf where key='com.test.entity.type.paas.db';");
//        List<Row> rows = rs.all();
//        for (Row row : rows) {
//            dbHolder.add(row.getString(0));
//        }
//    }
//    private void LoadDbToTableMap() {
//        ResultSet rs = session
//                .execute("select column1 from paasmetaks.metacf where key='com.test.entity.type.paas.table';");
//        List<Row> rows = rs.all();
//        for (Row row : rows) {
//            String key = row.getString(0).split("\\.")[0];
//            String table = row.getString(0).split("\\.")[1];
//            List<String> currval = null;
//            currval = dbToTableMap.get(key);
//            if (currval == null) {
//                currval = new ArrayList<String>();
//            }
//            currval.add(table);
//            dbToTableMap.put(key, currval);
//        } 
//    }
//    private void LoadDbToTimeSeriesMap() {
//        ResultSet rs = session
//                .execute("select column1 from paasmetaks.metacf where key='com.test.entity.type.paas.timeseries';");
//        List<Row> rows = rs.all();
//        for (Row row : rows) {
//            String key = row.getString(0).split("\\.")[0];
//            String table = row.getString(0).split("\\.")[1];
//            List<String> currval = null;
//            currval = dbToTimeseriesMap.get(key);
//            if (currval == null) {
//                currval = new ArrayList<String>();
//            }
//            currval.add(table);
//            dbToTimeseriesMap.put(key, currval);
//        } 
//    }

//    public Entity readMetaEntity(String rowKey) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    public String writeRow(String db, String table, JsonObject rowObj) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    public String listRow(String db, String table, String keycol, String key) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
    public Map<String, String> getStorageMap() {
        // TODO Auto-generated method stub
        return null;
    }

    public Map<String, JsonObject> runQuery(String key, String col) {
        // TODO Auto-generated method stub
        ResultSet rs;
        if (col!=null && !col.equals("*")) {
            rs = session
                .execute("select column1, value from paasmetaks.metacf where key='"+key+"' and column1='"+col+"';");
        }
        else {
            rs = session
                    .execute("select column1, value from paasmetaks.metacf where key='"+key+"';");
        }
        List<Row> rows = rs.all();
        Map<String,JsonObject> storageMap = new HashMap<String,JsonObject>();
        for (Row row : rows) {
            String field = row.getString(0);
            JsonObject val = new JsonObject(row.getString(1));
            storageMap.put(field, val);
        }
        return storageMap;
    }

}
