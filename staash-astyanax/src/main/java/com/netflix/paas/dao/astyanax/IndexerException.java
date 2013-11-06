package com.netflix.paas.dao.astyanax;

public class IndexerException extends Exception {
    public IndexerException(String message, Exception e) {
        super(message, e);
    }
    
    public IndexerException(String message) {
        super(message);
    }
}
