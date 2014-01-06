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
import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.cql.CqlStatementResult;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.staash.json.JsonObject;
import com.netflix.staash.rest.meta.entity.Entity;
import com.netflix.staash.rest.meta.entity.EntityType;
import com.netflix.staash.rest.meta.entity.PaasDBEntity;
import com.netflix.staash.rest.meta.entity.PaasStorageEntity;
import com.netflix.staash.rest.meta.entity.PaasTableEntity;
import com.netflix.staash.rest.modules.PaasPropertiesModule;
import com.netflix.staash.rest.util.MetaConstants;
import com.netflix.staash.rest.util.PaasUtils;

public class AstyanaxMetaDaoImpl implements MetaDao{
    private Keyspace keyspace;
    static ColumnFamily<String, String> TEST_CF = ColumnFamily
            .newColumnFamily("metacf", StringSerializer.get(),
                    StringSerializer.get());

    @Inject
    public AstyanaxMetaDaoImpl(@Named("astmetaks") Keyspace keyspace) {
        this.keyspace = keyspace;
        maybecreateschema();
    }
    private void maybecreateschema() {
        try {
            keyspace.createKeyspace(ImmutableMap
                    .<String, Object> builder()
                    .put("strategy_options",
                            ImmutableMap.<String, Object> builder()
                                    .put("us-east", "3").build())
                    .put("strategy_class", "NetworkTopologyStrategy").build());
        } catch (ConnectionException e) {
            //If we are here that means the meta artifacts already exist
        }


        try {
//            OperationResult<CqlStatementResult> result = null;

            String metaDynamic = "CREATE TABLE metacf (\n" + "    key text,\n"
                    + "    column1 text,\n" + "    value text,\n"
                    + "    PRIMARY KEY (key, column1)\n"
                    + ") WITH COMPACT STORAGE;";
            keyspace
            .prepareCqlStatement()
            .withCql(
                     metaDynamic)
            .execute();
        } catch (ConnectionException e) {
            // TODO Auto-generated catch block
            //if we are here means meta artifacts already exists, ignore
        }
    }
       /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Injector inj = Guice.createInjector(new PaasPropertiesModule());
        MetaDao metasvc = inj.getInstance(AstyanaxMetaDaoImpl.class);
        String storage = "{\"name\": \"unit.mysql\",\"type\": \"mysql\",\"jdbcurl\": \"jdbc:mysql://localhost:3306/\",\"host\":\"localhost\",\"user\":\"root\",\"password\":\"\",\"replicate\":\"another\"}";
        PaasStorageEntity pse = PaasStorageEntity.builder()
                .withJsonPayLoad(new JsonObject(storage)).build();
        metasvc.writeMetaEntity(pse);
        
        String dbpay = "{\"name\":\"astyanaxdb\"}";
        PaasDBEntity pdbe = PaasDBEntity.builder()
                .withJsonPayLoad(new JsonObject(dbpay)).build();
        metasvc.writeMetaEntity(pdbe);
        
        String tblpay = "{\"name\":\"unittbl2\",\"columns\":\"col1,col2,col3\",\"primarykey\":\"col1\",\"storage\":\"unit.mysql\"}";
        String db = "astyanaxdb";
        JsonObject pload = new JsonObject(tblpay);
        String schema = new JsonObject(tblpay).getString("db");

        PaasTableEntity pte = PaasTableEntity.builder()
                .withJsonPayLoad(new JsonObject(tblpay), schema)
                .build();
        pload.putString("db", db);
        metasvc.writeMetaEntity(pte);
        }
    public String writeMetaEntity(Entity entity) {
        // TODO Auto-generated method stub
        try {
            keyspace
            .prepareCqlStatement()
            .withCql(String.format(PaasUtils.INSERT_FORMAT, "paasmetaks" + "."
                    + MetaConstants.META_COLUMN_FAMILY, entity.getRowKey(), entity.getName(),
                    entity.getPayLoad())).execute();
        } catch (ConnectionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //addEntityToCache(entity.getRowKey(), entity);
        return "{\"msg\":\"ok\"}";
    }
    public Map<String, String> getStorageMap() {
        // TODO Auto-generated method stub
        return null;
    }

    public Map<String, JsonObject> runQuery(String key, String col) {
        // TODO Auto-generated method stub
        OperationResult<CqlStatementResult> rs;
        Map<String,JsonObject> resultMap = new HashMap<String,JsonObject>();
        try {
            String queryStr="";
            if (col!=null && !col.equals("*")) {
                queryStr = "select column1, value from paasmetaks.metacf where key='"+key+"' and column1='"+col+"';";
            } else {
                queryStr = "select column1, value from paasmetaks.metacf where key='"+key+"';";
            }
            rs = keyspace.prepareCqlStatement().withCql(queryStr)
                    .execute();
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
