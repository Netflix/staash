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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.google.inject.Inject;
import com.netflix.paas.json.JsonObject;
import com.netflix.paas.rest.dao.DataDao;
import com.netflix.paas.rest.dao.MetaDao;

@Path("/v1/data")
public class PaasDataResourceImpl {
    private MetaDao metadao;
    private DataDao datadao;

    @Inject
    public PaasDataResourceImpl(MetaDao meta, DataDao data) {
        this.metadao = meta;
        this.datadao =  data;
    }

    @GET
    public String listSchemas() {
        // TODO Auto-generated method stub
        return "hello data";
    }

    @GET
    @Path("{db}/{table}/{keycol}/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public String listRow(@PathParam("db") String db,
            @PathParam("table") String table, @PathParam("keycol") String keycol,@PathParam("key") String key) {
            return  datadao.listRow(db, table, keycol, key);
    }
    @GET
    @Path("/join/{db}/{table1}/{table2}/{joincol}/{value}")
    @Produces(MediaType.APPLICATION_JSON)
    public String doJoin(@PathParam("db") String db,
            @PathParam("table1") String table1, @PathParam("table2") String table2,@PathParam("joincol") String joincol,@PathParam("value") String value) {
            return  datadao.doJoin(db, table1, table2, joincol, value);
    }
    @GET
    @Path("/timeseries/{db}/{table}/{eventtime}")
    @Produces(MediaType.APPLICATION_JSON)
    public String readEvent(@PathParam("db") String db,
            @PathParam("table") String table, @PathParam("eventtime") String time) {
            return  datadao.readEvent(db, table, time);
    }

    @POST
    @Path("{db}/{table}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String updateRow(@PathParam("db") String db,
            @PathParam("table") String table, String rowObject) {
        return datadao.writeRow(db, table, new JsonObject(rowObject));
        // TODO Auto-generated method stub
    }
    
    @POST
    @Path("/timeseries/{db}/{table}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String insertEvent(@PathParam("db") String db,
            @PathParam("table") String table, String rowObject) {
        return datadao.writeEvent(db, table, new JsonObject(rowObject));
        // TODO Auto-generated method stub
    }

}
