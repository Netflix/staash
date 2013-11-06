package com.netflix.paas.cassandra.provider;

import java.util.List;

import com.google.common.base.Supplier;
import com.netflix.astyanax.connectionpool.Host;

public interface HostSupplierProvider {
    public Supplier<List<Host>> getSupplier(String clusterName);
}
