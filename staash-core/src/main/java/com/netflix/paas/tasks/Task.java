package com.netflix.paas.tasks;

public interface Task {
    public void execte(TaskContext context) throws Exception;
}
