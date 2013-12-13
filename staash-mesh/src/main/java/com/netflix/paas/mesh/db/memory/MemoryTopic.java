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
package com.netflix.paas.mesh.db.memory;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.collect.Maps;
import com.netflix.paas.mesh.db.Entry;
import com.netflix.paas.mesh.db.Topic;

public class MemoryTopic implements Topic {
    private final String name;
    
    private final ConcurrentMap<String, EntryHolder> rows;
    
    private volatile long deletedTimestamp;
    
    private AtomicLong size = new AtomicLong(0);
    
    public MemoryTopic(String name) {
        this.name = name;
        rows = Maps.newConcurrentMap();
    }
    
    @Override
    public boolean deleteTopic(long timestamp) {
        if (timestamp < deletedTimestamp) {
            return false;
        }
        
        deletedTimestamp = timestamp;
        for (java.util.Map.Entry<String, EntryHolder> entry : rows.entrySet()) {
            if (entry.getValue().delete(deletedTimestamp)) {
                size.incrementAndGet();
            }
        }
        return true;
    }

    @Override
    public boolean upsert(Entry entry) {
        EntryHolder existing = rows.putIfAbsent(entry.getKey(), new EntryHolder(entry));
        if (existing != null) {
            return existing.set(entry);
        }
        size.incrementAndGet();
        return true;
    }

    @Override
    public Entry read(String key) {
        EntryHolder holder = rows.get(key);
        if (holder == null) {
            return null;
        }
        return holder.getEntry();
    }

    @Override
    public boolean delete(Entry entry) {
        EntryHolder holder = rows.get(entry.getKey());
        if (holder != null) {
            if (holder.delete(entry.getTimestamp())) {
                size.decrementAndGet();
                return true;
            }
        }
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getCreatedTime() {
        return 0;
    }

    @Override
    public long getEntryCount() {
        return size.get();
    }

    @Override
    public long getDeletedTime() {
        return 0;
    }

}
