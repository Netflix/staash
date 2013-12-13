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

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.netflix.astyanax.ColumnListMutation;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.annotations.Component;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.serializers.AnnotatedCompositeSerializer;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.util.TimeUUIDUtils;

/**
 * Very very very simple and inefficient tagger that stores a single row per tag.  
 * Use this when storing and tagging a relatively small number of 'documents'.
 * The tagger works on equality and does not provide prefix or wildcard searches
 * 
 * RowKey:  <TagName>
 * Column:  <ForeignKey><VersionUUID>
 * 
 * @author elandau
 *
 */
public class SimpleReverseIndexer implements Indexer {
    private final static String ID_PREFIX  = "$";
    
    /**
     * Composite entry within the index CF
     * @author elandau
     *
     */
    public static class IndexEntry {
        public IndexEntry() {
            
        }
        
        public IndexEntry(String id, UUID uuid) {
            this.id = id;
            this.version = uuid;
        }

        @Component(ordinal = 0)
        public String id;
        
        @Component(ordinal = 1)
        public UUID version;
    }

    
    private static final AnnotatedCompositeSerializer<IndexEntry> EntrySerializer = new AnnotatedCompositeSerializer<IndexEntry>(IndexEntry.class);
    
    /**
     * Builder pattern
     * @author elandau
     */
    public static class Builder {
        private Keyspace keyspace;
        private String columnFamily;
        
        public Builder withKeyspace(Keyspace keyspace) {
            this.keyspace = keyspace;
            return this;
        }
        
        public Builder withColumnFamily(String columnFamily) {
            this.columnFamily = columnFamily;
            return this;
        }
        
