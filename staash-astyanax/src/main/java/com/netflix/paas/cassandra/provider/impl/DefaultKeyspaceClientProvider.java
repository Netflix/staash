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
package com.netflix.paas.cassandra.provider.impl;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.configuration.AbstractConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;
import com.netflix.paas.cassandra.keys.KeyspaceKey;
import com.netflix.paas.cassandra.provider.AstyanaxConfigurationProvider;
import com.netflix.paas.cassandra.provider.AstyanaxConnectionPoolConfigurationProvider;
import com.netflix.paas.cassandra.provider.AstyanaxConnectionPoolMonitorProvider;
import com.netflix.paas.cassandra.provider.HostSupplierProvider;
import com.netflix.paas.cassandra.provider.KeyspaceClientProvider;

public class DefaultKeyspaceClientProvider implements KeyspaceClientProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultKeyspaceClientProvider.class);
    
    private static final String DISCOVERY_TYPE_FORMAT = "com.netflix.paas.schema.%s.discovery";
    private static final String CLUSTER_NAME_FORMAT   = "com.netflix.paas.schema.%s.cluster";
    private static final String KEYSPACE_NAME__FORMAT = "com.netflix.paas.schema.%s.keyspace";
    ImmutableMap<String, Object> defaultKsOptions = ImmutableMap.<String, Object>builder()
            .put("strategy_options", ImmutableMap.<String, Object>builder()
                    .put("replication_factor", "1")
                    .build())
            .put("strategy_class",     "SimpleStrategy")
            .build();

    public static class KeyspaceContextHolder {
        private AstyanaxContext<Keyspace> context;
        private AtomicLong refCount = new AtomicLong(0);
        
        public KeyspaceContextHolder(AstyanaxContext<Keyspace> context) {
            this.context = context;
        }
        
        public Keyspace getKeyspace() {
            return context.getClient();
        }
        
        public void start() {
            context.start();
        }
        
        public void shutdown() {
            context.shutdown();
        }
        
        public long addRef() {
            return refCount.incrementAndGet();
        }
        
        public long releaseRef() {
            return refCount.decrementAndGet();
        }
    }
    
    private final Map<String, KeyspaceContextHolder> contextMap = Maps.newHashMap();
    
    private final AstyanaxConfigurationProvider               configurationProvider;
    private final AstyanaxConnectionPoolConfigurationProvider cpProvider;
    private final AstyanaxConnectionPoolMonitorProvider       monitorProvider;
    private final Map<String, HostSupplierProvider>           hostSupplierProviders;
    private final AbstractConfiguration                       configuration;
    
    @Inject
    public DefaultKeyspaceClientProvider(
            AbstractConfiguration                       configuration,
            Map<String, HostSupplierProvider>           hostSupplierProviders,
            AstyanaxConfigurationProvider               configurationProvider,
            AstyanaxConnectionPoolConfigurationProvider cpProvider,
            AstyanaxConnectionPoolMonitorProvider       monitorProvider) {
        this.configurationProvider = configurationProvider;
        this.cpProvider            = cpProvider;
        this.monitorProvider       = monitorProvider;
        this.hostSupplierProviders = hostSupplierProviders;
        this.configuration         = configuration;
    }
    
    @Override
    public synchronized Keyspace acquireKeyspace(String schemaName) {
        schemaName = schemaName.toLowerCase();
        
        Preconditions.checkNotNull(schemaName, "Invalid schema name 'null'");
        
        KeyspaceContextHolder holder = contextMap.get(schemaName);
        if (holder == null) {
            LOG.info("Creating schema for '{}'", new Object[]{schemaName});
            
            String clusterName   = configuration.getString(String.format(CLUSTER_NAME_FORMAT,   schemaName));
            String keyspaceName  = configuration.getString(String.format(KEYSPACE_NAME__FORMAT, schemaName));
            String discoveryType = configuration.getString(String.format(DISCOVERY_TYPE_FORMAT, schemaName));
            if (clusterName==null || clusterName.equals("")) clusterName   = configuration.getString(String.format(CLUSTER_NAME_FORMAT,   "configuration"));
            if (keyspaceName == null || keyspaceName.equals("")) keyspaceName = schemaName;
            if (discoveryType==null || discoveryType.equals("")) discoveryType = configuration.getString(String.format(DISCOVERY_TYPE_FORMAT, "configuration"));
            Preconditions.checkNotNull(clusterName,   "Missing cluster name for schema " + schemaName + " " + String.format(CLUSTER_NAME_FORMAT,schemaName));
            Preconditions.checkNotNull(keyspaceName,  "Missing cluster name for schema " + schemaName + " " + String.format(KEYSPACE_NAME__FORMAT,schemaName));
            Preconditions.checkNotNull(discoveryType, "Missing cluster name for schema " + schemaName + " " + String.format(DISCOVERY_TYPE_FORMAT,schemaName));
            
            HostSupplierProvider hostSupplierProvider = hostSupplierProviders.get(discoveryType);
            Preconditions.checkNotNull(hostSupplierProvider, 
                    String.format("Unknown host supplier provider '%s' for schema '%s'", discoveryType, schemaName));
            
            AstyanaxContext<Keyspace> context = new AstyanaxContext.Builder()
                .forCluster(clusterName)
                .forKeyspace(keyspaceName)
                .withAstyanaxConfiguration(configurationProvider.get(schemaName))
                .withConnectionPoolConfiguration(cpProvider.get(schemaName))
                .withConnectionPoolMonitor(monitorProvider.get(schemaName))
                .withHostSupplier(hostSupplierProvider.getSupplier(clusterName))
                .buildKeyspace(ThriftFamilyFactory.getInstance());
            context.start();
            try {
                context.getClient().createKeyspace(defaultKsOptions);
            } catch (ConnectionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            holder = new KeyspaceContextHolder(context);
            contextMap.put(schemaName, holder);
            holder.start();
        }
        holder.addRef();
        
        return holder.getKeyspace();
    }

    @Override
    public synchronized void releaseKeyspace(String schemaName) {
        KeyspaceContextHolder holder = contextMap.get(schemaName);
        if (holder.releaseRef() == 0) {
            contextMap.remove(schemaName);
            holder.shutdown();
        }
    }

    @Override
    public Keyspace acquireKeyspace(KeyspaceKey key) {
        String schemaName = key.getSchemaName().toLowerCase();
        
        Preconditions.checkNotNull(schemaName, "Invalid schema name 'null'");
        
        KeyspaceContextHolder holder = contextMap.get(schemaName);
        if (holder == null) {
            Preconditions.checkNotNull(key.getClusterName(),   "Missing cluster name for schema " + schemaName);
            Preconditions.checkNotNull(key.getKeyspaceName(),  "Missing cluster name for schema " + schemaName);
            Preconditions.checkNotNull(key.getDiscoveryType(), "Missing cluster name for schema " + schemaName);
            
            HostSupplierProvider hostSupplierProvider = hostSupplierProviders.get(key.getDiscoveryType());
            Preconditions.checkNotNull(hostSupplierProvider, "Unknown host supplier provider " + key.getDiscoveryType());
            
            AstyanaxContext<Keyspace> context = new AstyanaxContext.Builder()
                .forCluster(key.getClusterName())
                .forKeyspace(key.getKeyspaceName())
                .withAstyanaxConfiguration(configurationProvider.get(schemaName))
                .withConnectionPoolConfiguration(cpProvider.get(schemaName))
                .withConnectionPoolMonitor(monitorProvider.get(schemaName))
                .withHostSupplier(hostSupplierProvider.getSupplier(key.getClusterName()))
                .buildKeyspace(ThriftFamilyFactory.getInstance());
            
            holder = new KeyspaceContextHolder(context);
            contextMap.put(schemaName, holder);
            holder.start();
        }
        holder.addRef();
        
        return holder.getKeyspace();
    }
}
