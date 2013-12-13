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

import javax.persistence.PersistenceException;

/**
 * Generic DAO interface
 * @author elandau
 *
 * @param <T>
 */
public interface Dao<T> extends DaoStatus {
    /**
     * Read a single entity by key
     * 
     * @param key
     * @return
     */
    public T read(String key) throws PersistenceException;

    /**
     * Read entities for a set of keys
     * @param keys
     * @return
     * @throws PersistenceException
     */
    public Collection<T> read(Collection<String> keys) throws PersistenceException;
    
    /**
     * Write a single entity
     * @param entity
     */
    public void write(T entity) throws PersistenceException;
 
    /**
     * List all entities
     * 
     * @return
     * 
     * @todo
     */
    public Collection<T> list() throws PersistenceException;

    /**
     * List all ids without necessarily retrieving all the entities
     * @return
     * @throws PersistenceException
     */
    public Collection<String> listIds() throws PersistenceException;
    
    /**
     * Delete a row by key
     * @param key
     */
    public void delete(String key) throws PersistenceException;
    
    /**
     * Create the underlying storage for this dao
     * @throws PersistenceException
     */
    public void createTable() throws PersistenceException;
    
    /**
     * Delete the storage for this dao
     * @throws PersistenceException
     */
    public void deleteTable() throws PersistenceException;

    /**
     * Cleanup resources used by this dao as part of the shutdown process
     */
    public void shutdown();
}
