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
package com.netflix.paas.config.base;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.PropertyWrapper;
import com.netflix.governator.annotations.Configuration;
import com.netflix.paas.config.annotations.DefaultValue;
import com.netflix.paas.config.annotations.Dynamic;

import java.lang.reflect.Field;

import org.apache.commons.configuration.AbstractConfiguration;

/**
 * Utility class used by ConfigurationProxyFactory implementations to proxy methods of a 
 * configuration interface using information from the Configuration annotation
 * 
 * @author elandau
 */
public class ConfigurationProxyUtils  {
    public static class PropertyWrapperSupplier<T> implements Supplier<T> {
        private final PropertyWrapper<T> wrapper;
        public PropertyWrapperSupplier(PropertyWrapper<T> wrapper) {
            this.wrapper = wrapper;
        }
        
        @Override
        public T get() {
            return this.wrapper.getValue();
        }
    }
    
    static Supplier<?> getDynamicSupplier(Class<?> type, String key, String defaultValue, DynamicPropertyFactory propertyFactory) {
        if (type.isAssignableFrom(String.class)) {
            return new PropertyWrapperSupplier<String>(
                    propertyFactory.getStringProperty(
                            key, 
                            defaultValue));
        }
        else if (type.isAssignableFrom(Integer.class)) {
            return new PropertyWrapperSupplier<Integer>(
                    propertyFactory.getIntProperty(
                            key, 
                            defaultValue == null ? 0 : Integer.parseInt(defaultValue)));
        }
        else if (type.isAssignableFrom(Double.class)) {
            return new PropertyWrapperSupplier<Double>(
                    propertyFactory.getDoubleProperty(
                            key, 
                            defaultValue == null ? 0.0 : Double.parseDouble(defaultValue)));
        }
        else if (type.isAssignableFrom(Long.class)) {
            return new PropertyWrapperSupplier<Long>(
                    propertyFactory.getLongProperty(
                            key, 
                            defaultValue == null ? 0L : Long.parseLong(defaultValue)));
        }
        else if (type.isAssignableFrom(Boolean.class)) {
            return new PropertyWrapperSupplier<Boolean>(
                    propertyFactory.getBooleanProperty(
                            key, 
                            defaultValue == null ? false : Boolean.parseBoolean(defaultValue)));
        }
        throw new RuntimeException("Unsupported value type " + type.getCanonicalName());
    }
    
    static Supplier<?> getStaticSupplier(Class<?> type, String key, String defaultValue, AbstractConfiguration configuration) {
        if (type.isAssignableFrom(String.class)) {
            return Suppliers.ofInstance(
                    configuration.getString(
                            key, 
                            defaultValue));
        }
        else if (type.isAssignableFrom(Integer.class)) {
            return Suppliers.ofInstance(
                    configuration.getInteger(
                            key, 
                            defaultValue == null ? 0 : Integer.parseInt(defaultValue)));
        }
        else if (type.isAssignableFrom(Double.class)) {
            return Suppliers.ofInstance(
                    configuration.getDouble(
                            key, 
                            defaultValue == null ? 0.0 : Double.parseDouble(defaultValue)));
        }
        else if (type.isAssignableFrom(Long.class)) {
            return Suppliers.ofInstance(
                    configuration.getLong(
                            key, 
                            defaultValue == null ? 0L : Long.parseLong(defaultValue)));
        }
        else if (type.isAssignableFrom(Boolean.class)) {
            return Suppliers.ofInstance(
                    configuration.getBoolean(
                            key, 
                            defaultValue == null ? false : Boolean.parseBoolean(defaultValue)));
        }
        throw new RuntimeException("Unsupported value type " + type.getCanonicalName());
    }
    
    static String getPropertyName(Method method, Configuration c) {
        String name = c.value();
        if (name.isEmpty()) {
            name = method.getName();
            name = StringUtils.removeStart(name, "is");
            name = StringUtils.removeStart(name, "get");
            name = name.toLowerCase();
        }
        return name;
    }
    
