package com.netflix.paas.exceptions;

public class AlreadyExistsException extends Exception {
    private static final long serialVersionUID = 5796840344994375807L;
    
    private final String type;
    private final String id;
    
    public AlreadyExistsException(String type, String id) {
        super("%s:%s already exists".format(type, id));
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
