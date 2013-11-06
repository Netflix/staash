package com.netflix.paas.meta.entity;

import java.util.ArrayList;
import java.util.List;

import com.netflix.paas.json.JsonObject;
import com.netflix.paas.meta.impl.MetaConstants;
import com.netflix.paas.util.Pair;

public class PaasTableEntity extends Entity{
    private String schemaName;
    private List<Pair<String, String>> columns = new ArrayList<Pair<String, String>>();
    private String primarykey;

    public static class Builder {
        private PaasTableEntity entity = new PaasTableEntity();
        
        public Builder withJsonPayLoad(JsonObject payLoad, String schemaName) {
            entity.setRowKey(MetaConstants.PAAS_TABLE_ENTITY_TYPE);
            entity.setSchemaName(schemaName);
            String payLoadName = payLoad.getString("name");
            String load = payLoad.toString();
            entity.setName(payLoadName);
            String columnswithtypes = payLoad.getString("columns");
            String[] allCols = columnswithtypes.split(",");
            for (String col:allCols) {
                String type;
                String name;
                if (!col.contains(":")) {
                    type="text";
                    name=col;
                }
                else {
                    name = col.split(":")[0];
                    type = col.split(":")[1];
                }
                Pair<String, String> p = new Pair<String, String>(type, name);
                entity.addColumn(p);
            }
            entity.setPrimarykey(payLoad.getString("primarykey"));
            entity.setPayLoad(load);
            return this;
        }                
        public PaasTableEntity build() {
            return entity;
        }        
    }    
    public static Builder builder() {
        return new Builder();
    }
    public String getSchemaName() {
        return schemaName;
    }
    private void setSchemaName(String schemaname) {
        this.schemaName = schemaname;
    }
    private void addColumn(Pair<String, String> pair) {
        columns.add(pair);
    }
    public List<Pair<String,String>> getColumns() {
        return columns;
    }
    public String getPrimarykey() {
        return primarykey;
    }
    private void setPrimarykey(String primarykey) {
        this.primarykey = primarykey;
    }

}
