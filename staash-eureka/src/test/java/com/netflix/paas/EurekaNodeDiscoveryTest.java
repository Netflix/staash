/*******************************************************************************
 * /***
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
 ******************************************************************************/
package com.netflix.paas;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.netflix.astyanax.connectionpool.Host;
import com.netflix.governator.guice.LifecycleInjector;
import com.netflix.paas.cassandra.discovery.EurekaAstyanaxHostSupplier;
import com.netflix.paas.cassandra.discovery.EurekaModule;

public class EurekaNodeDiscoveryTest {
    Logger LOG = LoggerFactory.getLogger(EurekaNodeDiscoveryTest.class);

    @Test
    @Ignore
    public void testSupplier() {
        List<AbstractModule> modules = Lists.newArrayList(
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(String.class).annotatedWith(Names.named("groupName")).toInstance("testgroup");
                    }
                },
                new EurekaModule()
            );
            
            // Create the injector
            Injector injector = LifecycleInjector.builder()
                .withModules(modules)
                .createInjector();

            EurekaAstyanaxHostSupplier supplier = injector.getInstance(EurekaAstyanaxHostSupplier.class);
            Supplier<List<Host>> list1 = supplier.getSupplier("cass_sandbox");
            List<Host> hosts = list1.get();
            LOG.info("cass_sandbox");
            for (Host host:hosts) {
                LOG.info(host.getHostName());
            }
            Supplier<List<Host>> list2 = supplier.getSupplier("ABCLOUD");
            hosts = list2.get();
            LOG.info("ABCLOUD");
            for (Host host:hosts) {
                LOG.info(host.getHostName());
            }
            
            Supplier<List<Host>> list3 = supplier.getSupplier("CASSS_PAAS");
            hosts = list3.get();
            LOG.info("casss_paas");
            for (Host host:hosts) {
                LOG.info(host.getHostName());
            }

    }

}
