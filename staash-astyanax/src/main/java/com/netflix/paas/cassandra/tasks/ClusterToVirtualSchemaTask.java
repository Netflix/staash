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
