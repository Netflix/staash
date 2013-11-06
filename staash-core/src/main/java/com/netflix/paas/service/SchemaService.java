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
