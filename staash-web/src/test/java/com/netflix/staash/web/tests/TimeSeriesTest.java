package com.netflix.staash.web.tests;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.netflix.staash.json.JsonObject;
import com.netflix.staash.service.PaasDataService;
import com.netflix.staash.service.PaasMetaService;
import com.netflix.staash.test.modules.TestStaashModule;
import com.netflix.staash.test.core.CassandraRunner;

//@RequiresKeyspace(ksName = "paasmetaks")
//@RequiresColumnFamily(ksName = "paasmetaks", cfName = "metacf", comparator = "org.apache.cassandra.db.marshal.UTF8Type", keyValidator = "org.apache.cassandra.db.marshal.UTF8Type")
//@SuppressWarnings({ "rawtypes", "unchecked" })
@RunWith(CassandraRunner.class)
public class TimeSeriesTest {
	public static PaasMetaService metasvc;
    public static PaasDataService datasvc;
    public static final String db = "unitdb1";
    public static final String timeseries = "testtimeseries1";
	public static String timeseriespay = "{\"name\":\"timeseries1\",\"periodicity\":\"10000\",\"prefix\":\"server1\",\"storage\":\"cassandratest\"}";
	
	@BeforeClass
	public static void setup() {
		TestStaashModule pmod = new TestStaashModule();
        Injector inj = Guice.createInjector(pmod);
        metasvc = inj.getInstance(PaasMetaService.class);
        datasvc = inj.getInstance(PaasDataService.class);
        StaashTestHelper.createTestStorage(metasvc);
        StaashTestHelper.createTestDB(metasvc);
        StaashTestHelper.createTestTimeSeries(metasvc, timeseriespay);
        System.out.println("Done:");
	}
	@Test
	public void testTimeseriesWriteRead() {
		String payload1="{\"timestamp\":11000,\"event\":\"hi 11k event\",\"prefix\":\"source1\"}";
        String payload2="{\"timestamp\":21000,\"event\":\"hi 21k event\",\"prefix\":\"source1\"}";
        String payload3="{\"timestamp\":121000,\"event\":\"hi 121k event\",\"prefix\":\"source2\"}";
        StaashTestHelper.writeEvent(datasvc, new JsonObject(payload1));
        StaashTestHelper.writeEvent(datasvc, new JsonObject(payload2));
        StaashTestHelper.writeEvent(datasvc, new JsonObject(payload3));
        readTimeSeries();
	}
	
	private void readTimeSeries() {
        String db = "unitdb1";
        String table = "timeseries1";
        String out = "";
        out = datasvc.readEvent(db, table, "source2","121000");
        assert out.equals("{\"1 Jan 1970 00:02:01 GMT\":\"hi 121k event\"}");
        System.out.println("out=  "+out);
        out = datasvc.readEvent(db, table, "source1", "21000");
        assert out.equals("{\"1 Jan 1970 00:00:21 GMT\":\"hi 21k event\"}");
        System.out.println("out=  "+out);
        out = datasvc.readEvent(db, table, "source1", "11000");
        assert out.equals("{\"1 Jan 1970 00:00:11 GMT\":\"hi 11k event\"}");
        System.out.println("out=  "+out);		
	}	
}
