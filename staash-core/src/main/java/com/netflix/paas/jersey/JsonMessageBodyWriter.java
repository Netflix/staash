package com.netflix.paas.jersey;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * A MessageBodyWriter implementation that uses Jackson ObjectMapper to serialize objects to JSON.
 */
@Produces({"application/json"})
@Provider
public class JsonMessageBodyWriter implements MessageBodyWriter<Object> {

    private final ObjectMapper mapper;
    
    public JsonMessageBodyWriter() {
        mapper = new ObjectMapper();
        mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
    }
    
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, 
            MediaType mediaType) {
        return mapper.canSerialize(type);
    }


    public long getSize(Object data, Class<?> type, Type genericType, Annotation[] annotations, 
            MediaType mediaType) {
        return -1;
    }
    
    public void writeTo(Object data, Class<?> type, Type genericType, Annotation[] annotations, 
            MediaType mediaType, MultivaluedMap<String, Object> headers, OutputStream out) 
            throws IOException, WebApplicationException {
        mapper.writeValue(out, data);
        out.flush();
    }
}