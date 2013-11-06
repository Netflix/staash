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
