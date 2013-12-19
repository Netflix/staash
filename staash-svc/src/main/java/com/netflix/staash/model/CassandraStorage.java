/*******************************************************************************
 * /*
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
 *  *
 ******************************************************************************/
package com.netflix.staash.model;

import com.netflix.staash.json.JsonObject;

public class CassandraStorage extends Storage{
    private String cluster;
    public CassandraStorage(JsonObject conf) {
        this.name = conf.getString("name");
        this.cluster = conf.getString("cluster");
        this.replicateTo = conf.getString("replicateto");
        this.type = StorageType.CASSANDRA;
    }
    public String getName() {
        return name;
    }
    public String getCluster() {
        return cluster;
    }
    public StorageType getType() {
        return type;
    }
    public String getReplicateTo() {
        return replicateTo;
    }
}
