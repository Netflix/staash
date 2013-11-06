package com.netflix.paas.cassandra.entity;

import java.util.Collection;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

@Entity
public class CassandraClusterEntity {
    public static class Builder {
        private final CassandraClusterEntity entity = new CassandraClusterEntity();
        
        public Builder withName(String name) {
            entity.clusterName = name;
            return this;
        }
        
        public Builder withIsEnabled(Boolean enabled) {
            entity.enabled = enabled;
            return this;
        }
        
        public Builder withDiscoveryType(String discoveryType) {
            entity.discoveryType = discoveryType;
            return this;
        }
        
        public CassandraClusterEntity build() {
            Preconditions.checkNotNull(entity.clusterName);
            
            return this.entity;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    @Id
    private String clusterName;
    
    @Column
    private Map<String, String> keyspaces;
    
    @Column
    private Map<String, String> columnFamilies;
    
    @Column(name="enabled")
    private boolean enabled = true;

    @Column(name="discovery")
    private String discoveryType;
    
    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public Map<String, String> getKeyspaces() {
        return keyspaces;
    }

    public void setKeyspaces(Map<String, String> keyspaces) {
        this.keyspaces = keyspaces;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Collection<String> getColumnFamilyNames() {
        if (this.columnFamilies == null)
            return Lists.newArrayList();
        return columnFamilies.keySet();
    }
    
    public Collection<String> getKeyspaceNames() {
        if (this.keyspaces == null)
            return Lists.newArrayList();
        return keyspaces.keySet();
    }

    public Collection<String> getKeyspaceColumnFamilyNames(final String keyspaceName) {
        return Collections2.filter(this.columnFamilies.keySet(), new Predicate<String>() {
            @Override
            public boolean apply(String cfName) {
                return StringUtils.startsWith(cfName, keyspaceName + "|");
            }
        });
    }
    
    public Map<String, String> getColumnFamilies() {
        return columnFamilies;
    }
    
    public void setColumnFamilies(Map<String, String> columnFamilies) {
        this.columnFamilies = columnFamilies;
    }

    public String getDiscoveryType() {
        return discoveryType;
    }

    public void setDiscoveryType(String discoveryType) {
        this.discoveryType = discoveryType;
    }

}
