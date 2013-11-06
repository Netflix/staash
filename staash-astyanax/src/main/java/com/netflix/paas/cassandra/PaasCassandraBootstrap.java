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

public class PaasCassandraBootstrap {
    private static final Logger LOG = LoggerFactory.getLogger(PaasBootstrap.class);

    @Inject
    public PaasCassandraBootstrap(DaoProvider daoProvider) throws Exception {
        LOG.info("Bootstrap PaasAstyanax");
        DaoSchema schemaDao = daoProvider.getSchema(SchemaNames.CONFIGURATION.name());
        if (!schemaDao.isExists()) {
            schemaDao.createSchema();
        }
        
        Dao<CassandraClusterEntity> clusterDao = daoProvider.getDao(SchemaNames.CONFIGURATION.name(), CassandraClusterEntity.class);
        if (!clusterDao.isExists()) {
            clusterDao.createTable();
        }
    }
}
