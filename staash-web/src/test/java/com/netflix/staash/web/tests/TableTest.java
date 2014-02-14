package com.netflix.staash.web.tests;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.netflix.staash.json.JsonObject;
import com.netflix.staash.service.PaasDataService;
import com.netflix.staash.service.PaasMetaService;
import com.netflix.staash.test.core.CassandraRunner;
import com.netflix.staash.test.modules.TestStaashModule;

@RunWith(CassandraRunner.class)
public class TableTest {
	public static PaasMetaService metasvc;
    public static PaasDataService datasvc;
    public static final String db = "unitdb1";
    public static final String table = "table1";
    public static final String tblpay = "{\"name\":\"table1\",\"columns\":\"username,friends,wall,status\",\"primarykey\":\"username\",\"storage\":\"cassandratest\"}";
    public static final String insertPay1 = "{\"columns\":\"username,friends,wall,status\",\"values\":\"'rogerfederer','rafa#haas#tommy#sachin#beckham','getting ready for my next#out of wimbledon#out of french','looking fwd to my next match'\"}";
    public static final String insertPay2 = "{\"columns\":\"username,friends,wall,status\",\"values\":\"'rafaelnadal','rafa#haas#tommy#sachin#beckham','getting ready for my next#out of wimbledon#out of french','looking fwd to my next match'\"}";
    public static final String responseText1 = "{\"1\":{\"username\":\"rogerfederer\",\"friends\":\"rafa#haas#tommy#sachin#beckham\",\"status\":\"looking fwd to my next match\",\"wall\":\"getting ready for my next#out of wimbledon#out of french\"}}";
    public static final String responseText2 = "{\"1\":{\"username\":\"rafaelnadal\",\"friends\":\"rafa#haas#tommy#sachin#beckham\",\"status\":\"looking fwd to my next match\",\"wall\":\"getting ready for my next#out of wimbledon#out of french\"}}";



	@BeforeClass
	public static void setup() {
		TestStaashModule pmod = new TestStaashModule();
        Injector inj = Guice.createInjector(pmod);
        metasvc = inj.getInstance(PaasMetaService.class);
        datasvc = inj.getInstance(PaasDataService.class);
        StaashTestHelper.createTestStorage(metasvc);
        StaashTestHelper.createTestDB(metasvc);
        StaashTestHelper.createTestTable(metasvc,tblpay);
        System.out.println("Done:");
	}
	@Test
	public void testTableWriteRead() {
		datasvc.writeRow(db, table, new JsonObject(insertPay1));
		datasvc.writeRow(db, table, new JsonObject(insertPay2));
        readRows();
	}
	private void readRows() {
         String out = "";
        out = datasvc.listRow(db, table, "username", "rogerfederer");
        Assert.assertTrue("Did Not Get What Was Expected", out!=null?out.equals(responseText1):false);
        System.out.println("out=  "+out);
        out = datasvc.listRow(db, table, "username", "rafaelnadal");
        Assert.assertTrue("Did Not Get What Was Expected", out!=null?out.equals(responseText2):false);
        System.out.println("out=  "+out);
    }

}
