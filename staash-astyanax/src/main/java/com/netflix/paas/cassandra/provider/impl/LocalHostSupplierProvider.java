/*******************************************************************************
 * /***
 *  *
 *  *  Copyright 2013 Netflix, Inc.
 *  *
 *  *     Licensed under the Apache License, Version 2.0 (the "License");
 *  *     you may not use this file except in compliance with the License.
 *  *     You may obtain a copy of the License at
 *  *
 *  *         http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *     Unless required by applicable law or agreed to in writing, software
 *  *     distributed under the License is distributed on an "AS IS" BASIS,
 *  *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *     See the License for the specific language governing permissions and
 *  *     limitations under the License.
 *  *
 ******************************************************************************/
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
