package com.netflix.paas.exception;

public class InvalidConfException extends PaasException {
    private static final long serialVersionUID = 1L;
    public InvalidConfException() {
        super();
    }
    public InvalidConfException(String message) {
        super(message);
    }

}
