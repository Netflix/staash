package com.netflix.paas.events;

import com.netflix.paas.entity.DbEntity;

/**
 * Event notifying that a schema has either been added, changed or removed
 * 
 * @author elandau
 */
public class SchemaChangeEvent {
    private final DbEntity schema;
    private final boolean removed;
    
    public SchemaChangeEvent(DbEntity schema, boolean removed) {
        this.schema  = schema;
        this.removed = removed;
    }
    
    public DbEntity getSchema() {
        return this.schema;
    }
    
    public boolean isExists() {
        return removed;
    }
}
