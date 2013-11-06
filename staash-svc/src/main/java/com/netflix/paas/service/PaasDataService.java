package com.netflix.paas.service;

import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Inject;
import com.netflix.paas.cassandra.discovery.EurekaAstyanaxHostSupplier;
import com.netflix.paas.connection.ConnectionFactory;
import com.netflix.paas.connection.PaasConnection;
import com.netflix.paas.connection.PaasConnectionFactory;
import com.netflix.paas.json.JsonObject;
import com.netflix.paas.rest.meta.entity.EntityType;

public class PaasDataService implements DataService{
    private MetaService meta;
    private ConnectionFactory fac;
    ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
    @Inject
    public PaasDataService(MetaService meta, ConnectionFactory fac){
        this.meta = meta;
        //this.fac = new PaasConnectionFactory(CLIENTTYPE.ASTYANAX.getType());
        this.fac = fac;
    }

    public String writeRow(String db, String table, JsonObject rowObj) {
        // TODO Auto-generated method stub
        // JsonObject tbl = meta.runQuery(EntityType.TABLE, db+"."+table);
        // JsonObject storageConf = meta.runQuery(EntityType.STORAGE.getId(), tbl.getString("storage")).get(tbl.getString("storage"));
        JsonObject storageConf = meta.getStorageForTable(db+"."+table);
        PaasConnection conn = fac.createConnection(storageConf, db);
        return conn.insert(db,table,rowObj);
    }

    public String listRow(String db, String table, String keycol, String key) {
        // TODO Auto-generated method stub
//        JsonObject tbl = meta.runQuery(EntityType.TABLE, db+"."+table).get(db+"."+table);
//        JsonObject storageConf = meta.runQuery(EntityType.STORAGE.getId(), tbl.getString("storage")).get(tbl.getString("storage"));
        JsonObject storageConf = meta.getStorageForTable(db+"."+table);
        if (storageConf == null) return "{\"msg\":\"the requested table does not exist in paas\"}";
        PaasConnection conn = fac.createConnection(storageConf,db);
        return conn.read(db,table,keycol,key);
    }

    public String writeEvent(String db, String table, JsonObject rowObj) {
        // TODO Auto-generated method stub
        JsonObject tbl = meta.runQuery(EntityType.SERIES, db+"."+table);
//        JsonObject storageConf = meta.runQuery(EntityType.STORAGE.getId(), tbl.getString("storage")).get(tbl.getString("storage"));
        JsonObject storageConf = meta.getStorageForTable(db+"."+table);
        PaasConnection conn = fac.createConnection(storageConf,db);
        String periodicity = tbl.getString("periodicity");
        Long time = rowObj.getLong("time");
        Long rowkey = (time/Long.parseLong(periodicity))*Long.parseLong(periodicity);
        rowObj.putString("columns", "key,column1,value");
        rowObj.putString("values","'"+String.valueOf(rowkey)+"',"+time+",'"+rowObj.getString("event")+"'");
        return conn.insert(db,table,rowObj);
    }

    public String readEvent(String db, String table, String eventTime) {
        // TODO Auto-generated method stub
        JsonObject tbl = meta.runQuery(EntityType.SERIES, db+"."+table);
        JsonObject storageConf = meta.getStorageForTable(db+"."+table);
        PaasConnection conn = fac.createConnection(storageConf,db);
        String periodicity = tbl.getString("periodicity");
        Long time = Long.parseLong(eventTime);
        Long rowkey = (time/Long.parseLong(periodicity))*Long.parseLong(periodicity);
        return conn.read(db,table,"key",String.valueOf(rowkey),"column1",String.valueOf(eventTime));
    }

    public String doJoin(String db, String table1, String table2,
            String joincol, String value) {
        // TODO Auto-generated method stub
        String res1 = listRow(db,table1,joincol,value);
        String res2 = listRow(db,table2,joincol,value);
        return "{\""+table1+"\":"+res1+",\""+table2+"\":"+res2+"}"; 
    }

   
}
