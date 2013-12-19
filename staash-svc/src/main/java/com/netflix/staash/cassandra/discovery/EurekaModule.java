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
package com.netflix.staash.cassandra.discovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.netflix.appinfo.CloudInstanceConfig;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryManager;

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
