package com.netflix.paas.mesh.messages;

public interface AsyncResponse {
    void sendResponse(Message message, Message response);
}
