package com.netflix.paas.jetty;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.netflix.governator.guice.LifecycleInjector;
import com.netflix.paas.cassandra.MetaCassandraBootstrap;
import com.netflix.paas.cassandra.MetaModule;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class NewPaasGuiceServletConfig extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
        return LifecycleInjector.builder()
            .withModules(
                new MetaModule(),
                //new EurekaModule(),
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
                     }
                }
            )
            .createInjector();
    }
}
