package com.netflix.paas.config.base;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.configuration.AbstractConfiguration;

import com.google.common.base.Supplier;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.paas.config.ConfigurationFactory;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;

public class CglibArchaeusConfigurationFactory implements ConfigurationFactory {
    @Override
    public <T> T get(Class<T> configClass) throws Exception {
        return get(configClass, 
                DynamicPropertyFactory.getInstance(),
                ConfigurationManager.getConfigInstance());
    }
    
    @SuppressWarnings({ "unchecked", "static-access" })
    public <T> T get(Class<T> configClass, DynamicPropertyFactory propertyFactory, AbstractConfiguration configuration) throws Exception {
        final Map<String, Supplier<?>> methods = ConfigurationProxyUtils.getMethodSuppliers(configClass, propertyFactory, configuration);
        
        if (configClass.isInterface()) {
            final Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(configClass);
            enhancer.setCallback(new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    Supplier<?> supplier = (Supplier<?>)methods.get(method.getName());
                    return supplier.get();
                }
            });
            return (T) enhancer.create();
        }
        else {
            final Enhancer enhancer = new Enhancer();
            final Object obj = (T) enhancer.create(configClass, 
                new net.sf.cglib.proxy.InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    Supplier<?> supplier = (Supplier<?>)methods.get(method.getName());
                    if (supplier == null) {
                        return method.invoke(proxy,  args);
                    }
                    return supplier.get();
                }
            });

            ConfigurationProxyUtils.assignFieldValues(obj, configClass, propertyFactory, configuration);
            return (T)obj;
        }
     }
}
