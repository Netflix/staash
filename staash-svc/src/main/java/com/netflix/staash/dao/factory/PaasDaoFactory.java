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
package com.netflix.staash.dao.factory;

import java.util.HashMap;
import java.util.Map;

import com.datastax.driver.core.Cluster;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.netflix.staash.connection.ConnectionFactory;
import com.netflix.staash.rest.dao.AstyanaxDataDaoImpl;
import com.netflix.staash.rest.dao.CqlDataDaoImpl;
import com.netflix.staash.rest.dao.CqlMetaDaoImpl;
import com.netflix.staash.rest.dao.DataDao;
import com.netflix.staash.rest.dao.MetaDao;

public class PaasDaoFactory {
    static Map<String, DataDao> DaoMap = new HashMap<String, DataDao>();
    static String clientType = "cql";
    public MetaDao  metaDao;
    public ConnectionFactory factory;
    @Inject
    public PaasDaoFactory(MetaDao metaDao, ConnectionFactory factory) {
        this.metaDao = metaDao;
        this.factory = factory;
    }
    public  DataDao getDataDao(String storage, String clientType) {
        String storageType = getStorageType(storage);
        String cluster = getCluster(storage);
//        String hostName = getHostName(cluster);
        if (DaoMap.containsKey(cluster)) return DaoMap.get(cluster);
        if (storageType.equals("cassandra") &&  clientType.equals("cql")) {
            DataDao dataDao = DaoMap.get(cluster);
            if (dataDao==null) {
//                dataDao = new CqlDataDaoImpl(factory.getDataCluster(cluster),metaDao);
            }
            DaoMap.put(cluster,  dataDao);
            return dataDao;
        } else if (storageType.equals("cassandra") &&  clientType.equals("astyanax")) {
            DataDao dataDao = DaoMap.get(cluster);
            if (dataDao==null) {
                dataDao = new AstyanaxDataDaoImpl();//factory.getDataCluster(cluster),metaDao);
            }
            DaoMap.put(cluster,  dataDao);
            return dataDao;
        } else if (storageType.equals("mysql") && clientType==null) {
            
        }
        return null;
    }
    public  MetaDao getMetaDao() {
        return metaDao;
    }
    private String  getStorageType(String storage) {
        return "cassandra";
    }
    private String getCluster(String storage) {
        return "localhost";
    }
    private String getHostName(String cluster) {
        //eureka name resolution
        return "localhost";
    }

}
