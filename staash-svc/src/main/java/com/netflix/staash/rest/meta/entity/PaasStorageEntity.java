/*******************************************************************************
 * /*
 *  *
 *  *  Copyright 2013 Netflix, Inc.
 *  *
 *  *     Licensed under the Apache License, Version 2.0 (the "License");
 *  *     you may not use this file except in compliance with the License.
 *  *     You may obtain a copy of the License at
 *  *
 *  *         http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *     Unless required by applicable law or agreed to in writing, software
 *  *     distributed under the License is distributed on an "AS IS" BASIS,
 *  *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *     See the License for the specific language governing permissions and
 *  *     limitations under the License.
 *  *
 *  *
 ******************************************************************************/
package com.netflix.staash.rest.meta.entity;

import com.netflix.staash.json.JsonObject;
import com.netflix.staash.rest.util.MetaConstants;

public class PaasStorageEntity extends Entity{
    public static class Builder {
        private PaasStorageEntity entity = new PaasStorageEntity();
        
        public Builder withJsonPayLoad(JsonObject payLoad) {
            entity.setRowKey(MetaConstants.STAASH_STORAGE_TYPE_ENTITY);
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
