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
