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
