package com.netflix.paas.rest.dao;

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
import com.netflix.paas.json.JsonObject;
import com.netflix.paas.rest.meta.entity.Entity;
import com.netflix.paas.rest.meta.entity.EntityType;
import com.netflix.paas.rest.meta.entity.PaasDBEntity;
import com.netflix.paas.rest.meta.entity.PaasStorageEntity;
import com.netflix.paas.rest.meta.entity.PaasTableEntity;
import com.netflix.paas.rest.modules.PaasPropertiesModule;
import com.netflix.paas.rest.util.MetaConstants;
import com.netflix.paas.rest.util.PaasUtils;

public class AstyanaxMetaDaoImpl implements MetaDao{
//    private AstyanaxContext<Keyspace> keyspaceContext;
    private Keyspace keyspace;
//    private final String SEEDS = "localhost:9160";
//    private final String TEST_CLUSTER_NAME = "test cluster";
//    private final String TEST_KEYSPACE_NAME = "testpaasmetaks";
    private static final long CASSANDRA_WAIT_TIME = 1000;
//    List<String> dbHolder = new ArrayList<String>();
//    Map<String, String> tableToStorageMap = new ConcurrentHashMap<String, String>();
//    Map<String,JsonObject> storageMap = new ConcurrentHashMap<String,JsonObject>();
//    Map<String, List<String>> dbToTableMap = new ConcurrentHashMap<String, List<String>>();
//    Map<String, List<String>> dbToTimeseriesMap = new ConcurrentHashMap<String, List<String>>();
    static ColumnFamily<String, String> TEST_CF = ColumnFamily
            .newColumnFamily("metacf", StringSerializer.get(),
                    StringSerializer.get());

    @Inject
    public AstyanaxMetaDaoImpl(@Named("astmetaks") Keyspace keyspace) {
//        keyspaceContext = new AstyanaxContext.Builder()
//        .forCluster(TEST_CLUSTER_NAME)
//        .forKeyspace(TEST_KEYSPACE_NAME)
//        .withAstyanaxConfiguration(
//                new AstyanaxConfigurationImpl()
//                        .setDiscoveryType(
//                                NodeDiscoveryType.RING_DESCRIBE)
//                        .setConnectionPoolType(
//                                ConnectionPoolType.TOKEN_AWARE)
//                        .setDiscoveryDelayInSeconds(60000)
//                        .setTargetCassandraVersion("1.2")
//                        .setCqlVersion("3.0.0"))
//        .withConnectionPoolConfiguration(
//                new ConnectionPoolConfigurationImpl(TEST_CLUSTER_NAME
//                        + "_" + TEST_KEYSPACE_NAME)
//                        .setSocketTimeout(30000)
//                        .setMaxTimeoutWhenExhausted(2000)
//                        .setMaxConnsPerHost(10).setInitConnsPerHost(10)
//                        .setSeeds(SEEDS))
//        .withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
//        .buildKeyspace(ThriftFamilyFactory.getInstance());
//        keyspaceContext.start();
//        keyspace = keyspaceContext.getClient();
        this.keyspace = keyspace;
        maybecreateschema();
    }
    private void maybecreateschema() {
        try {
            //keyspace.dropKeyspace();
            //Thread.sleep(CASSANDRA_WAIT_TIME);
        } catch (Exception e) {
            //keyspace already exists
        }

        try {
            keyspace.createKeyspace(ImmutableMap
                    .<String, Object> builder()
                    .put("strategy_options",
                            ImmutableMap.<String, Object> builder()
                                    .put("us-east", "3").build())
                    .put("strategy_class", "NetworkTopologyStrategy").build());
        } catch (ConnectionException e) {
            // TODO Auto-generated catch block
            //If we are here that means the meta artifacts already exist
        }

//        try {
////            Thread.sleep(CASSANDRA_WAIT_TIME);
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            //e.printStackTrace();
//        }
        OperationResult<CqlStatementResult> result;

        try {
            String metaDynamic = "CREATE TABLE metacf (\n" + "    key text,\n"
                    + "    column1 text,\n" + "    value text,\n"
                    + "    PRIMARY KEY (key, column1)\n"
                    + ") WITH COMPACT STORAGE;";
            result = keyspace
                    .prepareCqlStatement()
                    .withCql(
                            metaDynamic)
                    .execute();
        } catch (ConnectionException e) {
            // TODO Auto-generated catch block
            //if we are here means meta artifacts already exists
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
        Map<String,JsonObject> map1 = metasvc.runQuery(EntityType.DB.getId(), "astyanaxdb");
        int i = 0;
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
//    private void addEntityToCache(String rowkey, Entity entity) {
//        switch (EntityType.valueOf(rowkey)) {
//        case STORAGE:
//            storageMap.put(entity.getName(), new JsonObject(entity.getPayLoad()));
//            break;
//        case DB:
//            dbHolder.add(entity.getName());
//            break;
//        case TABLE:
//            JsonObject payobject = new JsonObject(entity.getPayLoad());
//            tableToStorageMap.put(entity.getName(), payobject.getString("storage"));
//            String db = payobject.getString("db");
//            List<String> tables = dbToTableMap.get(db);
//            if (tables == null || tables.size() == 0) {
//                tables = new ArrayList<String>();
//                tables.add(entity.getName());
//            } else {
//                tables.add(entity.getName());
//            }
//            dbToTableMap.put(db, tables);
//            break;
//            
//        case SERIES:
//            JsonObject tsobject = new JsonObject(entity.getPayLoad());
//            tableToStorageMap.put(entity.getName(), tsobject.getString("storage"));
//            String dbname = tsobject.getString("db");
//            List<String> alltables = dbToTableMap.get(dbname);
//            if (alltables == null || alltables.size() == 0) {
//                alltables = new ArrayList<String>();
//                alltables.add(entity.getName());
//            } else {
//                alltables.add(entity.getName());
//            }
//            dbToTimeseriesMap.put(dbname, alltables);
//            break;
//        }
//    }

//    public Entity readMetaEntity(String rowKey) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    public String writeRow(String db, String table, JsonObject rowObj) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    public String listRow(String db, String table, String keycol, String key) {
//        // TODO Auto-generated method stub
//        return null;
//    }

//    public String listSchemas() {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    public String listTablesInSchema(String schemaname) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    public String listTimeseriesInSchema(String schemaname) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    public String listStorage() {
//        // TODO Auto-generated method stub
//        return null;
//    }

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
//            Assert.assertTrue(!rs.getResult().getRows(TEST_CF).isEmpty());
            for (Row<String, String> row : rs.getResult().getRows(TEST_CF)) {

                ColumnList<String> columns = row.getColumns();

                String key1 = columns.getStringValue("column1", null);
                String val1 = columns.getStringValue("value", null);
                resultMap.put(key1, new JsonObject(val1));
            }
        } catch (ConnectionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
        return resultMap;
    }
}
