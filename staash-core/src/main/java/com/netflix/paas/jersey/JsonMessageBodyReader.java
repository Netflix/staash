package com.netflix.paas.jersey;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.map.ObjectMapper;

@Consumes({"application/json"})
@Provider
public class JsonMessageBodyReader implements MessageBodyReader<Object> {
    private final ObjectMapper mapper;
    
    public JsonMessageBodyReader() {
        mapper = new ObjectMapper();
//        mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return mapper.canSerialize(type);
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> arg4, InputStream is) throws IOException, WebApplicationException {
        return mapper.readValue(is, type);
    }

}
