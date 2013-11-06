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
