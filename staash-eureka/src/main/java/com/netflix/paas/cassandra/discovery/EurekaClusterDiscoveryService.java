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
package com.netflix.paas.cassandra.discovery;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.configuration.AbstractConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.inject.Inject;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.discovery.shared.Application;

/**
 * Implementation of a cluster discovery service using Eureka
 * 
 * @author elandau
 *
 */
public class EurekaClusterDiscoveryService implements ClusterDiscoveryService {
    private static final Logger LOG = LoggerFactory.getLogger(EurekaClusterDiscoveryService.class);
    
    private static final String PROPERTY_MATCH = "com.netflix.paas.discovery.eureka.match";
    
    private DiscoveryClient client;
    
    private AbstractConfiguration config;
    
    @Inject
    public EurekaClusterDiscoveryService(AbstractConfiguration config) {
        this.config = config;
        initialize();
    }
    
    @PostConstruct
    public void initialize() {
        LOG.info("Initializing Eureka client");
        client = DiscoveryManager.getInstance().getDiscoveryClient();
    }
    
    @PreDestroy
    public void shutdown() {
        // TODO: Move this somewhere else
        LOG.info("Shutting down Eureka client");
        DiscoveryManager.getInstance().shutdownComponent();
        client = null;
    }
    
    @Override
    public Collection<String> getClusterNames() {
        final Pattern regex = Pattern.compile(this.config.getString(PROPERTY_MATCH));
        
        return Collections2.filter(
                Collections2.transform(client.getApplications().getRegisteredApplications(),
                    new Function<Application, String>() {
                        @Override
                        public String apply(Application app) {
                            return app.getName();
                        }
                }),
                new Predicate<String>() {
                    @Override
                    public boolean apply(String clusterName) {
                        Matcher m = regex.matcher(clusterName);
                        return m.matches();
                    }
                });
    }

    @Override
    public String getName() {
        return "eureka";
    }

}
