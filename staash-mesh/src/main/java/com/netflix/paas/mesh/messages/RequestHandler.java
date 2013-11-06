package com.netflix.paas.mesh.messages;

public interface RequestHandler {
    void onMessage(Message message, AsyncResponse response);
}
