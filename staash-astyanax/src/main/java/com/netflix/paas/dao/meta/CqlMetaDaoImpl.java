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
package com.netflix.paas.dao.meta;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.config.ColumnDefinition;
import org.apache.cassandra.cql3.ColumnSpecification;
import org.apache.cassandra.thrift.CqlResult;
import org.apache.cassandra.thrift.CqlRow;
import org.apache.cassandra.transport.messages.ResultMessage;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.ColumnDefinitions.Definition;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.AlreadyExistsException;
import com.datastax.driver.core.exceptions.DriverException;
import com.google.inject.Inject;
import com.netflix.paas.json.JsonObject;
import com.netflix.paas.meta.dao.MetaDao;
import com.netflix.paas.meta.entity.Entity;
import com.netflix.paas.meta.entity.PaasDBEntity;
import com.netflix.paas.meta.entity.PaasTableEntity;
import com.netflix.paas.util.Pair;
import static com.datastax.driver.core.querybuilder.QueryBuilder.*;

public class CqlMetaDaoImpl implements MetaDao{
    private Cluster cluster;
    private Session session;
    private static boolean schemaCreated = false;
    private static final String metaks = "paasmetaks";
    private static final String metacf = "metacf";

    @Inject
    public CqlMetaDaoImpl(Cluster cluster) {
        this.cluster = cluster;
        this.session = this.cluster.connect();
        maybeCreateMetaSchema();
    }
    @Override
    public void writeMetaEntity(Entity entity) {
        //if (entity instanceof PaasDBEntity) {
            //implies its a create request
            //insert into the meta some values for this dbentity
            //wait for creation of the actual keyspace
            try {
                session.execute(String.format(PaasUtils.INSERT_FORMAT, metaks+"."+metacf, entity.getRowKey(),entity.getName(),entity.getPayLoad()));
            } catch (AlreadyExistsException e) {
                // It's ok, ignore
            }
            
        //}
        if (entity instanceof PaasTableEntity) {
            //first create/check if schema db exists
            PaasTableEntity tableEnt = (PaasTableEntity)entity;
            try {
                String schemaName = tableEnt.getSchemaName();
                session.execute(String.format(PaasUtils.CREATE_KEYSPACE_SIMPLE_FORMAT, schemaName, 1));
            } catch (AlreadyExistsException e) {
                // It's ok, ignore
            }
            //if schema/db already exists now create the table
            String query = BuildQuery(tableEnt);
            Print(query);
            session.execute(query);
            //List<String> primaryKeys = entity.getPrimaryKey();
        }               
    }
    
    public void writeRow(String db, String table,JsonObject rowObj) {
        String query = BuildRowInsertQuery(db, table, rowObj);
        Print(query);
        session.execute(query);
    }
    
    private String BuildRowInsertQuery(String db, String table, JsonObject rowObj) {
        // TODO Auto-generated method stub
        String columns = rowObj.getString("columns");
        String values = rowObj.getString("values");
        return "INSERT INTO"+" "+db+"."+table+"("+columns+")"+" VALUES("+values+");";
    }
    private void Print(String str) {
        // TODO Auto-generated method stub
        System.out.println(str);
    }
    private String BuildQuery(PaasTableEntity tableEnt) {
        // TODO Auto-generated method stub
        String schema = tableEnt.getSchemaName();
        String tableName = tableEnt.getName();
        List<Pair<String, String>> columns = tableEnt.getColumns();
        String primary = tableEnt.getPrimarykey();
        String colStrs = "";
        for (Pair<String, String> colPair:columns) {
            colStrs = colStrs+colPair.getRight()+" "+colPair.getLeft()+", ";
        }
        String primarykeys = tableEnt.getPrimarykey();
        String PRIMARYSTR = "PRIMARY KEY("+primarykeys+")";
        return "CREATE TABLE "+schema+"."+tableName+" ("+colStrs+" "+PRIMARYSTR+");";
    }
    public void maybeCreateMetaSchema() {

        try {
            if (schemaCreated)
                return;

            try {
                session.execute(String.format(PaasUtils.CREATE_KEYSPACE_SIMPLE_FORMAT, metaks, 1));
            } catch (AlreadyExistsException e) {
                // It's ok, ignore
            }

            session.execute("USE " + metaks);

            for (String tableDef : getTableDefinitions()) {
                try {
                    session.execute(tableDef);
                } catch (AlreadyExistsException e) {
                    // It's ok, ignore
                }
            }

            schemaCreated = true;
        } catch (DriverException e) {
            throw e;
        }
    }
    protected Collection<String> getTableDefinitions() {

//        String sparse = "CREATE TABLE sparse (\n"
//                      + "    k text,\n"
//                      + "    c1 int,\n"
//                      + "    c2 float,\n"
//                      + "    l list<text>,\n"
//                      + "    v int,\n"
//                      + "    PRIMARY KEY (k, c1, c2)\n"
//                      + ");";
//
//        String st = "CREATE TABLE static (\n"
//                  + "    k text,\n"
//                  + "    i int,\n"
//                  + "    m map<text, timeuuid>,\n"
//                  + "    v int,\n"
//                  + "    PRIMARY KEY (k)\n"
//                  + ");";
//
//        String compactStatic = "CREATE TABLE compact_static (\n"
//                             + "    k text,\n"
//                             + "    i int,\n"
//                             + "    t timeuuid,\n"
//                             + "    v int,\n"
//                             + "    PRIMARY KEY (k)\n"
//                             + ") WITH COMPACT STORAGE;";

        //similar to old paas.db table, contains only the metadata about the paas entities
        String metaDynamic = "CREATE TABLE metacf (\n"
                              + "    key text,\n"
                              + "    column1 text,\n"
                              + "    value text,\n"
                              + "    PRIMARY KEY (key, column1)\n"
                              + ") WITH COMPACT STORAGE;";

//        String compactComposite = "CREATE TABLE compact_composite (\n"
//                                + "    k text,\n"
//                                + "    c1 int,\n"
//                                + "    c2 float,\n"
//                                + "    c3 double,\n"
//                                + "    v timeuuid,\n"
//                                + "    PRIMARY KEY (k, c1, c2, c3)\n"
//                                + ") WITH COMPACT STORAGE;";

        
//       String  withOptions = "CREATE TABLE with_options (\n"
//                    + "    k text,\n"
//                    + "    i int,\n"
//                    + "    PRIMARY KEY (k)\n"
//                    + ") WITH read_repair_chance = 0.5\n"
//                    + "   AND dclocal_read_repair_chance = 0.6\n"
//                    + "   AND replicate_on_write = true\n"
//                    + "   AND gc_grace_seconds = 42\n"
//                    + "   AND bloom_filter_fp_chance = 0.01\n"
//                    + "   AND caching = ALL\n"
//                    + "   AND comment = 'My awesome table'\n"
//                    + "   AND compaction = { 'class' : 'org.apache.cassandra.db.compaction.LeveledCompactionStrategy', 'sstable_size_in_mb' : 15 }\n"
//                    + "   AND compression = { 'sstable_compression' : 'org.apache.cassandra.io.compress.SnappyCompressor', 'chunk_length_kb' : 128 };";

        List<String> allDefs = new ArrayList<String>();
        allDefs.add(metaDynamic);
        return allDefs;
    }

