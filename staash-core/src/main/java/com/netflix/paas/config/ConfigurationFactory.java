/*******************************************************************************
 * /***
 *  *
 *  *  Copyright 2013 Netflix, Inc.
 *  *
 *  *     Licensed under the Apache License, Version 2.0 (the "License");
 *  *     you may not use this file except in compliance with the License.
 *  *     You may obtain a copy of the License at
 *  *
 *  *         http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *     Unless required by applicable law or agreed to in writing, software
 *  *     distributed under the License is distributed on an "AS IS" BASIS,
 *  *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *     See the License for the specific language governing permissions and
 *  *     limitations under the License.
 *  *
 ******************************************************************************/
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
