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

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Representation of rows as a sparse tree of rows to column value pairs
 * 
 * @author elandau
 *
 */
public class SchemalessRows {
    public static class Builder {
        private SchemalessRows rows = new SchemalessRows();

        public Builder() {
            rows.rows = Maps.newHashMap();
        }
        
        public Builder addRow(String row, Map<String, String> columns) {
            rows.rows.put(row, columns);
            return this;
        }
        
        public SchemalessRows build() {
            return rows;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    private Map<String, Map<String, String>> rows;

    public Map<String, Map<String, String>> getRows() {
        return rows;
    }

    public void setRows(Map<String, Map<String, String>> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "SchemalessRows [rows=" + rows + "]";
    }
    
}
