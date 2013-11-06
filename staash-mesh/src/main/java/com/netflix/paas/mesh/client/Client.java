package com.netflix.paas.mesh.client;

import com.netflix.paas.mesh.InstanceInfo;
import com.netflix.paas.mesh.messages.Message;
import com.netflix.paas.mesh.messages.ResponseHandler;

/**
 * Client of a PTP server
 * 
 * @author elandau
 *
 */
public interface Client {
    InstanceInfo getInstanceInfo();
    
    void sendMessage(Message request, ResponseHandler response);

    void shutdown();
    
    void connect();
    
}
