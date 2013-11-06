package com.netflix.paas.mesh.db;

public interface TopicFactory {
    Topic create(String name);
}
