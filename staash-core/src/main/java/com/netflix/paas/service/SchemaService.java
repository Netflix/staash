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
package com.netflix.paas.service;

import java.util.List;

import com.netflix.paas.entity.DbEntity;
import com.netflix.paas.entity.TableEntity;

/**
 * Abstraction for registry of schemas and tables visible to this deployment
 * @author elandau
 *
 */
public interface SchemaService {
    /**
     * List schemas that are available to this instance
     * 
     * @return
     */
    List<DbEntity> listSchema();
    
    /**
     * List all tables in the schema
     * 
     * @param schemaName
     * @return
     */
    List<TableEntity> listSchemaTables(String schemaName);
    
    /**
     * List all tables
     */
    List<TableEntity> listAllTables();

    /**
     * Refresh from storage
     */
    public void refresh();
}
