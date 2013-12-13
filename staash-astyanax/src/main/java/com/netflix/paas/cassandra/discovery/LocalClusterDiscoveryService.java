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
package com.netflix.paas.cassandra.discovery;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;
import com.netflix.paas.entity.ClusterEntity;
import com.netflix.paas.exceptions.AlreadyExistsException;

public class LocalClusterDiscoveryService implements ClusterDiscoveryService {
    private ConcurrentMap<String, ClusterEntity> clusters = Maps.newConcurrentMap();
    
    public void addCluster(ClusterEntity cluster) throws AlreadyExistsException {
        if (null != clusters.putIfAbsent(cluster.getName(), cluster)) {
            throw new AlreadyExistsException("cluster", cluster.getName());
        }
    }
    
    public void removeCluster(String clusterName)  {
    }
    
    @Override
    public Collection<String> getClusterNames() {
        return clusters.keySet();
    }

    @Override
    public String getName() {
        return "localhost";
    }
}
