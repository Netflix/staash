package com.netflix.paas.cassandra.provider;

import com.netflix.astyanax.connectionpool.ConnectionPoolMonitor;

public interface AstyanaxConnectionPoolMonitorProvider {
    public ConnectionPoolMonitor get(String name);
}
