package com.netflix.paas.cassandra.discovery;

import java.util.Collection;

/**
 * Abstraction for service that keeps track of clusters.  These clusters are normally stored
 * in a some naming service.  The implementation handles any custom exclusions.
 * 
 * @author elandau
 */
public interface ClusterDiscoveryService {
    /**
     * @return Return the complete list of clusters
     */
    public Collection<String> getClusterNames();
    
    /**
     * Return the name of this cluster service
     * @return
     */
    String getName();
}
