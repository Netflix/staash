package com.netflix.paas.jersey;

import com.sun.jersey.api.json.JSONJAXBContext;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

@Provider
class NaturalNotationContextResolver implements ContextResolver<JAXBContext> {
   private JAXBContext context;

   NaturalNotationContextResolver() {
       try {
           this.context = new JSONJAXBContext();
       }
       catch ( JAXBException e ) {
           throw new RuntimeException(e);
       }
   }

   public JAXBContext getContext(Class<?> objectType) {
       return context;
   }
}