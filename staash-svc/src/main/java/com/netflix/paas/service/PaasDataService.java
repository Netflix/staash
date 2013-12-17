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
package com.netflix.paas.service;

import java.util.concurrent.Executors;

import org.apache.cassandra.utils.Hex;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Inject;
import com.netflix.paas.cassandra.discovery.EurekaAstyanaxHostSupplier;
import com.netflix.paas.connection.ConnectionFactory;
import com.netflix.paas.connection.PaasConnection;
import com.netflix.paas.connection.PaasConnectionFactory;
import com.netflix.paas.json.JsonArray;
import com.netflix.paas.json.JsonObject;
import com.netflix.paas.rest.meta.entity.EntityType;

public class PaasDataService implements DataService{
    private MetaService meta;
    private ConnectionFactory fac;
    ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
    @Inject
    public PaasDataService(MetaService meta, ConnectionFactory fac){
        this.meta = meta;
        this.fac = fac;
    }

    public String writeRow(String db, String table, JsonObject rowObj) {
        JsonObject storageConf = meta.getStorageForTable(db+"."+table);
        PaasConnection conn = fac.createConnection(storageConf, db);
        return conn.insert(db,table,rowObj);
    }
    public String writeToKVStore(String db, String table, JsonObject rowObj) {
    	JsonObject storageConf = meta.getStorageForTable(db+"."+table);
        PaasConnection conn = fac.createConnection(storageConf, db);
        rowObj.putString("type", "kv");
		return conn.insert(db, table, rowObj);
    }

    public String listRow(String db, String table, String keycol, String key) {
        JsonObject storageConf = meta.getStorageForTable(db+"."+table);
        if (storageConf == null) return "{\"msg\":\"the requested table does not exist in paas\"}";
        PaasConnection conn = fac.createConnection(storageConf,db);
        return conn.read(db,table,keycol,key);
    }
    public String writeEvents(String db, String table, JsonArray events) {
    	
    	JsonObject msg = new JsonObject();
    		for (Object event: events) {
    			JsonObject obj = (JsonObject) event;
    			writeEvent(db, table, obj);
    		}
		return "{\"msg\":\"ok\"}";
    }

    public String writeEvent(String db, String table, JsonObject rowObj) {
        JsonObject tbl = meta.runQuery(EntityType.SERIES, db+"."+table);
        if (tbl == null) throw new RuntimeException("Table "+table+" does not exist");
        JsonObject storageConf = meta.getStorageForTable(db+"."+table);
        if (storageConf == null) throw new RuntimeException("Storage for  "+table+" does not exist");
        PaasConnection conn = fac.createConnection(storageConf,db);
        String periodicity = tbl.getString("periodicity");
        Long time = rowObj.getLong("timestamp");
        if (time == null || time <= 0) {
        	time = System.currentTimeMillis();
        }
        String prefix = rowObj.getString("prefix");
        if (prefix !=  null && !prefix.equals("")) prefix = prefix+":"; else prefix = "";
        Long rowkey = (time/Long.parseLong(periodicity))*Long.parseLong(periodicity);
        rowObj.putString("columns", "key,column1,value");
        rowObj.putString("values","'"+prefix+String.valueOf(rowkey)+"',"+time+",'"+rowObj.getString("event")+"'");
        return conn.insert(db,table,rowObj);
    }

    public String readEvent(String db, String table, String eventTime) {
        // TODO Auto-generated method stub
        JsonObject tbl = meta.runQuery(EntityType.SERIES, db+"."+table);
        if (tbl == null) throw new RuntimeException("Table "+table+" does not exist");
        JsonObject storageConf = meta.getStorageForTable(db+"."+table);
        if (storageConf == null) throw new RuntimeException("Storage for  "+table+" does not exist");
        PaasConnection conn = fac.createConnection(storageConf,db);
        String periodicity = tbl.getString("periodicity");
        Long time = Long.parseLong(eventTime);
        Long rowkey = (time/Long.parseLong(periodicity))*Long.parseLong(periodicity);
        return conn.read(db,table,"key",String.valueOf(rowkey),"column1",String.valueOf(eventTime));
    }
    
    public String readEvent(String db, String table, String prefix,String eventTime) {
        // TODO Auto-generated method stub
        JsonObject tbl = meta.runQuery(EntityType.SERIES, db+"."+table);
        if (tbl == null) throw new RuntimeException("Table "+table+" does not exist");
        JsonObject storageConf = meta.getStorageForTable(db+"."+table);
        if (storageConf == null) throw new RuntimeException("Storage for  "+table+" does not exist");
        PaasConnection conn = fac.createConnection(storageConf,db);
        String periodicity = tbl.getString("periodicity");
        Long time = Long.parseLong(eventTime);
        Long rowkey = (time/Long.parseLong(periodicity))*Long.parseLong(periodicity);
        if (prefix !=  null && !prefix.equals("")) prefix = prefix+":"; else prefix = "";
        return conn.read(db,table,"key",prefix+String.valueOf(rowkey),"column1",String.valueOf(eventTime));
    }

    public String readEvent(String db, String table, String prefix,String startTime, String endTime) {
        // TODO Auto-generated method stub
        JsonObject tbl = meta.runQuery(EntityType.SERIES, db+"."+table);
        if (tbl == null) throw new RuntimeException("Table "+table+" does not exist");
        JsonObject storageConf = meta.getStorageForTable(db+"."+table);
        if (storageConf == null) throw new RuntimeException("Storage for  "+table+" does not exist");
        PaasConnection conn = fac.createConnection(storageConf,db);
        String periodicity = tbl.getString("periodicity");
        Long time = Long.parseLong(startTime);
        Long startTimekey = (time/Long.parseLong(periodicity))*Long.parseLong(periodicity);
        Long endTimeL = Long.parseLong(endTime);
        Long endTimeKey = (endTimeL/Long.parseLong(periodicity))*Long.parseLong(periodicity);
        if (prefix !=  null && !prefix.equals("")) prefix = prefix+":"; else prefix = "";
        JsonObject response = new JsonObject();
        for (Long current = startTimekey; current < endTimeKey;current = current+Long.parseLong(periodicity) ) {
            JsonObject  slice = new JsonObject(conn.read(db,table,"key",prefix+String.valueOf(current)));
        	for (String field:slice.getFieldNames()) {
        		response.putString(field, slice.getString(field));
        	}
        }
        return response.toString();
    }
    public String doJoin(String db, String table1, String table2,
            String joincol, String value) {
        // TODO Auto-generated method stub
        String res1 = listRow(db,table1,joincol,value);
        String res2 = listRow(db,table2,joincol,value);
        return "{\""+table1+"\":"+res1+",\""+table2+"\":"+res2+"}"; 
    }

	public byte[] fetchValueForKey(String db, String table, String keycol,
			String key) {
		// TODO Auto-generated method stub
		JsonObject storageConf = meta.getStorageForTable(db+"."+table);
        if (storageConf == null) return "{\"msg\":\"the requested table does not exist in paas\"}".getBytes();
        PaasConnection conn = fac.createConnection(storageConf,db);
        String ret = conn.read(db,table,keycol,key);
        JsonObject keyval = new JsonObject(ret).getObject("1");
        String val = keyval.getString("value");
        return val.getBytes();
	}

   
}
