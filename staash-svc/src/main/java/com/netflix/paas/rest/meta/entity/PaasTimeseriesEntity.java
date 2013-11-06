package com.netflix.paas.rest.meta.entity;

import java.util.ArrayList;
import java.util.List;
import com.netflix.paas.json.JsonObject;
import com.netflix.paas.rest.util.MetaConstants;
import com.netflix.paas.rest.util.Pair;

public class PaasTimeseriesEntity extends Entity{
    private String schemaName;
    private List<Pair<String, String>> columns = new ArrayList<Pair<String, String>>();
    private String primarykey;

    public static class Builder {
        private PaasTimeseriesEntity entity = new PaasTimeseriesEntity();
        
        public Builder withJsonPayLoad(JsonObject payLoad, String schemaName) {
            entity.setRowKey(MetaConstants.PAAS_TS_ENTITY_TYPE);
            entity.setSchemaName(schemaName);
            String payLoadName = payLoad.getString("name");
            entity.setName(schemaName+"."+payLoadName);
            String type = payLoad.getString("seriestype");
            Pair<String, String> keycol;
            String columns = "";
            if (type!=null && type.equals(MetaConstants.PERIOD_TIME_SERIES))
                keycol = new Pair<String, String>("timestamp", "key");
            else keycol = new Pair<String, String>("text", "key");
            Pair<String, String> eventcol = new Pair<String, String>("timestamp", "column1");
            Pair<String, String> valuecol = new Pair<String, String>("text", "value");
            columns = keycol.getRight()+":"+keycol.getLeft()+","+eventcol.getRight()+":"+eventcol.getLeft()+","+valuecol.getRight()+":"+valuecol.getLeft();
            entity.addColumn(keycol);
            entity.addColumn(eventcol);
            entity.addColumn(valuecol);
            entity.setPrimarykey("key,column1");
            payLoad.putString("columns", columns);
            payLoad.putString("primarykey", entity.getPrimarykey());
            String load = payLoad.toString();
            entity.setPayLoad(load);
            return this;
        }                
        public PaasTimeseriesEntity build() {
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
