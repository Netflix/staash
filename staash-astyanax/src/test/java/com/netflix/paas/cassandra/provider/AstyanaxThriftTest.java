package com.netflix.paas.cassandra.provider;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.util.SingletonEmbeddedCassandra;
import com.netflix.governator.guice.LifecycleInjector;
import com.netflix.governator.lifecycle.LifecycleManager;
import com.netflix.paas.PaasBootstrap;
import com.netflix.paas.PaasModule;
import com.netflix.paas.cassandra.CassandraPaasModule;
import com.netflix.paas.cassandra.PaasCassandraBootstrap;
import com.netflix.paas.cassandra.admin.CassandraClusterAdminResource;
import com.netflix.paas.cassandra.admin.CassandraClusterAdminResourceFactory;
import com.netflix.paas.cassandra.discovery.ClusterDiscoveryService;
import com.netflix.paas.cassandra.discovery.LocalClusterDiscoveryService;
import com.netflix.paas.cassandra.entity.ColumnFamilyEntity;
import com.netflix.paas.cassandra.entity.KeyspaceEntity;
import com.netflix.paas.cassandra.keys.ClusterKey;
import com.netflix.paas.cassandra.keys.KeyspaceKey;
import com.netflix.paas.cassandra.provider.KeyspaceClientProvider;
import com.netflix.paas.cassandra.resources.AstyanaxThriftDataTableResource;
import com.netflix.paas.data.QueryResult;
import com.netflix.paas.data.RowData;
import com.netflix.paas.data.SchemalessRows;
import com.netflix.paas.resources.TableDataResource;

public class AstyanaxThriftTest {
    private static final Logger LOG = LoggerFactory.getLogger(AstyanaxThriftTest.class)
            ;
    private static Injector injector;
    
    private static final String CLUSTER_NAME   = "local";
    private static final String KEYSPACE_NAME  = "Keyspace1";
    private static final String CF_NAME        = "ColumnFamily1";
    private static final String LOCAL_DISCOVERY_TYPE = "local";
    
            
//    @BeforeClass
//    @Ignore
//    public static void initialize() throws Exception {
//        
//        System.setProperty("com.netflix.paas.title",                          "HelloPaas");
//        System.setProperty("com.netflix.paas.cassandra.dcs",                  "us-east");
//        System.setProperty("com.netflix.paas.schema.configuration.type",      "astyanax");
//        System.setProperty("com.netflix.paas.schema.configuration.discovery", "local");
//        System.setProperty("com.netflix.paas.schema.configuration.cluster",   "cass_sandbox");
//        System.setProperty("com.netflix.paas.schema.configuration.keyspace",  "paas");
//        System.setProperty("com.netflix.paas.schema.configuration.strategy_options.replication_factor", "1");
//        System.setProperty("com.netflix.paas.schema.configuration.strategy_class", "SimpleStrategy");
//        System.setProperty("com.netflix.paas.schema.audit",                   "configuration");
//
//        SingletonEmbeddedCassandra.getInstance();
//                
//        // Create the injector
//        injector = LifecycleInjector.builder()
//            .withModules(
//                    new PaasModule(),
//                    new CassandraPaasModule(),
//                    new AbstractModule() {
//                        @Override
//                        protected void configure() {
//                            bind(String.class).annotatedWith(Names.named("groupName")).toInstance("UnitTest1");
//                            bind(ClusterDiscoveryService.class).to(LocalClusterDiscoveryService.class).in(Scopes.SINGLETON);
//                            
//                            bind(PaasBootstrap.class).asEagerSingleton();
//                            bind(PaasCassandraBootstrap.class).asEagerSingleton();
//                        }
//                    })
//            .createInjector();
//
//        LifecycleManager    manager = injector.getInstance(LifecycleManager.class);
//        manager.start();    
//
//        CassandraClusterAdminResourceFactory factory = injector.getInstance(CassandraClusterAdminResourceFactory.class);
//        
//        // Create Keyspace
//        CassandraClusterAdminResource admin = factory.get(new ClusterKey(CLUSTER_NAME, "local"));
//        admin.createKeyspace(KeyspaceEntity.builder()
//                .withName(KEYSPACE_NAME)
//                .withOptions(ImmutableMap.<String, String>builder()
//                            .put("strategy_class", "SimpleStrategy")
//                            .put("strategy_options.replication_factor", "1")
//                            .build())
//                .build());
//        
//        // Create column family
//        admin.createColumnFamily(KEYSPACE_NAME, ColumnFamilyEntity.builder()
//                .withName(CF_NAME)
//                .withOptions(ImmutableMap.<String, String>builder()
//                            .put("comparator_type",      "LongType")
//                            .put("key_validation_class", "LongType")
//                            .build())
//                .build());
//        
//        // Create DB from cluster
//        
//    }
    
    @AfterClass
    public static void shutdown() {
        
    }
    
    @Test
    @Ignore
    public void testReadData() throws Exception {
        
        KeyspaceClientProvider clientProvider = injector.getInstance(KeyspaceClientProvider.class);
        Keyspace keyspace = clientProvider.acquireKeyspace(new KeyspaceKey(new ClusterKey(LOCAL_DISCOVERY_TYPE, CLUSTER_NAME), KEYSPACE_NAME));
//        
//        // Create the keyspace and column family
//        keyspace.createKeyspace(new Properties());
//        Properties props = new Properties();
//        props.setProperty("name",                   CF_NAME);
//        props.setProperty("comparator_type",        "LongType");
//        props.setProperty("key_validation_class",   "LongType");
//        keyspace.createColumnFamily(props);
//        
//        // Add some data
        TableDataResource thriftDataTableResource = new AstyanaxThriftDataTableResource(keyspace, CF_NAME);

        String rowKey = "100";
        
        SchemalessRows.Builder builder = SchemalessRows.builder();
        builder.addRow(rowKey, ImmutableMap.<String, String>builder().put("1", "11").put("2", "22").build());
        RowData dr = new RowData();
        dr.setSrows(builder.build());
        
//        thriftDataTableResource.updateRow(rowKey, dr);
        
        QueryResult result;
        
        result = thriftDataTableResource.readRow(rowKey,  1, null, null, false);
//        Assert.assertEquals(1,  Iterables.getFirst(result.getSrows().getRows(), null).getColumns().size());
        LOG.info(result.toString());
        
        result = thriftDataTableResource.readRow(rowKey, 10, null, null, false);
//        Assert.assertEquals(2,  Iterables.getFirst(result.getRows(), null).getColumns().size());
        
        LOG.info(result.toString());
    }
    
    @Test
    @Ignore
    public void testAdmin() {
        
    }
}
