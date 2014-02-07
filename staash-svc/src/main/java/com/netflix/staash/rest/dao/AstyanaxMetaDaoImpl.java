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

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.Host;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.cql.CqlStatementResult;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.staash.json.JsonObject;
import com.netflix.staash.rest.meta.entity.Entity;
import com.netflix.staash.rest.util.MetaConstants;
import com.netflix.staash.rest.util.PaasUtils;

public class AstyanaxMetaDaoImpl implements MetaDao {
	private Keyspace keyspace;
	private Logger logger = Logger.getLogger(AstyanaxMetaDaoImpl.class);
	static ColumnFamily<String, String> TEST_CF = ColumnFamily.newColumnFamily(
			"metacf", StringSerializer.get(), StringSerializer.get());

	@Inject
	public AstyanaxMetaDaoImpl(@Named("astmetaks") Keyspace keyspace) {
		this.keyspace = keyspace;
		maybecreateschema();
	}

	private void maybecreateschema() {
		try {
			boolean b = true;
			try {
			b  =  keyspace.getConnectionPool().hasHost(new Host("localhost:9160", 9160));
			} catch (ConnectionException ex) {
				
			}
			if (b) {
				keyspace.createKeyspace(ImmutableMap
						.<String, Object> builder()
						.put("strategy_options",
								ImmutableMap.<String, Object> builder()
										.put("replication_factor", "1").build())
						.put("strategy_class", "SimpleStrategy").build());
				
			} else {
			keyspace.createKeyspace(ImmutableMap
					.<String, Object> builder()
					.put("strategy_options",
							ImmutableMap.<String, Object> builder()
									.put("us-east", "3").build())
					.put("strategy_class", "NetworkTopologyStrategy").build());
			}
		} catch (ConnectionException e) {
			// If we are here that means the meta artifacts already exist
			logger.info("keyspaces for staash exists already");
		}

		try {
			String metaDynamic = "CREATE TABLE metacf (\n" + "    key text,\n"
					+ "    column1 text,\n" + "    value text,\n"
					+ "    PRIMARY KEY (key, column1)\n"
					+ ") WITH COMPACT STORAGE;";
			keyspace.prepareCqlStatement().withCql(metaDynamic).execute();
		} catch (ConnectionException e) {
			// if we are here means meta artifacts already exists, ignore
			logger.info("staash column family exists");
		}
	}

	public String writeMetaEntity(Entity entity) {
		try {
			String stmt = String.format(PaasUtils.INSERT_FORMAT, "paasmetaks"
					+ "." + MetaConstants.META_COLUMN_FAMILY,
					entity.getRowKey(), entity.getName(),
					entity.getPayLoad());
			keyspace.prepareCqlStatement()
					.withCql(
							stmt
							).execute();
		} catch (ConnectionException e) {
			e.printStackTrace();
		}
		return "{\"msg\":\"ok\"}";
	}

	public Map<String, String> getStorageMap() {
		return null;
	}

	public Map<String, JsonObject> runQuery(String key, String col) {
		OperationResult<CqlStatementResult> rs;
		Map<String, JsonObject> resultMap = new HashMap<String, JsonObject>();
		try {
			String queryStr = "";
			if (col != null && !col.equals("*")) {
				queryStr = "select column1, value from paasmetaks.metacf where key='"
						+ key + "' and column1='" + col + "';";
			} else {
				queryStr = "select column1, value from paasmetaks.metacf where key='"
						+ key + "';";
			}
			rs = keyspace.prepareCqlStatement().withCql(queryStr).execute();
			for (Row<String, String> row : rs.getResult().getRows(TEST_CF)) {

				ColumnList<String> columns = row.getColumns();

				String key1 = columns.getStringValue("column1", null);
				String val1 = columns.getStringValue("value", null);
				resultMap.put(key1, new JsonObject(val1));
			}
		} catch (ConnectionException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}

		return resultMap;
	}
}
