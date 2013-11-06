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
