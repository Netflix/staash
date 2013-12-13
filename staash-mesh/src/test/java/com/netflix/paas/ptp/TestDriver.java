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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.netflix.paas.mesh.InstanceRegistry;
import com.netflix.paas.mesh.client.ClientFactory;
import com.netflix.paas.mesh.client.memory.MemoryClientFactory;
import com.netflix.paas.mesh.db.Entry;
import com.netflix.paas.mesh.db.TopicRegistry;
import com.netflix.paas.mesh.db.memory.MemoryTopicFactory;
import com.netflix.paas.mesh.endpoints.ChordEndpointPolicy;
import com.netflix.paas.mesh.endpoints.EndpointPolicy;
import com.netflix.paas.mesh.server.Server;

public class TestDriver {

    public static void main(String[] args) {
        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);
        
        final TopicRegistry    topics   = new TopicRegistry(new MemoryTopicFactory());
        final InstanceRegistry registry = new InstanceRegistry();
        final ClientFactory    factory  = new MemoryClientFactory();
        final EndpointPolicy   endpointPolicy = new ChordEndpointPolicy();
        
        topics.createTopic("test");
        topics.addEntry("test",  new Entry("Key1", "Value1", System.currentTimeMillis()));
        topics.addEntry("test",  new Entry("Key2", "Value2", System.currentTimeMillis()));
        
        final AtomicInteger counter = new AtomicInteger();
        final int instanceCount     = 10;
        final int asgCount          = 10;
        final long asgCreateDelay   = 5;
        
//        // Thread to add random server
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                long id = counter.incrementAndGet();
//                if (id < asgCount) {
//                    for (int i = 0; i < instanceCount; i++) {
//                        try {
//                            Server server = new Server(registry, factory, endpointPolicy, "" + (id * instanceCount + i));
//                            server.start();
//                        }
//                        catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    executor.schedule(this, asgCreateDelay, TimeUnit.SECONDS);
//                }
//            }
//        });
//
//        executor.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//            }
//        }, 10, 10, TimeUnit.SECONDS);
//        
//        try {
//            Thread.sleep(100000);
//        } catch (InterruptedException e) {
//        }
    }

}
