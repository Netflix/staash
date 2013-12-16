package com.netflix.paas.rest.meta.entity;

import com.netflix.paas.json.JsonObject;
import com.netflix.paas.rest.util.MetaConstants;



public class PaasDBEntity extends Entity{
    public static class Builder {
        private PaasDBEntity entity = new PaasDBEntity();
        
        public Builder withJsonPayLoad(JsonObject payLoad) {
            entity.setRowKey(MetaConstants.PAAS_DB_ENTITY_TYPE);
            String payLoadName = payLoad.getString("name");
            String load = payLoad.toString();
            entity.setName(payLoadName);
            entity.setPayLoad(load);
            return this;
        }                
        public PaasDBEntity build() {
            return entity;
        }        
    }    
    public static Builder builder() {
        return new Builder();
    }
}
