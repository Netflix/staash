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
package com.netflix.staash.mesh.db;

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
