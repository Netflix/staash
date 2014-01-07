/*******************************************************************************
 * /*
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
 *  *
 ******************************************************************************/
package com.netflix.staash.common.query;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.lang.RuntimeException;
import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.Row;
import com.netflix.astyanax.cql.CqlStatementResult;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Rows;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.staash.json.JsonObject;
import com.netflix.staash.model.StorageType;

public class QueryUtils {

    public static String formatColumns(String columnswithtypes, StorageType stype) {
        // TODO Auto-generated method stub
        String[] allCols = columnswithtypes.split(",");
        String outputCols = "";
        for (String col:allCols) {
            String type;
            String name;
            if (!col.contains(":")) {
                if (stype!=null && stype.getCannonicalName().equals(StorageType.MYSQL.getCannonicalName())) type = "varchar(256)";
                else type="text";
                name=col;
            }
            else {
                name = col.split(":")[0];
                type = col.split(":")[1];
            }
            outputCols = outputCols + name + " " + type +",";
        }
        return outputCols;
    }
    public static String formatQueryResult(ResultSet rs) {
        try {
            JsonObject fullResponse = new JsonObject();
            int rcount = 1;
            while (rs.next()) {
                ResultSetMetaData rsmd = rs.getMetaData();
                String columns ="";
                String values = "";
                int count = rsmd.getColumnCount();
                for (int i=1;i<=count;i++) {
                    String colName = rsmd.getColumnName(i);
                    columns = columns + colName + ",";
                    String value = rs.getString(i);
                    values = values + value +",";
                }
                JsonObject response = new JsonObject();
                response.putString("columns", columns.substring(0, columns.length()-1));
                response.putString("values", values.substring(0, values.length()-1));
                fullResponse.putObject("row"+rcount++, response);
            }
            return fullResponse.toString();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            throw new RuntimeException(e);
        } 
    }
    public static String formatQueryResult(com.datastax.driver.core.ResultSet rs) {
        // TODO Auto-generated method stub
        String colStr = "";
        String rowStr = "";
        JsonObject response = new JsonObject();
        List<Row> rows = rs.all();
        if (!rows.isEmpty() && rows.size() == 1) {
            rowStr = rows.get(0).toString();
        }
        ColumnDefinitions colDefs = rs.getColumnDefinitions();
        colStr = colDefs.toString();
        response.putString("columns", colStr.substring(8, colStr.length() - 1));
        response.putString("values", rowStr.substring(4, rowStr.length() - 1));
        return response.toString();
    }
    public static String formatQueryResult(CqlStatementResult rs, String cfname) {
        // TODO Auto-generated method stub
        String value = "";
        JsonObject response = new JsonObject();
        ColumnFamily<String, String> cf = ColumnFamily
                .newColumnFamily(cfname, StringSerializer.get(),
                        StringSerializer.get());
        Rows<String, String> rows = rs.getRows(cf);
        int rcount = 1;
        for (com.netflix.astyanax.model.Row<String, String> row : rows) {
            ColumnList<String> columns = row.getColumns();
            Collection<String> colnames = columns.getColumnNames();
            String rowStr = "";
            String colStr = "";
            if (colnames.contains("key") && colnames.contains("column1")) {
            	colStr = colStr + columns.getDateValue("column1", null).toGMTString();
            	rowStr = rowStr + columns.getStringValue("value", null); 
            	response.putString(colStr, rowStr);
            } else {
                JsonObject rowObj = new JsonObject();
	            for (String colName:colnames) {
	                //colStr = colStr+colname+",";
	               value = columns.getStringValue(colName, null);
	               //rowStr=rowStr+value+",";
	               rowObj.putString(colName, value);
	            }
	            //rowobj.putString("columns", colStr);
	            //rowobj.putString("values", rowStr);
	            response.putObject(""+rcount++, rowObj);
            }
        }
        return response.toString();
        
    }
}
