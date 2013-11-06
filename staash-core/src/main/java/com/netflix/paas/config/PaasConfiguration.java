package com.netflix.paas.config;

public interface PaasConfiguration {
    public Integer getInteger(GenericProperty name);
    
    public String  getString(GenericProperty name);
    
    public Boolean getBoolean(GenericProperty name);
    
    public Double getDouble(GenericProperty name);
    
    public Long getLong(GenericProperty name);
}
