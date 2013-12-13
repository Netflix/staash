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
package com.netflix.paas.exceptions;

import javax.persistence.PersistenceException;

public class NotFoundException extends PersistenceException {
    private static final long serialVersionUID = 1320561942271503959L;
    
    private final String type;
    private final String id;
    
    public NotFoundException(Class<?> clazz, String id) {
        this(clazz.getName(), id);
    }
    
    public NotFoundException(String type, String id) {
        super(String.format("Cannot find %s:%s", type, id));
        this.type = type;
        this.id = id;
    }
    
    public String getType() {
        return type;
    }
    
    public String getId() {
        return id;
    }
}
