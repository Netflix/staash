package com.netflix.paas.cassandra.provider;

import com.netflix.astyanax.Cluster;
import com.netflix.paas.cassandra.keys.ClusterKey;

public class LocalClusterClientProvider implements ClusterClientProvider {
    @Override
    public Cluster acquireCluster(ClusterKey clusterName) {
        
        return null;
    }

    @Override
    public void releaseCluster(ClusterKey clusterName) {
    }
}
