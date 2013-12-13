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
package com.netflix.paas.events;

import com.netflix.paas.entity.DbEntity;

/**
 * Event notifying that a schema has either been added, changed or removed
 * 
 * @author elandau
 */
public class SchemaChangeEvent {
    private final DbEntity schema;
    private final boolean removed;
    
    public SchemaChangeEvent(DbEntity schema, boolean removed) {
        this.schema  = schema;
        this.removed = removed;
    }
    
    public DbEntity getSchema() {
        return this.schema;
    }
    
    public boolean isExists() {
        return removed;
    }
}
