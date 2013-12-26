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
package com.netflix.staash;

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
