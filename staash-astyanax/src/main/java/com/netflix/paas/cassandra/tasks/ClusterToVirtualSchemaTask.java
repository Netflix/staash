package com.netflix.paas.cassandra.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.paas.tasks.Task;
import com.netflix.paas.tasks.TaskContext;

/**
 * Load a schema into a task
 * @author elandau
 *
 */
public class ClusterToVirtualSchemaTask implements Task {
    private static final Logger LOG = LoggerFactory.getLogger(ClusterToVirtualSchemaTask.class);

    @Override
    public void execte(TaskContext context) throws Exception {
        String clusterName = context.getStringParameter("cluster");
        
    }
}
