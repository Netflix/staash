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
package com.netflix.staash.rest.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.AlreadyExistsException;
import com.datastax.driver.core.exceptions.DriverException;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.netflix.staash.json.JsonArray;
import com.netflix.staash.json.JsonObject;
import com.netflix.staash.rest.meta.entity.Entity;
import com.netflix.staash.rest.meta.entity.PaasDBEntity;
import com.netflix.staash.rest.meta.entity.PaasStorageEntity;
import com.netflix.staash.rest.meta.entity.PaasTableEntity;
import com.netflix.staash.rest.meta.entity.PaasTimeseriesEntity;
import com.netflix.staash.rest.util.MetaConstants;
import com.netflix.staash.rest.util.PaasUtils;
import com.netflix.staash.rest.util.Pair;
import com.netflix.staash.storage.service.MySqlService;

import static com.datastax.driver.core.querybuilder.QueryBuilder.*;

public class CqlMetaDaoImpl implements MetaDao {
    private Cluster cluster;
    Session session;
    private static boolean schemaCreated = false;
    static final String metaks = "paasmetaks";
    static final String metacf = "metacf";
    private Set<String> dbHolder = new HashSet<String>();
    private Map<String,List<String>> dbToTableMap = new HashMap<String,List<String>>();
    private Map<String,List<String>> dbToTimeseriesMap = new HashMap<String,List<String>>();
    private Map<String, String> tableToStorageMap = new HashMap<String, String>();
    private JsonObject jsonStorage = new JsonObject();


    @Inject
    public CqlMetaDaoImpl(@Named("metacluster") Cluster cluster) {
//        Cluster cluster = Cluster.builder().addContactPoint("localhost")
//                .build();
        this.cluster = cluster;
        this.session = this.cluster.connect();
        maybeCreateMetaSchema();
        LoadDbNames();
        LoadDbToTableMap();
        LoadDbToTimeSeriesMap();
        LoadStorage();
        LoadTableToStorage();
    }
    private void LoadTableToStorage() {
        ResultSet rs = session
                .execute("select column1, value from "+MetaConstants.META_KEY_SPACE+"."+MetaConstants.META_COLUMN_FAMILY+ " where key='"+MetaConstants.STAASH_TABLE_ENTITY_TYPE+"';");
        List<Row> rows = rs.all();
        for (Row row : rows) {
            String field = row.getString(0);
            JsonObject val = new JsonObject(row.getString(1));
            String storage = val.getField("storage");
            tableToStorageMap.put(field, storage);
        }
    }
    public  Map<String,JsonObject> LoadStorage() {
        ResultSet rs = session
                .execute("select column1, value from "+MetaConstants.META_KEY_SPACE+"."+MetaConstants.META_COLUMN_FAMILY+ " where key='"+MetaConstants.STAASH_STORAGE_TYPE_ENTITY+"';");
        List<Row> rows = rs.all();
        Map<String,JsonObject> storageMap = new HashMap<String,JsonObject>();
        for (Row row : rows) {
            String field = row.getString(0);
            JsonObject val = new JsonObject(row.getString(1));
            jsonStorage.putObject(field, val);
            storageMap.put(field, val);
        }
        return storageMap;
    }

