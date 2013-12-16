package com.netflix.paas.cassandra.discovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.netflix.appinfo.CloudInstanceConfig;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryManager;
//import com.netflix.paas.cassandra.provider.HostSupplierProvider;
//import com.netflix.paas.cassandra.tasks.ClusterDiscoveryTask;
//import com.netflix.paas.cassandra.tasks.ClusterRefreshTask;
//import com.netflix.paas.tasks.InlineTaskManager;
//import com.netflix.paas.tasks.TaskManager;

public class EurekaModule extends AbstractModule {
    private static final Logger LOG = LoggerFactory.getLogger(EurekaModule.class);

    @Override
    protected void configure() {
        LOG.info("Configuring EurekaModule");
        
        // Initialize eureka
        // TODO: Move this to a bootstrap thingy
        DiscoveryManager.getInstance().initComponent(
                new CloudInstanceConfig(),
                new DefaultEurekaClientConfig());

        // Eureka - Astyanax integration
        MapBinder<String, HostSupplierProvider> hostSuppliers = MapBinder.newMapBinder(binder(), String.class, HostSupplierProvider.class);
        hostSuppliers.addBinding("eureka").to(EurekaAstyanaxHostSupplier.class).asEagerSingleton();
        
        //bind(ClusterDiscoveryService.class).to(EurekaClusterDiscoveryService.class).asEagerSingleton();

    }
}
