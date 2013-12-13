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
package com.netflix.paas.data;

/**
 * Representation of a query result which contains both the row data as well
 * as the schema definition for the row
 * 
 * @author elandau
 *
 */
public class QueryResult {
    private SchemaRows      rows;
    
    private SchemalessRows  srows;
    
    /**
     * Cursor used to resume the call
     * @note Optional
     */
    private String cursor;
    
    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public SchemaRows getRows() {
        return rows;
    }

    public void setRows(SchemaRows rows) {
        this.rows = rows;
    }

    public SchemalessRows getSrows() {
        return srows;
    }

    public void setSrows(SchemalessRows srows) {
        this.srows = srows;
    }

    @Override
    public String toString() {
        return "QueryResult [rows=" + rows + ", srows=" + srows + ", cursor=" + cursor + "]";
    }

}
