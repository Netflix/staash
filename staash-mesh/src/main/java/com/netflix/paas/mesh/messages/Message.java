package com.netflix.paas.mesh.messages;

public class Message {
    private final Verb verb;
    
    public Message(Verb verb) {
        this.verb = verb;
    }
    
    public Verb getVerb() {
        return this.verb;
    }
}
