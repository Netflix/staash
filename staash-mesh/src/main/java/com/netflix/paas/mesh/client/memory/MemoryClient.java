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
