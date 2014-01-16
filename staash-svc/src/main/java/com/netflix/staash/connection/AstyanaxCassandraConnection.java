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
import java.util.HashMap;
import java.util.Map;
import org.apache.cassandra.utils.Hex;
import org.apache.log4j.Logger;
import com.google.common.collect.ImmutableMap;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolType;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.cql.CqlStatementResult;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.recipes.storage.CassandraChunkedStorageProvider;
import com.netflix.astyanax.recipes.storage.ChunkedStorage;
import com.netflix.astyanax.recipes.storage.ChunkedStorageProvider;
import com.netflix.astyanax.recipes.storage.ObjectMetadata;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;
import com.netflix.staash.cassandra.discovery.EurekaAstyanaxHostSupplier;
import com.netflix.staash.common.query.QueryFactory;
import com.netflix.staash.common.query.QueryType;
import com.netflix.staash.common.query.QueryUtils;
import com.netflix.staash.json.JsonObject;
import com.netflix.staash.model.StorageType;

public class AstyanaxCassandraConnection implements PaasConnection {
	private Keyspace keyspace;
	private static Logger logger = Logger
			.getLogger(AstyanaxCassandraConnection.class);

	public AstyanaxCassandraConnection(String cluster, String db,
			EurekaAstyanaxHostSupplier supplier) {
		this.keyspace = createAstyanaxKeyspace(cluster, db, supplier);
	}

	private Keyspace createAstyanaxKeyspace(String clustername, String db,
			EurekaAstyanaxHostSupplier supplier) {
		String clusterNameOnly = "";
		String[] clusterinfo = clustername.split(":");
		if (clusterinfo != null && clusterinfo.length == 2) {
			clusterNameOnly = clusterinfo[0];
		} else {
			clusterNameOnly = clustername;
		}
		AstyanaxContext<Keyspace> keyspaceContext = new AstyanaxContext.Builder()
				.forCluster("Casss_Paas")
				.forKeyspace(db)
				.withAstyanaxConfiguration(
						new AstyanaxConfigurationImpl()
								.setDiscoveryType(
										NodeDiscoveryType.DISCOVERY_SERVICE)
								.setConnectionPoolType(
										ConnectionPoolType.TOKEN_AWARE)
								.setDiscoveryDelayInSeconds(60000)
								.setTargetCassandraVersion("1.1")
								.setCqlVersion("3.0.0"))
				.withHostSupplier(supplier.getSupplier(clustername))
				.withConnectionPoolConfiguration(
						new ConnectionPoolConfigurationImpl(clusterNameOnly
								+ "_" + db).setSocketTimeout(10000)
								.setPort(7102).setMaxTimeoutWhenExhausted(2000)
								.setMaxConnsPerHost(3).setInitConnsPerHost(1)
								.setSeeds(null))
				.withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
				.buildKeyspace(ThriftFamilyFactory.getInstance());
		keyspaceContext.start();
		Keyspace keyspace;
		keyspace = keyspaceContext.getClient();
		return keyspace;
	}

