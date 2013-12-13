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
package com.netflix.paas.tasks;

import java.util.Map;

public class TaskContext {
    
    private final Map<String, Object> parameters;
    private final String className;
   
    public TaskContext(Class<?> className, Map<String, Object> arguments) {
        this.className = className.getCanonicalName();
        this.parameters = arguments;
    }
    
    public Object getParamater(String key) {
        return this.parameters.get(key);
    }
    
    public String getStringParameter(String key) {
        return (String)getParamater(key);
    }
    
    public String getStringParameter(String key, String defaultValue) {
        Object value = getParamater(key);
        return (value == null) ? defaultValue : (String)value;
    }
    
    public Boolean getBooleanParameter(String key) {
        return (Boolean)getParamater(key);
    }
    
    public Boolean getBooleanParameter(String key, Boolean defaultValue) {
        Object value = getParamater(key);
        return (value == null) ? defaultValue : (Boolean)value;
    }
    
    public Integer getIntegerParameter(String key) {
        return (Integer)getParamater(key);
    }
    
    public Integer getIntegerParameter(String key, Integer defaultValue) {
        Object value = getParamater(key);
        return (value == null) ? defaultValue : (Integer)value;
    }
    
    public Long getLongParameter(String key) {
        return (Long)getParamater(key);
    }
    
    public Long getLongParameter(String key, Long defaultValue) {
        Object value = getParamater(key);
        return (value == null) ? defaultValue : (Long)value;
    }
    
    public String getClassName(String className) {
        return this.className;
    }
    
    public String getKey() {
        if (parameters == null || !parameters.containsKey("key"))
            return className;
        
        return className + "$" + parameters.get("key");
    }
}
