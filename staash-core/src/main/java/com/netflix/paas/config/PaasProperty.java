package com.netflix.paas.config;

public enum PaasProperty implements GenericProperty {
    TEST("test", "0")
    ;

    PaasProperty(String name, String defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }
    
    private final String name;
    private final String defaultValue;
    
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDefault() {
        return defaultValue;
    }

}
