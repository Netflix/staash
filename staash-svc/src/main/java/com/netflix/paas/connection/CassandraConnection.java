package com.netflix.paas.connection;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.netflix.paas.common.query.QueryFactory;
import com.netflix.paas.common.query.QueryType;
import com.netflix.paas.common.query.QueryUtils;
import com.netflix.paas.json.JsonObject;
import com.netflix.paas.model.StorageType;

public class CassandraConnection implements PaasConnection{
    private Cluster cluster;
    private Session session;
    public CassandraConnection(Cluster cluster) {
        this.cluster = cluster;
        session = this.cluster.connect();
    }
    public Session getSession() {
        return cluster.connect();
    }
    public String insert(String db, String table, JsonObject payload) {
        // TODO Auto-generated method stub
        String query = QueryFactory.BuildQuery(QueryType.INSERT, StorageType.CASSANDRA);
        session.execute(String.format(query, db+"."+table,payload.getString("columns"),payload.getValue("values")));
        return "{\"message\":\"ok\"}";
    }
    public String createDB(String dbname) {
        // TODO Auto-generated method stub
          String sql = String.format(QueryFactory.BuildQuery(QueryType.CREATEDB, StorageType.CASSANDRA), dbname,1);;
          session.execute(sql);
          return "{\"message\":\"ok\"}";
    }
    public String createTable(JsonObject payload) {
        // TODO Auto-generated method stub
        String sql = String.format(QueryFactory.BuildQuery(QueryType.CREATETABLE, StorageType.CASSANDRA), payload.getString("db")+"."+payload.getString("name"),QueryUtils.formatColumns(payload.getString("columns"),StorageType.CASSANDRA),payload.getString("primarykey"));
        session.execute(sql+";");
        return "{\"message\":\"ok\"}";
    }

    public String read(String db, String table, String keycol, String key, String... keyvals) {
        // TODO Auto-generated method stub
        if (keyvals!=null && keyvals.length==2) {
            String query = QueryFactory.BuildQuery(QueryType.SELECTEVENT, StorageType.CASSANDRA);
            return QueryUtils.formatQueryResult(session.execute(String.format(query, db+"."+table,keycol,key,keyvals[0],keyvals[1])));
        } else {
        String query = QueryFactory.BuildQuery(QueryType.SELECT, StorageType.CASSANDRA);
        ResultSet rs = session.execute(String.format(query, db+"."+table,keycol,key));
        if (rs!=null && rs.all().size()>0)
            return QueryUtils.formatQueryResult(rs);
        return "{\"msg\":\"Nothing is found\"}";
        }
    }
    public String createRowIndexTable(JsonObject payload) {
        // TODO Auto-generated method stub
        return null;
    }
	public void closeConnection() {
		// TODO Auto-generated method stub
		
	}
}
