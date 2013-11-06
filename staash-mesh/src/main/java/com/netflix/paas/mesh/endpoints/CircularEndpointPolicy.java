package com.netflix.paas.mesh.endpoints;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import com.netflix.paas.mesh.CompareInstanceInfoByUuid;
import com.netflix.paas.mesh.InstanceInfo;

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