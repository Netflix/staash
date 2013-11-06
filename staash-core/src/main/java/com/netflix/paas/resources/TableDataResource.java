package com.netflix.paas.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.netflix.paas.data.QueryResult;
import com.netflix.paas.data.RowData;
import com.netflix.paas.exceptions.NotFoundException;
import com.netflix.paas.exceptions.PaasException;
import com.netflix.paas.json.JsonObject;

/**
 * Interface for access to a table.  Concrete implementation may implement a table
 * on top of any persistence technology.
 * 
 * @author elandau
 *
 */
//@Path("/v1/data")
public interface TableDataResource {
    // Table level API
    @GET
    public QueryResult listRows(
            String cursor, 
            Integer rowLimit, 
            Integer columnLimit
            ) throws PaasException;
    
    @DELETE
    public void truncateRows(
            ) throws PaasException;
    
    // Row level API
    @GET
    @Path("{key}")
    public QueryResult readRow(
            @PathParam("key")                               String  key, 
            @QueryParam("count")    @DefaultValue("0")      Integer columnCount,
            @QueryParam("start")    @DefaultValue("")       String  startColumn,
            @QueryParam("end")      @DefaultValue("")       String  endColumn,
            @QueryParam("reversed") @DefaultValue("false")  Boolean reversed
            ) throws PaasException;
    
    @DELETE
    @Path("{key}")
    public void deleteRow(
            @PathParam("key")    String key
            ) throws PaasException;
    
    @POST
    @Path("{db}/{table}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void updateRow(
            @PathParam("db")    String db,
            @PathParam("table")    String table,
            JsonObject rowData
            ) throws PaasException ;
    
    // Row level API
    @GET
    @Path("{key}/{column}")
    public QueryResult readColumn(
            @PathParam("key")    String key,
            @PathParam("column") String column
            ) throws PaasException, NotFoundException;
    
    @POST
    @Path("{key}/{column}")
    public void updateColumn(
            @PathParam("key")    String key,
            @PathParam("column") String column,
            String value
            ) throws PaasException, NotFoundException;
    
    @DELETE
    @Path("{key}/{column}")
    public void deleteColumn(
            @PathParam("key")    String key,
            @PathParam("column") String column
            ) throws PaasException;
    
    // TODO: Batch operations
    
    // TODO: Pagination
    
    // TODO: Index search
}