    private void LoadDbNames() {
        ResultSet rs = session
                .execute("select column1 from "+MetaConstants.META_KEY_SPACE+"."+MetaConstants.META_COLUMN_FAMILY+ " where key='com.test.entity.type.paas.db';");
        List<Row> rows = rs.all();
        for (Row row : rows) {
            dbHolder.add(row.getString(0));
        }
    }
    private void LoadDbToTableMap() {
        ResultSet rs = session
                .execute("select column1 from "+MetaConstants.META_KEY_SPACE+"."+MetaConstants.META_COLUMN_FAMILY+ " where key='com.test.entity.type.paas.table';");
        List<Row> rows = rs.all();
        for (Row row : rows) {
            String key = row.getString(0).split("\\.")[0];
            String table = row.getString(0).split("\\.")[1];
            List<String> currval = null;
            currval = dbToTableMap.get(key);
            if (currval == null) {
                currval = new ArrayList<String>();
            }
            currval.add(table);
            dbToTableMap.put(key, currval);
        } 
    }
    private void LoadDbToTimeSeriesMap() {
        ResultSet rs = session
                .execute("select column1 from "+MetaConstants.META_KEY_SPACE+"."+MetaConstants.META_COLUMN_FAMILY+ " where key='com.test.entity.type.paas.timeseries';");
        List<Row> rows = rs.all();
        for (Row row : rows) {
            String key = row.getString(0).split("\\.")[0];
            String table = row.getString(0).split("\\.")[1];
            List<String> currval = null;
            currval = dbToTimeseriesMap.get(key);
            if (currval == null) {
                currval = new ArrayList<String>();
            }
            currval.add(table);
            dbToTimeseriesMap.put(key, currval);
        } 
    }
    public String writeMetaEntityOnly(Entity entity) {
        session.execute(String.format(PaasUtils.INSERT_FORMAT, MetaConstants.META_KEY_SPACE + "."
                + MetaConstants.META_COLUMN_FAMILY, entity.getRowKey(), entity.getName(),
                entity.getPayLoad()));
        return "ok";
    }
    
    
    public String writeMetaEntity(Entity entity) {
        try {
            if (dbHolder.contains(entity.getName())) {
                JsonObject obj = new JsonObject(
                        "{\"status\":\"error\",\"message\":\"db names must be unique\"}");
                return obj.toString();
            }
            session.execute(String.format(PaasUtils.INSERT_FORMAT, MetaConstants.META_KEY_SPACE + "."
                    + MetaConstants.META_COLUMN_FAMILY, entity.getRowKey(), entity.getName(),
                    entity.getPayLoad()));
            if (entity instanceof PaasDBEntity) dbHolder.add(entity.getName());
            if (entity instanceof PaasStorageEntity) jsonStorage.putObject(entity.getName(), new JsonObject(entity.getPayLoad()));
        } catch (AlreadyExistsException e) {
            // It's ok, ignore
        }
        if (entity instanceof PaasTableEntity) {
            // first create/check if schema db exists
            PaasTableEntity tableEnt = (PaasTableEntity) entity;
            String schemaName = tableEnt.getSchemaName();
            String storage = tableEnt.getStorage();
            try {
//                String payLoad = tableEnt.getPayLoad();
                if (storage!=null && storage.contains("mysql")) {
                    MySqlService.createDbInMySql(schemaName);
                } //else {
                session.execute(String.format(
                        PaasUtils.CREATE_KEYSPACE_SIMPLE_FORMAT, schemaName, 1));
                //}//create counterpart in cassandra
            } catch (AlreadyExistsException e) {
                // It's ok, ignore
            }
            // if schema/db already exists now create the table
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String query = BuildQuery(tableEnt);
            Print(query);
            if (storage!=null && storage.contains("mysql")) {
               MySqlService.createTableInDb(schemaName, query); 
            } else {
                storage="cassandra";
                session.execute(query);
            }
            List<String> tables = dbToTableMap.get(tableEnt.getSchemaName());
            if (tables==null) tables = new ArrayList<String>();
            tables.add(tableEnt.getName());
            tableToStorageMap.put(tableEnt.getName(), storage);
            // List<String> primaryKeys = entity.getPrimaryKey();
        }
        if (entity instanceof PaasTimeseriesEntity) {
            // first create/check if schema db exists
            PaasTimeseriesEntity tableEnt = (PaasTimeseriesEntity) entity;
            try {
                String schemaName = tableEnt.getSchemaName();
                session.execute(String.format(
                        PaasUtils.CREATE_KEYSPACE_SIMPLE_FORMAT, schemaName, 1));
            } catch (AlreadyExistsException e) {
                // It's ok, ignore
            }
            // if schema/db already exists now create the table
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String query = BuildQuery(tableEnt);
            Print(query);
            session.execute(query);
            List<String> tables = dbToTimeseriesMap.get(tableEnt.getSchemaName());
            if (tables==null) tables = new ArrayList<String>();
            tables.add(tableEnt.getName().substring(tableEnt.getName().indexOf(".")+1));
            // List<String> primaryKeys = entity.getPrimaryKey();
        }
        JsonObject obj = new JsonObject("{\"status\":\"ok\"}");
        return obj.toString();
    }

    public String writeRow(String db, String table, JsonObject rowObj) {
        String query = BuildRowInsertQuery(db, table, rowObj);
        Print(query);
        String storage = tableToStorageMap.get(db+"."+table);
        if (storage!=null && storage.equals("mysql")) {
            MySqlService.insertRowIntoTable(db, table, query);
        } else {
        session.execute(query);
        }
        JsonObject obj = new JsonObject("{\"status\":\"ok\"}");
        return obj.toString();
    }

    private String BuildRowInsertQuery(String db, String table,
            JsonObject rowObj) {
        // TODO Auto-generated method stub
        String columns = rowObj.getString("columns");
        String values = rowObj.getString("values");
        String storage = tableToStorageMap.get(db+"."+table);
        if (storage!=null && storage.equals("mysql")) {
            return "INSERT INTO" + " " +  table + "(" + columns + ")"
                    + " VALUES(" + values + ");"; 
        }else {
        return "INSERT INTO" + " " + db + "." + table + "(" + columns + ")"
                + " VALUES(" + values + ");";
        }
    }

    private void Print(String str) {
        // TODO Auto-generated method stub
        System.out.println(str);
    }

