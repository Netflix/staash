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
package com.netflix.paas.cassandra.events;

import com.netflix.paas.cassandra.keys.KeyspaceKey;

public class KeyspaceUpdateEvent {
    private final KeyspaceKey keyspaceKey;

    public KeyspaceUpdateEvent(KeyspaceKey keyspaceKey) {
        super();
        this.keyspaceKey = keyspaceKey;
    }
    
    public KeyspaceKey getKeyspaceKey() {
        return keyspaceKey;
    }
}