	public String insert(String db, String table, JsonObject payload) {

		try {
			if (payload.getString("type").equals("kv")) {
				 String str = Hex.bytesToHex(payload.getBinary("value"));
				 String stmt = "insert into " + db + "." + table
				 + "(key, value)" + " values('"
				 + payload.getString("key") + "' , '" + str + "');";
				 keyspace.prepareCqlStatement().withCql(stmt).execute();
			} else {
				String query = QueryFactory.BuildQuery(QueryType.INSERT,
						StorageType.CASSANDRA);
				keyspace.prepareCqlStatement()
						.withCql(
								String.format(query, db + "." + table,
										payload.getString("columns"),
										payload.getValue("values"))).execute();
			}
		} catch (ConnectionException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		return "{\"message\":\"ok\"}";
	}

	public String writeChunked(String db, String table, String objectName, InputStream is)
			throws Exception {
		ChunkedStorageProvider provider = new CassandraChunkedStorageProvider(
				keyspace, table);
		ObjectMetadata meta = ChunkedStorage
				.newWriter(provider, objectName,
						is)
				.withChunkSize(0x40000)
				.withConcurrencyLevel(8)
				.withMaxWaitTime(10)
				.call();
		if (meta!=null && meta.getObjectSize()<=0)
			throw new RuntimeException("Object does not exist");
		return "{\"msg\":\"ok\"}";
	}

	public OutputStream readChunked(String db, String table, String objName)
			throws Exception {
		ChunkedStorageProvider provider = new CassandraChunkedStorageProvider(
				keyspace, table);
		ObjectMetadata meta = ChunkedStorage.newInfoReader(provider, objName)
				.call();
		ByteArrayOutputStream os = new ByteArrayOutputStream(meta
				.getObjectSize().intValue());
		meta = ChunkedStorage.newReader(provider, objName, os)
				.withConcurrencyLevel(8)
				.withMaxWaitTime(10)
				.withBatchSize(10)
				.call();
		return os;
	}

	public String createDB(String dbInfo) {
		JsonObject dbJson = new JsonObject(dbInfo);

		try {
			String rfString = dbJson.getString("rf");
			String strategy = dbJson.getString("strategy");
			String[] rfs = rfString.split(",");
			Map<String, Object> strategyMap = new HashMap<String, Object>();
			for (int i = 0; i < rfs.length; i++) {
				String[] rfparams = rfs[i].split(":");
				strategyMap.put(rfparams[0], (Object) rfparams[1]);
			}
			keyspace.createKeyspace(ImmutableMap.<String, Object> builder()
					.put("strategy_options", strategyMap)
					.put("strategy_class", strategy).build());
		} catch (Exception e) {
			logger.info("DB Exists, Skipping");
		}
		return "{\"message\":\"ok\"}";
	}

	public String createTable(JsonObject payload) {
		String sql = String
				.format(QueryFactory.BuildQuery(QueryType.CREATETABLE,
						StorageType.CASSANDRA), payload.getString("db") + "."
						+ payload.getString("name"), QueryUtils.formatColumns(
						payload.getString("columns"), StorageType.CASSANDRA),
						payload.getString("primarykey"));
		try {
			keyspace.prepareCqlStatement().withCql(sql + ";").execute();
		} catch (ConnectionException e) {
			logger.info("Table Exists, Skipping");
		}
		return "{\"message\":\"ok\"}";
	}

	public String read(String db, String table, String keycol, String key,
			String... keyvals) {
		try {
			if (keyvals != null && keyvals.length == 2) {
				String query = QueryFactory.BuildQuery(QueryType.SELECTEVENT,
						StorageType.CASSANDRA);
				return QueryUtils.formatQueryResult(
						keyspace.prepareCqlStatement()
								.withCql(
										String.format(query, db + "." + table,
												keycol, key, keyvals[0],
												keyvals[1])).execute()
								.getResult(), table);
			} else {
				String query = QueryFactory.BuildQuery(QueryType.SELECTALL,
						StorageType.CASSANDRA);
				OperationResult<CqlStatementResult> rs;
				if (keycol != null && !keycol.equals("")) {
					rs = keyspace
							.prepareCqlStatement()
							.withCql(
									String.format(query, db + "." + table,
											keycol, key)).execute();
				} else {
					rs = keyspace
							.prepareCqlStatement()
							.withCql(
									String.format("select * from %s", db + "."
											+ table)).execute();
				}
				if (rs != null)
					return QueryUtils.formatQueryResult(rs.getResult(), table);
				return "{\"msg\":\"Nothing is found\"}";
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public String createRowIndexTable(JsonObject payload) {
		return null;
	}

	public void closeConnection() {
		// TODO Auto-generated method stub
		// No API exists for this in current implementation todo:investigate 
	}
}
