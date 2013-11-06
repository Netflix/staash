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
