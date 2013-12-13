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

import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import com.netflix.config.PropertyWrapper;

public class ArchaeusPaasConfiguration implements PaasConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(ArchaeusPaasConfiguration.class);
    
    private static final DynamicStringProperty PAAS_PROPS_FILE = DynamicPropertyFactory.getInstance().getStringProperty("paas.client.props", "paas");
    private final String namespace;
    private static ConcurrentMap<String, PropertyWrapper<?>> parameters;
    
    @Inject
    public ArchaeusPaasConfiguration(@Named("namespace") String namespace) {
        LOG.info("Created");
        this.namespace = namespace;
    }
    
    @PostConstruct
    public void initialize() {
        LOG.info("Initializing");
        String filename = PAAS_PROPS_FILE.get();
        try {
            ConfigurationManager.loadCascadedPropertiesFromResources(filename);
        } catch (IOException e) {
            LOG.warn(
                    "Cannot find the properties specified : {}. This may be okay if there are other environment specific properties or the configuration is installed with a different mechanism.",
                    filename);
        }
    }

    @Override
    public Integer getInteger(GenericProperty name) {
        PropertyWrapper<Integer> prop = (PropertyWrapper<Integer>)parameters.get(name.getName());
        if (prop == null) {
            PropertyWrapper<Integer> newProp = DynamicPropertyFactory.getInstance().getIntProperty(namespace + name, Integer.parseInt(name.getDefault()));
            prop = (PropertyWrapper<Integer>) parameters.putIfAbsent(name.getName(),  newProp);
            if (prop == null)
                prop = newProp;
        }
        return prop.getValue();
    }

    @Override
    public String getString(GenericProperty name) {
        PropertyWrapper<String> prop = (PropertyWrapper<String>)parameters.get(name.getName());
        if (prop == null) {
            PropertyWrapper<String> newProp = DynamicPropertyFactory.getInstance().getStringProperty(namespace + name, name.getDefault());
            prop = (PropertyWrapper<String>) parameters.putIfAbsent(name.getName(),  newProp);
            if (prop == null)
                prop = newProp;
        }
        return prop.getValue();
    }

    @Override
    public Boolean getBoolean(GenericProperty name) {
        PropertyWrapper<Boolean> prop = (PropertyWrapper<Boolean>)parameters.get(name.getName());
        if (prop == null) {
            PropertyWrapper<Boolean> newProp = DynamicPropertyFactory.getInstance().getBooleanProperty(namespace + name, Boolean.parseBoolean(name.getDefault()));
            prop = (PropertyWrapper<Boolean>) parameters.putIfAbsent(name.getName(),  newProp);
            if (prop == null)
                prop = newProp;
        }
        return prop.getValue();
    }

    @Override
    public Long getLong(GenericProperty name) {
        PropertyWrapper<Long> prop = (PropertyWrapper<Long>)parameters.get(name.getName());
        if (prop == null) {
            PropertyWrapper<Long> newProp = DynamicPropertyFactory.getInstance().getLongProperty(namespace + name, Long.parseLong(name.getDefault()));
            prop = (PropertyWrapper<Long>) parameters.putIfAbsent(name.getName(),  newProp);
            if (prop == null)
                prop = newProp;
        }
        return prop.getValue();
    }

    @Override
    public Double getDouble(GenericProperty name) {
        PropertyWrapper<Double> prop = (PropertyWrapper<Double>)parameters.get(name.getName());
        if (prop == null) {
            PropertyWrapper<Double> newProp = DynamicPropertyFactory.getInstance().getDoubleProperty(namespace + name, Double.parseDouble(name.getDefault()));
            prop = (PropertyWrapper<Double>) parameters.putIfAbsent(name.getName(),  newProp);
            if (prop == null)
                prop = newProp;
        }
        return prop.getValue();
    }

}
