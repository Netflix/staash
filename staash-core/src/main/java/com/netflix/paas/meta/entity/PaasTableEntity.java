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
