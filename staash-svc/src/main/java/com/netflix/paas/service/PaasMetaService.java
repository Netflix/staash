package com.netflix.paas.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.netflix.paas.connection.ConnectionFactory;
import com.netflix.paas.connection.PaasConnection;
import com.netflix.paas.connection.PaasConnectionFactory;
import com.netflix.paas.exception.StorageDoesNotExistException;
import com.netflix.paas.json.JsonObject;
import com.netflix.paas.rest.dao.MetaDao;
import com.netflix.paas.rest.meta.entity.Entity;
import com.netflix.paas.rest.meta.entity.EntityType;
import com.netflix.paas.rest.meta.entity.PaasDBEntity;
import com.netflix.paas.rest.meta.entity.PaasStorageEntity;
import com.netflix.paas.rest.meta.entity.PaasTableEntity;
import com.netflix.paas.rest.meta.entity.PaasTimeseriesEntity;

public class PaasMetaService implements MetaService {
    private MetaDao meta;
    private ConnectionFactory cfactory;
    private CacheService cache;

    @Inject
    public PaasMetaService(@Named("newmetadao") MetaDao meta, ConnectionFactory fac, CacheService cache) {
        this.meta = meta;
//        this.cfactory = new PaasConnectionFactory(CLIENTTYPE.ASTYANAX.getType());
        this.cfactory = fac;
//        this.cache = new CacheService(meta);
        this.cache = cache;
    }

    public String writeMetaEntity(EntityType etype, String payload) throws StorageDoesNotExistException{
        // TODO Auto-generated method stub
        if (payload != null) {
            switch (etype) {
            case STORAGE:
                PaasStorageEntity pse = PaasStorageEntity.builder()
                        .withJsonPayLoad(new JsonObject(payload)).build();
                String retsto = meta.writeMetaEntity(pse);
                cache.addEntityToCache(EntityType.STORAGE, pse);
                return retsto;
            case DB:
                PaasDBEntity pdbe = PaasDBEntity.builder()
                        .withJsonPayLoad(new JsonObject(payload)).build();
                 String retdb = meta.writeMetaEntity(pdbe);
                cache.addEntityToCache(EntityType.DB, pdbe);
                return retdb;
            case TABLE:
                String schema = new JsonObject(payload).getString("db");
                PaasTableEntity pte = PaasTableEntity.builder()
                        .withJsonPayLoad(new JsonObject(payload), schema)
                        .build();
                createDBTable(pte.getPayLoad());
                String rettbl =  meta.writeMetaEntity(pte);
                cache.addEntityToCache(EntityType.TABLE, pte);
                return rettbl;
            case SERIES:
                String tsschema = new JsonObject(payload).getString("db");
                PaasTimeseriesEntity ptse = PaasTimeseriesEntity.builder()
                        .withJsonPayLoad(new JsonObject(payload), tsschema)
                        .build();
                JsonObject obj = new JsonObject(ptse.getPayLoad());                
                createDBTable(ptse.getPayLoad());
                String retseries =  meta.writeMetaEntity(ptse);
                cache.addEntityToCache(EntityType.SERIES, ptse);
                return retseries;
            }
        }
        return null;
    }

