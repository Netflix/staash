package com.netflix.paas.cassandra.provider.impl;

import java.util.List;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.netflix.astyanax.connectionpool.Host;
import com.netflix.paas.cassandra.provider.HostSupplierProvider;

public class LocalHostSupplierProvider implements HostSupplierProvider {
    private final List<Host> localhost;
    
    public LocalHostSupplierProvider() {
        localhost = Lists.newArrayList(new Host("localhost", 9160));
    }
    
    @Override
    public Supplier<List<Host>> getSupplier(String clusterName) {
        return Suppliers.ofInstance(localhost);
    }
}
