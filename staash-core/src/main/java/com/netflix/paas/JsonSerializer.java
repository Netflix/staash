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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.module.SimpleModule;

public class JsonSerializer {
    final static ObjectMapper mapper = new ObjectMapper();
    
    {
        mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        mapper.enableDefaultTyping();
    }

    public static <T> String toString(T entity) throws Exception  {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mapper.writeValue(baos, entity);
        baos.flush();
        return baos.toString();
    }
    
    public static <T> T fromString(String data, Class<T> clazz) throws Exception {
        return (T) mapper.readValue(
                new ByteArrayInputStream(data.getBytes()), 
                clazz);
    }
}
