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
package com.netflix.paas.cassandra.keys;

import org.apache.commons.lang.StringUtils;

public class ColumnFamilyKey {
    private final KeyspaceKey keyspaceKey;
    private final String columnFamilyName;
    
    public ColumnFamilyKey(KeyspaceKey keyspaceKey, String columnFamilyName) {
        super();
        this.keyspaceKey = keyspaceKey;
        this.columnFamilyName = columnFamilyName;
    }
    
    public ColumnFamilyKey(ClusterKey clusterKey, String keyspaceName, String columnFamilyName) {
        this.keyspaceKey = new KeyspaceKey(clusterKey, keyspaceName);
        this.columnFamilyName = columnFamilyName;
    }
    
    public KeyspaceKey getKeyspaceKey() {
        return keyspaceKey;
    }
    
    public String getColumnFamilyName() {
        return columnFamilyName;
    }
    
    public String getCanonicalName() {
        return StringUtils.join(new String[]{keyspaceKey.getCanonicalName(), columnFamilyName}, ".");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((columnFamilyName == null) ? 0 : columnFamilyName.hashCode());
        result = prime * result
                + ((keyspaceKey == null) ? 0 : keyspaceKey.hashCode());
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
        ColumnFamilyKey other = (ColumnFamilyKey) obj;
        if (columnFamilyName == null) {
            if (other.columnFamilyName != null)
                return false;
        } else if (!columnFamilyName.equals(other.columnFamilyName))
            return false;
        if (keyspaceKey == null) {
            if (other.keyspaceKey != null)
                return false;
        } else if (!keyspaceKey.equals(other.keyspaceKey))
            return false;
        return true;
    }
}
