/*******************************************************************************
 * /*
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
 *  *
 ******************************************************************************/
package com.netflix.staash.rest.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.google.inject.Inject;
import com.netflix.staash.exception.StorageDoesNotExistException;
import com.netflix.staash.json.JsonObject;
import com.netflix.staash.rest.meta.entity.EntityType;
import com.netflix.staash.service.MetaService;
import com.sun.jersey.spi.container.ResourceFilters;

@Path("/staash/v1/admin")
public class StaashAdminResourceImpl {
    private MetaService metasvc;
    @Inject
    public StaashAdminResourceImpl(MetaService meta) {
        this.metasvc = meta;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ResourceFilters(StaashAuditFilter.class)
    public String listSchemas() {
        String schemas = metasvc.listSchemas();
        return schemas;
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/storage")
    @ResourceFilters(StaashAuditFilter.class)
    public String listStorage() {
        String storages = metasvc.listStorage();
        return storages;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{schema}")
    @ResourceFilters(StaashAuditFilter.class)
    public String listTables(@PathParam("schema") String schema) {
        String schemas = metasvc.listTablesInSchema(schema);
        return schemas;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/timeseries/{schema}")
    @ResourceFilters(StaashAuditFilter.class)
    public String listTimeseries(@PathParam("schema") String schema) {
        String schemas = metasvc.listTimeseriesInSchema(schema);
        return schemas;
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ResourceFilters(StaashAuditFilter.class)
    public String createSchema(String payLoad) {
        if (payLoad!=null) {
            try {
                return metasvc.writeMetaEntity(EntityType.DB, payLoad);
            } catch (StorageDoesNotExistException e) {
                e.printStackTrace();
            }
        }
        JsonObject obj = new JsonObject("{\"message\":\"payload can not be null must conform to: {name:<name>,cluster:<cluster>}\"");
        return obj.toString();
    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/storage")
    @ResourceFilters(StaashAuditFilter.class)
    public String createStorage(String payload) {
        if (payload!=null) {
            try {
                return metasvc.writeMetaEntity(EntityType.STORAGE, payload);
            } catch (StorageDoesNotExistException e) {
                e.printStackTrace();
            }
        }
        JsonObject obj = new JsonObject("{\"message\":\"payload can not be null must conform to: {name:<name>,cluster:<cluster>}\"");
        return obj.toString();
    }

        
    @POST
    @Path("{schema}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ResourceFilters(StaashAuditFilter.class)
    public String createTable(@PathParam("schema") String schemaName, String payload) {
        JsonObject obj;
        try {
            if (payload!=null) {
                obj = new JsonObject(payload).putString("db", schemaName);
                return metasvc.writeMetaEntity(EntityType.TABLE, obj.toString());
            }
            obj = new JsonObject("{\"message\":\"payload can not be null must conform to: {name:<name>,cluster:<cluster>}\"");
        } catch (StorageDoesNotExistException e) {
            obj = new JsonObject("\"message\":\"Storage Does Not Exist\""); 
        }
        return obj.toString();
    }
    
    @POST
    @Path("/timeseries/{schema}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ResourceFilters(StaashAuditFilter.class)
    public String createTimeseries(@PathParam("schema") String schemaName, String payload) {

    	JsonObject obj;
        try {
            if (payload!=null) {
                return metasvc.writeMetaEntity(EntityType.SERIES, payload);
            }
            obj = new JsonObject("{\"message\":\"payload can not be null must conform to: {name:<name>,cluster:<cluster>}\"");
        } catch (StorageDoesNotExistException e) {
            obj = new JsonObject("\"message\":\"Storage Does Not Exist\"");
        }
        return obj.toString();
    }
}
