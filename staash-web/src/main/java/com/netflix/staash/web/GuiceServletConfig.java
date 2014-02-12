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
package com.netflix.staash.web;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.netflix.governator.guice.LifecycleInjector;
import com.netflix.staash.cassandra.discovery.EurekaModule;
import com.netflix.staash.rest.modules.PaasPropertiesModule;
import com.netflix.staash.rest.resources.PaasAdminResourceImpl;
import com.netflix.staash.rest.resources.PaasDataResourceImpl;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class GuiceServletConfig extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
        return LifecycleInjector.builder()
                .withModules(
                    new EurekaModule(),
                     new PaasPropertiesModule(),   
                    new JerseyServletModule() {
                        @Override
                        protected void configureServlets() {
                            bind(GuiceContainer.class).asEagerSingleton();
                            bind(PaasAdminResourceImpl.class);
                            bind(PaasDataResourceImpl.class);
                            serve("/*").with(GuiceContainer.class);
                        }
                    }
                )
                .createInjector();
    }
}
