package com.netflix.paas.rest.test;

import java.util.Random;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.netflix.paas.cassandra.discovery.EurekaModule;
import com.netflix.paas.connection.ConnectionFactory;
import com.netflix.paas.exception.StorageDoesNotExistException;
import com.netflix.paas.json.JsonObject;
import com.netflix.paas.rest.meta.entity.EntityType;
import com.netflix.paas.rest.modules.PaasPropertiesModule;
import com.netflix.paas.service.DataService;
import com.netflix.paas.service.PaasDataService;
import com.netflix.paas.service.PaasMetaService;

public class TestPaasRest {
    PaasMetaService metasvc;
    PaasDataService datasvc;
    @Before
    public void setup() {
//        metasvc = new PaasMetaService(new CqlMetaDaoImplNew(Cluster.builder().addContactPoint("localhost").build()));
        EurekaModule em = new EurekaModule();
        Injector einj = Guice.createInjector(em);
        TestPaasPropertiesModule pmod = new TestPaasPropertiesModule();
        Injector inj = Guice.createInjector(pmod);
        metasvc = inj.getInstance(PaasMetaService.class);
//        datasvc = new PaasDataService(metasvc, Guice.createInjector(pmod).getInstance(ConnectionFactory.class));
        datasvc = inj.getInstance(PaasDataService.class);
        int i = 0;
    }
    @Test
    @Ignore
    public void createStorage() {
        String payload = "{\"name\": \"social_nosql_storage\",\"type\": \"cassandra\",\"cluster\": \"cass_social\",\"replicate\":\"another\"}";
        String s;
        try {
            s = metasvc.writeMetaEntity(EntityType.STORAGE, payload);
        } catch (StorageDoesNotExistException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        Print(s);
    }
    private void Print(String s) {
        System.out.println(s);
    }
    
    @Test
    @Ignore
    public void testCreateDb() {
        //name
        //meta.writerow(dbentity)
        String payload = "{\"name\":\"unitdb1\"}";
        try {
            metasvc.writeMetaEntity(EntityType.DB, payload);
        } catch (StorageDoesNotExistException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    @Ignore
    public void testCreateTable() {
        String storage = "{\"name\": \"unit.mysql\",\"type\": \"mysql\",\"jdbcurl\": \"jdbc:mysql://localhost:3306/\",\"host\":\"localhost\",\"user\":\"root\",\"password\":\"\",\"replicate\":\"another\"}";
        try {
            metasvc.writeMetaEntity(EntityType.STORAGE, storage);
        } catch (StorageDoesNotExistException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        String dbpay = "{\"name\":\"unitdb2\"}";
        try {
            metasvc.writeMetaEntity(EntityType.DB, dbpay);
        } catch (StorageDoesNotExistException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        String tblpay = "{\"name\":\"unittbl2\",\"columns\":\"col1,col2,col3\",\"primarykey\":\"col1\",\"storage\":\"unit.mysql\"}";
        String db = "unitdb2";
        JsonObject pload = new JsonObject(tblpay);
        pload.putString("db", db);
        try {
            metasvc.writeMetaEntity(EntityType.TABLE, pload.toString());
        } catch (StorageDoesNotExistException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    @Ignore
    public void testCreateTableCass() {
        String storage = "{\"name\": \"unit.cassandra\",\"type\": \"cassandra\",\"cluster\": \"localhost\",\"replicate\":\"another\"}";
        try {
            metasvc.writeMetaEntity(EntityType.STORAGE, storage);
        } catch (StorageDoesNotExistException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        String dbpay = "{\"name\":\"unitdb2\"}";
        try {
            metasvc.writeMetaEntity(EntityType.DB, dbpay);
        } catch (StorageDoesNotExistException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        String tblpay = "{\"name\":\"tst2\",\"columns\":\"col1,col2,col3\",\"primarykey\":\"col1\",\"storage\":\"unit.cassandra\"}";
        String db = "unitdb2";
        JsonObject pload = new JsonObject(tblpay);
        pload.putString("db", db);
        try {
            metasvc.writeMetaEntity(EntityType.TABLE, pload.toString());
        } catch (StorageDoesNotExistException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Test
    @Ignore
    public void listTablesInaSchema() {
        
    }
    
    @Test
    @Ignore
    public void testInsertRow() {
        String storage = "{\"name\": \"unit.cassandra\",\"type\": \"cassandra\",\"cluster\": \"localhost\",\"replicate\":\"another\"}";
        try {
            metasvc.writeMetaEntity(EntityType.STORAGE, storage);
        } catch (StorageDoesNotExistException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        String dbpay = "{\"name\":\"unitcass\"}";
        try {
            metasvc.writeMetaEntity(EntityType.DB, dbpay);
        } catch (StorageDoesNotExistException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        String tblpay = "{\"name\":\"casstable1\",\"columns\":\"col1,col2,col3\",\"primarykey\":\"col1\",\"storage\":\"unit.cassandra\"}";
        String db = "unitcass";
        JsonObject pload = new JsonObject(tblpay);
        pload.putString("db", db);
        try {
            metasvc.writeMetaEntity(EntityType.TABLE, pload.toString());
        } catch (StorageDoesNotExistException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        String insertPay = "{\"columns\":\"col1,col2,col3\",\"values\":\"'val1','val2','val3'\"}";
        datasvc.writeRow(db, "casstable1", new JsonObject(insertPay));
        
    }
    @Test
    @Ignore
    public void testInsertRowMySql() {
        String storage = "{\"name\": \"unit.mysql\",\"type\": \"mysql\",\"jdbcurl\": \"jdbc:mysql://localhost:3306/\",\"host\":\"localhost\",\"user\":\"root\",\"password\":\"\",\"replicate\":\"another\"}";
        try {
            metasvc.writeMetaEntity(EntityType.STORAGE, storage);
        } catch (StorageDoesNotExistException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        String dbpay = "{\"name\":\"unitdb3\"}";
        try {
            metasvc.writeMetaEntity(EntityType.DB, dbpay);
        } catch (StorageDoesNotExistException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        String tblpay = "{\"name\":\"my2\",\"columns\":\"col1,col2,col3\",\"primarykey\":\"col1\",\"storage\":\"unit.mysql\"}";
        String db = "unitdb3";
        JsonObject pload = new JsonObject(tblpay);
        pload.putString("db", db);
        try {
            metasvc.writeMetaEntity(EntityType.TABLE, pload.toString());
        } catch (StorageDoesNotExistException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        String insertPay = "{\"columns\":\"col1,col2,col3\",\"values\":\"'val1','val2','val3'\"}";
        datasvc.writeRow(db, "my2", new JsonObject(insertPay));
        
    }
    
    @Test
    @Ignore
  public void testInsertRowMySqlRemote() {
      String storage = "{\"name\": \"unit.mysqlremote\",\"type\": \"mysql\",\"jdbcurl\": \"jdbc:mysql://cde.ceqg1dgfu0mp.us-east-1.rds.amazonaws.com:3306/\",\"host\":\"cde.ceqg1dgfu0mp.us-east-1.rds.amazonaws.com\",\"user\":\"cdeuser\",\"password\":\"cdeuser123\",\"replicate\":\"another\"}";
      try {
        metasvc.writeMetaEntity(EntityType.STORAGE, storage);
    } catch (StorageDoesNotExistException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
      
      String dbpay = "{\"name\":\"unitdb3\"}";
      try {
        metasvc.writeMetaEntity(EntityType.DB, dbpay);
    } catch (StorageDoesNotExistException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
      
      String tblpay = "{\"name\":\"my2\",\"columns\":\"col1,col2,col3\",\"primarykey\":\"col1\",\"storage\":\"unit.mysqlremote\"}";
      String db = "unitdb3";
      JsonObject pload = new JsonObject(tblpay);
      pload.putString("db", db);
      try {
        metasvc.writeMetaEntity(EntityType.TABLE, pload.toString());
    } catch (StorageDoesNotExistException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
      
      String insertPay = "{\"columns\":\"col1,col2,col3\",\"values\":\"'val1','val2','val3'\"}";
      datasvc.writeRow(db, "my2", new JsonObject(insertPay));
      
  }
    @Test
    @Ignore
    public void testRemoteJoin() {
        long start = System.currentTimeMillis();
        String clustername = "ec2-54-242-127-138.compute-1.amazonaws.com";
        String storage = "{\"name\": \"unit.remotecassandra\",\"type\": \"cassandra\",\"cluster\": \"ec2-54-242-127-138.compute-1.amazonaws.com:7102\",\"replicate\":\"another\"}";
        try {
            metasvc.writeMetaEntity(EntityType.STORAGE, storage);
        } catch (StorageDoesNotExistException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Print("Storage:"+(System.currentTimeMillis() - start));
        
        String dbpay = "{\"name\":\"jointest\"}";
        try {
            metasvc.writeMetaEntity(EntityType.DB, dbpay);
        } catch (StorageDoesNotExistException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Print("db:"+(System.currentTimeMillis() - start));

        
        String table1="cassjoin"+ new Random().nextInt(200);

        String tblpay = "{\"name\":\""+table1+"\",\"columns\":\"username,friends,wall,status\",\"primarykey\":\"username\",\"storage\":\"unit.remotecassandra\"}";
        String db = "jointest";
        JsonObject pload = new JsonObject(tblpay);
        pload.putString("db", db);
        try {
            metasvc.writeMetaEntity(EntityType.TABLE, pload.toString());
        } catch (StorageDoesNotExistException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Print("table:"+(System.currentTimeMillis() - start));

        
        String insertPay = "{\"columns\":\"username,friends,wall,status\",\"values\":\"'federer','rafa#haas#tommy#sachin#beckham','getting ready for my next#out of wimbledon#out of french','looking fwd to my next match'\"}";
        datasvc.writeRow(db, table1, new JsonObject(insertPay));
        Print("insert cass:"+(System.currentTimeMillis() - start));

        
        String table2="mysqljoin" + new Random().nextInt(200);;
 
        tblpay = "{\"name\":\""+table2+"\",\"columns\":\"username,first,last,lastlogin,paid,address,email\",\"primarykey\":\"username\",\"storage\":\"unit.mysqlremote\"}";
         pload = new JsonObject(tblpay);
        pload.putString("db", db);
        try {
            metasvc.writeMetaEntity(EntityType.TABLE, pload.toString());
        } catch (StorageDoesNotExistException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        
        Print("create mysql table:"+(System.currentTimeMillis() - start));

        insertPay = "{\"columns\":\"username,first,last,lastlogin,paid,address,email\",\"values\":\"'federer','Roger','Federer','july first','paid','1 swiss drive','RF@gmail.com'\"}";
        datasvc.writeRow(db, table2, new JsonObject(insertPay)); 
        Print("insert mysql row:"+(System.currentTimeMillis() - start));

        String str = datasvc.doJoin(db, table1, table2, "username", "rogerfederer");
        Print("Join:"+(System.currentTimeMillis() - start));

        Print("Join is");
        Print(str);
        
    }
    
    @Test
    @Ignore
    public void testjoin1()
    {
        String str = datasvc.doJoin("jointest", "mysqljoin43", "cassjoin78", "username", "federer");
//        Print("Join:"+(System.currentTimeMillis() - start));

        Print("Join is");
        Print(str);
    }
    @Test
    @Ignore
    public void testJoin() {
//        String clustername = "localhost";
//        String storage = "{\"name\": \"unit.cassandra\",\"type\": \"cassandra\",\"cluster\": \"localhost\",\"replicate\":\"another\"}";
//        metasvc.writeMetaEntity(EntityType.STORAGE, storage);
        
        String dbpay = "{\"name\":\"ljointest\"}";
        try {
            metasvc.writeMetaEntity(EntityType.DB, dbpay);
        } catch (StorageDoesNotExistException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        String table1="join1";

        String tblpay = "{\"name\":\"join1\",\"columns\":\"username,friends,wall,status\",\"primarykey\":\"username\",\"storage\":\"unit.cassandra\"}";
        String db = "jointest";
        JsonObject pload = new JsonObject(tblpay);
        pload.putString("db", db);
        try {
            metasvc.writeMetaEntity(EntityType.TABLE, pload.toString());
        } catch (StorageDoesNotExistException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        String insertPay = "{\"columns\":\"username,friends,wall,status\",\"values\":\"'rogerfederer','rafa#haas#tommy#sachin#beckham','getting ready for my next#out of wimbledon#out of french','looking fwd to my next match'\"}";
        datasvc.writeRow(db, "join1", new JsonObject(insertPay));
        
        String table2="join2";
 
        tblpay = "{\"name\":\"join2\",\"columns\":\"username,first,last,lastlogin,paid,address,email\",\"primarykey\":\"username\",\"storage\":\"unit.mysql\"}";
         pload = new JsonObject(tblpay);
        pload.putString("db", db);
        try {
            metasvc.writeMetaEntity(EntityType.TABLE, pload.toString());
        } catch (StorageDoesNotExistException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String str = datasvc.doJoin(db, table1, table2, "username", "rogerfederer");
        Print(str);        
    }
    
    @Test
    @Ignore
    public void testCreateTimeseriesTable() {
        String tblpay = "{\"name\":\"timeseries1\",\"periodicity\":\"10000\",\"prefix\":\"server1\",\"storage\":\"unit.cassandra\"}";
        String db = "unitdb2";
        JsonObject pload = new JsonObject(tblpay);
        pload.putString("db", db);
        try {
            metasvc.writeMetaEntity(EntityType.SERIES, pload.toString());
        } catch (StorageDoesNotExistException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Test
    @Ignore
    public void insertEvent() {
        String db = "unitdb2";
        String table = "timeseries1";
        String payload1="{\"time\":11000,\"event\":\"hello 11k event\"}";
        String payload2="{\"time\":21000,\"event\":\"hi 21k event\"}";
        datasvc.writeEvent(db, table, new JsonObject(payload1));
        datasvc.writeEvent(db, table, new JsonObject(payload2));        
    }
    @Test
    @Ignore
    public void readEvent() {
        String evtime = "11000";
        String db = "unitdb2";
        String table = "timeseries1";
        String out;
        out = datasvc.readEvent(db, table, evtime);
        out = datasvc.readEvent(db, table, "21000");
        out = datasvc.readEvent(db, table, "100000");
        int i = 0;
        
    }
    @Test
    @Ignore
    public void listStorage() {
        String out = metasvc.listStorage();
        Print(out);
    }
    @Test
    @Ignore
    public void listSchemas() {
        String out = metasvc.listSchemas();
        Print(out);
    }
    @Test
    @Ignore
    public void listTablesInSchema() {
        String out = metasvc.listTablesInSchema("unitdb2");
        Print(out);
    }
    @Test
    @Ignore
    public void listTimeseriesInSchema() {
        String out = metasvc.listTimeseriesInSchema("unitdb2");
        Print(out);
    }
    
    @Test
    @Ignore
    public void testInsertColumn() {
        
    }
    
    @Test
    @Ignore
    public void testUpdateRow() {
        
    }
    
    @Test
    @Ignore
    public void testDeleteRow() {        
    }
    
    @Test
    @Ignore
    public void testDeleteColumn() {        
    }
    @Test
    @Ignore
    public void testQuery() {
            }
    @Test
    @Ignore
    public void runMrJob() {
        
    }

}
