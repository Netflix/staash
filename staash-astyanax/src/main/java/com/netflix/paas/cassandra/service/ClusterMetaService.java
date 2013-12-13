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
package com.netflix.paas.cassandra.service;

import com.google.common.eventbus.Subscribe;
import com.netflix.paas.cassandra.events.ClusterUpdateEvent;
import com.netflix.paas.cassandra.events.ColumnFamilyDeleteEvent;
import com.netflix.paas.cassandra.events.ColumnFamilyUpdateEvent;
import com.netflix.paas.cassandra.events.KeyspaceDeleteEvent;
import com.netflix.paas.cassandra.events.KeyspaceUpdateEvent;

public class ClusterMetaService {
    
    public ClusterMetaService() {
        
    }
    
    @Subscribe
    public void handleClusterUpdateEvent(ClusterUpdateEvent event) {
        
    }
    
    @Subscribe
    public void handleKeyspaceUpdateEvent(KeyspaceUpdateEvent event) {
        
    }
    
    @Subscribe
    public void handleKeyspaceDeleteEvent(KeyspaceDeleteEvent event) {
        
    }

    @Subscribe
    public void handleColumnFamilyUpdateEvent(ColumnFamilyUpdateEvent event) {
        
    }
    
    @Subscribe
    public void handleColumnFamilyDeleteEvent(ColumnFamilyDeleteEvent event) {
        
    }
}
