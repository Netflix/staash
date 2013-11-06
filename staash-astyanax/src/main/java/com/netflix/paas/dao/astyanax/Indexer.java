package com.netflix.paas.dao.astyanax;

import java.util.Collection;
import java.util.Map;

/**
 * Very very very simple indexing API.  
 * 
 * @author elandau
 *
 */
public interface Indexer {
    /**
     * Add the id to the tags
     * @param id
     * @param tags
     */
    public void tagId(String id, Map<String, String> tags) throws IndexerException ;

    /**
     * Remove id from all it's tags
     * @param id
     */
    public void removeId(String id) throws IndexerException ;
    
    /**
     * Get all tags for a document
     * @param id
     * @return
     */
    public Map<String, String> getTags(String id) throws IndexerException;
    
    /**
     * Find all ids that have one or more of these tags
     * @param tags
     * @return
     */
    public Collection<String> findUnion(Map<String, String> tags) throws IndexerException ;
    
    /**
     * Find all ids that have all of the tags
     * @param tags
     * @return
     */
    public Collection<String> findIntersection(Map<String, String> tags) throws IndexerException ;
    
    /**
     * Find all ids that match the tag
     * @param tag
     * @return
     */
    public Collection<String> find(String name, String value) throws IndexerException ;
    
    /**
     * Create the underlying storage
     */
    public void createStorage() throws IndexerException ;
}
