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
