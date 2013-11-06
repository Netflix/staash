package com.netflix.paas.cassandra.events;

import com.netflix.paas.cassandra.keys.ClusterKey;

public class ClusterUpdateEvent {
    private final ClusterKey clusterKey;

    public ClusterUpdateEvent(ClusterKey clusterKey) {
        super();
        this.clusterKey = clusterKey;
    }

    public ClusterKey getClusterKey() {
        return clusterKey;
    }
    
    
}
