package com.netflix.staash.rest.resources;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.staash.rest.util.StaashRequestContext;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;

/**
 * Class the encapsulates pre and post filters that should be executed when serving requests via jersey resources.
 * 
 * @author poberai
 *
 */
public class StaashAuditFilter implements ResourceFilter, ContainerRequestFilter, ContainerResponseFilter {

	private static final Logger Logger = LoggerFactory.getLogger(StaashAuditFilter.class);

	@Context HttpServletRequest request;
	@Context HttpServletResponse response;
	
	public ContainerRequestFilter getRequestFilter() {
		return this;
	}

	public ContainerResponseFilter getResponseFilter() {
		return this;
	}

	public ContainerRequest filter(ContainerRequest cReq) {
		
		Logger.info("StaashAuditFilter PRE");
		
		StaashRequestContext.resetRequestContext();
		
		StaashRequestContext.addContext("PATH", cReq.getPath(true));
		StaashRequestContext.addContext("METHOD", cReq.getMethod());

		return cReq;
	}

	public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
		
		Logger.info("StaashAuditFilter POST");
		
		StaashRequestContext.addContext("STATUS", String.valueOf(response.getStatus()));

		StaashRequestContext.flushRequestContext();
		return response;
	}
	
}