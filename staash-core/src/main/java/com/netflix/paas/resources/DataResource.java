package com.netflix.paas.resources;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.netflix.paas.events.SchemaChangeEvent;
import com.netflix.paas.exceptions.NotFoundException;
import com.netflix.paas.provider.TableDataResourceFactory;

@Path("/v1/datares")
public class DataResource {
    private static final Logger LOG = LoggerFactory.getLogger(DataResource.class);
    
    private volatile HashMap<String, DbDataResource> schemaResources = Maps.newHashMap();
    
    private final Map<String, TableDataResourceFactory> tableDataResourceFactories;
    
    @Inject
    public DataResource(Map<String, TableDataResourceFactory> tableDataResourceFactories) {
        LOG.info("Creating DataResource");
        
        this.tableDataResourceFactories = tableDataResourceFactories;
        
        Preconditions.checkArgument(!tableDataResourceFactories.isEmpty(), "No TableDataResourceFactory instances exists.");
    }
    
    /**
     * Notification that a schema change was auto identified.  We recreate the entire schema
     * structure for the REST API.
     * @param event
     */
    @Subscribe
    public synchronized void schemaChangeEvent(SchemaChangeEvent event) {
        LOG.info("Schema changed " + event.getSchema().getName());
        DbDataResource resource = new DbDataResource(event.getSchema(), tableDataResourceFactories);
        HashMap<String, DbDataResource> newResources = Maps.newHashMap(schemaResources);
        newResources.put(event.getSchema().getName(), resource);
        schemaResources = newResources;
    }
    
    // Root API
//    @GET
//    public List<SchemaEntity> listSchemas() {
////        LOG.info("");
////        LOG.info("listSchemas");
////        return Lists.newArrayList(schemaService.listSchema());
//        return null;
//    }
    @GET
    @Produces("text/plain")
    public String hello() {
        return "hello";
    }
    
    @Path("{schema}")
    public DbDataResource getSchemaDataResource(
          @PathParam("schema") String schemaName
          ) throws NotFoundException {
        DbDataResource resource = schemaResources.get(schemaName);
        if (resource == null) {
            throw new NotFoundException(DbDataResource.class, schemaName);
        }
        return resource;
    }
}
