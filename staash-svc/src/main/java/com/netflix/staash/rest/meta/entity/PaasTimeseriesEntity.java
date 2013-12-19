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
package com.netflix.staash.rest.meta.entity;

import java.util.ArrayList;
import java.util.List;

import com.netflix.staash.json.JsonObject;
import com.netflix.staash.rest.util.MetaConstants;
import com.netflix.staash.rest.util.Pair;

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
