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
package com.netflix.paas.mesh.db;

public interface Topic {
    /**
     * Insert an entry into the topic
     * @param tuple
     * @return 
     */
    public boolean upsert(Entry tuple);
    
    /**
     * Read an entry from the topic
     * @param key
     * @return
     */
    public Entry  read(String key);
    
    /**
     * Delete an entry from the topic
     * @param tuple
     * @return
     */
    public boolean delete(Entry tuple);
    
    /**
     * Get the topic name
     * @return
     */
    public String getName();
    
    /**
     * Get the deleted time of the topic.  
     * @return  Time topic was deleted of 0 if it was not
     */
    public long getDeletedTime();
    
    /**
     * Get the time when the topic was created
     * @return
     */
    public long getCreatedTime();
    
    /**
     * Get the number of entries in the topic
     * @return
     */
    public long getEntryCount();
    
    /**
     * Make the topic as having been deleted.  Delete will only apply
     * if the topic last modified timestamp is less than the deleted time
     * @param timestamp
     * @return 
     */
    public boolean deleteTopic(long timestamp);
}
