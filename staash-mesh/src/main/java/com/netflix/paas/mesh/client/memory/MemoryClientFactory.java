package com.netflix.paas.mesh.client.memory;

import com.netflix.paas.mesh.InstanceInfo;
import com.netflix.paas.mesh.client.Client;
import com.netflix.paas.mesh.client.ClientFactory;

public class MemoryClientFactory implements ClientFactory {

    @Override
    public Client createClient(InstanceInfo instanceInfo) {
        return new MemoryClient(instanceInfo);
    }

}
