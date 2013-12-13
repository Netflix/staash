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

/**
 * Abstraction for service that keeps track of clusters.  These clusters are normally stored
 * in a some naming service.  The implementation handles any custom exclusions.
 * 
 * @author elandau
 */
public interface ClusterDiscoveryService {
    /**
     * @return Return the complete list of clusters
     */
    public Collection<String> getClusterNames();
    
    /**
     * Return the name of this cluster service
     * @return
     */
    String getName();
}
