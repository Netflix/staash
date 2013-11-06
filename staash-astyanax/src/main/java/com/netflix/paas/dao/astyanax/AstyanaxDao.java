package com.netflix.paas.dao.astyanax;

import java.util.Collection;
import java.util.List;

import javax.persistence.PersistenceException;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.entitystore.DefaultEntityManager;
import com.netflix.astyanax.entitystore.EntityManager;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.recipes.reader.AllRowsReader;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.paas.dao.Dao;

/**
 * Simple implementation of a Dao on top of the astyanax EntityManager API
 * @author elandau
 *
 * @param <T>
 */
public class AstyanaxDao<T> implements Dao<T> {
    private final static String DAO_NAME = "astyanax";
    
    private final EntityManager<T, String>      manager;
    private final Keyspace                      keyspace;
    private final ColumnFamily<String, String>  columnFamily;
    private final String                        entityName;
    private final String                        prefix;
    
    private static String entityNameFromClass(Class<?> entityType) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, 
                StringUtils.removeEnd(StringUtils.substringAfterLast(entityType.getName(),  "."), "Entity"));
    }
    
    public AstyanaxDao(Keyspace keyspace, Class<T> entityType) {
        this.keyspace     = keyspace;
        this.entityName   = entityNameFromClass(entityType);
        this.columnFamily = new ColumnFamily<String, String>(this.entityName, StringSerializer.get(), StringSerializer.get());
        this.prefix       = "";
        
        manager = new DefaultEntityManager.Builder<T, String>()
                .withKeyspace(keyspace)
                .withColumnFamily(columnFamily)
                .withEntityType(entityType)
                .build();
    }
    
    public AstyanaxDao(Keyspace keyspace, Class<T> entityType, String columnFamilyName) {
        this.keyspace     = keyspace;
        this.entityName   = entityNameFromClass(entityType);
        this.columnFamily = new ColumnFamily<String, String>(columnFamilyName, StringSerializer.get(), StringSerializer.get());
        this.prefix       = this.entityName + ":";
        
        manager = new DefaultEntityManager.Builder<T, String>()
                .withKeyspace(keyspace)
                .withColumnFamily(columnFamily)
                .withEntityType(entityType)
                .build();
    }
    
    @Override
    public T read(String id)  throws PersistenceException {
        return this.manager.get(id);
    }

    @Override
    public void write(T entity)  throws PersistenceException {
        this.manager.put(entity);
    }

    @Override
    public Collection<T> list()  throws PersistenceException{
        return this.manager.getAll();
    }

    @Override
    public void delete(String id)  throws PersistenceException{
        this.manager.delete(id);
    }

    @Override
    public void createTable()  throws PersistenceException {
        try {
            keyspace.createColumnFamily(columnFamily, null);
        } catch (ConnectionException e) {
            throw new PersistenceException("Failed to create column family : " + columnFamily.getName(), e);
        }
    }

    @Override
    public void deleteTable()  throws PersistenceException{
        try {
            keyspace.dropColumnFamily(columnFamily);
        } catch (ConnectionException e) {
            throw new PersistenceException("Failed to drop column family : " + columnFamily.getName(), e);
        }
    }

    @Override
    public String getEntityType() {
        return this.entityName;
    }

    @Override
    public String getDaoType() {
        return DAO_NAME;
    }

    @Override
    public Boolean healthcheck() {
        return isExists();
    }

    @Override
    public Boolean isExists() {
        try {
            return keyspace.describeKeyspace().getColumnFamily(columnFamily.getName()) != null;
        }
        catch (Throwable t) {
            return false;
        }
    }

    @Override
    public void shutdown() {
    }

    @Override
    public Collection<String> listIds() throws PersistenceException {
        final List<String> ids = Lists.newArrayList();
        try {
            new AllRowsReader.Builder<String, String>(keyspace, columnFamily)
                .withIncludeEmptyRows(false)
                .forEachRow(new Function<Row<String,String>, Boolean>() {
                    @Override
                    public Boolean apply(Row<String, String> row) {
                        ids.add(row.getKey());
                        return true;
                    }
                })
                .build()
                .call();
        } catch (Exception e) {
            throw new PersistenceException("Error trying to fetch row ids", e);
        }
            
        return ids;
    }

    @Override
    public Collection<T> read(Collection<String> keys) throws PersistenceException {
        return this.manager.get(keys);
    }
}
