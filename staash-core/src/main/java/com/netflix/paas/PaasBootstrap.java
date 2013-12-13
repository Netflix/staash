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
package com.netflix.paas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.netflix.paas.dao.Dao;
import com.netflix.paas.dao.DaoProvider;
import com.netflix.paas.dao.DaoSchema;
import com.netflix.paas.entity.PassGroupConfigEntity;
import com.netflix.paas.entity.DbEntity;
import com.netflix.paas.resources.DataResource;

public class PaasBootstrap {
    private static final Logger LOG = LoggerFactory.getLogger(PaasBootstrap.class);
    
    @Inject
    public PaasBootstrap(DaoProvider daoProvider) throws Exception {
        LOG.info("Bootstrapping PAAS");
        
        DaoSchema schemaDao                    = daoProvider.getSchema(SchemaNames.CONFIGURATION.name());
        if (!schemaDao.isExists()) {
            schemaDao.createSchema();
        }
        
        Dao<DbEntity> vschemaDao    = daoProvider.getDao(SchemaNames.CONFIGURATION.name(), DbEntity.class);
        if (!vschemaDao.isExists()) {
            vschemaDao.createTable();
        }
        
        Dao<PassGroupConfigEntity> groupDao    = daoProvider.getDao(SchemaNames.CONFIGURATION.name(), PassGroupConfigEntity.class);
        if (!groupDao.isExists()) {
            groupDao.createTable();
        }            
        
    }
}
