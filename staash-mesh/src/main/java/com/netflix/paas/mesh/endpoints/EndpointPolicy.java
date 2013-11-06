package com.netflix.paas.mesh.endpoints;

import java.util.List;

import com.netflix.paas.mesh.InstanceInfo;

public interface EndpointPolicy {
    List<InstanceInfo> getEndpoints(InstanceInfo current, List<InstanceInfo> instances);
}
