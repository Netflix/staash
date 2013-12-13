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

import com.netflix.paas.mesh.db.Entry;

public class EntryHolder {
    private Entry tuple;
    
    public EntryHolder(Entry tuple) {
        this.tuple = tuple;
    }
    
    public synchronized boolean delete(long timestamp) {
        if (timestamp > tuple.getTimestamp()) {
            this.tuple = new Entry(tuple.getKey(), null, timestamp);
            return true;
        }
        return false;
    }
    
    public synchronized boolean set(Entry tuple) {
        if (tuple.getTimestamp() > this.tuple.getTimestamp()) {
            this.tuple = tuple;
            return true;
        }
        return false;
    }
    
    public synchronized boolean isDeleted() {
        return this.tuple.getValue() == null;
    }
    
    public synchronized Entry getEntry() {
        return tuple;
    }
}
