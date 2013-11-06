package com.netflix.paas.connection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.netflix.paas.common.query.QueryFactory;
import com.netflix.paas.common.query.QueryType;
import com.netflix.paas.common.query.QueryUtils;
import com.netflix.paas.json.JsonObject;
import com.netflix.paas.model.StorageType;

public class MySqlConnection implements PaasConnection{
    private Connection conn;
    public MySqlConnection(Connection conn) {
        this.conn = conn;
    }
    public Connection getConnection() {
        return conn;
    }
    public String insert(String db, String table, JsonObject payload) {
        // TODO Auto-generated method stub
        String query = QueryFactory.BuildQuery(QueryType.INSERT, StorageType.MYSQL);
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("USE "+db);
            stmt.executeUpdate(String.format(query, table,payload.getString("columns"),payload.getValue("values")));
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace(); already exists ignore
            throw new RuntimeException(e);
        }
        return "\"message\":\"ok\"";
    }
    public String createDB(String dbname) {
        // TODO Auto-generated method stub
          String sql = String.format(QueryFactory.BuildQuery(QueryType.CREATEDB, StorageType.MYSQL), dbname);;
          Statement stmt = null;
          try {
              stmt = conn.createStatement();
              stmt.executeUpdate(sql);
          } catch (SQLException e) {
              // TODO Auto-generated catch block
//              e1.printStackTrace();Already Exists
              throw new RuntimeException(e);              
          }
          return "\"message\":\"ok\"";
    }
    public String createTable(JsonObject payload) {
        // TODO Auto-generated method stub
        Statement stmt = null;
        String sql = String.format(QueryFactory.BuildQuery(QueryType.SWITCHDB, StorageType.MYSQL), payload.getString("db"));
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new RuntimeException(e);              
        }
        String createTblQry = String.format(QueryFactory.BuildQuery(QueryType.CREATETABLE, StorageType.MYSQL), payload.getString("name"),QueryUtils.formatColumns(payload.getString("columns"),StorageType.MYSQL),payload.getString("primarykey"));
        try {
            stmt.executeUpdate(createTblQry);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            //throw new RuntimeException(e);              
        }
        return "\"message\":\"ok\"";
    }
    public String read(String db, String table, String keycol, String key, String... values) {
        // TODO Auto-generated method stub
        String query = QueryFactory.BuildQuery(QueryType.SELECTALL, StorageType.MYSQL);
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("USE "+db);
            ResultSet rs;
            if (keycol!=null && !keycol.equals(""))
                rs = stmt.executeQuery(String.format(query, table,keycol,key));
            else 
                rs = stmt.executeQuery(String.format("select * from %s", table));
            return QueryUtils.formatQueryResult(rs);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace(); already exists ignore
            throw new RuntimeException(e);              
        }
    }
    public String createRowIndexTable(JsonObject payload) {
        // TODO Auto-generated method stub
        return null;
    }
 }
