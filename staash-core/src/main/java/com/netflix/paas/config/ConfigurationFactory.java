package com.netflix.paas.config;

import org.apache.commons.configuration.AbstractConfiguration;

import com.netflix.config.DynamicPropertyFactory;

/**
 * Interface for creating a proxied Configuration of an interface which 
 * uses the Configuration annotation
 * 
 * @author elandau
 */
public interface ConfigurationFactory {
    /**
     * Create an instance of the configuration interface using the default DynamicPropertyFactory and AbstractConfiguration
     * 
     * @param configClass
     * @return
     * @throws Exception
     */
    public <T> T get(Class<T> configClass) throws Exception;
    
    /**
     * Create an instance of the configuration interface
     * 
     * @param configClass
     * @param propertyFactory
     * @param configuration
     * @return
     * @throws Exception
     */
    public <T> T get(Class<T> configClass, DynamicPropertyFactory propertyFactory, AbstractConfiguration configuration) throws Exception;

}
