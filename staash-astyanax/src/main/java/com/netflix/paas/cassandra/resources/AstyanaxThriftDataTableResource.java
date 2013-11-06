package com.netflix.paas.cassandra.resources;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.netflix.astyanax.ColumnListMutation;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.SerializerPackage;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.model.Rows;
import com.netflix.astyanax.partitioner.Partitioner;
import com.netflix.astyanax.query.RowQuery;
import com.netflix.astyanax.serializers.ByteBufferSerializer;
import com.netflix.astyanax.util.RangeBuilder;
import com.netflix.paas.data.QueryResult;
import com.netflix.paas.data.RowData;
import com.netflix.paas.data.SchemalessRows;
import com.netflix.paas.exceptions.NotFoundException;
import com.netflix.paas.exceptions.PaasException;
import com.netflix.paas.json.JsonObject;
import com.netflix.paas.resources.TableDataResource;

/**
 * Column family REST resource 
 * @author elandau
 *
 */
public class AstyanaxThriftDataTableResource implements TableDataResource {
    private static Logger LOG = LoggerFactory.getLogger(AstyanaxThriftDataTableResource.class);
    
    private final Keyspace                              keyspace;
    private final ColumnFamily<ByteBuffer, ByteBuffer>  columnFamily;

    private volatile SerializerPackage serializers;
    
    public AstyanaxThriftDataTableResource(Keyspace keyspace, String name) {
        this.keyspace = keyspace;
        this.columnFamily = ColumnFamily.newColumnFamily(name, ByteBufferSerializer.get(), ByteBufferSerializer.get());
    }
    
