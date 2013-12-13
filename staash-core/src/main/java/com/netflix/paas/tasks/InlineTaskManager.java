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
package com.netflix.paas.tasks;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;

public class InlineTaskManager implements TaskManager {
    private final static Logger LOG = LoggerFactory.getLogger(InlineTaskManager.class);
    private final Injector injector;
    
    public static class SyncListenableFuture implements ListenableFuture<Void> {
        private final Exception exception;
        
        public SyncListenableFuture(Exception exception) {
            this.exception = exception;
        }
        
        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public Void get() throws InterruptedException, ExecutionException {
            if (exception != null)
                throw new ExecutionException("Very bad", exception);
            return null;
        }

        @Override
        public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            if (exception != null)
                throw new ExecutionException("Very bad", exception);
            return get();
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public void addListener(Runnable listener, Executor executor) {
        }
    }
    
    @Inject
    public InlineTaskManager(Injector injector, EventBus eventBus) {
        this.injector = injector;
        
        LOG.info("SyncTaskManager " + this.injector);
        for (Entry<Key<?>, Binding<?>> key : this.injector.getBindings().entrySet()) {
            LOG.info("SyncTaskManager " + key.toString());
        }
    }
    
    @Override
    public ListenableFuture<Void> submit(Class<?> clazz, Map<String, Object> args) {
        Task task;
        Exception exception = null;
        TaskContext context = new TaskContext(clazz, args);
        try {
            LOG.info(clazz.getCanonicalName());
            task = (Task) injector.getInstance(clazz);
            task.execte(context);
        } catch (Exception e) {
            LOG.warn("Failed to execute task '{}'. '{}'", new Object[]{context.getKey(), e.getMessage(), e});
            exception = e;
        }
        return new SyncListenableFuture(exception);
    }

    @Override
    public ListenableFuture<Void> submit(Class<?> clazz) {
        return submit(clazz, null);
    }

}