    static String getPropertyName(Field field, Configuration c) {
        String name = c.value();
        if (name.isEmpty()) {
            return field.getName();
        }
        return name;
    }
    
    static <T> Map<String, Supplier<?>> getMethodSuppliers(Class<T> configClass, DynamicPropertyFactory propertyFactory, AbstractConfiguration configuration) {
        final Map<String, Supplier<?>> properties = Maps.newHashMap();
        
        for (Method method : configClass.getMethods()) {
            Configuration c = method.getAnnotation(Configuration.class);
            if (c == null)
                continue;
            String defaultValue = null;
            DefaultValue dv = method.getAnnotation(DefaultValue.class);
            if (dv != null)
                defaultValue = dv.value();
            
            String name = getPropertyName(method, c);
            
            if (method.getReturnType().isAssignableFrom(Supplier.class)) {
                Type returnType = method.getGenericReturnType();
    
                if(returnType instanceof ParameterizedType){
                    ParameterizedType type = (ParameterizedType) returnType;
                    Class<?> actualType = (Class<?>)type.getActualTypeArguments()[0];
                    
                    properties.put(method.getName(), 
                            method.getAnnotation(Dynamic.class) != null 
                            ? Suppliers.ofInstance(getDynamicSupplier(actualType, name, defaultValue, propertyFactory))
                            : Suppliers.ofInstance(getStaticSupplier(actualType, name, defaultValue, configuration)));
                }
                else {
                    throw new RuntimeException("We'll get to this later");
                }
            }
            else {
                properties.put(method.getName(), 
                        method.getAnnotation(Dynamic.class) != null 
                        ? getDynamicSupplier(method.getReturnType(), name, defaultValue, propertyFactory)
                        : getStaticSupplier (method.getReturnType(), name, defaultValue, configuration));
            }
        }
        
        return properties;
    }
    
    static void assignFieldValues(final Object obj, Class<?> type, DynamicPropertyFactory propertyFactory, AbstractConfiguration configuration) throws Exception {
        
        // Iterate through all fields and set initial value as well as set up dynamic properties
        // where necessary
        for (final Field field : type.getFields()) {
            Configuration c = field.getAnnotation(Configuration.class);
            if (c == null)
                continue;
            
            String defaultValue = field.get(obj).toString();
            String name         = ConfigurationProxyUtils.getPropertyName(field, c);
            Supplier<?> supplier = ConfigurationProxyUtils.getStaticSupplier(field.getType(), name, defaultValue, configuration);
            field.set(obj,  supplier.get());
            
            if (field.getAnnotation(Dynamic.class) != null) {
                final PropertyWrapper<?> property;
                if (field.getType().isAssignableFrom(String.class)) {
                    property = propertyFactory.getStringProperty(
                                    name, 
                                    defaultValue);
                }
                else if (field.getType().isAssignableFrom(Integer.class)) {
                    property = propertyFactory.getIntProperty(
                                    name, 
                                    defaultValue == null ? 0 : Integer.parseInt(defaultValue));
                }
                else if (field.getType().isAssignableFrom(Double.class)) {
                    property = propertyFactory.getDoubleProperty(
                                    name, 
                                    defaultValue == null ? 0.0 : Double.parseDouble(defaultValue));
                }
                else if (field.getType().isAssignableFrom(Long.class)) {
                    property = propertyFactory.getLongProperty(
                                    name, 
                                    defaultValue == null ? 0L : Long.parseLong(defaultValue));
                }
                else if (field.getType().isAssignableFrom(Boolean.class)) {
                    property = propertyFactory.getBooleanProperty(
                                    name, 
                                    defaultValue == null ? false : Boolean.parseBoolean(defaultValue));
                }
                else {
                    throw new RuntimeException("Unsupported type " + field.getType());
                }
                
                property.addCallback(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            field.set(obj, property.getValue());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }        
    }

}

