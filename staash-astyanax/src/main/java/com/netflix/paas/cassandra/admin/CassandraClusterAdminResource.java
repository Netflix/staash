package com.netflix.paas.cassandra.admin;

import java.util.Collection;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.netflix.paas.cassandra.entity.CassandraClusterEntity;
import com.netflix.paas.cassandra.entity.ColumnFamilyEntity;
import com.netflix.paas.cassandra.entity.KeyspaceEntity;
import com.netflix.paas.exceptions.NotFoundException;
import com.netflix.paas.exceptions.PaasException;

public interface CassandraClusterAdminResource {
    @GET
    public CassandraClusterEntity getClusterDetails();

    @GET
    @Path("ks")
    public Collection<KeyspaceEntity> listKeyspaces();
    
    @POST
    @Path("ks")
    public void createKeyspace(KeyspaceEntity keyspace) throws PaasException;
    
    @GET
    @Path("ks/{keyspace}")
    public KeyspaceEntity getKeyspace(@PathParam("keyspace") String keyspaceName) throws NotFoundException;
    
    @POST
    @Path("ks/{keyspace}")
    public void updateKeyspace(@PathParam("keyspace") String keyspaceName, KeyspaceEntity keyspace) throws PaasException;
    
    @DELETE
    @Path("ks/{keyspace}")
    public void deleteKeyspace(@PathParam("keyspace") String keyspaceName) throws PaasException;
    
    @POST
    @Path("ks/{keyspace}/cf")
    public void createColumnFamily(@PathParam("keyspace") String keyspaceName, ColumnFamilyEntity columnFamily) throws PaasException;

    @POST
    @Path("ks/{keyspace}/cf/{columnfamily}")
    public void updateColumnFamily(@PathParam("keyspace") String keyspaceName, String columnFamilyName, ColumnFamilyEntity columnFamily) throws PaasException;

    @GET
    @Path("ks/{keyspace}/cf/{columnfamily}")
    public ColumnFamilyEntity getColumnFamily(@PathParam("keyspace") String keyspaceName, String columnFamilyName) throws NotFoundException;
    
    @DELETE
    @Path("ks/{keyspace}/cf/{columnfamily}")
    public void deleteColumnFamily(@PathParam("keyspace") String keyspaceName, String columnFamilyName) throws PaasException;
    
    @GET
    @Path("cf")
    public Collection<ColumnFamilyEntity> listColumnFamilies();
    
    @GET
    @Path("names/ks")
    public Collection<String> listKeyspaceNames();
    
    @GET
    @Path("names/cf")
    public Collection<String> listColumnFamilyNames();
}
