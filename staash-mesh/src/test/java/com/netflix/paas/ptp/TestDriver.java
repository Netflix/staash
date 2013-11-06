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
