package com.netflix.paas.cassandra.provider;

//import org.mortbay.log.Log;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.netflix.astyanax.Keyspace;
import com.netflix.paas.cassandra.keys.ClusterKey;
import com.netflix.paas.cassandra.keys.KeyspaceKey;
import com.netflix.paas.cassandra.resources.AstyanaxThriftDataTableResource;
import com.netflix.paas.entity.TableEntity;
import com.netflix.paas.exceptions.NotFoundException;
import com.netflix.paas.provider.TableDataResourceFactory;
import com.netflix.paas.resources.TableDataResource;

public class CassandraTableResourceFactory implements TableDataResourceFactory {
    private final KeyspaceClientProvider clientProvider;
    
    @Inject
    public CassandraTableResourceFactory(KeyspaceClientProvider clientProvider) {
        this.clientProvider = clientProvider;
    }
    
    @Override
    public TableDataResource getTableDataResource(TableEntity table) throws NotFoundException {
        //Log.info(table.toString());
        
        String clusterName      = table.getOption("cassandra.cluster");
        String keyspaceName     = table.getOption("cassandra.keyspace");
        String columnFamilyName = table.getOption("cassandra.columnfamily");
        String discoveryType    = table.getOption("discovery");
        if (discoveryType == null)
            discoveryType = "eureka";
        
        Preconditions.checkNotNull(clusterName,      "Must specify cluster name for table "       + table.getTableName());
        Preconditions.checkNotNull(keyspaceName,     "Must specify keyspace name for table "      + table.getTableName());
        Preconditions.checkNotNull(columnFamilyName, "Must specify column family name for table " + table.getTableName());
        Preconditions.checkNotNull(discoveryType,    "Must specify discovery type for table "     + table.getTableName());
        
        Keyspace keyspace = clientProvider.acquireKeyspace(new KeyspaceKey(new ClusterKey(clusterName, discoveryType), keyspaceName));
        
        return new AstyanaxThriftDataTableResource(keyspace, columnFamilyName);
    }

}
