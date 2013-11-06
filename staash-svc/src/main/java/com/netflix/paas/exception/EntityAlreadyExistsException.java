package com.netflix.paas.exception;

public class EntityAlreadyExistsException extends PaasException{
    private static final long serialVersionUID = 1L;
    public EntityAlreadyExistsException() {
        super();
    }
    public EntityAlreadyExistsException(String message) {
        // TODO Auto-generated constructor stub
        super(message);

    }
}
