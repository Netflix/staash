package com.netflix.paas.rest.meta.entity;

public  class Entity {
    protected String rowKey;    
    protected String name;    
    protected String payLoad;
        
    public String getRowKey() {
        return rowKey;
    }

    public String getName() {
        return name;
    }
    protected void setRowKey(String rowkey) {
        this.rowKey = rowkey;
    }
    protected void setName(String name) {
        this.name = name;
    }

    public String getPayLoad() {
        return payLoad;
    }

    protected void setPayLoad(String payLoad) {
        this.payLoad = payLoad;
    }
}
