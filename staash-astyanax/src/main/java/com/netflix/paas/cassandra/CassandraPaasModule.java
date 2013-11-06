package com.netflix.paas.cassandra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.MapBinder;
import com.netflix.paas.cassandra.admin.CassandraClusterAdminResource;
import com.netflix.paas.cassandra.admin.CassandraClusterAdminResourceFactory;
import com.netflix.paas.cassandra.admin.CassandraSystemAdminResource;
import com.netflix.paas.cassandra.discovery.ClusterDiscoveryService;
import com.netflix.paas.cassandra.discovery.LocalClusterDiscoveryService;
import com.netflix.paas.cassandra.provider.AstyanaxConfigurationProvider;
import com.netflix.paas.cassandra.provider.AstyanaxConnectionPoolConfigurationProvider;
import com.netflix.paas.cassandra.provider.AstyanaxConnectionPoolMonitorProvider;
import com.netflix.paas.cassandra.provider.CassandraTableResourceFactory;
import com.netflix.paas.cassandra.provider.ClusterClientProvider;
import com.netflix.paas.cassandra.provider.HostSupplierProvider;
import com.netflix.paas.cassandra.provider.KeyspaceClientProvider;
import com.netflix.paas.cassandra.provider.impl.DefaultAstyanaxConfigurationProvider;
import com.netflix.paas.cassandra.provider.impl.DefaultAstyanaxConnectionPoolConfigurationProvider;
import com.netflix.paas.cassandra.provider.impl.DefaultAstyanaxConnectionPoolMonitorProvider;
import com.netflix.paas.cassandra.provider.impl.DefaultAstyanaxClusterClientProvider;
import com.netflix.paas.cassandra.provider.impl.DefaultKeyspaceClientProvider;
import com.netflix.paas.cassandra.provider.impl.LocalHostSupplierProvider;
import com.netflix.paas.cassandra.resources.admin.AstyanaxThriftClusterAdminResource;
import com.netflix.paas.cassandra.tasks.ClusterDiscoveryTask;
import com.netflix.paas.cassandra.tasks.ClusterRefreshTask;
import com.netflix.paas.dao.DaoSchemaProvider;
import com.netflix.paas.dao.astyanax.AstyanaxDaoSchemaProvider;
import com.netflix.paas.provider.TableDataResourceFactory;
import com.netflix.paas.resources.impl.JerseySchemaDataResourceImpl;

public class CassandraPaasModule extends AbstractModule {
    private static final Logger LOG = LoggerFactory.getLogger(CassandraPaasModule.class);
    
    @Override
    protected void configure() {
        LOG.info("Loading CassandraPaasModule");
        
        // There will be a different TableResourceProvider for each persistence technology
        MapBinder<String, TableDataResourceFactory> tableResourceProviders = MapBinder.newMapBinder(binder(), String.class, TableDataResourceFactory.class);
        tableResourceProviders.addBinding("cassandra").to(CassandraTableResourceFactory.class).in(Scopes.SINGLETON);
        
        // Binding to enable DAOs using astyanax
        MapBinder<String, DaoSchemaProvider> daoManagers = MapBinder.newMapBinder(binder(), String.class, DaoSchemaProvider.class);
        daoManagers.addBinding("astyanax").to(AstyanaxDaoSchemaProvider.class).in(Scopes.SINGLETON);
        
        bind(AstyanaxConfigurationProvider.class)              .to(DefaultAstyanaxConfigurationProvider.class).in(Scopes.SINGLETON);
        bind(AstyanaxConnectionPoolConfigurationProvider.class).to(DefaultAstyanaxConnectionPoolConfigurationProvider.class).in(Scopes.SINGLETON);
        bind(AstyanaxConnectionPoolMonitorProvider.class)      .to(DefaultAstyanaxConnectionPoolMonitorProvider.class).in(Scopes.SINGLETON);
        bind(KeyspaceClientProvider.class)                     .to(DefaultKeyspaceClientProvider.class).in(Scopes.SINGLETON);
        bind(ClusterClientProvider.class)                      .to(DefaultAstyanaxClusterClientProvider.class).in(Scopes.SINGLETON);
        
        install(new FactoryModuleBuilder()
            .implement(CassandraClusterAdminResource.class, AstyanaxThriftClusterAdminResource.class)
            .build(CassandraClusterAdminResourceFactory.class));
        
        // REST resources
        bind(ClusterDiscoveryService.class).to(LocalClusterDiscoveryService.class);
        bind(CassandraSystemAdminResource.class).in(Scopes.SINGLETON);
        bind(JerseySchemaDataResourceImpl.class).in(Scopes.SINGLETON);
        
        MapBinder<String, HostSupplierProvider> hostSuppliers = MapBinder.newMapBinder(binder(), String.class, HostSupplierProvider.class);
        hostSuppliers.addBinding("local").to(LocalHostSupplierProvider.class).in(Scopes.SINGLETON);
        
        // Tasks
        bind(ClusterDiscoveryTask.class);
        bind(ClusterRefreshTask.class);

    }
}
