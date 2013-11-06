package com.netflix.paas.dao;

public interface DaoStatus {
    public String getEntityType();
    public String getDaoType();
    public Boolean healthcheck();
    public Boolean isExists();
}
