package com.netflix.paas.jetty;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.google.inject.servlet.GuiceServletContextListener;
import com.netflix.governator.guice.LifecycleInjector;
import com.netflix.paas.PaasBootstrap;
import com.netflix.paas.PaasModule;
import com.netflix.paas.cassandra.CassandraPaasModule;
import com.netflix.paas.cassandra.MetaCassandraBootstrap;
import com.netflix.paas.cassandra.MetaModule;
import com.netflix.paas.cassandra.PaasCassandraBootstrap;
import com.netflix.paas.cassandra.discovery.EurekaModule;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class PaasGuiceServletConfig extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
        return LifecycleInjector.builder()
            .withModules(
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(String.class).annotatedWith(Names.named("groupName")).toInstance("UnitTest1");
                        bind(String.class).annotatedWith(Names.named("clustername")).toInstance("localhost");
                    }
                },
                new CassandraPaasModule(),
                new MetaModule(),
                //new EurekaModule(),
                new PaasModule(),
                new JerseyServletModule() {
                    @Override
                    protected void configureServlets() {
                        // Route all requests through GuiceContainer
                        bind(GuiceContainer.class).asEagerSingleton();
                        serve("/*").with(GuiceContainer.class);
                    }
                },
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(MetaCassandraBootstrap.class).asEagerSingleton();
                        bind(PaasBootstrap.class).asEagerSingleton();
                        bind(PaasCassandraBootstrap.class).asEagerSingleton();
                    }
                }
            )
            .createInjector();
    }
}
