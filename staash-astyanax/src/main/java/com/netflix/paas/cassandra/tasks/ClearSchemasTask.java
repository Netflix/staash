package com.netflix.paas.cassandra.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.paas.tasks.Task;
import com.netflix.paas.tasks.TaskContext;

public class ClearSchemasTask implements Task {
    private static final Logger LOG = LoggerFactory.getLogger(ClearSchemasTask.class);

    @Override
    public void execte(TaskContext context) throws Exception {
    }
}
