package com.netflix.paas.cassandra.resources.admin;

import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.PersistenceException;
import javax.ws.rs.PathParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import com.netflix.astyanax.Cluster;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.paas.cassandra.admin.CassandraClusterAdminResource;
import com.netflix.paas.cassandra.discovery.ClusterDiscoveryService;
import com.netflix.paas.cassandra.entity.CassandraClusterEntity;
import com.netflix.paas.cassandra.entity.ColumnFamilyEntity;
import com.netflix.paas.cassandra.entity.KeyspaceEntity;
import com.netflix.paas.cassandra.events.ColumnFamilyDeleteEvent;
import com.netflix.paas.cassandra.events.ColumnFamilyUpdateEvent;
import com.netflix.paas.cassandra.events.KeyspaceUpdateEvent;
import com.netflix.paas.cassandra.keys.ClusterKey;
import com.netflix.paas.cassandra.keys.ColumnFamilyKey;
import com.netflix.paas.cassandra.keys.KeyspaceKey;
import com.netflix.paas.cassandra.provider.ClusterClientProvider;
import com.netflix.paas.dao.Dao;
import com.netflix.paas.dao.DaoProvider;
import com.netflix.paas.dao.astyanax.DaoKeys;
import com.netflix.paas.exceptions.NotFoundException;
import com.netflix.paas.exceptions.PaasException;

/**
 * Implementation of a Cassandra Cluster Admin interface using Astyanax and Thrift.
 * Since this is the admin interface only one connection is actually needed to the cluster
 * 
 * @author elandau
 */
public class AstyanaxThriftClusterAdminResource implements CassandraClusterAdminResource {
    private static final Logger LOG = LoggerFactory.getLogger(AstyanaxThriftClusterAdminResource.class);
    
    private final ClusterKey                clusterKey;
    private final Cluster                   cluster;
    private final DaoProvider               daoProvider;
    private final EventBus                  eventBus;
    
    @Inject
    public AstyanaxThriftClusterAdminResource(
                                    EventBus                 eventBus,
                                    DaoProvider              daoProvider, 
            @Named("tasks")         ScheduledExecutorService taskExecutor,
                                    ClusterDiscoveryService  discoveryService,
                                    ClusterClientProvider    clusterProvider,
            @Assisted               ClusterKey               clusterKey) {
        this.clusterKey   = clusterKey;
        this.cluster      = clusterProvider.acquireCluster(clusterKey);
        this.daoProvider  = daoProvider;
        this.eventBus     = eventBus;
    }
    
    @PostConstruct
    public void initialize() {
    }
    
    @PreDestroy
    public void shutdown() {
    }
    
    @Override
    public CassandraClusterEntity getClusterDetails() throws PersistenceException {
        return daoProvider.getDao(DaoKeys.DAO_CASSANDRA_CLUSTER_ENTITY).read(clusterKey.getCanonicalName());
    }

    @Override
    public KeyspaceEntity getKeyspace(String keyspaceName) throws PersistenceException {
        Dao<KeyspaceEntity> dao = daoProvider.getDao(DaoKeys.DAO_KEYSPACE_ENTITY);
        return dao.read(keyspaceName);
    }

    @Override
    public void createKeyspace(KeyspaceEntity keyspace) throws PaasException {
        LOG.info("Creating keyspace '{}'", new Object[] {keyspace.getName()});
        
        Preconditions.checkNotNull(keyspace, "Missing keyspace entity definition");
        
        Properties props = new Properties();
        props.putAll(getDefaultKeyspaceProperties());
        if (keyspace.getOptions() != null) {
            props.putAll(keyspace.getOptions());
        }
        props.setProperty("name", keyspace.getName());
        keyspace.setClusterName(clusterKey.getClusterName());

        try {
            cluster.createKeyspace(props);
            eventBus.post(new KeyspaceUpdateEvent(new KeyspaceKey(clusterKey, keyspace.getName())));
        } catch (ConnectionException e) {
            throw new PaasException(String.format("Error creating keyspace '%s' from cluster '%s'", keyspace.getName(), clusterKey.getClusterName()), e);
        }
    }
    
    @Override
    public void updateKeyspace(@PathParam("keyspace") String keyspaceName, KeyspaceEntity keyspace) throws PaasException {
        try {
            if (keyspace.getOptions() == null) {
                return; // Nothing to do 
            }
            
            // Add them as existing values to the properties object
            Properties props = new Properties();
            props.putAll(cluster.getKeyspaceProperties(keyspaceName));
            props.putAll(keyspace.getOptions());
            props.setProperty("name", keyspace.getName());
            keyspace.setClusterName(clusterKey.getClusterName());
            
            cluster.updateKeyspace(props);
            eventBus.post(new KeyspaceUpdateEvent(new KeyspaceKey(clusterKey, keyspace.getName())));
        } catch (ConnectionException e) {
            throw new PaasException(String.format("Error creating keyspace '%s' from cluster '%s'", keyspace.getName(), clusterKey.getClusterName()), e);
        }
    }

