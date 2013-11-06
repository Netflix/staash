package com.netflix.paas.mesh.server.tasks;

import com.netflix.paas.mesh.InstanceRegistry;
import com.netflix.paas.mesh.server.Server;

public class RefreshRingRunnable implements Runnable {
    private final Server server;
    private final InstanceRegistry instanceRegistry;
    
    public RefreshRingRunnable(Server server, InstanceRegistry instanceRegistry) {
        this.server = server;
        this.instanceRegistry = instanceRegistry;
    }
    
    @Override
    public void run() {
        try {
//            server.(this.instanceRegistry.getMembers());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
