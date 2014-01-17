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

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.netflix.staash.common.query.QueryFactory;
import com.netflix.staash.common.query.QueryType;
import com.netflix.staash.common.query.QueryUtils;
import com.netflix.staash.json.JsonObject;
import com.netflix.staash.model.StorageType;

public class CassandraConnection implements PaasConnection {
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
		String query = QueryFactory.BuildQuery(QueryType.INSERT,
				StorageType.CASSANDRA);
		session.execute(String.format(query, db + "." + table,
				payload.getString("columns"), payload.getValue("values")));
		return "{\"message\":\"ok\"}";
	}

	public String createDB(String dbname) {
		String sql = String.format(QueryFactory.BuildQuery(QueryType.CREATEDB,
				StorageType.CASSANDRA), dbname, 1);
		;
		session.execute(sql);
		return "{\"message\":\"ok\"}";
	}

	public String createTable(JsonObject payload) {
		String sql = String
				.format(QueryFactory.BuildQuery(QueryType.CREATETABLE,
						StorageType.CASSANDRA), payload.getString("db") + "."
						+ payload.getString("name"), QueryUtils.formatColumns(
						payload.getString("columns"), StorageType.CASSANDRA),
						payload.getString("primarykey"));
		session.execute(sql + ";");
		return "{\"message\":\"ok\"}";
	}

	public String read(String db, String table, String keycol, String key,
			String... keyvals) {
		if (keyvals != null && keyvals.length == 2) {
			String query = QueryFactory.BuildQuery(QueryType.SELECTEVENT,
					StorageType.CASSANDRA);
			return QueryUtils.formatQueryResult(session.execute(String.format(
					query, db + "." + table, keycol, key, keyvals[0],
					keyvals[1])));
		} else {
			String query = QueryFactory.BuildQuery(QueryType.SELECT,
					StorageType.CASSANDRA);
			ResultSet rs = session.execute(String.format(query, db + "."
					+ table, keycol, key));
			if (rs != null && rs.all().size() > 0)
				return QueryUtils.formatQueryResult(rs);
			return "{\"msg\":\"Nothing is found\"}";
		}
	}

	public void closeConnection() {
        //Implement as per driver choice
	}

	public ByteArrayOutputStream readChunked(String db, String table, String objectName) {
		return null;
	}

	public String writeChunked(String db, String table, String objectName,
			InputStream is) {
		return null;
	}
}
