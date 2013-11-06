package com.netflix.paas.dao;

import java.util.Collection;

import com.netflix.paas.exceptions.NotFoundException;

/**
 * Manage all DAOs for an application.
 * 
 * @author elandau
 *
 */
public interface DaoSchemaProvider {
    /**
     * List all schemas for which daos were created
     * @return
     */
    Collection<DaoSchema> listSchemas();
    
    /**
     * Get the schema by name
     * @return
     * @throws NotFoundException 
     */
    DaoSchema getSchema(String schema) throws NotFoundException;
}
