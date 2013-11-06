package com.netflix.paas.entity;

import java.util.Collection;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.google.common.collect.Sets;

@Entity
public class PassGroupConfigEntity {
    public static class Builder {
        private PassGroupConfigEntity entity = new PassGroupConfigEntity();
        
        public Builder withName(String name) {
            entity.deploymentName = name;
            return this;
        }
        
        public Builder withSchemas(Collection<String> names) {
            if (entity.schemas == null) {
                entity.schemas = Sets.newHashSet();
            }
            entity.schemas.addAll(names);
            return this;
        }
        
        public Builder addSchema(String vschemaName) {
            if (entity.schemas == null) {
                entity.schemas = Sets.newHashSet();
            }
            entity.schemas.add(vschemaName);
            return this;
        }
        
        public PassGroupConfigEntity build() {
            return entity;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    @Id
    private String deploymentName;
    
    @Column
    private Set<String> schemas;

    public String getDeploymentName() {
        return deploymentName;
    }

    public void setDeploymentName(String deploymentName) {
        this.deploymentName = deploymentName;
    }

    public Set<String> getSchemas() {
        return schemas;
    }

    public void setSchemas(Set<String> schemas) {
        this.schemas = schemas;
    }

    @Override
    public String toString() {
        return "PassGroupConfigEntity [deploymentName=" + deploymentName + ", schemas=" + schemas + "]";
    }
    
}
