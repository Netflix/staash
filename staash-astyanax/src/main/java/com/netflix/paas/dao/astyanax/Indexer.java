/*******************************************************************************
 * /***
 *  *
 *  *  Copyright 2013 Netflix, Inc.
 *  *
 *  *     Licensed under the Apache License, Version 2.0 (the "License");
 *  *     you may not use this file except in compliance with the License.
 *  *     You may obtain a copy of the License at
 *  *
 *  *         http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *     Unless required by applicable law or agreed to in writing, software
 *  *     distributed under the License is distributed on an "AS IS" BASIS,
 *  *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *     See the License for the specific language governing permissions and
 *  *     limitations under the License.
 *  *
 ******************************************************************************/
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
