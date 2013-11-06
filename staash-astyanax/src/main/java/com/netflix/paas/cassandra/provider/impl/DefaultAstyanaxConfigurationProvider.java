package com.netflix.paas.cassandra.provider.impl;

import com.netflix.astyanax.AstyanaxConfiguration;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolType;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.paas.cassandra.provider.AstyanaxConfigurationProvider;

public class DefaultAstyanaxConfigurationProvider implements AstyanaxConfigurationProvider {

    @Override
    public AstyanaxConfiguration get(String name) {
        return new AstyanaxConfigurationImpl()
            .setDiscoveryType(NodeDiscoveryType.NONE)
            .setConnectionPoolType(ConnectionPoolType.ROUND_ROBIN)
            .setDiscoveryDelayInSeconds(60000)
            .setCqlVersion("3.0.0");
    }

}
