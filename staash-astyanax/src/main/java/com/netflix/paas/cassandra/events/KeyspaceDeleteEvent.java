package com.netflix.paas.cassandra.events;

import com.netflix.paas.cassandra.keys.KeyspaceKey;

public class KeyspaceDeleteEvent {
    private final KeyspaceKey keyspaceKey;

    public KeyspaceDeleteEvent(KeyspaceKey keyspaceKey) {
        super();
        this.keyspaceKey = keyspaceKey;
    }
    
    public KeyspaceKey getKeyspaceKey() {
        return keyspaceKey;
    }
}
