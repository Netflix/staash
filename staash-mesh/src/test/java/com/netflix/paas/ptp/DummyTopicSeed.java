package com.netflix.paas.ptp;

import com.netflix.paas.mesh.db.Entry;
import com.netflix.paas.mesh.db.TopicRegistry;
import com.netflix.paas.mesh.db.memory.MemoryTopicFactory;
import com.netflix.paas.mesh.seed.TopicSeed;

public class DummyTopicSeed implements TopicSeed {
    private static final String TOPIC_NAME = "test";
    public DummyTopicSeed() {
        final TopicRegistry    topics   = new TopicRegistry(new MemoryTopicFactory());
        
        topics.createTopic(TOPIC_NAME);
        topics.addEntry(TOPIC_NAME,  new Entry("Key1", "Value1", System.currentTimeMillis()));
        topics.addEntry(TOPIC_NAME,  new Entry("Key2", "Value2", System.currentTimeMillis()));
    }
    
}
