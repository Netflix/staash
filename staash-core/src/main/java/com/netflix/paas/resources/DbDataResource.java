package com.netflix.paas.resources;

import java.util.Collection;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.netflix.paas.entity.DbEntity;
import com.netflix.paas.entity.TableEntity;
import com.netflix.paas.exceptions.NotFoundException;
import com.netflix.paas.provider.TableDataResourceFactory;

/**
 * REST interface to a specific schema.  This interface provides access to multiple tables
 * 
 * @author elandau
 */
public class DbDataResource {
    private static final Logger LOG = LoggerFactory.getLogger(DbDataResource.class);
    
    private final Map<String, TableDataResourceFactory>   tableDataResourceFactories;
    private final DbEntity                     schemaEntity;
    private final ImmutableMap<String, TableDataResource> tables;
    
    public DbDataResource(DbEntity schemaEntity, Map<String, TableDataResourceFactory> tableDataResourceFactories) {
        this.tableDataResourceFactories = tableDataResourceFactories;
        this.schemaEntity             = schemaEntity;
        
        ImmutableMap.Builder<String, TableDataResource> builder = ImmutableMap.builder();
        
        for (TableEntity table : schemaEntity.getTables().values()) {
            LOG.info("Adding table '{}' to schema '{}'", new Object[]{table.getTableName(), schemaEntity.getName()});
            try {
                Preconditions.checkNotNull(table.getStorageType());
                
                TableDataResourceFactory tableDataResourceFactory = tableDataResourceFactories.get(table.getStorageType());
                if (tableDataResourceFactory == null) {
                    throw new NotFoundException(TableDataResourceFactory.class, table.getStorageType());
                }
                
                builder.put(table.getTableName(), tableDataResourceFactory.getTableDataResource(table));
            }
            catch (Exception e) {
                LOG.error("Failed to create storage for table '{}' in schema '{}", new Object[]{table.getTableName(), schemaEntity.getName(), e});
            }
        }
        
        tables = builder.build();
    }
    
    @GET
    public Collection<TableEntity> listTables() {
        return schemaEntity.getTables().values();
    }

    @Path("{table}")
    public TableDataResource getTableSubresource(@PathParam("table") String tableName) throws NotFoundException {
        TableDataResource resource = tables.get(tableName);
        if (resource == null) {
            throw new NotFoundException(TableDataResource.class, tableName);
        }
        return resource;
    }
}
