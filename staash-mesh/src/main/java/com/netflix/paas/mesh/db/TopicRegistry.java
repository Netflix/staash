package com.netflix.paas.mesh.db;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;

public class TopicRegistry {
    private ConcurrentMap<String, Topic> topics = Maps.newConcurrentMap();
    private TopicFactory topicFactory;
    
    public TopicRegistry(TopicFactory topicFactory) {
        this.topicFactory = topicFactory;
    }
    
    public boolean createTopic(String topicName) {
        Topic topic = topicFactory.create(topicName);
        if (null == topics.putIfAbsent(topicName,  topic)) {
            return true;
        }
        return false;
    }
    
    public boolean removeTopic(String topicName, long timestamp) {
        Topic topic = topics.get(topicName);
        if (topic != null) {
            return topic.deleteTopic(timestamp);
        }
        return false;
    }
    
    public boolean addEntry(String topicName, Entry tuple) {
        Topic topic = topics.get(topicName);
        if (topic != null) {
            return topic.upsert(tuple);
        }
        return false;
    }
    
    public Iterable<String> listTopics() {
        return topics.keySet();
    }
    
}
