package com.netflix.paas.meta.entity;

import java.util.ArrayList;
import java.util.List;
import com.netflix.paas.util.Pair;

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
