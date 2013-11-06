package com.netflix.paas.dao;

import java.util.Map;

import org.apache.commons.configuration.AbstractConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.netflix.paas.exceptions.NotFoundException;

/**
 * Return an implementation of a DAO by schemaName and type.  The schema name makes is possible
 * to have separates daos for the same type.  
 * 
 * @author elandau
 */
public class DaoProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DaoProvider.class);
    
    private static final String DAO_TYPE_FORMAT = "com.netflix.paas.schema.%s.type";
            
    private final Map<String, DaoSchemaProvider> schemaTypes;
    private final Map<String, DaoSchema>         schemas;
    
    private final AbstractConfiguration  configuration;
    
    @Inject
    public DaoProvider(Map<String, DaoSchemaProvider> schemaTypes, AbstractConfiguration configuration) {
        this.schemaTypes = schemaTypes;
        this.schemas     = Maps.newHashMap();
        this.configuration = configuration;
    }
    
    public <T> Dao<T> getDao(String schemaName, Class<T> type) throws NotFoundException {
        return getDao(new DaoKey<T>(schemaName, type));
    }
    
    public synchronized <T> Dao<T> getDao(DaoKey<T> key) throws NotFoundException  {
        return getSchema(key.getSchema()).getDao(key.getType());
    }
    
    public synchronized DaoSchema getSchema(String schemaName) throws NotFoundException {
        DaoSchema schema = schemas.get(schemaName);
        if (schema == null) {
            String propertyName = String.format(DAO_TYPE_FORMAT, schemaName.toLowerCase());
            String daoType = configuration.getString(propertyName);
            Preconditions.checkNotNull(daoType, "No DaoType specified for " + schemaName + " (" + propertyName + ")");
            DaoSchemaProvider provider = schemaTypes.get(daoType);
            if (provider == null) {
                LOG.warn("Unable to find DaoManager for schema " + schemaName + "(" + daoType + ")");
                throw new NotFoundException(DaoSchemaProvider.class, daoType);
            }
            schema = provider.getSchema(schemaName);
            schemas.put(schemaName, schema);
            LOG.info("Created DaoSchema for " + schemaName);
        }
        return schema;
    }
}
