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
package com.netflix.paas.dao.astyanax;

import com.netflix.paas.SchemaNames;
import com.netflix.paas.cassandra.entity.CassandraClusterEntity;
import com.netflix.paas.cassandra.entity.ColumnFamilyEntity;
import com.netflix.paas.cassandra.entity.KeyspaceEntity;
import com.netflix.paas.dao.DaoKey;
import com.netflix.paas.entity.ClusterEntity;

public class DaoKeys {
    public final static DaoKey<KeyspaceEntity>          DAO_KEYSPACE_ENTITY      
        = new DaoKey<KeyspaceEntity>        (SchemaNames.CONFIGURATION.name(), KeyspaceEntity.class);

    public final static DaoKey<CassandraClusterEntity>  DAO_CASSANDRA_CLUSTER_ENTITY       
        = new DaoKey<CassandraClusterEntity>(SchemaNames.CONFIGURATION.name(), CassandraClusterEntity.class);
    
    public final static DaoKey<ColumnFamilyEntity>      DAO_COLUMN_FAMILY_ENTITY 
        = new DaoKey<ColumnFamilyEntity>    (SchemaNames.CONFIGURATION.name(), ColumnFamilyEntity.class);
}
