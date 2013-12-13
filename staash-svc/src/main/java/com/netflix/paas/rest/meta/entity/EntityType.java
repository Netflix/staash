/*******************************************************************************
 * /***
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
 ******************************************************************************/
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
