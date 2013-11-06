package com.netflix.paas.cassandra.tasks;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.inject.Inject;
import com.netflix.astyanax.Cluster;
import com.netflix.astyanax.ddl.ColumnDefinition;
import com.netflix.astyanax.ddl.ColumnFamilyDefinition;
import com.netflix.astyanax.ddl.FieldMetadata;
import com.netflix.astyanax.ddl.KeyspaceDefinition;
import com.netflix.paas.JsonSerializer;
import com.netflix.paas.SchemaNames;
import com.netflix.paas.cassandra.entity.CassandraClusterEntity;
import com.netflix.paas.cassandra.entity.MapStringToObject;
import com.netflix.paas.cassandra.keys.ClusterKey;
import com.netflix.paas.cassandra.provider.ClusterClientProvider;
import com.netflix.paas.dao.Dao;
import com.netflix.paas.dao.DaoProvider;
import com.netflix.paas.tasks.Task;
import com.netflix.paas.tasks.TaskContext;

/**
 * Refresh the information for a cluster
 * 
 * @author elandau
 *
 */
public class ClusterRefreshTask implements Task {
    private static Logger LOG = LoggerFactory.getLogger(ClusterRefreshTask.class);
    
    private final ClusterClientProvider       provider;
    private final Dao<CassandraClusterEntity> clusterDao;
    
    @Inject
    public ClusterRefreshTask(ClusterClientProvider provider, DaoProvider daoProvider) throws Exception {
        this.provider        = provider;
        this.clusterDao      = daoProvider.getDao(SchemaNames.CONFIGURATION.name(), CassandraClusterEntity.class);
    }

