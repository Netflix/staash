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
package com.netflix.paas.dao;

import java.util.Collection;

public interface DaoSchema {
    /**
     * Create the underlying storage for the schema.  Does not create the Daos
     */
    public void createSchema();
    
    /**
     * Delete store for the schema and all child daos
     */
    public void dropSchema();
    
    /**
     * Get a dao for this type
     * @param type
     * @return
     */
    public <T> Dao<T> getDao(Class<T> type);
    
    /**
     * Retrive all Daos managed by this schema
     * @return
     */
    public Collection<Dao<?>> listDaos();
    
    /**
     * Determine if the storage for this schema exists
     * @return
     */
    public boolean isExists();
}
