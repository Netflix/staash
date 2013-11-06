package com.netflix.paas.trigger.impl;

import com.netflix.paas.trigger.TableTrigger;

public class LoggingTableTrigger implements TableTrigger {

    @Override
    public void onDeleteRow(String schema, String table, String rowkey) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onUpsertRow(String schema, String table, String rowkey) {
        // TODO Auto-generated method stub
        
    }

}
