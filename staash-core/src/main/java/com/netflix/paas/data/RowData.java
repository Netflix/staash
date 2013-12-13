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

public class RowData {
    private SchemaRows      rows;
    
    private SchemalessRows  srows;
    
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
    
    public boolean hasSchemalessRows() {
        return this.srows != null;
    }
    
    public boolean hasSchemaRows() {
        return this.rows != null;
    }

    @Override
    public String toString() {
        return "RowData [rows=" + rows + ", srows=" + srows + "]";
    }
}
