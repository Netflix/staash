package com.netflix.paas.cassandra.discovery;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;
import com.netflix.paas.entity.ClusterEntity;
import com.netflix.paas.exceptions.AlreadyExistsException;

public class LocalClusterDiscoveryService implements ClusterDiscoveryService {
    private ConcurrentMap<String, ClusterEntity> clusters = Maps.newConcurrentMap();
    
    public void addCluster(ClusterEntity cluster) throws AlreadyExistsException {
        if (null != clusters.putIfAbsent(cluster.getName(), cluster)) {
            throw new AlreadyExistsException("cluster", cluster.getName());
        }
    }
    
    public void removeCluster(String clusterName)  {
    }
    
    @Override
    public Collection<String> getClusterNames() {
        return clusters.keySet();
    }

    @Override
    public String getName() {
        return "localhost";
    }
}
