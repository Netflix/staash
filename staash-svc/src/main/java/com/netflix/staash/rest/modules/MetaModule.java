/*******************************************************************************
 * /*
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
 *  *
 ******************************************************************************/
package com.netflix.staash.rest.modules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.inject.AbstractModule;
import com.netflix.staash.rest.dao.CqlDataDaoImpl;
import com.netflix.staash.rest.dao.CqlMetaDaoImpl;
import com.netflix.staash.rest.dao.DataDao;
import com.netflix.staash.rest.dao.MetaDao;

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
