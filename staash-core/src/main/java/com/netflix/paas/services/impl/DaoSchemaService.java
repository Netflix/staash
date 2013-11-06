package com.netflix.paas.services.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.netflix.paas.SchemaNames;
import com.netflix.paas.dao.Dao;
import com.netflix.paas.dao.DaoProvider;
import com.netflix.paas.entity.PassGroupConfigEntity;
import com.netflix.paas.entity.DbEntity;
import com.netflix.paas.entity.TableEntity;
import com.netflix.paas.events.SchemaChangeEvent;
import com.netflix.paas.resources.DataResource;
import com.netflix.paas.service.SchemaService;

/**
 * Schema registry using a persistent DAO
 * 
 * @author elandau
 *
 */
public class DaoSchemaService implements SchemaService {
    private final static Logger LOG = LoggerFactory.getLogger(DaoSchemaService.class);
    
    private final DaoProvider                daoProvider;
    private final String                     groupName;
    private final EventBus                   eventBus;
    
    private Dao<PassGroupConfigEntity>       groupDao;
    private Dao<DbEntity>         schemaDao;
    private Map<String, DbEntity> schemas;
    
    @Inject
    public DaoSchemaService(@Named("groupName") String groupName, DataResource dataResource, DaoProvider daoProvider, EventBus eventBus) {
        this.daoProvider = daoProvider;
        this.groupName   = groupName;
        this.eventBus    = eventBus;
    }

    @PostConstruct
    public void initialize() throws Exception {
        LOG.info("Initializing");
        groupDao  = daoProvider.getDao(SchemaNames.CONFIGURATION.name(), PassGroupConfigEntity.class);
        schemaDao = daoProvider.getDao(SchemaNames.CONFIGURATION.name(), DbEntity.class);
        
        try {
            refresh();
        }
        catch (Exception e) {
            LOG.error("Error refreshing schema list", e);
        }
    }
    
    @Override
    public List<DbEntity> listSchema() {
//        return ImmutableList.copyOf(this.dao.list());
        return null;
    }
    
    @Override
    public List<TableEntity> listSchemaTables(String schemaName) {
        return null;
    }

    @Override
    public List<TableEntity> listAllTables() {
        return null;
    }
    
    /**
     * Refresh the schema list from the DAO
     */
    @Override
    public void refresh() {
        LOG.info("Refreshing schema list for group: " + groupName);
        PassGroupConfigEntity group = groupDao.read(groupName);
        if (group == null) {
            LOG.error("Failed to load configuration for group: " + groupName);
        }
        else {
            Collection<DbEntity> foundEntities = schemaDao.read(group.getSchemas());
            if (foundEntities.isEmpty()) {
                LOG.warn("Not virtual schemas associated with group: " + groupName);
            }
            else {
                for (DbEntity entity : foundEntities) {
                    LOG.info("Found schema : " + entity.getName());
                    if (entity.hasTables()) {
                        for (Entry<String, TableEntity> table : entity.getTables().entrySet()) {
                            LOG.info(" Found table : " + table.getKey());
                        }
                    }
                    eventBus.post(new SchemaChangeEvent(entity, false));
                }
            }
        }
    }
}
