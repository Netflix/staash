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
package com.netflix.paas.cassandra.resources;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.paas.exceptions.AlreadyExistsException;
import com.netflix.paas.exceptions.NotFoundException;
import com.netflix.paas.resources.TableDataResource;

/**
 * Tracks a keyspace and column families that are accessible on it to this instance
 * 
 * @author elandau
 *
 */
public class CassandraKeyspaceHolder {
    private final AstyanaxContext<Keyspace> context;
    private final ConcurrentMap<String, TableDataResource> columnFamilies = Maps.newConcurrentMap();
    
    public CassandraKeyspaceHolder(AstyanaxContext<Keyspace> context) {
        this.context = context;
    }

    /**
     * Register a column family on this keyspace and create the appropriate DataTableResource
     * to read from it.
     * 
     * @param columnFamily
     * @throws AlreadyExistsException
     */
    public synchronized void registerColumnFamily(String columnFamily) throws AlreadyExistsException {
        if (columnFamilies.containsKey(columnFamily))
            throw new AlreadyExistsException("columnfamily", columnFamily);
        
        columnFamilies.put(columnFamily, new AstyanaxThriftDataTableResource(context.getClient(), columnFamily));
    }
    
    /**
     * Unregister a column family so that it is no longer available
     * @param columnFamily
     * @throws NotFoundException
     */
    public synchronized void unregisterColumnFamily(String columnFamily) throws NotFoundException {
        columnFamilies.remove(columnFamily);
    }
    
    /**
     * Retrieve a register column family resource
     * 
     * @param columnFamily
     * @return
     * @throws NotFoundException
     */
    public TableDataResource getColumnFamilyDataResource(String columnFamily) throws NotFoundException {
        TableDataResource resource = columnFamilies.get(columnFamily);
        if (resource == null)
            throw new NotFoundException("columnfamily", columnFamily);
        return resource;
    }
    
    public void shutdown() {
        this.context.shutdown();
    }
    
    public void initialize() {
        this.context.start();
    }

}
