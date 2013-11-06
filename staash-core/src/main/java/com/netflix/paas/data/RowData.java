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
