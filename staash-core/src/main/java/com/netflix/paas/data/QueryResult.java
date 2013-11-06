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
