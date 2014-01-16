package com.netflix.staash.test.core;


import java.util.Map;
import org.apache.cassandra.config.CFMetaData;
import org.apache.cassandra.config.KSMetaData;
import org.apache.cassandra.config.Schema;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(CassandraRunner.class)
@RequiresKeyspace(ksName = "myks")
@RequiresColumnFamily(ksName = "myks", cfName = "uuidtest", comparator = "org.apache.cassandra.db.marshal.UTF8Type", keyValidator = "org.apache.cassandra.db.marshal.UUIDType")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class DumyTest {
    @Test
    @Ignore
    public void mytest2() {
        System.out.println("Hello World!");
        for (String ks :Schema.instance.getTables()) {
        	KSMetaData ksm = Schema.instance.getKSMetaData(ks);
        	Map<String, CFMetaData> cfm = ksm.cfMetaData();
        }
    }
}
