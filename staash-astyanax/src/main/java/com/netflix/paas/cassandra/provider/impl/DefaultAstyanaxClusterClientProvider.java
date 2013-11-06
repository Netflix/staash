package com.netflix.paas.cassandra.provider.impl;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.configuration.AbstractConfiguration;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Cluster;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;
import com.netflix.paas.cassandra.keys.ClusterKey;
import com.netflix.paas.cassandra.provider.AstyanaxConfigurationProvider;
import com.netflix.paas.cassandra.provider.AstyanaxConnectionPoolConfigurationProvider;
import com.netflix.paas.cassandra.provider.AstyanaxConnectionPoolMonitorProvider;
import com.netflix.paas.cassandra.provider.ClusterClientProvider;
import com.netflix.paas.cassandra.provider.HostSupplierProvider;

public class DefaultAstyanaxClusterClientProvider implements ClusterClientProvider {

    /**
     * Track cluster references
     * 
     * @author elandau
     *
     */
    public static class ClusterContextHolder {
        private AstyanaxContext<Cluster> context;
        private AtomicLong refCount = new AtomicLong(0);
        
        public ClusterContextHolder(AstyanaxContext<Cluster> context) {
            this.context = context;
        }
        
        public Cluster getKeyspace() {
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
    
    private final Map<String, ClusterContextHolder> contextMap = Maps.newHashMap();
    
    private final AstyanaxConfigurationProvider               configurationProvider;
    private final AstyanaxConnectionPoolConfigurationProvider cpProvider;
    private final AstyanaxConnectionPoolMonitorProvider       monitorProvider;
    private final Map<String, HostSupplierProvider>           hostSupplierProviders;
    private final AbstractConfiguration                       configuration;
    
    @Inject
    public DefaultAstyanaxClusterClientProvider(
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
    public synchronized Cluster acquireCluster(ClusterKey clusterKey) {
        String clusterName = clusterKey.getClusterName().toLowerCase();
        Preconditions.checkNotNull(clusterName, "Invalid cluster name 'null'");
        
        ClusterContextHolder holder = contextMap.get(clusterName);
        if (holder == null) {
            HostSupplierProvider hostSupplierProvider = hostSupplierProviders.get(clusterKey.getDiscoveryType());
            Preconditions.checkNotNull(hostSupplierProvider, String.format("Unknown host supplier provider '%s' for cluster '%s'", clusterKey.getDiscoveryType(), clusterName));
            
            AstyanaxContext<Cluster> context = new AstyanaxContext.Builder()
                .forCluster(clusterName)
                .withAstyanaxConfiguration(configurationProvider.get(clusterName))
                .withConnectionPoolConfiguration(cpProvider.get(clusterName))
                .withConnectionPoolMonitor(monitorProvider.get(clusterName))
                .withHostSupplier(hostSupplierProvider.getSupplier(clusterName))
                .buildCluster(ThriftFamilyFactory.getInstance());
            
            holder = new ClusterContextHolder(context);
            holder.start();
        }
        holder.addRef();
        
        return holder.getKeyspace();
    }

    @Override
    public synchronized void releaseCluster(ClusterKey clusterKey) {
        String clusterName = clusterKey.getClusterName().toLowerCase();
        ClusterContextHolder holder = contextMap.get(clusterName);
        if (holder.releaseRef() == 0) {
            contextMap.remove(clusterName);
            holder.shutdown();
        }
    }
}
