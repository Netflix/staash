package com.netflix.paas.cassandra.entity;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.Transient;

import com.google.common.base.Preconditions;
import com.netflix.paas.exceptions.NotFoundException;

@Entity
public class KeyspaceEntity {
    public static class Builder {
        private final KeyspaceEntity entity = new KeyspaceEntity();
        
        public Builder withName(String name) {
            entity.name = name;
            return this;
        }
        
        public Builder addColumnFamily(String columnFamilyName) {
            if (entity.getColumnFamilies() == null) {
                entity.setColumnFamilies(new HashSet<String>());
            }
            entity.getColumnFamilies().add(columnFamilyName);
            return this;
        }
        
        public Builder withOptions(Map<String, String> options) {
            entity.setOptions(options);
            return this;
        }
        
        public KeyspaceEntity build() {
            return this.entity;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    @Id
    private String id;
    
    @Column(name="name")
    private String name;
    
    @Column(name="cluster")
    private String clusterName;
    
    @Column(name="options")
    private Map<String, String> options;
    
    @Column(name="cfs")
    private Set<String> columnFamilies;

    @Transient
    private Map<String, ColumnFamilyEntity> columnFamilyEntities;
    
    @PrePersist
    private void prePersist() {
        this.id = String.format("%s.%s", clusterName, name);
    }
    
    @PostLoad
    private void postLoad() {
        this.id = String.format("%s.%s", clusterName, name);
    }
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, ColumnFamilyEntity> getColumnFamilyEntities() {
        return columnFamilyEntities;
    }

    public void setColumnFamilyEntities(Map<String, ColumnFamilyEntity> columnFamilyEntities) {
        this.columnFamilyEntities = columnFamilyEntities;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public Set<String> getColumnFamilies() {
        return columnFamilies;
    }

    public void setColumnFamilies(Set<String> columnFamilies) {
        this.columnFamilies = columnFamilies;
    }

    public ColumnFamilyEntity getColumnFamily(String columnFamilyName) throws NotFoundException {
        ColumnFamilyEntity entity = columnFamilyEntities.get(columnFamilyName);
        if (entity == null)
            throw new NotFoundException("columnfamily", columnFamilyName);
        return entity;
    }

}
