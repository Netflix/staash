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
package com.netflix.paas.entity;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.google.common.collect.Maps;

/**
 * Definition for a table in the system.  This definition provides this system will
 * all the information for connecting to the target persistence implementation
 * 
 * @author elandau
 *
 */
@Entity
public class TableEntity {
    public static class Builder {
        private TableEntity entity = new TableEntity();
        
        public Builder withStorageType(String type) {
            entity.storageType = type;
            return this;
        }

        public Builder withTableName(String tableName) {
            entity.tableName = tableName;
            return this;
        }
        
        public Builder withSchemaName(String schema) {
            entity.schema = schema;
            return this;
        }
        public Builder withOptions(Map<String, String> options) {
            entity.options = options;
            return this;
        }
        
        public Builder withOption(String key, String value) {
            if (entity.options == null) {
                entity.options = Maps.newHashMap();
            }
            
            entity.options.put(key,  value);
            return this;
        }
        public TableEntity build() {
            return entity;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Unique table name within the schema
     */
    @Id
    private String tableName;
    
    /**
     * Type of storage for this table.  (ex. cassandra)
     */
    @Column(name="type")
    private String storageType;
    
    /**
     * Parent schema
     */
    @Column(name="schema")
    private String schema;
    
    /**
     * Additional configuration options for the table.  These parameters are normally
     * specific to the underlying persistence technology
     */
    @Column(name="options")
    private Map<String, String> options;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }
    
    public String getOption(String option) {
        return getOption(option, null);
    }
    
    public String getOption(String key, String defaultValue) {
        if (this.options == null)
            return defaultValue;
        String value = options.get(key);
        if (value == null)
            return defaultValue;
        return value;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    @Override
    public String toString() {
        return "VirtualTableEntity [tableName=" + tableName + ", storageType=" + storageType + ", schema=" + schema + ", options="
                + options + "]";
    }
    
}
