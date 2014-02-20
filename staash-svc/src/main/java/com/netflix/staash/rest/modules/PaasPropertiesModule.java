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
package com.netflix.staash.rest.modules;

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
import com.netflix.staash.cassandra.discovery.EurekaAstyanaxHostSupplier;
import com.netflix.staash.connection.ConnectionFactory;
import com.netflix.staash.connection.PaasConnectionFactory;
import com.netflix.staash.rest.dao.AstyanaxDataDaoImpl;
import com.netflix.staash.rest.dao.AstyanaxMetaDaoImpl;
import com.netflix.staash.rest.dao.CqlDataDaoImpl;
import com.netflix.staash.rest.dao.CqlMetaDaoImpl;
import com.netflix.staash.rest.dao.CqlMetaDaoImplNew;
import com.netflix.staash.rest.dao.DataDao;
import com.netflix.staash.rest.dao.MetaDao;
import com.netflix.staash.rest.util.HostSupplier;
import com.netflix.staash.rest.util.MetaConstants;
import com.netflix.staash.service.CacheService;
import com.netflix.staash.service.DataService;
import com.netflix.staash.service.MetaService;
import com.netflix.staash.service.PaasDataService;
import com.netflix.staash.service.PaasMetaService;

public class PaasPropertiesModule extends AbstractModule {
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
        ClassLoader loader = PaasPropertiesModule.class.getClassLoader();
        URL url = loader.getResource("staash.properties");
        properties.load(url.openStream());
        return properties;
    }
    @Provides
    @Named("metacluster")
    Cluster provideCluster(@Named("staash.cassclient") String clientType,@Named("staash.metacluster") String clustername) {
        if (clientType.equals("cql")) {
        Cluster cluster = Cluster.builder().addContactPoint(clustername).build();
        return cluster;
        } else return null;
    }
    @Provides
    HostSupplier provideHostSupplier(@Named("staash.metacluster") String clustername) {
        
        return null;        
    }
    @Provides
    @Named("astmetaks")
    Keyspace provideKeyspace(@Named("staash.metacluster") String clustername,EurekaAstyanaxHostSupplier hs) {
        String clusterNameOnly = "";
        String[] clusterinfo = clustername.split(":");
        if (clusterinfo != null && clusterinfo.length == 2) {
            clusterNameOnly = clusterinfo[0];
        } else {
            clusterNameOnly = clustername;
        }
        AstyanaxContext<Keyspace> keyspaceContext = new AstyanaxContext.Builder()
        .forCluster(clusterNameOnly)
        .forKeyspace(MetaConstants.META_KEY_SPACE)
        .withAstyanaxConfiguration(
                new AstyanaxConfigurationImpl()
                        .setDiscoveryType(
                                NodeDiscoveryType.RING_DESCRIBE)
                        .setConnectionPoolType(
                                ConnectionPoolType.TOKEN_AWARE)
                        .setDiscoveryDelayInSeconds(60)
                        .setTargetCassandraVersion("1.2")
                        .setCqlVersion("3.0.0"))
                        .withHostSupplier(hs.getSupplier(clustername))
        .withConnectionPoolConfiguration(
                new ConnectionPoolConfigurationImpl(clusterNameOnly
                        + "_" + MetaConstants.META_KEY_SPACE)
                        .setSocketTimeout(11000)
                        .setConnectTimeout(2000)
                        .setMaxConnsPerHost(10).setInitConnsPerHost(3))
        .withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
        .buildKeyspace(ThriftFamilyFactory.getInstance());
        keyspaceContext.start();
        Keyspace keyspace;
        keyspace = keyspaceContext.getClient();
        return keyspace;
    }
    @Provides
    @Named("datacluster")
    Cluster provideDataCluster(@Named("staash.datacluster") String clustername) {        
        Cluster cluster = Cluster.builder().addContactPoint(clustername).build();
        return cluster;
    }    
    @Provides
    MetaDao provideCqlMetaDao(@Named("staash.cassclient") String clientType, @Named("metacluster") Cluster cluster,@Named("astmetaks") Keyspace keyspace) {
        if (clientType.equals("cql"))
        return new CqlMetaDaoImpl(cluster );
        else return new AstyanaxMetaDaoImpl(keyspace);
    }
    @Provides
    DataDao provideCqlDataDao(@Named("staash.cassclient") String clientType, @Named("datacluster") Cluster cluster, MetaDao meta) {
        if (clientType.equals("cql"))
        return new CqlDataDaoImpl(cluster, meta);
        else return new AstyanaxDataDaoImpl();
    }
    @Provides
    @Named("pooledmetacluster")
    Cluster providePooledCluster(@Named("staash.cassclient") String clientType,@Named("staash.metacluster") String clustername) {
        if (clientType.equals("cql")) {
        Cluster cluster = Cluster.builder().withLoadBalancingPolicy(new TokenAwarePolicy(new RoundRobinPolicy())).addContactPoint(clustername).build();
        return cluster;
        }else {
            return null;
        }
    }
    @Provides
    @Named("newmetadao")
    MetaDao provideCqlMetaDaoNew(@Named("staash.cassclient") String clientType, @Named("metacluster") Cluster cluster, @Named("astmetaks") Keyspace keyspace) {
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
    ConnectionFactory provideConnectionFactory(@Named("staash.cassclient") String clientType,EurekaAstyanaxHostSupplier hs) {
        return new PaasConnectionFactory(clientType, hs);
    }
}
