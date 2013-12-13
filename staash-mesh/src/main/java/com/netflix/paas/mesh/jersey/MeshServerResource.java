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
