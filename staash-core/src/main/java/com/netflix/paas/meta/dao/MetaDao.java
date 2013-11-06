package com.netflix.paas.meta.dao;

import com.netflix.paas.json.JsonObject;
import com.netflix.paas.meta.entity.Entity;

public interface MetaDao {
    public void writeMetaEntity(Entity entity);
    public Entity readMetaEntity(String rowKey);
    public void writeRow(String db, String table, JsonObject rowObj);
    public String listRow(String db, String table, String keycol, String key);
}
