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
package com.netflix.paas.cassandra.entity;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;

import com.google.common.base.Preconditions;

@Entity
public class ColumnFamilyEntity {
    
    public static class Builder {
        private final ColumnFamilyEntity entity = new ColumnFamilyEntity();
        
        public Builder withName(String name) {
            entity.name = name;
            return this;
        }
        
        public Builder withKeyspace(String name) {
            entity.keyspaceName = name;
            return this;
        }
        
        public Builder withCluster(String name) {
            entity.clusterName = name;
            return this;
        }
        
        public Builder withOptions(Map<String, String> options) {
            entity.setOptions(options);
            return this;
        }
        
        public ColumnFamilyEntity build() {
            Preconditions.checkNotNull(entity.name);
            
            return this.entity;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    @PrePersist
    private void prePersist() {
        this.id = String.format("%s.%s.%s", clusterName, keyspaceName, name);
    }
    
    @PostLoad
    private void postLoad() {
        this.id = String.format("%s.%s.%s", clusterName, keyspaceName, name);
    }
    
    @Id
    private String id;
    
    @Column(name="name")
    private String name;
    
    @Column(name="keyspace")
    private String keyspaceName;
    
    @Column(name="cluster")
    private String clusterName;
    
    /**
     * Low level Cassandra column family configuration parameters
     */
    @Column(name="options")
    private Map<String, String> options;

    public String getKeyspaceName() {
        return keyspaceName;
    }

    public void setKeyspaceName(String keyspaceName) {
        this.keyspaceName = keyspaceName;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }
}
