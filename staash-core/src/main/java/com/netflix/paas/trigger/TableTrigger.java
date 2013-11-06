package com.netflix.paas.trigger;

public interface TableTrigger {
    void onDeleteRow(String schema, String table, String rowkey);
    
    void onUpsertRow(String schema, String table, String rowkey);
}
