package com.netflix.paas.cassandra.admin;

import com.netflix.paas.cassandra.keys.ClusterKey;

public interface CassandraClusterAdminResourceFactory {
    public CassandraClusterAdminResource get(ClusterKey clusterKey);
}
