package com.netflix.paas.mesh.server;

import com.netflix.paas.mesh.InstanceInfo;

public interface ServerFactory {
    Server createServer(InstanceInfo ii);
}
