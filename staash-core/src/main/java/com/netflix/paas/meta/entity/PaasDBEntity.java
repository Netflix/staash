package com.netflix.paas.meta.entity;

import com.netflix.paas.json.JsonObject;
import com.netflix.paas.meta.impl.MetaConstants;
import com.netflix.paas.util.Pair;

public class PaasDBEntity extends Entity{
    public static class Builder {
        private PaasDBEntity entity = new PaasDBEntity();
        
        public Builder withJsonPayLoad(JsonObject payLoad) {
            entity.setRowKey(MetaConstants.PAAS_DB_ENTITY_TYPE);
            String payLoadName = payLoad.getString("name");
            String load = payLoad.toString();
            entity.setName(payLoadName);
            entity.setPayLoad(load);
//            Pair<String, String> p = new Pair<String, String>(payLoadName, load);
//            entity.addColumn(p);
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
