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
