package com.netflix.paas.mesh.jersey;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/1/mesh")
public class MeshServerResource {
    /**
     * Return the list of topics and their metadata
     * @return
     */
    @GET
    @Path("topic")
    public List<String> getTopics() {
        return null;
    }
    
    @GET
    @Path("topic/{name}")
    public void getTopicData(@PathParam("name") String topicName) {
        
    }
    
    /**
     * 
     * @param topicName
     */
    @POST
    @Path("topic/{name}")
    public void postTopic(@PathParam("name") String topicName) {
        
    }
    
    @POST
    @Path("topic/{name}/{key}")
    public void postTopicKey(@PathParam("name") String topicName, @PathParam("key") String key) {
        
    }
    
    @DELETE
    @Path("topic/{name}")
    public void deleteTopic(@PathParam("name") String topicName) {
        
    }
    
    @DELETE
    @Path("topic/{name}/{key}")
    public void deleteTopicKey(@PathParam("name") String topicName, @PathParam("key") String key) {
        
    }

}