    @Override
    public Entity readMetaEntity(String rowKey) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String listRow(String db, String table, String keycol, String key) {
        // TODO Auto-generated method stub
        String query = select().all().from(db, table).where(eq(keycol,key)).getQueryString();
        ResultSet rs = session.execute(query);
        return convertResultSet(rs);
    }
    private String convertResultSet(ResultSet rs) {
        // TODO Auto-generated method stub
        String colStr = "";
        String rowStr = "";
        JsonObject response = new JsonObject();
        List<Row> rows = rs.all();
        if (!rows.isEmpty() && rows.size()==1) {
            rowStr = rows.get(0).toString();
        }
        ColumnDefinitions colDefs = rs.getColumnDefinitions();
        colStr = colDefs.toString();
        response.putString("columns", colStr.substring(8,colStr.length()-1));
        response.putString("values", rowStr.substring(4,rowStr.length()-1));
        return response.toString();
        
//        for (Row ro:rows) {
//            Print(ro.toString());
////            ro.getColumnDefinitions()
//        }
//        return null;
//        if (rm.kind == ResultMessage.Kind.ROWS) {
//          //ToDo maybe processInternal
//          boolean bSwitch = true;
//          if (bSwitch) {
//            ResultMessage.Rows cqlRows = (ResultMessage.Rows) rm;
//            List<ColumnSpecification> columnSpecs = cqlRows.result.metadata.names;
//
//            for (List<ByteBuffer> row : cqlRows.result.rows) {
//              Map<String,Object> map = new HashMap<String,Object>();
//              int i = 0;
//              for (ByteBuffer bytes : row) {
//                ColumnSpecification specs = columnSpecs.get(i++);
//                if (specs.name!=null && specs.type!=null && bytes!=null && bytes.hasRemaining()) {
//                System.out.println("name = "+specs.name.toString()+" ,type= "+specs.type.compose(bytes));
//                map.put(specs.name.toString(), specs.type.compose(bytes));
//                }
//              }
//              returnRows.add(map);
//            }
//          } else {
//            boolean convert = true;;
//            CqlResult result = rm.toThriftResult();
//            List<CqlRow> rows = result.getRows();
//            for (CqlRow row: rows) {
//              List<org.apache.cassandra.thrift.Column> columns = row.getColumns();
//              for (org.apache.cassandra.thrift.Column c: columns){
//                HashMap<String,Object> m = new HashMap<String,Object>();
//                if (convert) {
//                  m.put("name" , TypeHelper.getCqlTyped(result.schema.name_types.get(c.name), c.name) );
//                  m.put("value" , TypeHelper.getCqlTyped(result.schema.name_types.get(c.name), c.value) );
//                } else {
//                  m.put("value", TypeHelper.getBytes(c.value));
//                  m.put("name", TypeHelper.getBytes(c.name));
//                }
//                returnRows.add(m);
//              }
//            }
//          }
//        }
//        JsonObject response = new JsonObject();
//        JsonArray array = new JsonArray();
//        for (Map<String,Object> m : returnRows) {
//          array.add(new JsonObject(m));
//        }
//        response.putString(Long.toString(counter.incrementAndGet()), "OK");
//        response.putArray(Long.toString(counter.incrementAndGet()), array);
//        String testQry = "CREATE KEYSPACE testdb WITH REPLICATION = {'class' : 'SimpleStrategy', 'replication_factor': 1};";
////        create("testdb",1);
//        return response.toString();
//        return null;
//    }
    }

}
