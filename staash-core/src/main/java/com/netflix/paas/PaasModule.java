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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.commons.configuration.AbstractConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.netflix.config.ConfigurationManager;
import com.netflix.paas.config.ArchaeusPaasConfiguration;
import com.netflix.paas.config.PaasConfiguration;
import com.netflix.paas.dao.DaoProvider;
import com.netflix.paas.resources.DataResource;
import com.netflix.paas.resources.SchemaAdminResource;
import com.netflix.paas.resources.impl.JerseySchemaAdminResourceImpl;
import com.netflix.paas.service.SchemaService;
import com.netflix.paas.services.impl.DaoSchemaService;
import com.netflix.paas.tasks.InlineTaskManager;
import com.netflix.paas.tasks.TaskManager;

/**
 * Core bindings for PAAS.  Anything configurable will be loaded by different modules
 * @author elandau
 *
 */
public class PaasModule extends AbstractModule {
    private static final Logger LOG = LoggerFactory.getLogger(PaasModule.class);
    
    private final EventBus eventBus = new EventBus("Default EventBus");
    
    @Override
    protected void configure() {
        LOG.info("Loading PaasModule");
        
        bind(EventBus.class).toInstance(eventBus);
        bindListener(Matchers.any(), new TypeListener() {
            public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
                typeEncounter.register(new InjectionListener<I>() {
                    public void afterInjection(I i) {
                        eventBus.register(i);
                    }
                });
            }
        });

        bind(TaskManager.class).to(InlineTaskManager.class);

        // Constants
        bind(String.class).annotatedWith(Names.named("namespace")).toInstance("com.netflix.pass.");
        bind(String.class).annotatedWith(Names.named("appname"  )).toInstance("paas");
        
        bind(AbstractConfiguration.class).toInstance(ConfigurationManager.getConfigInstance());

        // Configuration
        bind(PaasConfiguration.class).to(ArchaeusPaasConfiguration.class).in(Scopes.SINGLETON);
        
        // Stuff
        bind(ScheduledExecutorService.class).annotatedWith(Names.named("tasks")).toInstance(Executors.newScheduledThreadPool(10));
        
        bind(DaoProvider.class).in(Scopes.SINGLETON);
        
        // Rest resources
        bind(DataResource.class).in(Scopes.SINGLETON);
        bind(SchemaAdminResource.class).to(JerseySchemaAdminResourceImpl.class).in(Scopes.SINGLETON);
        bind(SchemaService.class).to(DaoSchemaService.class).in(Scopes.SINGLETON);
        
    }
}
