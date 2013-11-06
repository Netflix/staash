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