    @Override
    public void deleteKeyspace(String keyspaceName) throws PaasException {
        LOG.info("Dropping keyspace");
        try {
            cluster.dropKeyspace(keyspaceName);
        } catch (ConnectionException e) {
            throw new PaasException(String.format("Error deleting keyspace '%s' from cluster '%s'", keyspaceName, clusterKey.getClusterName()), e);
        }
    }

    @Override
    public ColumnFamilyEntity getColumnFamily(String keyspaceName, String columnFamilyName) throws NotFoundException {
        Dao<ColumnFamilyEntity> dao = daoProvider.getDao(DaoKeys.DAO_COLUMN_FAMILY_ENTITY);
        return dao.read(new ColumnFamilyKey(clusterKey, keyspaceName, columnFamilyName).getCanonicalName());
    }

    @Override
    public void deleteColumnFamily(String keyspaceName, String columnFamilyName) throws PaasException {
        LOG.info("Deleting column family: '{}.{}.{}'", new Object[] {clusterKey.getClusterName(), keyspaceName, columnFamilyName});
        
        try {
            cluster.dropColumnFamily(keyspaceName, columnFamilyName);
        } catch (ConnectionException e) {
            throw new PaasException(String.format("Error creating column family '%s.%s' on cluster '%s'", 
                    keyspaceName, columnFamilyName, clusterKey.getClusterName()), e);
        }
        eventBus.post(new ColumnFamilyDeleteEvent(new ColumnFamilyKey(clusterKey, keyspaceName, columnFamilyName)));
    }

    @Override
    public void createColumnFamily(@PathParam("keyspace") String keyspaceName, ColumnFamilyEntity columnFamily) throws PaasException {
        LOG.info("Creating column family: '{}.{}.{}'", new Object[] {clusterKey.getClusterName(), keyspaceName, columnFamily.getName()});
        
        columnFamily.setKeyspaceName(keyspaceName);
        columnFamily.setClusterName(clusterKey.getClusterName());
        
        Properties props = new Properties();
        props.putAll(getDefaultColumnFamilyProperties());
        if (columnFamily.getOptions() != null) {
            props.putAll(columnFamily.getOptions());
        }
        props.setProperty("name",     columnFamily.getName());
        props.setProperty("keyspace", columnFamily.getKeyspaceName());

        try {
            cluster.createColumnFamily(props);
            eventBus.post(new ColumnFamilyUpdateEvent(new ColumnFamilyKey(new KeyspaceKey(clusterKey, keyspaceName), columnFamily.getName())));
        } catch (ConnectionException e) {
            throw new PaasException(String.format("Error creating column family '%s.%s' on cluster '%s'", 
                    keyspaceName, columnFamily.getName(), clusterKey.getClusterName()), e);
        }
    }

    @Override
    public void updateColumnFamily(@PathParam("keyspace") String keyspaceName, String columnFamilyName, ColumnFamilyEntity columnFamily) throws PaasException {
        LOG.info("Updating column family: '{}.{}.{}'", new Object[] {clusterKey.getClusterName(), keyspaceName, columnFamily.getName()});
        
        columnFamily.setKeyspaceName(keyspaceName);
        columnFamily.setClusterName(clusterKey.getClusterName());
        
        try {
            Properties props = new Properties();
            props.putAll(cluster.getColumnFamilyProperties(keyspaceName, columnFamilyName));
            if (columnFamily.getOptions() != null) {
                props.putAll(columnFamily.getOptions());
            }
            props.setProperty("name",     columnFamily.getName());
            props.setProperty("keyspace", columnFamily.getKeyspaceName());

            cluster.createColumnFamily(props);
            eventBus.post(new ColumnFamilyUpdateEvent(new ColumnFamilyKey(new KeyspaceKey(clusterKey, keyspaceName), columnFamily.getName())));
        } catch (ConnectionException e) {
            throw new PaasException(String.format("Error creating column family '%s.%s' on cluster '%s'", 
                    keyspaceName, columnFamily.getName(), clusterKey.getClusterName()), e);
        }
    }

    @Override
    public Collection<ColumnFamilyEntity> listColumnFamilies() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<String> listKeyspaceNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<String> listColumnFamilyNames() {
        // TODO Auto-generated method stub
        return null;
    }

    private Properties getDefaultKeyspaceProperties() {
        // TODO: Read from configuration
        return new Properties();
    }
    
    private Properties getDefaultColumnFamilyProperties() {
        return new Properties();
    }

    @Override
    public Collection<KeyspaceEntity> listKeyspaces() {
        // TODO Auto-generated method stub
        return null;
    }

}
