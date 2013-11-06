package com.netflix.paas.mesh.db;

public class Entry {
    private String key;
    private String value;
    private long   timestamp;
    
    public Entry(String key, String value, long timestamp) {
        this.key = key;
        this.value = value;
        this.timestamp = timestamp;
    }
    
    public Entry(String key, String value) {
        this.key = key;
        this.value = value;
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getKey() {
        return key;
    }
    
    public String getValue() {
        return value;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public boolean isDeleted() {
        return this.value == null;
    }
}