    private String BuildQuery(PaasTableEntity tableEnt) {
        // TODO Auto-generated method stub
        String storage = tableEnt.getStorage();
        if (storage!=null && storage.equals("mysql")) {
            String schema = tableEnt.getSchemaName();
            String tableName = tableEnt.getName().split("\\.")[1];
            List<Pair<String, String>> columns = tableEnt.getColumns();
            String colStrs = "";
            for (Pair<String, String> colPair : columns) {
                colStrs = colStrs + colPair.getRight() + " " + colPair.getLeft()
                        + ", ";
            }
            String primarykeys = tableEnt.getPrimarykey();
            String PRIMARYSTR = "PRIMARY KEY(" + primarykeys + ")";
            return "CREATE TABLE " +  tableName + " (" + colStrs
                    + " " + PRIMARYSTR + ");";
        } else {
        String schema = tableEnt.getSchemaName();
        String tableName = tableEnt.getName().split("\\.")[1];
        List<Pair<String, String>> columns = tableEnt.getColumns();
        String colStrs = "";
        for (Pair<String, String> colPair : columns) {
            colStrs = colStrs + colPair.getRight() + " " + colPair.getLeft()
                    + ", ";
        }
        String primarykeys = tableEnt.getPrimarykey();
        String PRIMARYSTR = "PRIMARY KEY(" + primarykeys + ")";
        return "CREATE TABLE " + schema + "." + tableName + " (" + colStrs
                + " " + PRIMARYSTR + ");";
        }
    }
    
    private String BuildQuery(PaasTimeseriesEntity tableEnt) {
        // TODO Auto-generated method stub
        String schema = tableEnt.getSchemaName();
        String tableName = tableEnt.getName().split("\\.")[1];
        List<Pair<String, String>> columns = tableEnt.getColumns();
        String colStrs = "";
        for (Pair<String, String> colPair : columns) {
            colStrs = colStrs + colPair.getRight() + " " + colPair.getLeft()
                    + ", ";
        }
        String primarykeys = tableEnt.getPrimarykey();
        String PRIMARYSTR = "PRIMARY KEY(" + primarykeys + ")";
        return "CREATE TABLE " + schema + "." + tableName + " (" + colStrs
                + " " + PRIMARYSTR + ");";
    }

    public void maybeCreateMetaSchema() {

        try {
            if (schemaCreated)
                return;

            try {
                session.execute(String.format(
                        PaasUtils.CREATE_KEYSPACE_SIMPLE_FORMAT, metaks, 1));
            } catch (AlreadyExistsException e) {
                // It's ok, ignore
            }

            session.execute("USE " + metaks);

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

    public Entity readMetaEntity(String rowKey) {
        // TODO Auto-generated method stub
        return null;
    }

    public String listRow(String db, String table, String keycol, String key) {
        // TODO Auto-generated method stub
        String query = select().all().from(db, table).where(eq(keycol, key))
                .getQueryString();
        ResultSet rs = session.execute(query);
        return convertResultSet(rs);
    }

    private String convertResultSet(ResultSet rs) {
        // TODO Auto-generated method stub
        String colStr = "";
        String rowStr = "";
        JsonObject response = new JsonObject();
        List<Row> rows = rs.all();
        if (!rows.isEmpty() && rows.size() == 1) {
            rowStr = rows.get(0).toString();
        }
        ColumnDefinitions colDefs = rs.getColumnDefinitions();
        colStr = colDefs.toString();
        response.putString("columns", colStr.substring(8, colStr.length() - 1));
        response.putString("values", rowStr.substring(4, rowStr.length() - 1));
        return response.toString();

    }

    public String listSchemas() {
        // TODO Auto-generated method stub
        JsonObject obj = new JsonObject();
        JsonArray arr = new JsonArray();
        for (String db: dbHolder) {
            arr.addString(db);
        }
        obj.putArray("schemas", arr);
        return obj.toString();
    }
    public String listTablesInSchema(String schemaname) {
        // TODO Auto-generated method stub
        JsonObject obj = new JsonObject();
        JsonArray arr = new JsonArray();
        List<String> tblNames = dbToTableMap.get(schemaname);
        for (String name: tblNames) {
            arr.addString(name);
        }
        obj.putArray(schemaname, arr);
        return obj.toString();
    }
    public String listTimeseriesInSchema(String schemaname) {
        // TODO Auto-generated method stub
        JsonObject obj = new JsonObject();
        JsonArray arr = new JsonArray();
        List<String> tblNames = dbToTimeseriesMap.get(schemaname);
        for (String name: tblNames) {
            arr.addString(name);
        }
        obj.putArray(schemaname, arr);
        return obj.toString();
    }

    public String listStorage() {
        // TODO Auto-generated method stub
        
        return jsonStorage.toString();
    }
    public Map<String, String> getTableToStorageMap() {
        return tableToStorageMap;
    }
    public Map<String, String> getStorageMap() {
        // TODO Auto-generated method stub
        return tableToStorageMap;
    }
    public Map<String, JsonObject> runQuery(String key, String col) {
        // TODO Auto-generated method stub
        ResultSet rs = session
                .execute("select column1, value from "+MetaConstants.META_KEY_SPACE+"."+MetaConstants.META_COLUMN_FAMILY+ " where key='"+key+"' and column1='"+col+"';");
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
