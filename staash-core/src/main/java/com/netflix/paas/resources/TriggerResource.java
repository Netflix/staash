package com.netflix.paas.resources;

import java.util.List;

import com.netflix.paas.entity.TriggerEntity;

public interface TriggerResource {
    void createTableTrigger(String schema, String table, String name, TriggerEntity trigger);
    
    void deleteTableTrigger(String schema, String table, String trigger);
    
    List<TriggerEntity> listTableTriggers(String schema, String table);
    
    List<TriggerEntity> listSchemaTriggers(String schema);
    
    List<TriggerEntity> listAllTriggers();
}
