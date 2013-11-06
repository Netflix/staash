package com.netflix.paas.ptp;

import java.util.concurrent.atomic.AtomicLong;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.netflix.paas.mesh.InstanceInfo;
import com.netflix.paas.mesh.InstanceRegistry;
import com.netflix.paas.mesh.client.ClientFactory;
import com.netflix.paas.mesh.endpoints.EndpointPolicy;
import com.netflix.paas.mesh.server.MemoryServer;
import com.netflix.paas.mesh.server.Server;
import com.netflix.paas.mesh.server.ServerFactory;

public class RandomServerProvider implements ServerFactory {
    private final InstanceRegistry instanceRegistry;
    private final ClientFactory clientFactory;
    private final EndpointPolicy endpointPolicy;
    private final AtomicLong counter = new AtomicLong();
    
    @Inject
    public RandomServerProvider(InstanceRegistry instanceRegistry, ClientFactory clientFactory, EndpointPolicy endpointPolicy) {
        this.instanceRegistry = instanceRegistry;
        this.clientFactory = clientFactory;
        this.endpointPolicy = endpointPolicy;
    }
    
    @Override
    public Server createServer(InstanceInfo ii) {
        return new MemoryServer(instanceRegistry, clientFactory, endpointPolicy, "" + counter.incrementAndGet());
    }
}
