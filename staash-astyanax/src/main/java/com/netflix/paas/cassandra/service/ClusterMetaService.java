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
