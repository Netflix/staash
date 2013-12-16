package com.netflix.paas.model;

public enum StorageType {
    CASSANDRA("cassandra"),MYSQL("mysql");
    private String cannonicalName;
    StorageType(String cannonicalName) {
        this.cannonicalName = cannonicalName;
    }
    public String getCannonicalName() {
        return cannonicalName;
    }
}
