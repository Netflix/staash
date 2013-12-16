package com.netflix.paas.service;

public enum CLIENTTYPE {
    ASTYANAX("astyanax"),CQL("cql");
    private String type;
     CLIENTTYPE(String type) {
        this.type = type;;
    }
     public String getType() {
         return type;
     }
}
