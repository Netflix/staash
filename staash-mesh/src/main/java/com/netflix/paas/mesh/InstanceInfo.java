package com.netflix.paas.mesh;

import java.util.UUID;

public class InstanceInfo {
    private final UUID uuid;
    private final String id;
    
    public InstanceInfo(String id, UUID uuid)  {
        this.uuid = uuid;        
        this.id   = id;
    }
    
    public UUID getUuid() {
        return uuid;
    }

    public String getId() {
        return id;
    }
    
    public String toString() {
        return uuid.toString() + "(" + id + ")";
    }
}
