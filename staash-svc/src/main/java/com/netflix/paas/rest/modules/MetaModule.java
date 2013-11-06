package com.netflix.paas.rest.modules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.inject.AbstractModule;
import com.netflix.paas.rest.dao.CqlDataDaoImpl;
import com.netflix.paas.rest.dao.CqlMetaDaoImpl;
import com.netflix.paas.rest.dao.DataDao;
import com.netflix.paas.rest.dao.MetaDao;

public class MetaModule extends AbstractModule{
    private static final Logger LOG = LoggerFactory.getLogger(MetaModule.class);
//    @Provides
//    @Named("datacluster")
//    Cluster provideDataCluster() {
//        //String nodes = eureka.getNodes(clustername);
//        //get nodes in the cluster, to pass as parameters to the underlying apis
//     // TODO Auto-generated method stub
//        Configurator.reset();
//        String clusterName= "";
//        try {
//            Configurator.addConfiguratorsByName(PaasConfigurator.class.getName());
//            Configurator.initializeConfigurator("-paas.cluster", "localhost");
//            final Injector injector = Configurator.getSharedInjector();
//            PaasConfiguration config = injector.getInstance(PaasConfiguration.class);
//            clusterName = config.getClusterName();
//        } catch (SecurityException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IllegalArgumentException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (InvalidConfException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (PaasException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        if (clusterName==null || clusterName.equals("")) clusterName = "localhost";
//        Cluster cluster = Cluster.builder().addContactPoint(clusterName).build();
//        return cluster;
//    }

    @Override
    protected void configure() {
        bind(MetaDao.class).to(CqlMetaDaoImpl.class).asEagerSingleton();
        bind(DataDao.class).to(CqlDataDaoImpl.class).asEagerSingleton();
//        bind(PaasAdminResourceImpl.class);
//        bind(PaasDataResourceImpl.class);
    }
    
}
