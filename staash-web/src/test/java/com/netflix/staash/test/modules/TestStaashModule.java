package com.netflix.staash.test.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolType;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;
import com.netflix.staash.cassandra.discovery.EurekaAstyanaxHostSupplier;
import com.netflix.staash.connection.ConnectionFactory;
import com.netflix.staash.connection.PaasConnectionFactory;
import com.netflix.staash.rest.dao.AstyanaxMetaDaoImpl;
import com.netflix.staash.rest.dao.MetaDao;
import com.netflix.staash.rest.util.MetaConstants;
import com.netflix.staash.service.CacheService;
import com.netflix.staash.service.DataService;
import com.netflix.staash.service.MetaService;
import com.netflix.staash.service.PaasDataService;
import com.netflix.staash.service.PaasMetaService;

public class TestStaashModule extends AbstractModule {
    @Provides
    @Named("astmetaks")
    @Singleton
    Keyspace provideKeyspace() {
         AstyanaxContext<Keyspace> keyspaceContext = new AstyanaxContext.Builder()
        .forCluster("test cluster")
        .forKeyspace(MetaConstants.META_KEY_SPACE)
        .withAstyanaxConfiguration(
                new AstyanaxConfigurationImpl()
                        .setDiscoveryType(
                                NodeDiscoveryType.NONE)
                        .setConnectionPoolType(
                                ConnectionPoolType.ROUND_ROBIN)
                        .setTargetCassandraVersion("1.2")
                        .setCqlVersion("3.0.0"))
//                        .withHostSupplier(hs.getSupplier(clustername))
        .withConnectionPoolConfiguration(
                new ConnectionPoolConfigurationImpl("localpool"
                        + "_" + MetaConstants.META_KEY_SPACE)
                        .setSocketTimeout(30000)
                        .setMaxTimeoutWhenExhausted(20000)
                        .setMaxConnsPerHost(3).setInitConnsPerHost(1)
                        .setSeeds("localhost"+":"+"9160"))  //uncomment for localhost
//        .withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
        .buildKeyspace(ThriftFamilyFactory.getInstance());
        keyspaceContext.start();
        Keyspace keyspace;
        keyspace = keyspaceContext.getClient();
        return keyspace;
    }
    @Provides
    @Named("newmetadao")
    @Singleton
    MetaDao provideCqlMetaDaoNew(@Named("astmetaks") Keyspace keyspace) {
        return new AstyanaxMetaDaoImpl(keyspace);
    }
    @Provides
    MetaService providePaasMetaService(@Named("newmetadao") MetaDao metad, CacheService cache) {
    	ConnectionFactory fac = new PaasConnectionFactory("astyanax", null);
        PaasMetaService metasvc = new PaasMetaService(metad, fac, cache);
        return metasvc;
    }
    @Provides
    ConnectionFactory provideConnectionFactory() {
        return new PaasConnectionFactory("astyanax", null);
    }
    @Provides
    DataService providePaasDataService( MetaService metasvc) {
    	ConnectionFactory fac = new PaasConnectionFactory("astyanax", null);
    	PaasDataService datasvc = new PaasDataService(metasvc, fac);
        return datasvc;
    }
    @Provides
    CacheService provideCacheService(@Named("newmetadao") MetaDao metad) {
        return new CacheService(metad);
    }
    
    @Override
	protected void configure() {
		
	}
}
