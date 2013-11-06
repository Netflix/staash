package com.netflix.paas.cassandra.events;

import com.netflix.paas.cassandra.keys.KeyspaceKey;

public class KeyspaceUpdateEvent {
    private final KeyspaceKey keyspaceKey;

    public KeyspaceUpdateEvent(KeyspaceKey keyspaceKey) {
        super();
        this.keyspaceKey = keyspaceKey;
    }
    
    public KeyspaceKey getKeyspaceKey() {
        return keyspaceKey;
    }
}
