package com.netflix.paas.cassandra.provider;

import com.netflix.astyanax.connectionpool.ConnectionPoolConfiguration;

public interface AstyanaxConnectionPoolConfigurationProvider {
    public ConnectionPoolConfiguration get(String name);
}
