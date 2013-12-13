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
