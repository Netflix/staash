package com.netflix.paas.cassandra.provider.impl;

import com.netflix.astyanax.connectionpool.ConnectionPoolConfiguration;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.paas.cassandra.provider.AstyanaxConnectionPoolConfigurationProvider;

public class DefaultAstyanaxConnectionPoolConfigurationProvider implements AstyanaxConnectionPoolConfigurationProvider {
    @Override
    public ConnectionPoolConfiguration get(String name) {
        return new ConnectionPoolConfigurationImpl(name);
    }
}
