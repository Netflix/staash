package com.netflix.paas.tasks;

import java.util.Map;

import com.google.common.util.concurrent.ListenableFuture;

public interface TaskManager {
    ListenableFuture<Void> submit(Class<?> className);
    
    ListenableFuture<Void> submit(Class<?> className, Map<String, Object> args);
}
