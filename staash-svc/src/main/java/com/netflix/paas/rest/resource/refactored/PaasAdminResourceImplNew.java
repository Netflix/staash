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
package com.netflix.paas.rest.resource.refactored;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.netflix.paas.exception.StorageDoesNotExistException;
import com.netflix.paas.json.JsonObject;
import com.netflix.paas.rest.dao.MetaDao;
import com.netflix.paas.rest.meta.entity.EntityType;
import com.netflix.paas.rest.meta.entity.PaasDBEntity;
import com.netflix.paas.rest.meta.entity.PaasStorageEntity;
import com.netflix.paas.rest.meta.entity.PaasTableEntity;
import com.netflix.paas.rest.meta.entity.PaasTimeseriesEntity;
import com.netflix.paas.service.MetaService;

@Path("/v1/admin")
public class PaasAdminResourceImplNew {
    private MetaService metasvc;
    @Inject
    public PaasAdminResourceImplNew(MetaService meta) {
        this.metasvc = meta;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String listSchemas() {
        // TODO Auto-generated method stub
        String schemas = metasvc.listSchemas();
        return schemas;
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/storage")
    public String listStorage() {
        // TODO Auto-generated method stub
        String storages = metasvc.listStorage();
        return storages;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{schema}")
    public String listTables(@PathParam("schema") String schema) {
        // TODO Auto-generated method stub
        String schemas = metasvc.listTablesInSchema(schema);
        return schemas;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/timeseries/{schema}")
    public String listTimeseries(@PathParam("schema") String schema) {
        // TODO Auto-generated method stub
        String schemas = metasvc.listTimeseriesInSchema(schema);
        return schemas;
    }
    
    @GET
    @Path("cluster")
    public String listSchemas(@PathParam("cluster")String cluster) {
        // TODO Auto-generated method stub
        //just a test method to see service is loaded
        return "hello";
    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String createSchema(String payLoad) {
        // TODO Auto-generated method stub
        if (payLoad!=null) {
//            JsonObject jsonPayLoad =  new JsonObject(payLoad);
//            PaasDBEntity pdbe = PaasDBEntity.builder().withJsonPayLoad(jsonPayLoad).build();
            try {
                return metasvc.writeMetaEntity(EntityType.DB, payLoad);
            } catch (StorageDoesNotExistException e) {
                // TODO Auto-generated catch block
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
    public String createStorage(String payload) {
        // TODO Auto-generated method stub
        if (payload!=null) {
//            JsonObject jsonPayLoad =  new JsonObject(payLoad);
//            PaasStorageEntity pse = PaasStorageEntity.builder().withJsonPayLoad(jsonPayLoad).build();
            try {
                return metasvc.writeMetaEntity(EntityType.STORAGE, payload);
            } catch (StorageDoesNotExistException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        JsonObject obj = new JsonObject("{\"message\":\"payload can not be null must conform to: {name:<name>,cluster:<cluster>}\"");
        return obj.toString();
    }

    @DELETE
    @Path("{schema}")
    public void deleteSchema(@PathParam("schema") String schemaName) {
        // TODO Auto-generated method stub
        
    }

        
    @POST
    @Path("{schema}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String createTable(@PathParam("schema") String schemaName, String payload) {
        // TODO Auto-generated method stub
        JsonObject obj;
        try {
            if (payload!=null) {
//            JsonObject jsonPayLoad =  new JsonObject(payLoad);
//            PaasTableEntity ptbe = PaasTableEntity.builder().withJsonPayLoad(jsonPayLoad, schemaName).build();
                obj = new JsonObject(payload).putString("db", schemaName);
                return metasvc.writeMetaEntity(EntityType.TABLE, obj.toString());
                //create new ks
                //create new cf
            }
            obj = new JsonObject("{\"message\":\"payload can not be null must conform to: {name:<name>,cluster:<cluster>}\"");
        } catch (StorageDoesNotExistException e) {
            // TODO Auto-generated catch block
           // e.printStackTrace();
            obj = new JsonObject("\"message\":\"Storage Does Not Exist\""); 
        }
        return obj.toString();
    }
    
    @POST
    @Path("/timeseries/{schema}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String createTimeseries(@PathParam("schema") String schemaName, String payload) {
        // TODO Auto-generated method stub
        JsonObject obj;
        try {
            if (payload!=null) {
//            JsonObject jsonPayLoad =  new JsonObject(payLoad);
//            PaasTimeseriesEntity ptbe = PaasTimeseriesEntity.builder().withJsonPayLoad(jsonPayLoad, schemaName).build();
                return metasvc.writeMetaEntity(EntityType.SERIES, payload);
                //create new ks
                //create new cf
            }
            obj = new JsonObject("{\"message\":\"payload can not be null must conform to: {name:<name>,cluster:<cluster>}\"");
        } catch (StorageDoesNotExistException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            obj = new JsonObject("\"message\":\"Storage Does Not Exist\"");
        }
        return obj.toString();
    }
    
    @GET
    @Path("/extend/{db}/{region}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String extendDB(String db, String region) {
        return metasvc.extenddb(db, region).toString();
    }

}
