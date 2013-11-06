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