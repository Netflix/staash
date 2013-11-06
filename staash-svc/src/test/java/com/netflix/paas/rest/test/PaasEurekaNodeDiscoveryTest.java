package com.netflix.paas.rest.test;

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

public class PaasEurekaNodeDiscoveryTest {
    Logger LOG = LoggerFactory.getLogger(PaasEurekaNodeDiscoveryTest.class);

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
//        EurekaAstyanaxHostSupplier supplier = new EurekaAstyanaxHostSupplier();
            Supplier<List<Host>> list1 = supplier.getSupplier("c_sandbox");
            List<Host> hosts = list1.get();
            LOG.info("cass_sandbox");
            for (Host host:hosts) {
                LOG.info(host.getHostName());
            }
//            Supplier<List<Host>> list2 = supplier.getSupplier("ABCLOUD");
//            hosts = list2.get();
//            LOG.info("ABCLOUD");
//            for (Host host:hosts) {
//                LOG.info(host.getHostName());
//            }
            
            Supplier<List<Host>> list3 = supplier.getSupplier("C_PAAS");
            hosts = list3.get();
            LOG.info("c_paas");
            for (Host host:hosts) {
                LOG.info(host.getHostName());
            }

    }

}
