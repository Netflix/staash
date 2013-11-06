package com.netflix.paas.rest.dao.factory;

import java.util.HashMap;
import java.util.Map;

import com.datastax.driver.core.Cluster;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.netflix.paas.connection.ConnectionFactory;
import com.netflix.paas.rest.dao.AstyanaxDataDaoImpl;
import com.netflix.paas.rest.dao.CqlDataDaoImpl;
import com.netflix.paas.rest.dao.CqlMetaDaoImpl;
import com.netflix.paas.rest.dao.DataDao;
import com.netflix.paas.rest.dao.MetaDao;

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
        String hostName = getHostName(cluster);
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