        public SimpleReverseIndexer build() {
            return new SimpleReverseIndexer(this);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    private Keyspace                         keyspace;
    private ColumnFamily<String, IndexEntry> indexCf;
    private ColumnFamily<String, String>     dataCf;
    
    private SimpleReverseIndexer(Builder builder) {
        indexCf = new ColumnFamily<String, IndexEntry>(builder.columnFamily + "_idx",  StringSerializer.get(), EntrySerializer);
        dataCf  = new ColumnFamily<String, String>    (builder.columnFamily + "_data", StringSerializer.get(), StringSerializer.get());
        keyspace     = builder.keyspace;
    }

    @Override
    public Collection<String> findUnion(Map<String, String> tags) throws IndexerException {
        Set<String> ids = Sets.newHashSet();
        MutationBatch mb = keyspace.prepareMutationBatch();
        try {
            for (Row<String, IndexEntry> row : keyspace.prepareQuery(indexCf).getKeySlice(fieldsToSet(tags)).execute().getResult()) {
                ColumnListMutation<IndexEntry> mrow = null;
                IndexEntry previousEntry = null;
                for (Column<IndexEntry> column : row.getColumns()) {
                    IndexEntry entry = column.getName();
                    if (previousEntry != null && entry.id == previousEntry.id) {
                        if (mrow == null)
                            mrow = mb.withRow(indexCf, row.getKey());
                        mrow.deleteColumn(previousEntry);
                    }
                    ids.add(entry.id);
                }
            }
        } catch (ConnectionException e) {
            throw new IndexerException("Failed to get tags : " + tags, e);
        } finally {
            try {
                mb.execute();
            }
            catch (Exception e) {
                // OK to ignore
            }
        }
        return ids;
    }
    
    private Collection<String> fieldsToSet(Map<String, String> tags) {
        return Collections2.transform(tags.entrySet(),  new Function<Entry<String, String>, String>() {
            public String apply(Entry<String, String> entry) {
                return entry.getKey() + "=" + entry.getValue();
            }
        });
    }

    @Override
    public Collection<String> findIntersection(Map<String, String> tags) throws IndexerException  {
        Set<String> ids = Sets.newHashSet();
        
        MutationBatch mb = keyspace.prepareMutationBatch();
        try {
            boolean first = true;
            Set<Entry<String, String>> elements = tags.entrySet();
            
            for (Row<String, IndexEntry> row : keyspace.prepareQuery(indexCf).getKeySlice(fieldsToSet(tags)).execute().getResult()) {
                Set<String> rowIds = Sets.newHashSet();
                ColumnListMutation<IndexEntry> mrow = null;
                IndexEntry previousEntry = null;
                for (Column<IndexEntry> column : row.getColumns()) {
                    IndexEntry entry = column.getName();
                    if (previousEntry != null && entry.id == previousEntry.id) {
                        if (mrow == null)
                            mrow = mb.withRow(indexCf, row.getKey());
                        mrow.deleteColumn(previousEntry);
                    }
                    
                    rowIds.add(entry.id);
                }
                
                if (first) {
                    first = false;
                    ids = rowIds;
                }
                else {
                    ids = Sets.intersection(ids, rowIds);
                    if (ids.isEmpty()) 
                        return ids;
                }
            }
        } catch (ConnectionException e) {
            throw new IndexerException("Failed to get tags : " + tags, e);
        } finally {
            try {
                mb.execute();
            }
            catch (ConnectionException e) {
                // OK to ignore
            }
        }
        return ids;
    }

    @Override
    public Collection<String> find(String field, String value) throws IndexerException  {
        Set<String> ids = Sets.newHashSet();
        String indexRowKey = field + "=" + value;
        
        MutationBatch mb = keyspace.prepareMutationBatch();
        try {
            boolean first = true;
            ColumnList<IndexEntry> row = keyspace.prepareQuery(indexCf).getRow(indexRowKey).execute().getResult();
            IndexEntry previousEntry = null;
            for (Column<IndexEntry> column : row) {
                IndexEntry entry = column.getName();
                ColumnListMutation<IndexEntry> mrow = null;
                if (previousEntry != null && entry.id == previousEntry.id) {
                    if (mrow == null)
                        mrow = mb.withRow(indexCf, indexRowKey);
                    mrow.deleteColumn(previousEntry);
                }
                else {
                    ids.add(entry.id);
                }
            }
        } catch (ConnectionException e) {
            throw new IndexerException("Failed to get tag : " + indexRowKey, e);
        } finally {
            try {
                mb.execute();
            }
            catch (ConnectionException e) {
                // OK to ignore
            }
        }
        return ids;
    }

    @Override
    public void tagId(String id, Map<String, String> tags) throws IndexerException  {
        MutationBatch mb = keyspace.prepareMutationBatch();
        
        ColumnListMutation<String> idRow = mb.withRow(dataCf, id);
        UUID uuid = TimeUUIDUtils.getUniqueTimeUUIDinMicros();
        for (Map.Entry<String, String> tag : tags.entrySet()) {
            String rowkey = tag.getKey() + "=" + tag.getValue();
            System.out.println("Rowkey: " + rowkey);
            mb.withRow(indexCf, tag.getKey() + "=" + tag.getValue())
              .putEmptyColumn(new IndexEntry(id, uuid));
//            idRow.putColumn(tag.getKey(), tag.getValue());
        }
        
        try {
            mb.execute();
        } catch (ConnectionException e) {
            throw new IndexerException("Failed to store tags : " + tags + " for id " + id, e);
        }
    }

    @Override
    public void removeId(String id) throws IndexerException  {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void createStorage() throws IndexerException  {
        try {
            keyspace.createColumnFamily(indexCf, ImmutableMap.<String, Object>builder()
                    .put("comparator_type",          "CompositeType(UTF8Type, TimeUUIDType)")
                    .build());
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
        
        try {
            keyspace.createColumnFamily(dataCf, ImmutableMap.<String, Object>builder()
                    .put("default_validation_class", "LongType")
                    .put("key_validation_class",     "UTF8Type")
                    .put("comparator_type",          "UTF8Type")
                    .build());
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, String> getTags(String id) throws IndexerException {
        try {
            ColumnList<String> fields = keyspace.prepareQuery(dataCf).getRow(id).execute().getResult();
            Map<String, String> mapped = Maps.newHashMap();
            for (Column<String> column : fields) {
                mapped.put(column.getName(),  column.getStringValue());
            }
            return mapped;
        } catch (ConnectionException e) {
            throw new IndexerException("Failed to get tags for id " + id, e);
        }
    }
}
