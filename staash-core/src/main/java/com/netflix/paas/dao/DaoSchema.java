package com.netflix.paas.dao;

import java.util.Collection;

public interface DaoSchema {
    /**
     * Create the underlying storage for the schema.  Does not create the Daos
     */
    public void createSchema();
    
    /**
     * Delete store for the schema and all child daos
     */
    public void dropSchema();
    
    /**
     * Get a dao for this type
     * @param type
     * @return
     */
    public <T> Dao<T> getDao(Class<T> type);
    
    /**
     * Retrive all Daos managed by this schema
     * @return
     */
    public Collection<Dao<?>> listDaos();
    
    /**
     * Determine if the storage for this schema exists
     * @return
     */
    public boolean isExists();
}
