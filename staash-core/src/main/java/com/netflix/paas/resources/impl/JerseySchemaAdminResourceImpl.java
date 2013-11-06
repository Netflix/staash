package com.netflix.paas.resources.impl;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import com.google.inject.Inject;
import com.netflix.paas.dao.DaoProvider;
import com.netflix.paas.entity.DbEntity;
import com.netflix.paas.entity.TableEntity;
import com.netflix.paas.json.JsonObject;
import com.netflix.paas.meta.dao.MetaDao;
import com.netflix.paas.meta.entity.PaasDBEntity;
import com.netflix.paas.meta.entity.PaasTableEntity;
import com.netflix.paas.resources.SchemaAdminResource;

public class JerseySchemaAdminResourceImpl implements SchemaAdminResource {
    private DaoProvider provider;
    private MetaDao metadao;
    @Inject
    public JerseySchemaAdminResourceImpl(DaoProvider provider, MetaDao meta) {
        this.provider = provider;
        this.metadao = meta;
    }

    @Override
    @GET
    public String listSchemas() {
        // TODO Auto-generated method stub
        return "hello";
    }

    @Override
    public void createSchema(String payLoad) {
        // TODO Auto-generated method stub
        if (payLoad!=null) {
            JsonObject jsonPayLoad =  new JsonObject(payLoad);
            PaasDBEntity pdbe = PaasDBEntity.builder().withJsonPayLoad(jsonPayLoad).build();
            metadao.writeMetaEntity(pdbe);
//            Dao<DbEntity> dbDao = provider.getDao("configuration", DbEntity.class);
//            DbEntity dbe = DbEntity.builder().withName(schema.getString("name")).build();
//            boolean exists = dbDao.isExists();
//            dbDao.write(dbe);
//            System.out.println("schema created");
//            System.out.println("schema name is "+schema.getFieldNames()+" "+schema.toString());
            
        }
    }

    @Override
    @DELETE
    @Path("{schema}")
    public void deleteSchema(@PathParam("schema") String schemaName) {
        // TODO Auto-generated method stub
        
    }

    @Override
    @POST
    @Path("{schema}")
    public void updateSchema(@PathParam("schema") String schemaName, DbEntity schema) {
        // TODO Auto-generated method stub
        
    }

    @Override
    @GET
    @Path("{schema}")
    public DbEntity getSchema(@PathParam("schema") String schemaName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @GET
    @Path("{schema}/tables/{table}")
    public TableEntity getTable(@PathParam("schema") String schemaName, @PathParam("table") String tableName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @DELETE
    @Path("{schema}/tables/{table}")
    public void deleteTable(@PathParam("schema") String schemaName, @PathParam("table") String tableName) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void createTable(@PathParam("schema") String schemaName, String payLoad) {
        // TODO Auto-generated method stub
        if (payLoad!=null) {
            JsonObject jsonPayLoad =  new JsonObject(payLoad);
            PaasTableEntity ptbe = PaasTableEntity.builder().withJsonPayLoad(jsonPayLoad, schemaName).build();
            metadao.writeMetaEntity(ptbe);
            //create new ks
            //create new cf
        }       
    }

    @Override
    @POST
    @Path("{schema}/tables/{table}")
    public void updateTable(@PathParam("schema") String schemaName, @PathParam("table") String tableName, TableEntity table) {
        // TODO Auto-generated method stub
        
    }



}
