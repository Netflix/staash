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
