package com.netflix.paas.exceptions;

public class PaasException extends Exception {
    
    public PaasException(String message, Exception e) {
        super(message, e);
    }
}
