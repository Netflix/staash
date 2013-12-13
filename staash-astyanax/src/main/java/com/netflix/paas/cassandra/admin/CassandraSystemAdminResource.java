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
package com.netflix.paas.cassandra.admin;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.netflix.paas.SchemaNames;
import com.netflix.paas.cassandra.discovery.ClusterDiscoveryService;
import com.netflix.paas.cassandra.entity.CassandraClusterEntity;
import com.netflix.paas.cassandra.tasks.ClusterDiscoveryTask;
import com.netflix.paas.dao.Dao;
import com.netflix.paas.dao.DaoProvider;
import com.netflix.paas.exceptions.NotFoundException;
import com.netflix.paas.tasks.TaskManager;

@Path("/v1/cassandra")
public class CassandraSystemAdminResource {
    private static final Logger LOG = LoggerFactory.getLogger(CassandraSystemAdminResource.class);
    
    private final Dao<CassandraClusterEntity>           dao;
    private final CassandraClusterAdminResourceFactory  clusterResourceFactory;
    private final ClusterDiscoveryService               clusterDiscovery;
    private final TaskManager                           taskManager;
    private final ConcurrentMap<String, CassandraClusterAdminResource> clusters = Maps.newConcurrentMap();
    
    private static class CassandraClusterEntityToName implements Function<CassandraClusterEntity, String> {
        @Override
        public String apply(CassandraClusterEntity cluster) {
            return cluster.getClusterName(); 
        }
    }
    
    @Inject
    public CassandraSystemAdminResource(
            TaskManager taskManager,
            DaoProvider daoProvider,
            ClusterDiscoveryService clusterDiscovery,
            CassandraClusterAdminResourceFactory clusterResourceFactory) throws Exception {
        this.clusterResourceFactory = clusterResourceFactory;
        this.dao                    = daoProvider.getDao(SchemaNames.CONFIGURATION.name(), CassandraClusterEntity.class);
        this.clusterDiscovery       = clusterDiscovery;
        this.taskManager            = taskManager;
    }
    
    @PostConstruct
    public void initialize() {
    }
    
    @PreDestroy
    public void shutdown() {
    }
    
    @Path("clusters/{id}")
    public CassandraClusterAdminResource getCluster(@PathParam("id") String clusterName) throws NotFoundException {
        CassandraClusterAdminResource resource = clusters.get(clusterName);
        if (resource == null) {
            throw new NotFoundException(CassandraClusterAdminResource.class, clusterName);
        }
        return resource;
    }
    
    @GET
    @Path("clusters")
    public Set<String> listClusters() {
        return clusters.keySet();
    }

    @GET
    @Path("discover")
    public void discoverClusters() {
        taskManager.submit(ClusterDiscoveryTask.class);
        
//        Set<String> foundNames                      = Sets.newHashSet(clusterDiscovery.getClusterNames());
//        Map<String, CassandraClusterEntity> current = Maps.uniqueIndex(dao.list(), new CassandraClusterEntityToName());
//
//        // Look for new clusters (may contain clusters that are disabled)
//        for (String clusterName : Sets.difference(foundNames, current.keySet())) {
////            CassandraClusterEntity entity = CassandraClusterEntity.builder();
//        }
//        
//        // Look for clusters that were removed
//        for (String clusterName : Sets.difference(current.keySet(), foundNames)) {
//        }
    }

    @POST
    @Path("clusters")
    public void refreshClusterList() {
//        Map<String, CassandraClusterEntity> newList = Maps.uniqueIndex(dao.list(), new CassandraClusterEntityToName());
//        
//        // Look for new clusters (may contain clusters that are disabled)
//        for (String clusterName : Sets.difference(newList.keySet(), clusters.keySet())) {
//            CassandraClusterEntity entity = newList.get(clusterName);
//            if (entity.isEnabled()) {
//                CassandraClusterAdminResource resource = clusterResourceFactory.get(clusterName);
//                if (null == clusters.putIfAbsent(clusterName,  resource)) {
//                    // TODO: Start it
//                }
//            }
//        }
//        
//        // Look for clusters that were removed
//        for (String clusterName : Sets.difference(clusters.keySet(), newList.keySet())) {
//            CassandraClusterAdminResource resource = clusters.remove(clusterName);
//            if (resource != null) {
//                // TODO: Shut it down
//            }
//        }
//        
//        // Look for clusters that may have been disabled
//        for (String clusterName : Sets.intersection(clusters.keySet(), newList.keySet())) {
//            CassandraClusterEntity entity = newList.get(clusterName);
//            if (!entity.isEnabled()) {
//                CassandraClusterAdminResource resource = clusters.remove(clusterName);
//                if (resource != null) {
//                    // TODO: Shut it down
//                }
//            }
//        }
    }
}
