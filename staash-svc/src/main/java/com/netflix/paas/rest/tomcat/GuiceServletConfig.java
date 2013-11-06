package com.netflix.paas.rest.tomcat;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.netflix.governator.guice.LifecycleInjector;
import com.netflix.paas.cassandra.discovery.EurekaModule;
import com.netflix.paas.rest.dao.CqlDataDaoImpl;
import com.netflix.paas.rest.dao.CqlMetaDaoImpl;
import com.netflix.paas.rest.dao.DataDao;
import com.netflix.paas.rest.dao.MetaDao;
import com.netflix.paas.rest.modules.MetaModule;
import com.netflix.paas.rest.modules.PaasPropertiesModule;
import com.netflix.paas.rest.resource.refactored.PaasAdminResourceImplNew;
import com.netflix.paas.rest.resource.refactored.PaasDataResourceImplNew;
import com.netflix.paas.rest.resources.PaasAdminResourceImpl;
import com.netflix.paas.rest.resources.PaasDataResourceImpl;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class GuiceServletConfig extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
        return LifecycleInjector.builder()
                .withModules(
                    new EurekaModule(),
//                        new MetaModule(),
                     new PaasPropertiesModule(),   
                    new JerseyServletModule() {
                        @Override
                        protected void configureServlets() {
                            // Route all requests through GuiceContainer
                            bind(GuiceContainer.class).asEagerSingleton();
                            //bind(MetaDao.class).to(CqlMetaDaoImpl.class).asEagerSingleton();
                            //bind(DataDao.class).to(CqlDataDaoImpl.class).asEagerSingleton();
//                            bind(PaasAdminResourceImpl.class);
//                            bind(PaasDataResourceImpl.class);
                            bind(PaasAdminResourceImplNew.class);
                            bind(PaasDataResourceImplNew.class);
                            serve("/*").with(GuiceContainer.class);
                        }
                    }
                )
                .createInjector();
//        return Guice.createInjector(new JerseyServletModule() {
//
//            @Override
//            protected void configureServlets() {
//
//                /* Bindings */
//                bind(GuiceContainer.class).asEagerSingleton();
//                bind(PaasAdminResourceImpl.class);
//                bind(PaasDataResourceImpl.class);
//                bind(MetaDao.class).to(CqlMetaDaoImpl.class).asEagerSingleton();
//
//                serve("/*").with(GuiceContainer.class);
//            } 
//        });
    }
}
