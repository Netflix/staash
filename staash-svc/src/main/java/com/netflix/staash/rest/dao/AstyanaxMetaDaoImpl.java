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
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import com.netflix.staash.json.JsonObject;
import com.netflix.staash.rest.meta.entity.Entity;
import com.netflix.staash.rest.util.MetaConstants;
import com.netflix.staash.rest.util.PaasUtils;
import com.netflix.staash.rest.util.StaashRequestContext;

public class AstyanaxMetaDaoImpl implements MetaDao {
	private Keyspace keyspace;
	private Logger logger = Logger.getLogger(AstyanaxMetaDaoImpl.class);
	private static final DynamicStringProperty METASTRATEGY = DynamicPropertyFactory
			.getInstance().getStringProperty("staash.metastrategy",
					"NetworkTopologyStrategy");
	private static final DynamicStringProperty METARF = DynamicPropertyFactory
			.getInstance().getStringProperty("staash.metareplicationfactor",
					"us-east:3");

	static ColumnFamily<String, String> METACF = ColumnFamily.newColumnFamily(
			MetaConstants.META_COLUMN_FAMILY, StringSerializer.get(), StringSerializer.get());

	@Inject
	public AstyanaxMetaDaoImpl(@Named("astmetaks") Keyspace keyspace) {
		this.keyspace = keyspace;
		try {
			keyspace.describeKeyspace();
			logger.info("keyspaces for staash exists already");
			StaashRequestContext.addContext("Meta_Init",
					"keyspace already existed");
		} catch (ConnectionException ex) {
			StaashRequestContext.addContext("Meta_Init",
					"Keyspace did not exist , creating keyspace "+MetaConstants.META_KEY_SPACE
							);
			maybecreateschema();
		}
	}

	private Map<String, Object> populateMap() {
		Map<String, Object> rfMap = ImmutableMap.<String, Object> builder()
				.build();
		String rfStr = METARF.getValue();
		String[] pairs = rfStr.split(",");
		for (String pair : pairs) {
			String[] kv = pair.split(":");
			rfMap.put(kv[0], kv[1]);
		}
		return rfMap;
	}

	private void maybecreateschema() {
		try {
			boolean b = true;
			logger.info("Strategy: " + METASTRATEGY.getValue() + " RF: "
					+ METARF.getValue());
			try {
				b = keyspace.getConnectionPool().hasHost(
						new Host("localhost:9160", 9160));
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
				// keyspace.createKeyspace(ImmutableMap
				// .<String, Object> builder()
				// .put("strategy_options",
				// ImmutableMap.<String, Object> builder()
				// .put("us-east", "3").build())
				// .put("strategy_class", METASTRATEGY).build());
				keyspace.createKeyspace(ImmutableMap.<String, Object> builder()
						.put("strategy_options", populateMap())
						.put("strategy_class", METASTRATEGY).build());
			}
			StaashRequestContext.addContext("Meta_Init",
					"Keyspace did not exist , created keyspace "+MetaConstants.META_KEY_SPACE +" with rf:"
							+ METARF.getValue());
		} catch (ConnectionException e) {
			// If we are here that means the meta artifacts already exist
			logger.info("keyspaces for staash exists already");
			StaashRequestContext.addContext("Meta_Init",
					"keyspace already existed");
		}

		try {
			String metaDynamic = "CREATE TABLE " + MetaConstants.META_COLUMN_FAMILY +"(\n" + "    key text,\n"
					+ "    column1 text,\n" + "    value text,\n"
					+ "    PRIMARY KEY (key, column1)\n"
					+ ") WITH COMPACT STORAGE;";
			keyspace.prepareCqlStatement().withCql(metaDynamic).execute();
			StaashRequestContext
					.addContext("Meta_Init",
							"Columnfamily did not exist , created column family "+MetaConstants.META_COLUMN_FAMILY + " in keyspace "+MetaConstants.META_KEY_SPACE);
		} catch (ConnectionException e) {
			// if we are here means meta artifacts already exists, ignore
			logger.info("staash column family exists");
			StaashRequestContext.addContext("Meta_Init",
					"Columnfamily already existed");
		}
	}

	public String writeMetaEntity(Entity entity) {
		try {
			String stmt = String.format(PaasUtils.INSERT_FORMAT, MetaConstants.META_KEY_SPACE
					+ "." + MetaConstants.META_COLUMN_FAMILY,
					entity.getRowKey(), entity.getName(), entity.getPayLoad());
			keyspace.prepareCqlStatement().withCql(stmt).execute();
			StaashRequestContext.addContext("Meta_Write", "write succeeded on meta: "+ entity!=null?entity.getPayLoad():null);
		} catch (ConnectionException e) {
			logger.info("Write of the entity failed "+entity!=null?entity.getPayLoad():null);
			StaashRequestContext.addContext("Meta_Write", "write failed on meta: "+ entity!=null?entity.getPayLoad():null);
			throw new RuntimeException(e.getMessage());
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
				queryStr = "select column1, value from "+MetaConstants.META_KEY_SPACE + "." + MetaConstants.META_COLUMN_FAMILY +" where key='"
						+ key + "' and column1='" + col + "';";
			} else {
				queryStr = "select column1, value from "+MetaConstants.META_KEY_SPACE + "." + MetaConstants.META_COLUMN_FAMILY +" where key='"
						+ key + "';";
			}
			rs = keyspace.prepareCqlStatement().withCql(queryStr).execute();
			for (Row<String, String> row : rs.getResult().getRows(METACF)) {

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
