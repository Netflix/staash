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
package com.netflix.paas.cassandra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.netflix.paas.PaasModule;
import com.netflix.paas.dao.astyanax.MetaDaoImpl;
import com.netflix.paas.dao.meta.CqlMetaDaoImpl;
import com.netflix.paas.meta.dao.MetaDao;

public class MetaModule extends AbstractModule{
    private static final Logger LOG = LoggerFactory.getLogger(MetaModule.class);


    @Override
    protected void configure() {
        // TODO Auto-generated method stub
//        bind(MetaDao.class).to(MetaDaoImpl.class).asEagerSingleton();
        bind(MetaDao.class).to(CqlMetaDaoImpl.class).asEagerSingleton();
    }
    @Provides 
    Cluster provideCluster(@Named("clustername") String clustername) {
        //String nodes = eureka.getNodes(clustername);
        //get nodes in the cluster, to pass as parameters to the underlying apis
        Cluster cluster = Cluster.builder().addContactPoint("localhost").build();
        return cluster;
    }
}
