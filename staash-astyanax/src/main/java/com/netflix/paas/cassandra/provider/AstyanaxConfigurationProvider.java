package com.netflix.paas.cassandra.provider;

import com.netflix.astyanax.AstyanaxConfiguration;

public interface AstyanaxConfigurationProvider {
    public AstyanaxConfiguration get(String name);
}
