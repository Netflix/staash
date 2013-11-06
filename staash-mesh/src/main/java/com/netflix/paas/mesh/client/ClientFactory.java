package com.netflix.paas.mesh.client;

import com.netflix.paas.mesh.InstanceInfo;

public interface ClientFactory {
    Client createClient(InstanceInfo instanceInfo);
}
