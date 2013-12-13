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
package com.netflix.paas.mesh.server;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.netflix.paas.mesh.CompareInstanceInfoByUuid;
import com.netflix.paas.mesh.InstanceInfo;
import com.netflix.paas.mesh.InstanceRegistry;
import com.netflix.paas.mesh.client.Client;
import com.netflix.paas.mesh.client.ClientFactory;
import com.netflix.paas.mesh.db.TopicRegistry;
import com.netflix.paas.mesh.db.memory.MemoryTopicFactory;
import com.netflix.paas.mesh.endpoints.EndpointPolicy;
import com.netflix.paas.mesh.messages.AsyncResponse;
import com.netflix.paas.mesh.messages.Message;
import com.netflix.paas.mesh.messages.RequestHandler;
import com.netflix.paas.mesh.messages.Verb;
import com.netflix.paas.mesh.server.handlers.DataPushHandler;
import com.netflix.paas.mesh.server.handlers.DataRequestHandler;
import com.netflix.paas.mesh.server.handlers.DataResponseHandler;
import com.netflix.paas.mesh.server.handlers.DigestRequestHandler;
import com.netflix.paas.mesh.server.handlers.DigestResponseHandler;

public class MemoryServer implements Server, RequestHandler {
    private static final CompareInstanceInfoByUuid comparator = new CompareInstanceInfoByUuid();
    private static final AtomicLong changeCounter = new AtomicLong();
    
    private final InstanceRegistry         instanceRegistry;
    private final ClientFactory            clientFactory;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    private final InstanceInfo             instanceInfo;
    private final EndpointPolicy           endpointPolicy;
    private final SortedMap<InstanceInfo, Client>  peers  = Maps.newTreeMap(comparator);
    private long  generationCounter = 0;
    private Map<Verb, RequestHandler>         verbHandlers = Maps.newEnumMap(Verb.class);
    private TopicRegistry topics = new TopicRegistry(new MemoryTopicFactory());
    
    @Inject
    public MemoryServer(InstanceRegistry instanceRegistry, ClientFactory clientFactory, EndpointPolicy endpointPolicy, String id) {
        this.instanceRegistry = instanceRegistry;
        this.clientFactory    = clientFactory;
        this.instanceInfo     = new InstanceInfo(id, UUID.randomUUID());
        this.endpointPolicy   = endpointPolicy;
        
        verbHandlers.put(Verb.DATA_PUSH,       new DataPushHandler());
        verbHandlers.put(Verb.DATA_REQUEST,    new DataRequestHandler());
        verbHandlers.put(Verb.DATA_RESPONSE,   new DataResponseHandler());
        verbHandlers.put(Verb.DIGEST_REQUEST,  new DigestRequestHandler());
        verbHandlers.put(Verb.DIGEST_RESPONSE, new DigestResponseHandler());
    }
    
    public void start() {
        System.out.println("Starting " + instanceInfo);
        
        this.instanceRegistry.join(instanceInfo);
        
//        executor.scheduleAtFixedRate(
//                new RefreshRingRunnable(this, instanceRegistry), 
//                10, 10, TimeUnit.SECONDS);
    }
    
    public void stop() {
        System.out.println("Stopping " + instanceInfo);
        
        this.instanceRegistry.leave(instanceInfo);
    }

    /**
     * Update the list of all members in the ring
     * @param ring
     */
    public void setMembers(List<InstanceInfo> ring) {
        List<InstanceInfo> instances = endpointPolicy.getEndpoints(instanceInfo, ring);
        Collections.sort(instances, comparator);
        
        List<InstanceInfo> toRemove = Lists.newArrayList();
        List<InstanceInfo> toAdd    = Lists.newArrayList();
        List<InstanceInfo> toDisconnect = Lists.newArrayList();
        
        int changeCount = 0;
        for (Entry<InstanceInfo, Client> peer : peers.entrySet()) {
            // Determine if peers have been removed from the ring
            if (Collections.binarySearch(ring, peer.getKey(), comparator) < 0) {
                toRemove.add(peer.getKey());
                changeCount++;
            }
            // Determine if peers are no longer appropriate
            else if (Collections.binarySearch(instances, peer.getKey(), comparator) < 0) {
                toDisconnect.add(peer.getKey());
                changeCount++;
            }
        }
        
        // Add new peers
        for (InstanceInfo peer : instances) {
            if (!peers.containsKey(peer)) {
                toAdd.add(peer);
                changeCount++;
            }
        }
        
        for (InstanceInfo ii : toRemove) {
            removePeer(ii);
        }
        
        for (InstanceInfo ii : toDisconnect) {
            disconnectPeer(ii);
        }
        
        for (InstanceInfo ii : toAdd) {
            addPeer(ii);
        }
        
        generationCounter++;
        if (generationCounter > 1 && changeCount != 0)
            printPeers(changeCount);
    }
    
    /**
     * Remove a peer that is no longer in the ring.
     * @param instanceInfo
     */
    private void removePeer(InstanceInfo instanceInfo) {
        System.out.println("Removing peer " + this.instanceInfo + " -> " + instanceInfo);
        Client client = peers.remove(instanceInfo);
        client.shutdown();
    }
    
    /**
     * Add a new peer connection
     * @param instanceInfo
     */
    private void addPeer(InstanceInfo instanceInfo) {
//        System.out.println("Adding peer " + this.instanceInfo + " -> " + instanceInfo);
        Client client = clientFactory.createClient(instanceInfo);
        peers.put(instanceInfo, client);
        boostrapClient(client);
    }
    
    /**
     * Disconnect a peer that is no longer in our path
     * @param instanceInfo
     */
    private void disconnectPeer(InstanceInfo instanceInfo) {
        System.out.println("Disconnect peer " + this.instanceInfo + " -> " + instanceInfo);
        Client client = peers.remove(instanceInfo);
        if (client != null) {
            client.shutdown();
        }
        else {
            System.out.println(instanceInfo + " > " + peers);
        }
    }
    
    /**
     * List all peers to which this server is connected
     * @return
     */
    public Iterable<InstanceInfo> listPeers() {
        return peers.keySet();
    }
    
    private void boostrapClient(Client client) {
        
    }
    
    private void printPeers(int changeCount) {
        changeCounter.addAndGet(changeCount);
        StringBuilder sb = new StringBuilder();
        sb.append(">>> " + instanceInfo + " (" + changeCount + " of " + peers.size() + " / " + changeCounter.get() + ") gen=" + generationCounter + "\n");
//        for (Entry<InstanceInfo, Client> peer : peers.entrySet()) {
//            sb.append("   " + peer.getKey()).append("\n");
//        }
//        
        System.out.print(sb.toString());
    }

    @Override
    public void onMessage(Message message, AsyncResponse response) {
        try {
            RequestHandler handler = verbHandlers.get(message.getVerb());
            if (handler != null) {
                handler.onMessage(message,  response);
            }
        }
        catch (Exception e) {
            // Notify error
        }
    }
}
