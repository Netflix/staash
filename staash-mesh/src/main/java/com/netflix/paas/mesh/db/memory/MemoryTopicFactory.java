package com.netflix.paas.mesh.db.memory;

import com.netflix.paas.mesh.db.Topic;
import com.netflix.paas.mesh.db.TopicFactory;

public class MemoryTopicFactory implements TopicFactory {
    @Override
    public Topic create(String name) {
        return new MemoryTopic(name);
    }
}
