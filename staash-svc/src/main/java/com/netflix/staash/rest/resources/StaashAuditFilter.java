package com.netflix.staash.rest.resources;

import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.staash.json.JsonObject;
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
		
		StaashRequestContext.recordRequestStart();
		StaashRequestContext.logDate();

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
		StaashRequestContext.recordRequestEnd();
		StaashRequestContext.flushRequestContext();
		
		// Add RequestId to response
		addRequestIdToResponse(response);
		
		return response;
	}
	
	/**
	 * Private helper that adds the request-id to the response payload.
	 * @param response
	 */
	private void addRequestIdToResponse(ContainerResponse response) {
		
		// The request-id to be injected in the response
		String requestId = StaashRequestContext.getRequestId();
		
		// The key response attributes
		int status = response.getStatus();
		MediaType mediaType = response.getMediaType();
		String message = (String)response.getEntity();
		
		if (mediaType.equals(MediaType.APPLICATION_JSON_TYPE)) {
			
			JsonObject json = new JsonObject(message);
			json.putString("request-id", requestId);
			
			Response newJerseyResponse = Response.status(status).type(mediaType).entity(json.toString()).build();
			response.setResponse(newJerseyResponse);
		}
			
		// Add the request id to the response regardless of the media type, 
		// this allows non json responses to have a request id in the response
		response.getHttpHeaders().add("x-nflx-staash-request-id", requestId);
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