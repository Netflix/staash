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
package com.netflix.staash.mesh.endpoints;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import com.netflix.staash.mesh.CompareInstanceInfoByUuid;
import com.netflix.staash.mesh.InstanceInfo;

public class CircularEndpointPolicy implements EndpointPolicy {
    private static final CompareInstanceInfoByUuid comparator = new CompareInstanceInfoByUuid();
    private final int replicationFactor;
    
    public CircularEndpointPolicy(int replicationFactor) {
        this.replicationFactor = replicationFactor;
    }
    
    @Override
    public List<InstanceInfo> getEndpoints(InstanceInfo current, List<InstanceInfo> instances) {
        int position = Collections.binarySearch(instances, current, comparator);
        int size = instances.size();
        
        List<InstanceInfo> endpoints = Lists.newArrayListWithCapacity(replicationFactor);
        
        int count = Math.max(size-1,  replicationFactor);
        for (int i = 0; i < count; i++) {
            endpoints.add(instances.get((position + i) % size));
        }
        return endpoints;
    }
}
