package com.netflix.paas.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import com.netflix.paas.exceptions.PaasException;
import com.netflix.paas.json.JsonObject;
@Path("/v1/data")
public interface PaasDataResource {
    @POST
    @Path("{db}/{table}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void updateRow(
            @PathParam("db")    String db,
            @PathParam("table")    String table,
            String rowData
            ) ;
    @GET
    @Path("{db}/{table}/{keycol}/{key}")
    public String listRow(@PathParam("db") String db,
            @PathParam("table") String table, @PathParam("keycol") String keycol,@PathParam("key") String key);

    String listSchemas();

}
