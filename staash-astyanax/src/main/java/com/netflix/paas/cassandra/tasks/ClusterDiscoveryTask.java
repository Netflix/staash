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
package com.netflix.paas.cassandra.tasks;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.netflix.paas.SchemaNames;
import com.netflix.paas.cassandra.discovery.ClusterDiscoveryService;
import com.netflix.paas.cassandra.entity.CassandraClusterEntity;
import com.netflix.paas.dao.Dao;
import com.netflix.paas.dao.DaoProvider;
import com.netflix.paas.exceptions.NotFoundException;
import com.netflix.paas.tasks.Task;
import com.netflix.paas.tasks.TaskContext;
import com.netflix.paas.tasks.TaskManager;

/**
 * Task to compare the list of clusters in the Dao and the list of clusters from the discovery
 * service and add/remove/update in response to any changes.
 * 
 * @author elandau
 *
 */
public class ClusterDiscoveryTask implements Task {
    private static final Logger LOG = LoggerFactory.getLogger(ClusterDiscoveryTask.class);
    
    private final ClusterDiscoveryService       discoveryService;
    private final Dao<CassandraClusterEntity>   clusterDao;
    private final TaskManager                   taskManager;    
    
    @Inject
    public ClusterDiscoveryTask(
            ClusterDiscoveryService discoveryService, 
            DaoProvider daoProvider, 
            TaskManager taskManager) throws NotFoundException{
        this.discoveryService     = discoveryService;
        this.clusterDao           = daoProvider.getDao(SchemaNames.CONFIGURATION.name(), CassandraClusterEntity.class);
        this.taskManager          = taskManager;
    }
    
    @Override
    public void execte(TaskContext context) throws Exception {
        // Get complete set of existing clusters from the discovery service
        Collection<String> clusters = Sets.newHashSet(discoveryService.getClusterNames());
        LOG.info(clusters.toString());
        
        // Load entire list of previously known clusters to a map of <ClusterName> => <ClusterEntity>
        Map<String, CassandraClusterEntity> existingClusters = Maps.uniqueIndex(
            this.clusterDao.list(),
            new Function<CassandraClusterEntity, String>() {
                @Override
                public String apply(CassandraClusterEntity cluster) {
                    LOG.info("Found existing cluster : " + cluster.getClusterName());
                    return cluster.getClusterName();
                }
            });

        // Iterate through new list of clusters and look for changes
        for (String clusterName : clusters) {
            CassandraClusterEntity entity = existingClusters.get(clusterName);
            
            // This is a new cluster
            if (entity == null) {
                LOG.info("Found new cluster : " + clusterName);
                
                entity = CassandraClusterEntity.builder()
                        .withName(clusterName)
                        .withIsEnabled(true)
                        .withDiscoveryType(discoveryService.getName())
                        .build();
                
                try {
                    clusterDao.write(entity);
                }
                catch (Exception e) {
                    LOG.warn("Failed to persist cluster info for '{}'", new Object[]{clusterName, e});
                }
                
                updateCluster(entity);
            }
            
            // We knew about it before and it is disabled
            else if (!entity.isEnabled()) {
                LOG.info("Cluster '{}' is disabled and will not be refreshed", new Object[]{clusterName});
            }
            
            // Refresh the info for an existing cluster
            else {
                LOG.info("Cluster '{}' is being refreshed", new Object[]{clusterName});
                if (entity.getDiscoveryType() == null) {
                    entity.setDiscoveryType(discoveryService.getName()); 
                    try {
                        clusterDao.write(entity);
                    }
                    catch (Exception e) {
                        LOG.warn("Failed to persist cluster info for '{}'", new Object[]{clusterName, e});
                    }
                }
                
                updateCluster(entity);
            }
        }
    }

    private void updateCluster(CassandraClusterEntity entity) {
        LOG.info("Need to update cluster " + entity.getClusterName());
        try {
            taskManager.submit(ClusterRefreshTask.class, ImmutableMap.<String, Object>builder()
                    .put("entity", entity)
                    .build());
        } catch (Exception e) {
            LOG.warn("Failed to create ClusterRefreshTask for " + entity.getClusterName(), e);
        }
    }
}
