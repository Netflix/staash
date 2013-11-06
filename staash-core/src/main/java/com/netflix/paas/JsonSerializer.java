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
