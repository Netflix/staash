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

public class ClusterKey {
    private final String clusterName;
    
    private final String discoveryType;
    
    public ClusterKey(String clusterName, String discoveryType) {
        this.clusterName = clusterName;
        this.discoveryType = discoveryType;
    }
    
    public String getDiscoveryType() {
        return discoveryType;
    }

    public String getClusterName() {
        return this.clusterName;
    }
    
    public String getCanonicalName() {
        return StringUtils.join(new String[]{this.discoveryType, this.clusterName}, ".");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((clusterName == null) ? 0 : clusterName.hashCode());
        result = prime * result
                + ((discoveryType == null) ? 0 : discoveryType.hashCode());
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
        ClusterKey other = (ClusterKey) obj;
        if (clusterName == null) {
            if (other.clusterName != null)
                return false;
        } else if (!clusterName.equals(other.clusterName))
            return false;
        if (discoveryType == null) {
            if (other.discoveryType != null)
                return false;
        } else if (!discoveryType.equals(other.discoveryType))
            return false;
        return true;
    }
}
