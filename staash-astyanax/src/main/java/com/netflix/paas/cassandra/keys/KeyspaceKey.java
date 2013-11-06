package com.netflix.paas.cassandra.keys;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

/**
 * Global unique keyspace identifier
 * @author elandau
 *
 */
public class KeyspaceKey {
    private final ClusterKey clusterKey;
    
    private final String keyspaceName;
    private final String schemaName;
    
    public KeyspaceKey(String schemaName) {
        String parts[] = StringUtils.split(schemaName, ".");
        Preconditions.checkState(parts.length == 2, String.format("Schema name must have format <cluster>.<keyspace> ('%s')", schemaName));
        
        this.clusterKey    = new ClusterKey(parts[0], null);    // TODO
        this.keyspaceName  = parts[1];
        this.schemaName    = schemaName;
    }
    
    public KeyspaceKey(ClusterKey clusterKey, String keyspaceName) {
        this.clusterKey    = clusterKey;
        this.keyspaceName  = keyspaceName;
        this.schemaName    = StringUtils.join(new String[]{clusterKey.getClusterName(), keyspaceName}, ".");
    }
    
    public ClusterKey getClusterKey() {
        return clusterKey;
    }
    
    public String getClusterName() {
        return clusterKey.getClusterName();
    }
    
    public String getKeyspaceName() {
        return this.keyspaceName;
    }
    
    public String getDiscoveryType() {
        return this.clusterKey.getDiscoveryType();
    }
    
    public String getSchemaName() {
        return this.schemaName;
    }
    
    public String getCanonicalName() {
        return StringUtils.join(new String[]{this.clusterKey.getCanonicalName(), getKeyspaceName()}, ".");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((clusterKey == null) ? 0 : clusterKey.hashCode());
        result = prime * result
                + ((keyspaceName == null) ? 0 : keyspaceName.hashCode());
        result = prime * result
                + ((schemaName == null) ? 0 : schemaName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        KeyspaceKey other = (KeyspaceKey) obj;
        if (clusterKey == null) {
            if (other.clusterKey != null)
                return false;
        } else if (!clusterKey.equals(other.clusterKey))
            return false;
        if (keyspaceName == null) {
            if (other.keyspaceName != null)
                return false;
        } else if (!keyspaceName.equals(other.keyspaceName))
            return false;
        if (schemaName == null) {
            if (other.schemaName != null)
                return false;
        } else if (!schemaName.equals(other.schemaName))
            return false;
        return true;
    }

}
