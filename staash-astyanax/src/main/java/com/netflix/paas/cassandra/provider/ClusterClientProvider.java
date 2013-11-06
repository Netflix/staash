package com.netflix.paas.cassandra.provider;

import com.netflix.astyanax.Cluster;
import com.netflix.paas.cassandra.keys.ClusterKey;

/**
 * Provider for cluster level client.  For now the cluster level client is used
 * mostly for admin purposes
 * 
 * @author elandau
 *
 */
public interface ClusterClientProvider {
    /**
     * Acquire a cassandra cluster by name.  Must call releaseCluster once done.
     * The concrete provider must implement it's own reference counting and
     * garbage collection to shutdown Cluster clients that are not longer in use.
     * 
     * @param clusterName
     */
    public Cluster acquireCluster(ClusterKey clusterName);

    /**
     * Release a cassandra cluster that was acquired using acquireCluster
     * 
     * @param clusterName
     */
    public void releaseCluster(ClusterKey clusterName);
}
