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
package com.netflix.paas;

import java.lang.reflect.Method;

import org.apache.commons.lang.time.StopWatch;
import org.junit.Ignore;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.ProvidedBy;
import com.google.inject.Provider;
import com.netflix.config.ConfigurationManager;
import com.netflix.paas.config.annotations.Configuration;
import com.netflix.paas.config.annotations.DefaultValue;
import com.netflix.paas.config.annotations.Dynamic;
import com.netflix.paas.config.base.CglibArchaeusConfigurationFactory;
import com.netflix.paas.config.base.DynamicProxyArchaeusConfigurationFactory;
import com.netflix.paas.config.base.JavaAssistArchaeusConfigurationFactory;

public class ArchaeusPassConfigurationTest {
    
    public class TestProxyProvider<T> implements Provider<T> {
        private Class<T> type;
        
        public TestProxyProvider(Class<T> type) {
            this.type = type;
        }
        
        @Override
        public T get() {
            System.out.println("TestProxyProvider " + type.getCanonicalName());
            return null;
        }
    }
    
    public interface MyConfig {
        @Configuration(value="test.property.dynamic.string")
        @Dynamic
        @DefaultValue("DefaultA")
        String getDynamicString();
        
        @Configuration(value="test.property.dynamic.int")
        @DefaultValue("123")
        @Dynamic
        Integer getDynamicInt();
        
        @Configuration(value="test.property.dynamic.boolean")
        @DefaultValue("true")
        @Dynamic
        Boolean getDynamicBoolean();
        
        @Configuration(value="test.property.dynamic.long")
        @DefaultValue("456")
        @Dynamic
        Long getDynamicLong();
        
        @Configuration(value="test.property.dynamic.double")
        @DefaultValue("1.2")
        @Dynamic
        Double getDynamicDouble();

//        @Configuration(value="test.property.supplier.string", defaultValue="suppliedstring", dynamic=true)
//        Supplier<String> getDynamicStringSupplier();
        
        @Configuration(value="test.property.static.string")
        @DefaultValue("DefaultA")
        String getStaticString();
        
        @Configuration(value="test.property.static.int")
        @DefaultValue("123")
        Integer getStaticInt();
        
        @Configuration(value="test.property.static.boolean")
        @DefaultValue("true")
        Boolean getStaticBoolean();
        
        @Configuration(value="test.property.static.long")
        @DefaultValue("456")
        Long getStaticLong();
        
        @Configuration(value="test.property.static.double")
        @DefaultValue("1.2")
        Double getStaticDouble();
    }
    
    @Test
    @Ignore
    public void test() throws Exception {
        MyConfig config = new DynamicProxyArchaeusConfigurationFactory().get(MyConfig.class);
        
        System.out.println("----- BEFORE -----");
        printContents(config);
        
        ConfigurationManager.getConfigInstance().setProperty("test.property.dynamic.string",  "NewA");
        ConfigurationManager.getConfigInstance().setProperty("test.property.dynamic.int",     "321");
        ConfigurationManager.getConfigInstance().setProperty("test.property.dynamic.boolean", "false");
        ConfigurationManager.getConfigInstance().setProperty("test.property.dynamic.long",    "654");
        ConfigurationManager.getConfigInstance().setProperty("test.property.dynamic.double",  "2.1");
        ConfigurationManager.getConfigInstance().setProperty("test.property.static.string",  "NewA");
        ConfigurationManager.getConfigInstance().setProperty("test.property.static.int",     "321");
        ConfigurationManager.getConfigInstance().setProperty("test.property.static.boolean", "false");
        ConfigurationManager.getConfigInstance().setProperty("test.property.static.long",    "654");
        ConfigurationManager.getConfigInstance().setProperty("test.property.static.double",  "2.1");
        
        System.out.println("----- AFTER -----");
        printContents(config);
        
//        Supplier<String> supplier = config.getDynamicStringSupplier();
//        System.out.println("Supplier value : " + supplier.get());
        
        int count = 1000000;
        
        MyConfig configDynamicProxy = new DynamicProxyArchaeusConfigurationFactory().get(MyConfig.class);
        MyConfig configJavaAssixt   = new JavaAssistArchaeusConfigurationFactory().get(MyConfig.class);
        MyConfig configCglib        = new CglibArchaeusConfigurationFactory().get(MyConfig.class);
        
        for (int i = 0; i < 10; i++) {
            System.out.println("==== Run " + i + " ====");
            timeConfig(configDynamicProxy,  "Dynamic Proxy", count);
            timeConfig(configJavaAssixt,    "Java Assist  ", count);
            timeConfig(configCglib,         "CGLIB        ", count);
        }
    }
    
    @Test
    @Ignore
    public void testWithInjection() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(MyConfig.class).toProvider(new TestProxyProvider<MyConfig>(MyConfig.class));
            }
        });
        
        MyConfig config = injector.getInstance(MyConfig.class);
    }
    
    void timeConfig(MyConfig config, String name, int count) {
        StopWatch sw = new StopWatch();
        sw.start();
        for (int i = 0; i < count; i++) {
            for (Method method : MyConfig.class.getMethods()) {
                try {
                    Object value = method.invoke(config);
//                    System.out.println(name + " " + method.getName() + " " + value);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        System.out.println(name + " took " + sw.getTime());
    }
    
    void printContents(MyConfig config) {
        for (Method method : MyConfig.class.getMethods()) {
            try {
                System.out.println(method.getName() + " " + method.invoke(config));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    
}
