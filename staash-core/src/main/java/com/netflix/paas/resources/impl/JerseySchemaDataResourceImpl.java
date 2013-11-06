package com.netflix.paas.resources.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;
import com.netflix.paas.dao.DaoProvider;
import com.netflix.paas.data.QueryResult;
import com.netflix.paas.data.RowData;
import com.netflix.paas.exceptions.NotFoundException;
import com.netflix.paas.exceptions.PaasException;
import com.netflix.paas.json.JsonObject;
import com.netflix.paas.meta.dao.MetaDao;
import com.netflix.paas.resources.PaasDataResource;
import com.netflix.paas.resources.TableDataResource;

public class JerseySchemaDataResourceImpl implements PaasDataResource {
    private DaoProvider provider;
    private MetaDao metadao;

    @Inject
    public JerseySchemaDataResourceImpl(DaoProvider provider, MetaDao meta) {
        this.provider = provider;
        this.metadao = meta;
    }

    @Override
    @GET
    public String listSchemas() {
        // TODO Auto-generated method stub
        return "hello data";
    }

    @Override
    @GET
    @Path("{db}/{table}/{keycol}/{key}")
    public String listRow(@PathParam("db") String db,
            @PathParam("table") String table, @PathParam("keycol") String keycol,@PathParam("key") String key) {
            return  metadao.listRow(db, table, keycol, key);
    }

    @Override
    @POST
    @Path("{db}/{table}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void updateRow(@PathParam("db") String db,
            @PathParam("table") String table, String rowObject) {
        metadao.writeRow(db, table, new JsonObject(rowObject));
        // TODO Auto-generated method stub
    }

}
