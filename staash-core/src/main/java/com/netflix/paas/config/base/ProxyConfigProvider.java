package com.netflix.paas.config.base;

import com.google.inject.Provider;

public class ProxyConfigProvider implements Provider<Object> {
    @Override
    public Object get() {
        System.out.println("ProxyConfigProvider");
        return null;
    }

}
