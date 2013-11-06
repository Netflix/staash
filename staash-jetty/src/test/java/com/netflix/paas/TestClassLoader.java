package com.netflix.paas;

import java.net.URL;

import org.junit.Test;

public class TestClassLoader {
    @Test
    public void testLoader() {
        String defaultConfigFileName1 = "config.properties";
        String defaultConfigFileName2 = "/tmp/config.properties";
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(defaultConfigFileName1);
        URL url2 = loader.getResource(defaultConfigFileName1);
        int i =0;
    }

}
