package com.netflix.paas;

import com.netflix.astyanax.test.EmbeddedCassandra;

public class SingletonEmbeddedCassandra {
    
    private final EmbeddedCassandra         cassandra;
    private static class Holder {
        private final static SingletonEmbeddedCassandra instance = new SingletonEmbeddedCassandra();
    }
    
    
    public static SingletonEmbeddedCassandra getInstance() {
        return Holder.instance;
    }
    
    public SingletonEmbeddedCassandra() {
        try {
            cassandra = new EmbeddedCassandra();
            cassandra.start();
        } catch (Exception e) {
            throw new RuntimeException("Failed to start embedded cassandra", e);
        }
    }
    
    public void finalize() {
        try {
            cassandra.stop();
        }
        catch (Exception e) {
            
        }
    }
}
