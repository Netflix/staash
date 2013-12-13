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

import java.util.List;

/**
 * Collection of rows using arrays to represent the data.  All rows must
 * have the same number of columns.
 * 
 * @author elandau
 */
public class SchemaRows {
    /**
     * Names of the columns
     */
    private List<String> names;
    
    /**
     * Data types for columns (ex. int, vchar, ...)
     */
    private List<String> types;
    
    /**
     * Data rows as a list of column values.  Index of column position must match
     * index position in 'names' and 'types'
     */
    private List<List<String>> rows;

    public List<String> getNames() {
        return names;
    }
    public void setNames(List<String> names) {
        this.names = names;
    }
    public List<String> getTypes() {
        return types;
    }
    public void setTypes(List<String> types) {
        this.types = types;
    }
    public List<List<String>> getRows() {
        return rows;
    }
    public void setRows(List<List<String>> rows) {
        this.rows = rows;
    }
    @Override
    public String toString() {
        return "SchemaRows [names=" + names + ", types=" + types + ", rows="
                + rows + "]";
    }
    
}
