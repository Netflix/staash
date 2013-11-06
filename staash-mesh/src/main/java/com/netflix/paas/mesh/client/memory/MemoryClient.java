package com.netflix.paas.mesh.client.memory;

import com.netflix.paas.mesh.InstanceInfo;
import com.netflix.paas.mesh.client.Client;
import com.netflix.paas.mesh.messages.Message;
import com.netflix.paas.mesh.messages.ResponseHandler;

public class MemoryClient implements Client {
    private final InstanceInfo instanceInfo;
    
    public MemoryClient(InstanceInfo instanceInfo) {
        this.instanceInfo = instanceInfo;
    }

    @Override
    public InstanceInfo getInstanceInfo() {
        return instanceInfo;
    }

    @Override
    public void sendMessage(Message request, ResponseHandler response) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void shutdown() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void connect() {
        // TODO Auto-generated method stub
        
    }

}
