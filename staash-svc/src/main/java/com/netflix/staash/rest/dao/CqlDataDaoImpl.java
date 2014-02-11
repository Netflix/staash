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

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.netflix.staash.json.JsonObject;
import com.netflix.staash.rest.meta.entity.PaasTableEntity;
import com.netflix.staash.rest.util.Pair;
import com.netflix.staash.service.CacheService;
import com.netflix.staash.storage.service.MySqlService;

public class CqlDataDaoImpl implements DataDao {
    private MetaDao meta;
    private Cluster cluster;
    private Session session;
    @Inject
    public CqlDataDaoImpl(@Named("datacluster") Cluster cluster,  MetaDao meta) {
        this.cluster = cluster;
        this.meta = meta;
        this.session = this.cluster.connect();
        //from the meta get the name of the cluster for this db
    }
    public String writeRow(String db, String table, JsonObject rowObj) {
        String query = BuildRowInsertQuery(db, table, rowObj);
        Print(query);
        //String storage = rowObj.getField("storage");
        String storage = meta.runQuery("com.netflix.test.storage",db+"."+table).get(db+"."+table).getString("storage");
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
        //String storage = rowObj.getField("storage");
        String storage = meta.runQuery("com.netflix.test.storage",db+"."+table).get(db+"."+table).getString("storage");
        if (storage!=null && storage.contains("mysql")) return "INSERT INTO" + " " + table + "(" + columns + ")"
                + " VALUES(" + values + ");";else
        return "INSERT INTO" + " " + db + "." + table + "(" + columns + ")"
                + " VALUES(" + values + ");";
    }

    private void Print(String str) {
        // TODO Auto-generated method stub
        System.out.println(str);
    }

    private String BuildQuery(PaasTableEntity tableEnt) {
        // TODO Auto-generated method stub
        String schema = tableEnt.getSchemaName();
        String tableName = tableEnt.getName();
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

    public String listRow(String db, String table, String keycol, String key) {
        // TODO Auto-generated method stub
        String storage = meta.runQuery("com.netflix.test.storage",db+"."+table).get(db+"."+table).getString("storage");
        if (storage!=null && storage.contains("mysql")) {
            String query = "select * from "+table+" where "+keycol+"=\'"+key+"\'";
            Print(query);
            java.sql.ResultSet rs = MySqlService.executeRead(db, query);
            try {
                while (rs.next()) {
                    ResultSetMetaData rsmd = rs.getMetaData();
                    String columns ="";
                    String values = "";
                    int count = rsmd.getColumnCount();
                    for (int i=1;i<=count;i++) {
                        String colName = rsmd.getColumnName(i);
                        columns = columns + colName + ",";
                        String value = rs.getString(i);
                        values = values + value +",";
                    }
                    JsonObject response = new JsonObject();
                    response.putString("columns", columns.substring(0, columns.length()-1));
                    response.putString("values", values.substring(0, values.length()-1));
                    return response.toString();
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
        }
        String query = select().all().from(db, table).where(eq(keycol, key))
                .getQueryString();
        Cluster cluster = Cluster.builder().addContactPoints("localhost").build();
        Session session = cluster.connect(db);
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
    public String writeEvent(String db, String table, JsonObject rowObj) {
        // TODO Auto-generated method stub
        Long evTime = rowObj.getLong("time");
        String value = rowObj.getString("event");
        Long periodicity = 100L;
        Long rowKey = (evTime/periodicity)*periodicity;
        String INSERTSTR = "insert into "+db+"."+table+"(key,column1,value) values('"+rowKey.toString()+"',"+evTime+",'"+
        value+"');";
        Print(INSERTSTR);
        session.execute(INSERTSTR);
        JsonObject obj = new JsonObject("{\"status\":\"ok\"}");
        return obj.toString(); 
    }
    public String readEvent(String db, String table, String evTime) {
        // TODO Auto-generated method stub
        Long periodicity = 100L;
        Long rowKey = (Long.valueOf(evTime)/periodicity)*periodicity;
        String query = select().all().from(db, table).where(eq("key", String.valueOf(rowKey))).and(eq("column1",Long.valueOf(evTime)))
                .getQueryString();
        Cluster cluster = Cluster.builder().addContactPoints("localhost").build();
        Session session = cluster.connect(db);
        ResultSet rs = session.execute(query);
        return convertResultSet(rs);
    }
    public String doJoin(String db, String table1, String table2,
        String joincol, String value) {
        String res1 = listRow(db,table1,joincol,value);
        String res2 = listRow(db,table2,joincol,value);
        return "{\""+table1+"\":"+res1+",\""+table2+"\":"+res2+"}";
    }

}
