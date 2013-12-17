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
package com.netflix.paas.rest.resources;

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
import com.netflix.paas.json.JsonObject;
import com.netflix.paas.rest.dao.MetaDao;
import com.netflix.paas.rest.meta.entity.PaasDBEntity;
import com.netflix.paas.rest.meta.entity.PaasStorageEntity;
import com.netflix.paas.rest.meta.entity.PaasTableEntity;
import com.netflix.paas.rest.meta.entity.PaasTimeseriesEntity;

@Path("/v1/admin")
public class PaasAdminResourceImpl {
    private MetaDao metadao;
    @Inject
    public PaasAdminResourceImpl(MetaDao meta) {
        this.metadao = meta;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String listSchemas() {
        // TODO Auto-generated method stub
//        String schemas = metadao.listSchemas();
//        return schemas;
        return null;
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/storage")
    public String listStorage() {
        // TODO Auto-generated method stub
//        String storages = metadao.listStorage();
//        return storages;
        return null;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{schema}")
    public String listTables(@PathParam("schema") String schema) {
        // TODO Auto-generated method stub
//        String schemas = metadao.listTablesInSchema(schema);
//        return schemas;
        return null;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/timeseries/{schema}")
    public String listTimeseries(@PathParam("schema") String schema) {
        // TODO Auto-generated method stub
//        String schemas = metadao.listTimeseriesInSchema(schema);
//        return schemas;
        return null;

    }
    
    @GET
    @Path("cluster")
    public String listSchemas(@PathParam("cluster")String cluster) {
        // TODO Auto-generated method stub
        return "hello";
    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String createSchema(String payLoad) {
        // TODO Auto-generated method stub
        if (payLoad!=null) {
            JsonObject jsonPayLoad =  new JsonObject(payLoad);
            PaasDBEntity pdbe = PaasDBEntity.builder().withJsonPayLoad(jsonPayLoad).build();
            return metadao.writeMetaEntity(pdbe);
        }
        JsonObject obj = new JsonObject("{\"message\":\"payload can not be null must conform to: {name:<name>,cluster:<cluster>}\"");
        return obj.toString();
    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/storage")
    public String createStorage(String payLoad) {
        // TODO Auto-generated method stub
        if (payLoad!=null) {
            JsonObject jsonPayLoad =  new JsonObject(payLoad);
            PaasStorageEntity pse = PaasStorageEntity.builder().withJsonPayLoad(jsonPayLoad).build();
            return metadao.writeMetaEntity(pse);
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
    public String createTable(@PathParam("schema") String schemaName, String payLoad) {
        // TODO Auto-generated method stub
        if (payLoad!=null) {
            JsonObject jsonPayLoad =  new JsonObject(payLoad);
            PaasTableEntity ptbe = PaasTableEntity.builder().withJsonPayLoad(jsonPayLoad, schemaName).build();
            return metadao.writeMetaEntity(ptbe);
            //create new ks
            //create new cf
        }
        JsonObject obj = new JsonObject("{\"message\":\"payload can not be null must conform to: {name:<name>,cluster:<cluster>}\"");
        return obj.toString();
    }
    
    @POST
    @Path("/timeseries/{schema}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String createTimeseries(@PathParam("schema") String schemaName, String payLoad) {
        // TODO Auto-generated method stub
        if (payLoad!=null) {
            JsonObject jsonPayLoad =  new JsonObject(payLoad);
            PaasTimeseriesEntity ptbe = PaasTimeseriesEntity.builder().withJsonPayLoad(jsonPayLoad, schemaName).build();
            return metadao.writeMetaEntity(ptbe);
            //create new ks
            //create new cf
        }
        JsonObject obj = new JsonObject("{\"message\":\"payload can not be null must conform to: {name:<name>,cluster:<cluster>}\"");
        return obj.toString();
    }

}
