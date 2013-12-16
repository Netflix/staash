package com.netflix.paas.rest.test;

import java.net.URL;
import java.util.Properties;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.policies.RoundRobinPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolType;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;
import com.netflix.paas.cassandra.discovery.EurekaAstyanaxHostSupplier;
import com.netflix.paas.connection.ConnectionFactory;
import com.netflix.paas.connection.PaasConnectionFactory;
import com.netflix.paas.rest.dao.AstyanaxDataDaoImpl;
import com.netflix.paas.rest.dao.AstyanaxMetaDaoImpl;
import com.netflix.paas.rest.dao.CqlDataDaoImpl;
import com.netflix.paas.rest.dao.CqlMetaDaoImpl;
import com.netflix.paas.rest.dao.CqlMetaDaoImplNew;
import com.netflix.paas.rest.dao.DataDao;
import com.netflix.paas.rest.dao.MetaDao;
import com.netflix.paas.rest.util.HostSupplier;
import com.netflix.paas.service.CacheService;
import com.netflix.paas.service.DataService;
import com.netflix.paas.service.MetaService;
import com.netflix.paas.service.PaasDataService;
import com.netflix.paas.service.PaasMetaService;

public class TestPaasPropertiesModule extends AbstractModule {
    @Override
    protected void configure() {
        try {
            Properties props = loadProperties();
            Names.bindProperties(binder(), props);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Properties loadProperties() throws Exception {
        Properties properties = new Properties();
        ClassLoader loader = TestPaasPropertiesModule.class.getClassLoader();
        URL url = loader.getResource("paas.properties");
        properties.load(url.openStream());
        return properties;
    }
    @Provides
    @Named("metacluster")
    Cluster provideCluster(@Named("paas.cassclient") String clientType,@Named("paas.metacluster") String clustername) {
        if (clientType.equals("cql")) {
        Cluster cluster = Cluster.builder().addContactPoint(clustername).build();
        return cluster;
        } else return null;
    }
    @Provides
    HostSupplier provideHostSupplier(@Named("paas.metacluster") String clustername) {
        
        return null;        
    }
    @Provides
    @Named("astmetaks")
    @Singleton
    Keyspace provideKeyspace(@Named("paas.metacluster") String clustername) {
        String clusterNameOnly = "";
        String clusterPortOnly = "";
        String[] clusterinfo = clustername.split(":");
        if (clusterinfo != null && clusterinfo.length == 2) {
            clusterNameOnly = clusterinfo[0];
            clusterPortOnly = clusterinfo[1];
        } else {
            clusterNameOnly = clustername;
            clusterPortOnly = "9160";
        }
//        hs = new EurekaAstyanaxHostSupplier();
        AstyanaxContext<Keyspace> keyspaceContext = new AstyanaxContext.Builder()
        .forCluster(clusterNameOnly)
        .forKeyspace("paasmetaks")
        .withAstyanaxConfiguration(
                new AstyanaxConfigurationImpl()
                        .setDiscoveryType(
                                NodeDiscoveryType.RING_DESCRIBE)
                        .setConnectionPoolType(
                                ConnectionPoolType.TOKEN_AWARE)
                        .setDiscoveryDelayInSeconds(60000)
                        .setTargetCassandraVersion("1.1")
                        .setCqlVersion("3.0.0"))
//                        .withHostSupplier(hs.getSupplier(clustername))
        .withConnectionPoolConfiguration(
                new ConnectionPoolConfigurationImpl(clusterNameOnly
                        + "_" + "paasmetaks")
                        .setSocketTimeout(3000)
                        .setMaxTimeoutWhenExhausted(2000)
                        .setMaxConnsPerHost(3).setInitConnsPerHost(1)
                        .setSeeds(clusterNameOnly+":"+clusterPortOnly))  //uncomment for localhost
        .withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
        .buildKeyspace(ThriftFamilyFactory.getInstance());
        keyspaceContext.start();
        Keyspace keyspace;
        keyspace = keyspaceContext.getClient();
        return keyspace;
    }
    @Provides
    @Named("datacluster")
    Cluster provideDataCluster(@Named("paas.datacluster") String clustername) {        
        Cluster cluster = Cluster.builder().addContactPoint(clustername).build();
        return cluster;
    }    
    @Provides
    @Singleton
    MetaDao provideCqlMetaDao(@Named("paas.cassclient") String clientType, @Named("metacluster") Cluster cluster,@Named("astmetaks") Keyspace keyspace) {
        if (clientType.equals("cql"))
        return new CqlMetaDaoImpl(cluster );
        else return new AstyanaxMetaDaoImpl(keyspace);
    }
    @Provides
    DataDao provideCqlDataDao(@Named("paas.cassclient") String clientType, @Named("datacluster") Cluster cluster, MetaDao meta) {
        if (clientType.equals("cql"))
        return new CqlDataDaoImpl(cluster, meta);
        else return new AstyanaxDataDaoImpl();
    }
    @Provides
    @Named("pooledmetacluster")
    Cluster providePooledCluster(@Named("paas.cassclient") String clientType,@Named("paas.metacluster") String clustername) {
        if (clientType.equals("cql")) {
        Cluster cluster = Cluster.builder().withLoadBalancingPolicy(new TokenAwarePolicy(new RoundRobinPolicy())).addContactPoint(clustername).build();
//        Cluster cluster = Cluster.builder().addContactPoint(clustername).build();
        return cluster;
        }else {
            return null;
        }
    }
    @Provides
    @Named("newmetadao")
    @Singleton
    MetaDao provideCqlMetaDaoNew(@Named("paas.cassclient") String clientType, @Named("metacluster") Cluster cluster, @Named("astmetaks") Keyspace keyspace) {
        if (clientType.equals("cql"))
        return new CqlMetaDaoImplNew(cluster );
        else  return new AstyanaxMetaDaoImpl(keyspace);
    }
    @Provides
    MetaService providePaasMetaService(@Named("newmetadao") MetaDao metad, ConnectionFactory fac, CacheService cache) {
        PaasMetaService metasvc = new PaasMetaService(metad, fac, cache);
        return metasvc;
    }
    @Provides
    DataService providePaasDataService( MetaService metasvc, ConnectionFactory fac) {
        PaasDataService datasvc = new PaasDataService(metasvc, fac);
        return datasvc;
    }
    @Provides
    CacheService provideCacheService(@Named("newmetadao") MetaDao metad) {
        return new CacheService(metad);
    }
    @Provides
    ConnectionFactory provideConnectionFactory(@Named("paas.cassclient") String clientType, EurekaAstyanaxHostSupplier hs) {
        return new PaasConnectionFactory(clientType, hs);
    }
}
