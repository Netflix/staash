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
package com.netflix.paas.cassandra.resources;

import java.util.concurrent.ConcurrentMap;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolType;
import com.netflix.astyanax.connectionpool.impl.Slf4jConnectionPoolMonitorImpl;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;
import com.netflix.paas.exceptions.AlreadyExistsException;
import com.netflix.paas.exceptions.NotFoundException;

/**
 * Tracks accessible keyspaces for this cluster
 * 
 * @author elandau
 */
public class CassandraClusterHolder {
    private final String clusterName;
    private final ConcurrentMap<String, CassandraKeyspaceHolder> keyspaces = Maps.newConcurrentMap();
    
    public CassandraClusterHolder(String clusterName) {
        this.clusterName = clusterName;
    }
    
    /**
     * Register a keyspace such that a client is created for it and it is now accessible to 
     * this instance
     * 
     * @param keyspaceName
     * @throws AlreadyExistsException
     */
    public synchronized void registerKeyspace(String keyspaceName) throws AlreadyExistsException {
        Preconditions.checkNotNull(keyspaceName);
        
        if (keyspaces.containsKey(keyspaceName)) {
            throw new AlreadyExistsException("keyspace", keyspaceName);
        }
        
        CassandraKeyspaceHolder keyspace = new CassandraKeyspaceHolder(new AstyanaxContext.Builder()
                .forCluster(clusterName)
                .forKeyspace(keyspaceName)
                .withAstyanaxConfiguration(
                        new AstyanaxConfigurationImpl()
                                .setDiscoveryType(NodeDiscoveryType.RING_DESCRIBE)
                                .setConnectionPoolType(ConnectionPoolType.ROUND_ROBIN)
                                .setDiscoveryDelayInSeconds(60000))
                .withConnectionPoolConfiguration(
                        new ConnectionPoolConfigurationImpl(
                                clusterName + "_" + keyspaceName)
                                .setSeeds("localhost:9160"))
                .withConnectionPoolMonitor(new Slf4jConnectionPoolMonitorImpl())
                .buildKeyspace(ThriftFamilyFactory.getInstance()));
        
        try {
            keyspace.initialize();
        }
        finally {
            keyspaces.put(keyspaceName, keyspace);
        }
    }
    
    /**
     * Unregister a keyspace so that it is no longer accessible to this instance
     * @param keyspaceName
     */
    public void unregisterKeyspace(String keyspaceName) {
        Preconditions.checkNotNull(keyspaceName);
        
        CassandraKeyspaceHolder keyspace = keyspaces.remove(keyspaceName);
        if (keyspace != null) {
            keyspace.shutdown();
        }
    }
    
    /**
     * Get the Keyspace holder for the specified keyspace name
     * 
     * @param keyspaceName
     * @return
     * @throws NotFoundException
     */
    public CassandraKeyspaceHolder getKeyspace(String keyspaceName) throws NotFoundException {
        Preconditions.checkNotNull(keyspaceName);
        
        CassandraKeyspaceHolder keyspace = keyspaces.get(keyspaceName);
        if (keyspace == null)
            throw new NotFoundException("keyspace", keyspaceName);
        return keyspace;
    }
    
    public String getClusterName() {
        return this.clusterName;
    }

    public void shutdown() {
        // TODO
    }
    
    public void initialize() {
        // TODO
    }
    
}
