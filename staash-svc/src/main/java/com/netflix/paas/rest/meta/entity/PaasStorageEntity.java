package com.netflix.paas.rest.meta.entity;

import com.netflix.paas.json.JsonObject;
import com.netflix.paas.rest.meta.entity.PaasDBEntity.Builder;
import com.netflix.paas.rest.util.MetaConstants;

public class PaasStorageEntity extends Entity{
    public static class Builder {
        private PaasStorageEntity entity = new PaasStorageEntity();
        
        public Builder withJsonPayLoad(JsonObject payLoad) {
            entity.setRowKey(MetaConstants.PAAS_STORAGE_TYPE_ENTITY);
            String payLoadName = payLoad.getString("name");
            String load = payLoad.toString();
            entity.setName(payLoadName);
            entity.setPayLoad(load);
            return this;
        }                
        public PaasStorageEntity build() {
            return entity;
        }        
    }    
    public static Builder builder() {
        return new Builder();
    }

}
