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
package com.netflix.paas.cassandra.provider;

import com.netflix.astyanax.Cluster;
import com.netflix.paas.cassandra.keys.ClusterKey;

/**
 * Provider for cluster level client.  For now the cluster level client is used
 * mostly for admin purposes
 * 
 * @author elandau
 *
 */
public interface ClusterClientProvider {
    /**
     * Acquire a cassandra cluster by name.  Must call releaseCluster once done.
     * The concrete provider must implement it's own reference counting and
     * garbage collection to shutdown Cluster clients that are not longer in use.
     * 
     * @param clusterName
     */
    public Cluster acquireCluster(ClusterKey clusterName);

    /**
     * Release a cassandra cluster that was acquired using acquireCluster
     * 
     * @param clusterName
     */
    public void releaseCluster(ClusterKey clusterName);
}
