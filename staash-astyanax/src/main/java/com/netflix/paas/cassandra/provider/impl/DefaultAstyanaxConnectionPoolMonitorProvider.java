package com.netflix.paas.cassandra.provider.impl;

import com.netflix.astyanax.connectionpool.ConnectionPoolMonitor;
import com.netflix.astyanax.connectionpool.impl.Slf4jConnectionPoolMonitorImpl;
import com.netflix.paas.cassandra.provider.AstyanaxConnectionPoolMonitorProvider;

public class DefaultAstyanaxConnectionPoolMonitorProvider implements AstyanaxConnectionPoolMonitorProvider {

    @Override
    public ConnectionPoolMonitor get(String name) {
        return new Slf4jConnectionPoolMonitorImpl();
    }

}
