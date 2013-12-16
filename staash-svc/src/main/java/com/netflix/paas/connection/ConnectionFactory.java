package com.netflix.paas.connection;

import com.netflix.paas.json.JsonObject;

public interface ConnectionFactory {
    public PaasConnection createConnection(JsonObject storageConf, String db) ;
}
