package com.netflix.paas.rest.resource.refactored;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.google.inject.Inject;
import com.netflix.paas.json.JsonObject;
import com.netflix.paas.service.DataService;

@Path("/paas/v1/data")
public class PaasDataResourceImplNew {
    private DataService datasvc;

    @Inject
    public PaasDataResourceImplNew(DataService data) {
        this.datasvc =  data;
    }

    @GET
    public String listSchemas() {
        // TODO Auto-generated method stub
        return "hello data";
    }
    @GET
    @Path("{db}/{table}")
    @Produces(MediaType.APPLICATION_JSON)
    public String listAllRow(@PathParam("db") String db,
            @PathParam("table") String table) {
            return  datasvc.listRow(db, table, "", "");
    }

    @GET
    @Path("{db}/{table}/{keycol}/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public String listRow(@PathParam("db") String db,
            @PathParam("table") String table, @PathParam("keycol") String keycol,@PathParam("key") String key) {
            return  datasvc.listRow(db, table, keycol, key);
    }
    @GET
    @Path("/join/{db}/{table1}/{table2}/{joincol}/{value}")
    @Produces(MediaType.APPLICATION_JSON)
    public String doJoin(@PathParam("db") String db,
            @PathParam("table1") String table1, @PathParam("table2") String table2,@PathParam("joincol") String joincol,@PathParam("value") String value) {
        return  datasvc.doJoin(db, table1, table2, joincol, value);
    }
    @GET
    @Path("/timeseries/{db}/{table}/{eventtime}")
    @Produces(MediaType.APPLICATION_JSON)
    public String readEvent(@PathParam("db") String db,
            @PathParam("table") String table, @PathParam("eventtime") String time) {
            return  datasvc.readEvent(db, table, time);
    }

    @POST
    @Path("{db}/{table}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String updateRow(@PathParam("db") String db,
            @PathParam("table") String table, String rowObject) {
        return datasvc.writeRow(db, table, new JsonObject(rowObject));
        // TODO Auto-generated method stub
    }
    
    @POST
    @Path("/timeseries/{db}/{table}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String insertEvent(@PathParam("db") String db,
            @PathParam("table") String table, String rowObject) {
        return datasvc.writeEvent(db, table, new JsonObject(rowObject));
        // TODO Auto-generated method stub
    }

}
