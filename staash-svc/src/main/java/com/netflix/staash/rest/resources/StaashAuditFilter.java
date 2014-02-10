package com.netflix.staash.rest.resources;

import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;

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

		Logger.info("Adding headers to request context");
		addRequestHeaders(cReq);
		
		Logger.info("Adding query params to request context");
		addQueryParameters(cReq);

		return cReq;
	}

	public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
		
		Logger.info("StaashAuditFilter POST");
		
		StaashRequestContext.addContext("STATUS", String.valueOf(response.getStatus()));

		StaashRequestContext.flushRequestContext();
		return response;
	}
	
	
	private void addRequestHeaders(ContainerRequest cReq) {
		
		MultivaluedMap<String, String> headers = cReq.getRequestHeaders();
		for (Entry<String, List<String>> e : headers.entrySet()) {
			StaashRequestContext.addContext("H__" + e.getKey(), e.getValue().toString());
		}
	}
	
	private void addQueryParameters(ContainerRequest cReq) {
		
		MultivaluedMap<String, String> params = cReq.getQueryParameters();
		for (Entry<String, List<String>> e : params.entrySet()) {
			StaashRequestContext.addContext("Q__" + e.getKey(), e.getValue().toString());
		}
	}
}