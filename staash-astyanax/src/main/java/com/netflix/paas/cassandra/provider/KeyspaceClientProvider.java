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
package com.netflix.paas.cassandra.provider;

import com.netflix.astyanax.Keyspace;
import com.netflix.paas.cassandra.keys.KeyspaceKey;

/**
 * Abstraction for getting a keyspace.  The implementation will handle lifecycle
 * management for the keyspace.
 * 
 * @author elandau
 *
 */
public interface KeyspaceClientProvider {
    /**
     * Get a keyspace by name.  Will create one if one does not exist.  The provider
     * will internally keep track of references to the keyspace and will auto remove
     * it once releaseKeyspace is called and the reference count goes down to 0.
     * 
     * @param keyspaceName  Globally unique keyspace name
     * 
     * @return A new or previously created keyspace.
     */
    public Keyspace acquireKeyspace(String schemaName);
    
    /**
     * Get a keyspace by key.
     * @param key
     * @return
     */
    public Keyspace acquireKeyspace(KeyspaceKey key);
    
    /**
     * Release a previously acquried keyspace
     * @param keyspace
     */
    public void releaseKeyspace(String schemaName);
}
