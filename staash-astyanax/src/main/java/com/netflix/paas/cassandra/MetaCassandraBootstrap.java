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

import com.google.inject.Inject;
import com.netflix.paas.PaasBootstrap;
import com.netflix.paas.SchemaNames;
import com.netflix.paas.cassandra.entity.CassandraClusterEntity;
import com.netflix.paas.dao.Dao;
import com.netflix.paas.dao.DaoProvider;
import com.netflix.paas.dao.DaoSchema;

public class MetaCassandraBootstrap {

    private static final Logger LOG = LoggerFactory.getLogger(PaasBootstrap.class);

    @Inject
    public MetaCassandraBootstrap(DaoProvider daoProvider) throws Exception {
        LOG.info("Bootstrap Meta Cassandra");
        DaoSchema schemaDao = daoProvider.getSchema(SchemaNames.META.name());
        if (!schemaDao.isExists()) {
            schemaDao.createSchema();
        }
        
        Dao<CassandraClusterEntity> clusterDao = daoProvider.getDao(SchemaNames.META.name(), CassandraClusterEntity.class);
        if (!clusterDao.isExists()) {
            clusterDao.createTable();
        }
    }
}
