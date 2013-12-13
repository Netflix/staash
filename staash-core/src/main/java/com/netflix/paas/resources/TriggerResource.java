/*******************************************************************************
 * /***
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
 ******************************************************************************/
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
