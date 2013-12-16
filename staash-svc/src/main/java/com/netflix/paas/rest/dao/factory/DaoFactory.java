package com.netflix.paas.rest.dao.factory;

import com.netflix.paas.rest.dao.DataDao;
import com.netflix.paas.rest.dao.MetaDao;

public interface DaoFactory {
    public  DataDao getDataDao(String storage, String clientType);
    public  MetaDao getMetaDao();
}
