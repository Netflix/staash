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
package com.netflix.paas.mesh.endpoints;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import com.netflix.paas.mesh.CompareInstanceInfoByUuid;
import com.netflix.paas.mesh.InstanceInfo;

/**
 * Return a list of endpoints that are of exponential distance from the current position
 * 
 * Example,
 * 
 * pos + 1
 * pos + 2
 * pos + 4
 * pos + 8
 * ...
 * 
 * @author elandau
 *
 */
public class ChordEndpointPolicy implements EndpointPolicy {
    private static final CompareInstanceInfoByUuid comparator = new CompareInstanceInfoByUuid();
    private static double LOG_2 = Math.log(2);
    
    @Override
    public List<InstanceInfo> getEndpoints(InstanceInfo current, List<InstanceInfo> instances) {
        int position = Collections.binarySearch(instances, current, comparator);
        int size = instances.size();
        int count = (int)Math.ceil(Math.log(size) / LOG_2);
        
        List<InstanceInfo> endpoints = Lists.newArrayListWithCapacity(count);
        
        int offset = 1;
        for (int i = 0; i < count; i++) {
            endpoints.add(instances.get((position + offset) % size));
            offset *= 2;
        }
        return endpoints;
    }

}
