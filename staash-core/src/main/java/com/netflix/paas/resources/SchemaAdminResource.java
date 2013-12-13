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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import com.netflix.paas.entity.DbEntity;
import com.netflix.paas.entity.TableEntity;

/**
 * Admin resource for managing schemas
 */
@Path("/v1/admin")
public interface SchemaAdminResource {
    /**
     * List all schemas
     */
    @GET
    public String listSchemas();
    
    /**
     * Create a new schema
     */
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
//    @Path("/db")
    public void createSchema(String payLd);
    
    /**
     * Delete an existing schema
     * @param schemaName
     */
    @DELETE
    @Path("{schema}")
    public void deleteSchema(@PathParam("schema") String schemaName);
    
    /**
     * Update an existing schema
     * @param schemaName
     * @param schema
     */
    @POST
    @Path("{schema}")
    public void updateSchema(@PathParam("schema") String schemaName, DbEntity schema);
    
    /**
     * Get details for a schema
     * @param schemaName
     * @return
     */
    @GET
    @Path("{schema}")
    public DbEntity getSchema(@PathParam("schema") String schemaName);
    
    /**
     * Get details for a subtable of schema
     * @param schemaName
     * @param tableName
     */
    @GET
    @Path("{schema}/tables/{table}")
    public TableEntity getTable(@PathParam("schema") String schemaName, @PathParam("table") String tableName);
    
    /**
     * Remove a table from the schema
     * @param schemaName
     * @param tableName
     */
    @DELETE
    @Path("{schema}/tables/{table}")
    public void deleteTable(@PathParam("schema") String schemaName, @PathParam("table") String tableName);

    /**
     * Create a table in the schema
     * @param schemaName
     * @param table
     */
    @POST
    @Path("{schema}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void createTable(@PathParam("schema") String schemaName, String table);
    
    /**
     * Update an existing table in the schema
     * @param schemaName
     * @param tableName
     * @param table
     */
    @POST
    @Path("{schema}/tables/{table}")
    public void updateTable(@PathParam("schema") String schemaName, @PathParam("table") String tableName, TableEntity table);
}
