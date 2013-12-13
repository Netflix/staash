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

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.google.common.collect.Sets;

@Entity(name="cluster")
public class ClusterEntity {
    public static class Builder {
        private ClusterEntity entity = new ClusterEntity();
        
        public Builder withName(String name) {
            entity.name = name;
            return this;
        }
        
        public Builder withNodes(Set<String> nodes) {
            entity.nodes = nodes;
            return this;
        }
        
        public Builder addNode(String node) {
            if (entity.nodes == null)
                entity.nodes = Sets.newHashSet();
            entity.nodes.add(node);
            return this;
        }
        
        public ClusterEntity build() {
            return entity;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    @Id
    @Column
    private String name;
    
    @Column
    private Set<String> nodes;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Set<String> getNodes() {
        return nodes;
    }
    public void setNodes(Set<String> nodes) {
        this.nodes = nodes;
    }
}
