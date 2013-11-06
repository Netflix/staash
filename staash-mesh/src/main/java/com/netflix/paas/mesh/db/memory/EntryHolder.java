package com.netflix.paas.mesh.db.memory;

import com.netflix.paas.mesh.db.Entry;

public class EntryHolder {
    private Entry tuple;
    
    public EntryHolder(Entry tuple) {
        this.tuple = tuple;
    }
    
    public synchronized boolean delete(long timestamp) {
        if (timestamp > tuple.getTimestamp()) {
            this.tuple = new Entry(tuple.getKey(), null, timestamp);
            return true;
        }
        return false;
    }
    
    public synchronized boolean set(Entry tuple) {
        if (tuple.getTimestamp() > this.tuple.getTimestamp()) {
            this.tuple = tuple;
            return true;
        }
        return false;
    }
    
    public synchronized boolean isDeleted() {
        return this.tuple.getValue() == null;
    }
    
    public synchronized Entry getEntry() {
        return tuple;
    }
}