    private void createDBTable(String payload ) throws StorageDoesNotExistException {
//        String payload = pte.getPayLoad();
        JsonObject obj = new JsonObject(payload);
        String schema = obj.getString("db");
        String storage = obj.getString("storage");
        String index_row_keys = obj.getString("indexrowkeys");
        Map<String, JsonObject> sMap = meta.runQuery(
                EntityType.STORAGE.getId(), storage);
        JsonObject storageConfig = sMap.get(storage);
        String strategy = storageConfig.getString("strategy");
        String rf = storageConfig.getString("rf");
        if (strategy==null  || strategy.equals("") || strategy.equalsIgnoreCase("network")) strategy = "NetworkTopologyStrategy";
        if (rf==null || rf.equals("")) rf = "us-east:3";
        Map<String,JsonObject> dbMap = meta.runQuery(EntityType.DB.getId(), schema);
        JsonObject dbConfig = dbMap.get(schema);
        if (dbConfig.getString("strategy")==null || dbConfig.getString("strategy").equals("") || dbConfig.getString("rf")==null || dbConfig.getString("rf").equals(""))
        {    
            dbConfig.putString("strategy", strategy);
            dbConfig.putString("rf", rf);
        }
        if (storageConfig == null) throw new StorageDoesNotExistException();
        PaasConnection conn = cfactory.createConnection(storageConfig, schema);
        try {
            if (storageConfig.getString("type").equals("mysql"))
                conn.createDB(dbConfig.getString("name"));
            else
             conn.createDB(dbConfig.toString());
        } catch (Exception e) {
            // TODO: handle exception
        }
        try {
            conn.createTable(obj);
            if (index_row_keys!=null && index_row_keys.equals("true")) {
                JsonObject idxObj = new JsonObject();
                idxObj.putString("db", schema);
                idxObj.putString("name", obj.getString("name")+"ROWKEYS");
                idxObj.putString("columns", "key,column1,value");
                idxObj.putString("primarykey", "key,column1");
                conn.createTable(idxObj);
                //conn.createRowIndexTable(obj)
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

//    public Entity readMetaEntity(String rowKey) {
//        // TODO Auto-generated method stub
//        return meta.readMetaEntity(rowKey);
//    }
//
//    public String writeRow(String db, String table, JsonObject rowObj) {
//        // TODO Auto-generated method stub
//        return meta.writeRow(db, table, rowObj);
//    }
//
//    public String listRow(String db, String table, String keycol, String key) {
//        // TODO Auto-generated method stub
//        return meta.listRow(db, table, keycol, key);
//    }

    public String listSchemas() {
        // TODO Auto-generated method stub
        return cache.listSchemas();
    }

    public String listTablesInSchema(String schemaname) {
        // TODO Auto-generated method stub
        return cache.listTablesInSchema(schemaname);
    }

    public String listTimeseriesInSchema(String schemaname) {
        // TODO Auto-generated method stub
        return cache.listTimeseriesInSchema(schemaname);
    }

    public String listStorage() {
        // TODO Auto-generated method stub
        return cache.listStorage();
    }

    public Map<String, String> getStorageMap() {
        // TODO Auto-generated method stub
        return meta.getStorageMap();
    }

    public String CreateDB() {
        // TODO Auto-generated method stub
        return null;
    }

    public String createTable() {
        // TODO Auto-generated method stub
        return null;
    }
    public JsonObject getStorageForTable(String table) {
        return cache.getStorageForTable(table);
    }

    public JsonObject runQuery(EntityType etype, String col) {
//        switch (etype) {
//        case STORAGE:
//            final Set<String> storages = cache.getStorageNames();
//            if (storages.contains(col)) {
//                return cache.getStorage(col);
//            }
//            break;
//        case DB:
//            final Set<String> dbnames = cache.getDbNames();
//            if (dbnames.contains(col)) {
//                return new JsonObject("\"name\":\""+col+"\"");
//            }
//            break;
//        case TABLE:
//            List<String> tables = cache.getTableNames(col);
//            break;
//        case SERIES:
//            String tsschema = new JsonObject(payload).getString("db");
//            PaasTimeseriesEntity ptse = PaasTimeseriesEntity.builder()
//                    .withJsonPayLoad(new JsonObject(payload), tsschema)
//                    .build();
//            JsonObject obj = new JsonObject(ptse.getPayLoad());                
//            createDBTable(ptse.getPayLoad());
//            String retseries =  meta.writeMetaEntity(ptse);
//            cache.addEntityToCache(ptse.getRowKey(), ptse);
//            return retseries;
//        }
        return meta.runQuery(etype.getId(), col).get(col);
    }

    public JsonObject extenddb(String db, String region) {
        // TODO Auto-generated method stub
        return null;
    }

}
