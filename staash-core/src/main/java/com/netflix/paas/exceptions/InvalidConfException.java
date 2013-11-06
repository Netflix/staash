package com.netflix.paas.exceptions;

public class InvalidConfException extends Exception{
    private static final long serialVersionUID = 1L;
    public InvalidConfException() {
        super();
    }
    public InvalidConfException(String message) {
        super(message);
    }
}
