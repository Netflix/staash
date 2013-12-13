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
package com.netflix.paas.ptp;

import com.google.inject.AbstractModule;
import com.netflix.paas.mesh.InstanceRegistry;
import com.netflix.paas.mesh.client.ClientFactory;
import com.netflix.paas.mesh.client.memory.MemoryClientFactory;
import com.netflix.paas.mesh.db.TopicRegistry;
import com.netflix.paas.mesh.endpoints.ChordEndpointPolicy;
import com.netflix.paas.mesh.endpoints.EndpointPolicy;
import com.netflix.paas.mesh.seed.TopicSeed;

public class TestPtpGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TopicSeed.class).to(DummyTopicSeed.class);
        bind(TopicRegistry.class).asEagerSingleton();
        bind(InstanceRegistry.class).asEagerSingleton();
        
        bind(ClientFactory.class).to(MemoryClientFactory.class).asEagerSingleton();
        bind(EndpointPolicy.class).to(ChordEndpointPolicy.class).asEagerSingleton();
//        bind(TopicFactory.class).to(MemoryTopicFactory());
    }

}
