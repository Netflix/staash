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
package com.netflix.staash.jetty;

import org.eclipse.jetty.server.Server;
import com.google.inject.servlet.GuiceFilter;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaasLauncher {
    private static final Logger LOG = LoggerFactory.getLogger(PaasLauncher.class);
    
    private static final int DEFAULT_PORT = 8080;
    
    public static void main(String[] args) throws Exception {
        LOG.info("Starting PAAS");
        
        // Create the server.
        Server server = new Server(DEFAULT_PORT);
         
        // Create a servlet context and add the jersey servlet.
        ServletContextHandler sch = new ServletContextHandler(server, "/");
         
        // Add our Guice listener that includes our bindings
        sch.addEventListener(new PaasGuiceServletConfig());
         
        // Then add GuiceFilter and configure the server to 
        // reroute all requests through this filter. 
        sch.addFilter(GuiceFilter.class, "/*", null);
         
        // Must add DefaultServlet for embedded Jetty. 
        // Failing to do this will cause 404 errors.
        // This is not needed if web.xml is used instead.
        sch.addServlet(DefaultServlet.class, "/");
         
        // Start the server
        server.start();
        server.join();  
        
        LOG.info("Stopping PAAS");
    }
}
