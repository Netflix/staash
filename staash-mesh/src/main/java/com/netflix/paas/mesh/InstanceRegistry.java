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
package com.netflix.paas.mesh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Registry for all active instances.  Keeps track of a sorted list of instances.
 * 
 * @author elandau
 *
 */
public class InstanceRegistry {
    private final Map<UUID, InstanceInfo> members = Maps.newHashMap();
    private final AtomicReference<List<InstanceInfo>> ring = new AtomicReference<List<InstanceInfo>>(new ArrayList<InstanceInfo>());
    private static final CompareInstanceInfoByUuid comparator = new CompareInstanceInfoByUuid();
    
    /**
     * A new instance has joined the ring
     * @param node
     */
    public synchronized void join(InstanceInfo node) {
        members.put(node.getUuid(), node);
        update();
    }
    
    /**
     * An instance was removed from the the ring
     * @param node
     */
    public synchronized void leave(InstanceInfo node) {
        members.remove(node.getUuid());
        update();
    }
    
    /**
     * Resort the ring
     */
    private void update() {
        List<InstanceInfo> list = Lists.newArrayList(members.values());
        Collections.sort(list, comparator);
        ring.set(list);
    }
    
    /**
     * Return a sorted list of InstanceInfo.  Sorted by UUID.
     * @return
     */
    public List<InstanceInfo> getMembers() {
        return ring.get();
    }
}
