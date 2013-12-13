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
