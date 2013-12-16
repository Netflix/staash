package com.netflix.paas.rest.meta.entity;

import com.netflix.paas.rest.util.MetaConstants;

public enum EntityType {
    STORAGE(MetaConstants.PAAS_STORAGE_TYPE_ENTITY),DB(MetaConstants.PAAS_DB_ENTITY_TYPE),TABLE(MetaConstants.PAAS_TABLE_ENTITY_TYPE),SERIES(MetaConstants.PAAS_TS_ENTITY_TYPE);
    private String id;
    EntityType(String id) {
        this.id = id;
    }
    public String getId(){
        return id;
    }
}