    @Override
    public void execte(TaskContext context) throws Exception{
        // Get parameters from the context
        String clusterName = context.getStringParameter("cluster");
        Boolean ignoreSystem = context.getBooleanParameter("ignoreSystem", true);
        CassandraClusterEntity entity = (CassandraClusterEntity)context.getParamater("entity");
        
        LOG.info("Refreshing cluster " + clusterName);
        
        // Read the current state from the DAO
//        CassandraClusterEntity entity = clusterDao.read(clusterName);
        
        Map<String, String> existingKeyspaces = entity.getKeyspaces();
        if (existingKeyspaces == null) {
            existingKeyspaces = Maps.newHashMap();
            entity.setKeyspaces(existingKeyspaces);
        }
        
        Map<String, String> existingColumnFamilies = entity.getColumnFamilies();
        if (existingColumnFamilies == null) {
            existingColumnFamilies = Maps.newHashMap();
            entity.setColumnFamilies(existingColumnFamilies);
        }
        
        Set<String> foundKeyspaces      = Sets.newHashSet();
        Set<String> foundColumnFamilies = Sets.newHashSet();
        
        Cluster cluster = provider.acquireCluster(new ClusterKey(entity.getClusterName(), entity.getDiscoveryType()));
        
        boolean changed = false;
        
//        // Iterate found keyspaces
        try {
            for (KeyspaceDefinition keyspace : cluster.describeKeyspaces()) {
                // Extract data from the KeyspaceDefinition
                String ksName = keyspace.getName();
                MapStringToObject keyspaceOptions = getKeyspaceOptions(keyspace);
                
                if (existingKeyspaces.containsKey(ksName)) {
                    MapStringToObject previousOptions = JsonSerializer.fromString(existingKeyspaces.get(ksName), MapStringToObject.class);
                    MapDifference keyspaceDiff = Maps.difference(keyspaceOptions, previousOptions);
                    if (keyspaceDiff.areEqual()) {
                        LOG.info("Keyspace '{}' didn't change", new Object[]{ksName});
                    }
                    else {
                        changed = true;
                        LOG.info("CF Changed: " + keyspaceDiff.entriesDiffering());
                    }
                }
                else {
                    changed = true;
                }
                String strKeyspaceOptions = JsonSerializer.toString(keyspaceOptions);
                
//                // Keep track of keyspace
                foundKeyspaces.add(keyspace.getName());
                existingKeyspaces.put(ksName, strKeyspaceOptions);
                
                LOG.info("Found keyspace '{}|{}' : {}", new Object[]{entity.getClusterName(), ksName, keyspaceOptions});
                
//                // Iterate found column families
                for (ColumnFamilyDefinition cf : keyspace.getColumnFamilyList()) {
                    // Extract data from the ColumnFamilyDefinition
                    String cfName = String.format("%s|%s", keyspace.getName(), cf.getName());
                    MapStringToObject cfOptions = getColumnFamilyOptions(cf);
                    String strCfOptions = JsonSerializer.toString(cfOptions);
//                    
//                    // Check for changes
                    if (existingColumnFamilies.containsKey(cfName)) {
                        MapStringToObject previousOptions = JsonSerializer.fromString(existingColumnFamilies.get(cfName), MapStringToObject.class);
                        
                        LOG.info("Old options: " + previousOptions);
                        
                        MapDifference cfDiff = Maps.difference(cfOptions, previousOptions);
                        if (cfDiff.areEqual()) {
                            LOG.info("CF '{}' didn't change", new Object[]{cfName});
                        }
                        else {
                            changed = true;
                            LOG.info("CF Changed: " + cfDiff.entriesDiffering());
                        }

                    }
                    else {
                        changed = true;
                    }
//                    
//                    // Keep track of the cf
                    foundColumnFamilies.add(cfName);
                    existingColumnFamilies.put(cfName,  strCfOptions);
                    
                    LOG.info("Found column family '{}|{}|{}' : {}", new Object[]{entity.getClusterName(), keyspace.getName(), cf.getName(), strCfOptions});
                }
            }
        }
        catch (Exception e) {
            LOG.info("Error refreshing cluster: " + entity.getClusterName(), e);
            entity.setEnabled(false);
        }
        
        SetView<String> ksRemoved = Sets.difference(existingKeyspaces.keySet(),      foundKeyspaces);
        LOG.info("Keyspaces removed: " + ksRemoved);
        
        SetView<String> cfRemoved = Sets.difference(existingColumnFamilies.keySet(), foundColumnFamilies);
        LOG.info("CF removed: " + cfRemoved);
        
        clusterDao.write(entity);
    }
    
    private MapStringToObject getKeyspaceOptions(KeyspaceDefinition keyspace) {
        MapStringToObject result = new MapStringToObject();
        for (FieldMetadata field : keyspace.getFieldsMetadata()) {
            result.put(field.getName(), keyspace.getFieldValue(field.getName()));
        }

        result.remove("CF_DEFS");
        return result;
    }

    private MapStringToObject getColumnFamilyOptions(ColumnFamilyDefinition cf) {
        MapStringToObject result = new MapStringToObject();
        for (FieldMetadata field : cf.getFieldsMetadata()) {
            if (field.getName().equals("COLUMN_METADATA")) {
//                // This will get handled below
            }
            else {
                Object value = cf.getFieldValue(field.getName());
                if (value instanceof ByteBuffer) {
                    result.put(field.getName(),  ((ByteBuffer)value).array());
                }
                else {
                    result.put(field.getName(), value);
                }
            }
        }
//        // Hack to get the column metadata
        List<MapStringToObject> columns = Lists.newArrayList();
        for (ColumnDefinition column : cf.getColumnDefinitionList()) {
            MapStringToObject map = new MapStringToObject();
            for (FieldMetadata field : column.getFieldsMetadata()) {
                Object value = column.getFieldValue(field.getName());
                if (value instanceof ByteBuffer) {
                    result.put(field.getName(),  ((ByteBuffer)value).array());
                }
                else {
                    map.put(field.getName(), value);
                }
            }
            columns.add(map);
        }
        result.put("COLUMN_METADATA", columns);
        return result;
    }
}
