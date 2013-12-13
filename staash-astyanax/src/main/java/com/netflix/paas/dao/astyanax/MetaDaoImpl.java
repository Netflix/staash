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
package com.netflix.paas.dao.astyanax;

import com.google.inject.Inject;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.paas.json.JsonObject;
import com.netflix.paas.meta.dao.MetaDao;
import com.netflix.paas.meta.entity.Entity;
import com.netflix.paas.meta.entity.PaasTableEntity;
import com.netflix.paas.cassandra.provider.KeyspaceClientProvider;

public class MetaDaoImpl implements MetaDao{
    KeyspaceClientProvider kscp;
    public static ColumnFamily<String, String> dbcf = ColumnFamily
            .newColumnFamily(
                    "db", 
                    StringSerializer.get(),
                    StringSerializer.get());
    @Inject
    public MetaDaoImpl(KeyspaceClientProvider kscp) {
        this.kscp = kscp;
    }

    @Override
    public void writeMetaEntity(Entity entity) {
        // TODO Auto-generated method stub
        Keyspace ks = kscp.acquireKeyspace("meta");
        ks.prepareMutationBatch();
        MutationBatch m;
        OperationResult<Void> result;
        m = ks.prepareMutationBatch();
        m.withRow(dbcf, entity.getRowKey()).putColumn(entity.getName(), entity.getPayLoad(), null);
        try {
            result = m.execute();
            if (entity instanceof PaasTableEntity) {
                String schemaName = ((PaasTableEntity)entity).getSchemaName();
                Keyspace schemaks = kscp.acquireKeyspace(schemaName);
                ColumnFamily<String, String> cf = ColumnFamily.newColumnFamily(entity.getName(), StringSerializer.get(), StringSerializer.get());
                schemaks.createColumnFamily(cf, null);
            }
            int i = 0;
        } catch (ConnectionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public Entity readMetaEntity(String rowKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void writeRow(String db, String table, JsonObject rowObj) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String listRow(String db, String table, String keycol, String key) {
        // TODO Auto-generated method stub
        return null;
    }

}
