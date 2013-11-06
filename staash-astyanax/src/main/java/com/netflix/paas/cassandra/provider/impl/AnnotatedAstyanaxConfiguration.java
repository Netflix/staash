package com.netflix.paas.cassandra.provider.impl;

import java.util.concurrent.ExecutorService;

import com.netflix.astyanax.AstyanaxConfiguration;
import com.netflix.astyanax.Clock;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolType;
import com.netflix.astyanax.model.ConsistencyLevel;
import com.netflix.astyanax.partitioner.Partitioner;
import com.netflix.astyanax.retry.RetryPolicy;
import com.netflix.astyanax.retry.RunOnce;
import com.netflix.governator.annotations.Configuration;

public class AnnotatedAstyanaxConfiguration implements AstyanaxConfiguration {
    @Configuration("${prefix}.${name}.retryPolicy")
    private RetryPolicy retryPolicy = RunOnce.get();

    @Configuration("${prefix}.${name}.defaultReadConsistencyLevel")
    private ConsistencyLevel defaultReadConsistencyLevel = ConsistencyLevel.CL_ONE;
    
    @Configuration("${prefix}.${name}.defaultWriteConsistencyLevel")
    private ConsistencyLevel defaultWriteConsistencyLevel = ConsistencyLevel.CL_ONE;
    
    @Configuration("${prefix}.${name}.clock")
    private String clockName = null;
    
    @Configuration("${prefix}.${name}.discoveryDelayInSeconds")
    private int discoveryDelayInSeconds;

    @Configuration("${prefix}.${name}.discoveryType")
    private NodeDiscoveryType discoveryType;
    
    @Configuration("${prefix}.${name}.connectionPoolType")
    private ConnectionPoolType getConnectionPoolType;

    @Configuration("${prefix}.${name}.cqlVersion")
    private String cqlVersion;
    
    private Clock clock;
            
    void initialize() {
        
    }
    
    void cleanup() {
        
    }
    
    @Override
    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    @Override
    public ConsistencyLevel getDefaultReadConsistencyLevel() {
        return this.defaultReadConsistencyLevel;
    }

    @Override
    public ConsistencyLevel getDefaultWriteConsistencyLevel() {
        return this.defaultWriteConsistencyLevel;
    }

    @Override
    public Clock getClock() {
        return this.clock;
    }

    @Override
    public ExecutorService getAsyncExecutor() {
        return null;
    }

    @Override
    public int getDiscoveryDelayInSeconds() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public NodeDiscoveryType getDiscoveryType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ConnectionPoolType getConnectionPoolType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getCqlVersion() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTargetCassandraVersion() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Partitioner getPartitioner(String partitionerName) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

}
