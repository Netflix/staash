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
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Transient;
import com.google.common.collect.Maps;

/**
 * Definition of a global schema in the system.  A schema may contain
 * tables that are implemented using different technologies.
 * 
 * @author elandau
 */
@Entity(name="db")
public class DbEntity {
    
    public static class Builder {
        private DbEntity entity = new DbEntity();
        
        public Builder withName(String name) {
            entity.name = name;
            return this;
        }
        
        public Builder addTable(TableEntity table) throws Exception {
            if (null == entity.getTables()) {
                entity.tables = Maps.newHashMap();
            }
            entity.tables.put(table.getTableName(), table);
            return this;
        }
        
        public Builder withOptions(Map<String, String> options) {
            entity.options = options;
            return this;
        }
        
        public Builder addOption(String name, String value) {
            if (null == entity.getOptions()) {
                entity.options = Maps.newHashMap();
            }
            entity.options.put(name, value);
            return this;
        }
        
        public DbEntity build() {
            return entity;
        }
        
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    @Id
    private String name;
    
    @Column
    private Set<String> tableNames;
    
    @Column
    private Map<String, String> options;
    
    @Transient
    private Map<String, TableEntity> tables;
    
    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    @PrePersist
    private void prePersist() {
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public void setTables(Map<String, TableEntity> tables) {
        this.tables = tables;
    }
    
    public Map<String, TableEntity> getTables() {
        return tables;
    }

    public void setTableNames(Set<String> tableNames) {
        this.tableNames = tableNames;
    }

    public Set<String> getTableNames() {
        return tableNames;
    }

    public boolean hasTables() {
        return this.tables != null && !this.tables.isEmpty();
    }

    @Override
    public String toString() {
        return "SchemaEntity [name=" + name + ", tables=" + tables + "]";
    }

}
