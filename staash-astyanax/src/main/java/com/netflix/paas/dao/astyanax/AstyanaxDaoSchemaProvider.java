package com.netflix.paas.dao.astyanax;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.exceptions.BadRequestException;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.paas.cassandra.provider.KeyspaceClientProvider;
import com.netflix.paas.dao.Dao;
import com.netflix.paas.dao.DaoSchemaProvider;
import com.netflix.paas.dao.DaoSchema;
import com.netflix.paas.exceptions.NotFoundException;

/**
 * Astyanax based Dao factory for persisting PAAS state
 * 
 * @author elandau
 *
 */
public class AstyanaxDaoSchemaProvider implements DaoSchemaProvider { 
    private final Logger LOG = LoggerFactory.getLogger(AstyanaxDaoSchemaProvider.class);
    
    private final static String CONFIG_PREFIX_FORMAT = "com.netflix.paas.schema.%s";
            
    private final KeyspaceClientProvider    keyspaceProvider;
    private final Map<String, DaoSchema>    schemas = Maps.newHashMap();
    private final AbstractConfiguration     configuration;
    
    public class AstyanaxDaoSchema implements DaoSchema {
        private final IdentityHashMap<Class<?>, Dao<?>> daos = Maps.newIdentityHashMap();
        private final Keyspace keyspace;
        private final String schemaName;
        
        public AstyanaxDaoSchema(String schemaName, Keyspace keyspace) {
            this.keyspace = keyspace;
            this.schemaName = schemaName;
            
            Configuration config = configuration.subset(String.format(CONFIG_PREFIX_FORMAT, schemaName.toLowerCase()));
            if (config.getBoolean("autocreate", false)) {
                try {
                    createSchema();
                }
                catch (Exception e) {
                    LOG.error("Error creating column keyspace", e);
                }
            }
        }
        
        @Override
        public synchronized void createSchema() {
            final Properties props = ConfigurationConverter.getProperties(configuration.subset(String.format(CONFIG_PREFIX_FORMAT, schemaName.toLowerCase())));
            try {
                props.setProperty("name", props.getProperty("keyspace"));
                LOG.info("Creating schema: " + schemaName + " " + props);
                this.keyspace.createKeyspace(props);
            } catch (ConnectionException e) {
                LOG.error("Failed to create schema '{}' with properties '{}'", new Object[]{schemaName, props.toString(), e});
                throw new RuntimeException("Failed to create keyspace " + keyspace.getKeyspaceName(), e);
            }
        }

        @Override
        public synchronized void dropSchema() {
            try {
                this.keyspace.dropKeyspace();
            } catch (ConnectionException e) {
                throw new RuntimeException("Failed to drop keyspace " + keyspace.getKeyspaceName(), e);
            }
        }

        @Override
        public synchronized Collection<Dao<?>> listDaos() {
            return Lists.newArrayList(daos.values());
        }

        @Override
        public boolean isExists() {
            try {
                this.keyspace.describeKeyspace();
                return true;
            }
            catch (BadRequestException e) {
                return false;
            }
            catch (Exception e) {
                throw new RuntimeException("Failed to determine if keyspace " + keyspace.getKeyspaceName() + " exists", e);
            }
        }
        
        @Override
        public synchronized <T> Dao<T> getDao(Class<T> type) {
            Dao<?> dao = daos.get(type);
            if (dao == null) {
                dao = new AstyanaxDao<T>(keyspace, type);
                daos.put(type, dao);
            }
            return (Dao<T>) dao;
        }
    }
    
    @Inject
    public AstyanaxDaoSchemaProvider(KeyspaceClientProvider keyspaceProvider, AbstractConfiguration configuration) {  
        this.keyspaceProvider = keyspaceProvider;
        this.configuration    = configuration;
    }

    @PostConstruct
    public void start() {

    }
    
    @PreDestroy 
    public void stop() {
    }

    @Override
    public synchronized Collection<DaoSchema> listSchemas() {
        return Lists.newArrayList(schemas.values());
    }

    @Override
    public synchronized DaoSchema getSchema(String schemaName) throws NotFoundException {
        AstyanaxDaoSchema schema = (AstyanaxDaoSchema)schemas.get(schemaName);
        if (schema == null) {
            LOG.info("Creating schema '{}'", new Object[]{schemaName});
            Keyspace keyspace = keyspaceProvider.acquireKeyspace(schemaName);
            schema = new AstyanaxDaoSchema(schemaName, keyspace);
            schemas.put(schemaName, schema);
        }
        return schema;
    }
}