    @Override
    public QueryResult listRows(String cursor, Integer rowLimit, Integer columnLimit) throws PaasException {
        try {
            invariant();
            
            // Execute the query
            Partitioner partitioner = keyspace.getPartitioner();
            Rows<ByteBuffer, ByteBuffer> result = keyspace
                .prepareQuery(columnFamily)
                .getKeyRange(null,  null, cursor != null ? cursor : partitioner.getMinToken(),  partitioner.getMaxToken(),  rowLimit)
                .execute()
                .getResult();
            
            // Convert raw data into a simple sparse tree
            SchemalessRows.Builder builder = SchemalessRows.builder();
            for (Row<ByteBuffer, ByteBuffer> row : result) { 
                Map<String, String> columns = Maps.newHashMap();
                for (Column<ByteBuffer> column : row.getColumns()) {
                    columns.put(serializers.columnAsString(column.getRawName()), serializers.valueAsString(column.getRawName(), column.getByteBufferValue()));
                }
                builder.addRow(serializers.keyAsString(row.getKey()), columns);
            }
            
            QueryResult dr = new QueryResult();
            dr.setSrows(builder.build());
            
            if (!result.isEmpty()) {
                dr.setCursor(partitioner.getTokenForKey(Iterables.getLast(result).getKey()));
            }
            return dr;
        } catch (ConnectionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void truncateRows() {
    }

    @Override
    public QueryResult readRow(String key, Integer columnCount, String startColumn, String endColumn, Boolean reversed) throws PaasException {
        invariant();
        
        try {
            // Construct the query
            RowQuery<ByteBuffer, ByteBuffer> query = keyspace
                    .prepareQuery(this.columnFamily)
                    .getRow(serializers.keyAsByteBuffer(key));
                    
            RangeBuilder range = new RangeBuilder();
            if (columnCount != null && columnCount > 0) {
                range.setLimit(columnCount);
            }
            if (startColumn != null && !startColumn.isEmpty()) {
                range.setStart(serializers.columnAsByteBuffer(startColumn));
            }
            if (endColumn != null && !endColumn.isEmpty()) {
                range.setEnd(serializers.columnAsByteBuffer(endColumn));
            }
            range.setReversed(reversed);
            query.withColumnRange(range.build());

            // Execute the query
            ColumnList<ByteBuffer> result = query.execute().getResult();
            
            // Convert raw data into a simple sparse tree
            SchemalessRows.Builder builder = SchemalessRows.builder();
            Map<String, String> columns = Maps.newHashMap();
            if (!result.isEmpty()) {
                for (Column<ByteBuffer> column : result) { 
                    columns.put(serializers.columnAsString(column.getRawName()), serializers.valueAsString(column.getRawName(), column.getByteBufferValue()));
                }
                builder.addRow(key, columns);
            }

            QueryResult dr = new QueryResult();
            dr.setSrows(builder.build());
            return dr;
        } catch (ConnectionException e) {
            throw new PaasException(
                    String.format("Failed to read row '%s' in column family '%s.%s'" , 
                                  key, this.keyspace.getKeyspaceName(), this.columnFamily.getName()),
                    e);
        }
    }

    @Override
    public void deleteRow(String key) throws PaasException {
        invariant();
        
        MutationBatch mb = keyspace.prepareMutationBatch();
        mb.withRow(this.columnFamily, serializers.keyAsByteBuffer(key)).delete();
        
        try {
            mb.execute();
        } catch (ConnectionException e) {
            throw new PaasException(
                    String.format("Failed to update row '%s' in column family '%s.%s'" , 
                                  key, this.keyspace.getKeyspaceName(), this.columnFamily.getName()),
                    e);
        }
    }

    public void updateRow(String key, RowData rowData) throws PaasException {
        LOG.info("Update row: " + rowData.toString());
        invariant();
        
        MutationBatch mb = keyspace.prepareMutationBatch();
        if (rowData.hasSchemalessRows()) {
            ColumnListMutation<ByteBuffer> mbRow = mb.withRow(this.columnFamily, serializers.keyAsByteBuffer(key));
            for (Entry<String, Map<String, String>> row : rowData.getSrows().getRows().entrySet()) {
                for (Entry<String, String> column : row.getValue().entrySet()) {
                    mbRow.putColumn(serializers.columnAsByteBuffer(column.getKey()),  
                                    serializers.valueAsByteBuffer(column.getKey(), column.getValue()));
                }
            }
        }
        
        try {
            mb.execute();
        } catch (ConnectionException e) {
            throw new PaasException(
                    String.format("Failed to update row '%s' in column family '%s.%s'" , 
                                  key, this.keyspace.getKeyspaceName(), this.columnFamily.getName()),
                    e);
        }
    }

    @Override
    public QueryResult readColumn(String key, String column) throws NotFoundException, PaasException {
        invariant();
        
        try {
            Column<ByteBuffer> result = keyspace
                    .prepareQuery(this.columnFamily)
                    .getRow(serializers.keyAsByteBuffer(key))
                    .getColumn(serializers.columnAsByteBuffer(column))
                    .execute()
                    .getResult();
            
            // Convert raw data into a simple sparse tree
            SchemalessRows.Builder builder = SchemalessRows.builder();
            Map<String, String> columns = Maps.newHashMap();
            columns.put(serializers.columnAsString(result.getRawName()), serializers.valueAsString(result.getRawName(), result.getByteBufferValue()));
            builder.addRow(key, columns);

            QueryResult dr = new QueryResult();
            dr.setSrows(builder.build());
            
            return dr;
        } catch (com.netflix.astyanax.connectionpool.exceptions.NotFoundException e) {
            throw new NotFoundException(
                    "column",
                    String.format("%s.%s.%s.%s", key, column, this.keyspace.getKeyspaceName(), this.columnFamily.getName()));
        } catch (ConnectionException e) {
            throw new PaasException(
                    String.format("Failed to read row '%s' in column family '%s.%s'" , 
                                  key, this.keyspace.getKeyspaceName(), this.columnFamily.getName()),
                    e);
        }
    }

    @Override
    public void updateColumn(String key, String column, String value) throws NotFoundException, PaasException {
        LOG.info("Update row");
        invariant();
        
        MutationBatch mb = keyspace.prepareMutationBatch();
        ColumnListMutation<ByteBuffer> mbRow = mb.withRow(this.columnFamily, serializers.keyAsByteBuffer(key));
        mbRow.putColumn(serializers.columnAsByteBuffer(column),  
                        serializers.valueAsByteBuffer(column, value));
        
        try {
            mb.execute();
        } catch (ConnectionException e) {
            throw new PaasException(
                    String.format("Failed to update row '%s' in column family '%s.%s'" , 
                                  key, this.keyspace.getKeyspaceName(), this.columnFamily.getName()),
                    e);
        }
    }

    @Override
    public void deleteColumn(String key, String column) throws PaasException {
        LOG.info("Update row");
        invariant();
        
        MutationBatch mb = keyspace.prepareMutationBatch();
        ColumnListMutation<ByteBuffer> mbRow = mb.withRow(this.columnFamily, serializers.keyAsByteBuffer(key));
        mbRow.deleteColumn(serializers.columnAsByteBuffer(column));
        try {
            mb.execute();
        } catch (ConnectionException e) {
            throw new PaasException(
                    String.format("Failed to update row '%s' in column family '%s.%s'" , 
                                  key, this.keyspace.getKeyspaceName(), this.columnFamily.getName()),
                    e);
        }
    }
    
    private void invariant() throws PaasException {
        if (this.serializers == null)
            refreshSerializers();
    }
    
    private void refreshSerializers() throws PaasException {
        try {
            this.serializers = this.keyspace.getSerializerPackage(this.columnFamily.getName(), true);
        } catch (Exception e) {
            LOG.error("Failed to get serializer package for column family '{}.{}'", new Object[]{keyspace.getKeyspaceName(), this.columnFamily.getName(), e});
            throw new PaasException(
                    String.format("Failed to get serializer package for column family '%s.%s' in keyspace", 
                            this.keyspace.getKeyspaceName(), this.columnFamily.getName()), 
                    e);
        }
    }

    @Override
    @POST
    @Path("{db}/{table}")
    public void updateRow(@PathParam("db") String db,
            @PathParam("table") String table, JsonObject rowData)
            throws PaasException {
        // TODO Auto-generated method stub
        
    }
}