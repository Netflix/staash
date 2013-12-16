package com.netflix.paas.model;

import com.netflix.paas.json.JsonObject;

public class CassandraStorage extends Storage{
    private String cluster;
    public CassandraStorage(JsonObject conf) {
        this.name = conf.getString("name");
        this.cluster = conf.getString("cluster");
        this.replicateTo = conf.getString("replicateto");
        this.type = StorageType.CASSANDRA;
    }
    public String getName() {
        return name;
    }
    public String getCluster() {
        return cluster;
    }
    public StorageType getType() {
        return type;
    }
    public String getReplicateTo() {
        return replicateTo;
    }
}
