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
package com.netflix.staash.connection;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.netflix.staash.common.query.QueryFactory;
import com.netflix.staash.common.query.QueryType;
import com.netflix.staash.common.query.QueryUtils;
import com.netflix.staash.json.JsonObject;
import com.netflix.staash.model.StorageType;

public class MySqlConnection implements PaasConnection{
    private Connection conn;
    public MySqlConnection(Connection conn) {
        this.conn = conn;
    }
    public Connection getConnection() {
        return conn;
    }
    public String insert(String db, String table, JsonObject payload) {
        String query = QueryFactory.BuildQuery(QueryType.INSERT, StorageType.MYSQL);
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("USE "+db);
            stmt.executeUpdate(String.format(query, table,payload.getString("columns"),payload.getValue("values")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "\"message\":\"ok\"";
    }
    public String createDB(String dbname) {
          String sql = String.format(QueryFactory.BuildQuery(QueryType.CREATEDB, StorageType.MYSQL), dbname);;
          Statement stmt = null;
          try {
              stmt = conn.createStatement();
              stmt.executeUpdate(sql);
          } catch (SQLException e) {
              throw new RuntimeException(e);              
          }
          return "\"message\":\"ok\"";
    }
    public String createTable(JsonObject payload) {
        Statement stmt = null;
        String sql = String.format(QueryFactory.BuildQuery(QueryType.SWITCHDB, StorageType.MYSQL), payload.getString("db"));
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);              
        }
        String createTblQry = String.format(QueryFactory.BuildQuery(QueryType.CREATETABLE, StorageType.MYSQL), payload.getString("name"),QueryUtils.formatColumns(payload.getString("columns"),StorageType.MYSQL),payload.getString("primarykey"));
        try {
            stmt.executeUpdate(createTblQry);
        } catch (SQLException e) {
        }
        return "\"message\":\"ok\"";
    }
    public String read(String db, String table, String keycol, String key, String... values) {
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
            throw new RuntimeException(e);              
        }
    }
   	public void closeConnection() {
		if (conn!=null)
			try {
				conn.close();
			} catch (SQLException e) {
				throw new RuntimeException(e.getMessage());
			}
   	}
	public ByteArrayOutputStream readChunked(String db, String table, String objectName) {
		return null;
	}
	public String writeChunked(String db, String table, String objectName, InputStream is) {
		return null;
	}
 }
